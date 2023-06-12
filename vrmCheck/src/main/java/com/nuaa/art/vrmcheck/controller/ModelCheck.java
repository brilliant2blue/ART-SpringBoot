package com.nuaa.art.vrmcheck.controller;

import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.VRMOfXML;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrmcheck.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.bind.annotation.*;

@RestController
public class ModelCheck {
    @Resource
    ModelCheckBasicHandler basicHandler;
    @Resource
    ModelCheckInputHandler inputHandler;

    @Resource
    ModelCheckConditionHandler conditionHandler;

    @Resource
    ModelCheckEventHandler eventHandler;

    @Resource
    ModelCheckModeTransHandler modeTransHandler;

    @Resource
    ModelCheckOutputHandler outputHandler;

    @Resource
    ReportHandler reportHandler;

    @Resource
    DaoHandler daoHandler;

    @PostMapping("/vrm/{id}/check")
    @Operation(summary = "模型分析", description = "花费时间很长，应为异步响应操作")
    @Parameter(name = "id", description = "系统工程编号")
    public HttpResult<Boolean> check(@PathVariable(value = "id") Integer systemId) {
        String url = "";
        url = PathUtils.DefaultPath();
        String fileName = "";
        SystemProject systemProject = daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId);
        if (null != systemProject) {
            fileName = url + systemProject.getSystemName() + "model.xml";
        } else {
            fileName = url + "model.xml";
        }
        Document modelDoc = FileUtils.readXML(fileName);
        if (modelDoc != null) {
            try {
                VRMOfXML vrmModel = new VRMOfXML(modelDoc);
                CheckErrorReporter reporter = new CheckErrorReporter();
                reporter.setModelName(systemProject.getSystemName());

                // 基本语法分析
                LogUtils.info("基本语法分析中");
                basicHandler.checkTypeBasic(vrmModel, reporter);
                basicHandler.checkConstantBasic(vrmModel, reporter);
                basicHandler.checkInputBasic(vrmModel, reporter);
                basicHandler.checkVariableBasic(vrmModel, reporter);
                if (reporter.isBasicRight()) {
                    basicHandler.checkTableBasic(vrmModel, reporter);
                    basicHandler.checkModeClassBasic(vrmModel, reporter);
                }
                // 输入完整性分析
                if (reporter.isBasicRight()) {
                    LogUtils.info("输入完整性分析中");
                    inputHandler.checkInputIntegrityOfCondition(vrmModel, reporter);
                    inputHandler.checkInputIntegrityOfEvent(vrmModel, reporter);
                    inputHandler.checkInputIntegrityOfModeTrans(vrmModel, reporter);

                    if (reporter.isInputIntegrityRight()) {
                        // 表函数分析
                        LogUtils.info("事件一致性分析");
                        eventHandler.checkEvent(vrmModel, reporter);
                        LogUtils.info("条件一致性完整性分析");
                        conditionHandler.checkCondition(vrmModel, reporter);
                        LogUtils.info("模式转换一致性分析");
                        modeTransHandler.checkModeTrans(vrmModel, reporter);
                    }
                }

                LogUtils.info("输出完整性分析");
                outputHandler.checkOutputIntegrityOfEvent(vrmModel, reporter);
                outputHandler.checkOutputIntegrityOfCondition(vrmModel, reporter);
                reportHandler.saveCheckReport(reporter, url + systemProject.getSystemName() + "CheckReport.xml");
                return new HttpResult<>(HttpCodeEnum.SUCCESS, systemProject.getSystemName()+"模型正在验证",true);
            } catch (Exception e){
                e.printStackTrace();
                LogUtils.error(e.getMessage());
            }
        }
        return new HttpResult<>(HttpCodeEnum.NOT_EXTENDED,"模型读取失败！请检查是否已经生成模型",false);
    }

    @GetMapping("/vrm/{id}/check")
    @Operation(summary = "获取分析报告")
    public HttpResult<CheckErrorReporter> getCheckReport(@PathVariable("id") Integer systemId){
        CheckErrorReporter errorReporter = reportHandler.readCheckReport(systemId);
        if(errorReporter != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, errorReporter);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_EXTENDED, null);
        }
    }

    @GetMapping("/vrm/{id}/check/file")
    @Operation(summary = "导出分析报告到指定文件")
    public HttpResult<String> exportCheckReport(@PathVariable("id") Integer systemId, @RequestParam("url")String fileUrl){
        if(reportHandler.exportCheckReport(systemId,fileUrl)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, fileUrl);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, "");
        }
    }




}
