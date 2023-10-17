package com.nuaa.art.vrmcheck.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.model.VariableRealationModel;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.VRMOfXML;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrmcheck.service.*;
import com.nuaa.art.vrmcheck.service.obj.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.dom4j.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;

@RestController
public class ModelCheck {
    @Resource
    WebSocketService webSocket;
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

    @Resource(name = "vrm-object")
    ModelCreateHandler modelObjectCreate;


    @PostMapping("/vrm/{id}/check")
    @Operation(summary = "模型分析", description = "花费时间很长，应为异步响应操作")
    @Parameter(name = "id", description = "系统工程编号")
    public HttpResult<Boolean> checkmodel(@PathVariable(value = "id") Integer systemId) {

        SystemProject systemProject = daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId);
        String fileName = PathUtils.DefaultPath() + systemProject.getSystemName() + "model.xml";
        VariableRealationModel vrm = null;
        try {
            webSocket.sendMsg(SocketMessage.asText("vrm-check","开始解析模型文件"));
            sleep(500);
            vrm = (VariableRealationModel) modelObjectCreate.modelFile(systemId, fileName);
            webSocket.sendMsg(SocketMessage.asText("vrm-check","模型文件解析成功, 开始进行模型分析。"));
//            webSocket.sendMsg(SocketMessage.asObject("test", vrm));
        } catch (Exception e){
            e.printStackTrace();
            return HttpResult.fail("模型文件解析失败。");
        }


        if (vrm != null) {
            try {
                checkProcess(vrm);
            } catch (Exception e) {
                e.printStackTrace();
                webSocket.sendMsg(SocketMessage.asText("vrm-model","模型分析失败"));
            }

        }
        return HttpResult.success();
    }

    @Resource
    BasicCheck basicCheck;
    @Resource
    InputCheck inputCheck;
    @Resource
    OutputCheck outputCheck;

    @Resource
    ConditionCheck conditionCheck;

    @Resource
    EventCheck eventCheck;


    @Async("AsyncTask")
    void checkProcess(VariableRealationModel vrmModel) throws Exception {
        CheckErrorReporter reporter = new CheckErrorReporter();
        reporter.setModelName(vrmModel.getSystem().getSystemName());

        // 基本语法分析
        LogUtils.info("基本语法分析中");
        webSocket.sendMsg(SocketMessage.asText("vrm-check","基本语法分析中"));
        basicCheck.checkTypeBasic(vrmModel, reporter);
        basicCheck.checkConstantBasic(vrmModel, reporter);
        basicCheck.checkInputBasic(vrmModel, reporter);
        basicCheck.checkVariableBasic(vrmModel, reporter);
        if (reporter.isBasicRight()) {
            basicCheck.checkTableBasic(vrmModel, reporter);
            basicCheck.checkModeClassBasic(vrmModel, reporter);
        }

        // 输入完整性分析
        if (reporter.isBasicRight()) {
            LogUtils.info("输入完整性分析中");
            webSocket.sendMsg(SocketMessage.asText("vrm-check","输入完整性分析中"));
            sleep(500);

            inputCheck.checkInputIntegrityOfCondition(vrmModel, reporter);
            inputCheck.checkInputIntegrityOfEvent(vrmModel, reporter);
            inputCheck.checkInputIntegrityOfModeTrans(vrmModel, reporter);


            if (reporter.isInputIntegrityRight()) {
                // 表函数分析

                LogUtils.info("条件一致性完整性分析");
                webSocket.sendMsg(SocketMessage.asText("vrm-check","条件一致性完整性分析中"));
                sleep(500);

                conditionCheck.checkConditionIntegrityAndConsistency(vrmModel, reporter);

                LogUtils.info("事件一致性分析");
                webSocket.sendMsg(SocketMessage.asText("vrm-check","事件一致性分析中"));
                sleep(500);
                eventCheck.checkEventConsistency(vrmModel, reporter);

                LogUtils.info("模式转换一致性分析");
                webSocket.sendMsg(SocketMessage.asText("vrm-check","模式转换一致性分析中"));
                sleep(500);
                eventCheck.checkModeTransConsistency(vrmModel, reporter);
            }

            LogUtils.info("输出完整性分析中");
            webSocket.sendMsg(SocketMessage.asText("vrm-check","输出完整性分析中"));
            sleep(500);
            outputCheck.checkOutputIntegrityOfEvent(vrmModel, reporter);
            outputCheck.checkOutputIntegrityOfCondition(vrmModel, reporter);
        }

        reportHandler.saveCheckReport(reporter, PathUtils.DefaultPath() + vrmModel.getSystem().getSystemName() + "CheckReport.xml");
        webSocket.sendMsg(SocketMessage.asText("vrm-check","模型分析结束，共发现错误数目："+reporter.getErrorCount().toString()));
        webSocket.sendMsg(SocketMessage.asObject("vrm-check", reporter));
    }


    @GetMapping("/vrm/{id}/check")
    @Operation(summary = "获取分析报告")
    public HttpResult getCheckReport(@PathVariable("id") Integer systemId){
            read(systemId);
            return HttpResult.success();
    }

    @Async("AsyncTask")
    public void read(int systemId) {
        webSocket.sendMsg(SocketMessage.asText("vrm-check","解析报告中"));
        CheckErrorReporter errorReporter = reportHandler.readCheckReport(systemId);
        webSocket.sendMsg(SocketMessage.asObject("vrm-check", errorReporter));
        webSocket.sendMsg(SocketMessage.asText("vrm-check","报告解析完毕。打开报告："+errorReporter.getModelName()));
    }

    @PostMapping("/vrm/{id}/check/file")
    @Operation(summary = "导出分析报告到指定文件")
    public HttpResult<String> exportCheckReport(@PathVariable("id") Integer systemId, @RequestParam("url")String fileUrl){
        if(reportHandler.exportCheckReport(systemId,fileUrl)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, fileUrl);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, "");
        }
    }

    @GetMapping("vrm/{id}/check/report")
    @Operation(summary = "检查本地报告是否存在")
    public HttpResult<String> exist(@PathVariable(value = "id") Integer systemId){
        try {
            SystemProject system = daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId);
            String fileName = PathUtils.DefaultPath() + system.getSystemName() + "CheckReport.xml";
            File file = new File(fileName);
            if(file.exists())
                return HttpResult.success(fileName);
            else return HttpResult.success("");
        } catch (Exception e){
            e.printStackTrace();
            return HttpResult.fail();
        }
    }




}
