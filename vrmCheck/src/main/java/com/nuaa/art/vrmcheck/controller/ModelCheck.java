package com.nuaa.art.vrmcheck.controller;

import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    DaoHandler daoHandler;

    @GetMapping("/vrm/{id}/modelcheck/")
    @Operation(summary = "模型分析", description = "花费时间很长，应为异步响应操作")
    @Parameter(name = "id", description = "系统工程编号")
    public HttpResult<CheckErrorReporter> check(@RequestParam(value = "id") Integer systemId) {
        String url = "";
        url = PathUtils.DefaultPath();
        String fileName = "";
        if (null != daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId)) {
            fileName = url + daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId).getSystemName() + "model.xml";
        } else {
            fileName = url + "model.xml";
        }
        Document modelDoc = FileUtils.readXML(fileName);
        if (modelDoc != null) {
            VRMOfXML vrmModel = new VRMOfXML(modelDoc);
            CheckErrorReporter reporter = new CheckErrorReporter();

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

            System.out.println(reporter.toString());
            return new HttpResult<>(HttpCodeEnum.SUCCESS,reporter);
        }
        return new HttpResult<>(HttpCodeEnum.NOT_EXTENDED,"模型读取失败！请检查是否已经生成模型",null);
    }

}
