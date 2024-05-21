package com.nuaa.art.vrmcheck.controller.asynctask;

import com.nuaa.art.common.EventLevelEnum;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrm.service.handler.ModelHandler;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;
import com.nuaa.art.vrmcheck.service.ReportHandler;
import com.nuaa.art.vrmcheck.service.table.*;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * 异步任务处理程序
 * 受springboot限制，异步函数不能和调用函数写在一个类中。
 * 故单独创建一个用于处理异步函数的类
 *
 * @author konsin
 * @date 2023/12/13
 */
@Component("vrmCheck")
public class AsyncTaskHandler {
    @Resource(name = "concept-object")
    ModelCreateHandler conceptObj;

    @Resource
    BasicCheck<VRM> basicVRMCheck;

    @Resource
    WebSocketService webSocket;

    @Async("AsyncTask")
    public void asyncConceptCheck(Integer systemId) {
        webSocket.sendProgressMsg("整理领域概念库条目中...");
        VRM vrm = (VRM) conceptObj.createModel(systemId);
        CheckErrorReporter reporter = new CheckErrorReporter();
        reporter.setModelName(vrm.getSystem().getSystemName());
        webSocket.sendProgressMsg("检查类型中...");
        basicVRMCheck.checkTypeBasic(vrm, reporter);
        webSocket.sendProgressMsg("检查常量中...");
        basicVRMCheck.checkConstantBasic(vrm, reporter);
        webSocket.sendProgressMsg("检查输入变量中...");
        basicVRMCheck.checkInputBasic(vrm, reporter);
        webSocket.sendProgressMsg("检查中间变量中...");
        webSocket.sendProgressMsg("检查输出变量中...");
        basicVRMCheck.checkVariableBasic(vrm, reporter);
        webSocket.sendProgressMsg("检查模式集中...");
        basicVRMCheck.checkModeClassBasic(vrm, reporter);
        webSocket.sendProgressMsg("检查完毕，共" + reporter.errorCount + "条错误", EventLevelEnum.SUCCESS);
        webSocket.sendObject("concept-check", reporter);
    }

    @Resource
    BasicCheck<HVRM> basicCheck;

    @Resource
    ModuleCheck moduleCheck;

    @Resource
    InputCheck inputCheck;
    @Resource
    OutputCheck outputCheck;

    @Resource(name = "ConditionCheckV2")
    ConditionCheck conditionCheck;

    @Resource(name = "AndOrEvent")
    EventCheck eventCheck;

    @Resource
    ModelHandler modelHandler;

    @Resource
    ReportHandler reportHandler;


    @Async("AsyncTask")
    public void checkProcess(HVRM vrmModel) throws Exception {
        webSocket.sendProgressMsg("需求模型%s分析中...".formatted(vrmModel.getSystem().getSystemName()));
        CheckErrorReporter reporter = new CheckErrorReporter();
        reporter.setModelName(vrmModel.getSystem().getSystemName());
        int pre =0;
        // 基本语法分析
        LogUtils.info("基本语法分析中");
        webSocket.sendProgressMsg( "基本语法分析中...");
        basicCheck.checkTypeBasic(vrmModel, reporter);
        basicCheck.checkConstantBasic(vrmModel, reporter);
        basicCheck.checkInputBasic(vrmModel, reporter);
        basicCheck.checkVariableBasic(vrmModel, reporter);
        if (reporter.isBasicRight()) {
            basicCheck.checkTableBasic(vrmModel, reporter);
            basicCheck.checkModeClassBasic(vrmModel, reporter);
        }

        pre = printErrorAndUpdatePre(pre,reporter.errorCount,"基本语法错误数");

        if (reporter.isBasicRight()) {
            webSocket.sendProgressMsg("模式转换输入完整性分析中");
            VRM tModel = modelHandler.convert(vrmModel, VRM.class);
            inputCheck.checkInputIntegrityOfModeTrans(tModel, reporter);
            pre = printErrorAndUpdatePre(pre,reporter.errorCount,"模式转换输入完整性错误数");
            webSocket.sendProgressMsg("模块一致性完整性分析中");
            moduleCheck.checkModuleIntegrityAndConsistency(vrmModel, reporter);
            pre = printErrorAndUpdatePre(pre,reporter.errorCount,"模块一致性完整性错误数");
            if (reporter.isModuleRight()) {
                List<VRM> subModel = new ArrayList<>();
                modelHandler.getModuleModels(vrmModel, subModel, VRM.class);
                for (VRM model : subModel) {
                    webSocket.sendProgressMsg("分析模块%s中...".formatted(model.getSystem().getSystemName()));
                    originCheckProcess(model, reporter);
                }
            }
            LogUtils.info("模式转换一致性分析");
            pre = reporter.errorCount;
            webSocket.sendProgressMsg("模式转换一致性分析中...");
            sleep(500);
            eventCheck.checkModeTransConsistency(tModel, reporter);
            pre = printErrorAndUpdatePre(pre,reporter.errorCount,"模式转换一致性错误数");

        }

        reportHandler.saveCheckReport(reporter, PathUtils.DefaultPath() + vrmModel.getSystem().getSystemName() + "CheckReport.xml");
        if(reporter.getErrorCount().equals(0)){
            webSocket.sendProgressMsg("模型分析结束，共发现错误数目：" + reporter.getErrorCount().toString(), EventLevelEnum.ERROR);
        } else {
            webSocket.sendProgressMsg("模型分析结束，共发现错误数目：" + reporter.getErrorCount().toString(), EventLevelEnum.ERROR);
        }
        webSocket.sendObject("vrm-check", reporter);
    }

    public void originCheckProcess(VRM model, CheckErrorReporter reporter) throws InterruptedException {
        int pre = reporter.errorCount;
        // 输入完整性分析
        LogUtils.info("输入完整性分析中");
        webSocket.sendProgressMsg("输入完整性分析中...");
        //sleep(500);
        reporter.setInputIntegrityRight(true);
        inputCheck.checkInputIntegrityOfCondition(model, reporter);
        inputCheck.checkInputIntegrityOfEvent(model, reporter);
        pre = printErrorAndUpdatePre(pre,reporter.errorCount,"输入完整性错误数");
        if (reporter.isInputIntegrityRight()) {
            // 表函数分析

            LogUtils.info("条件一致性完整性分析");
            webSocket.sendProgressMsg("条件一致性完整性分析中...");
            //sleep(500);

            conditionCheck.checkConditionIntegrityAndConsistency(model, reporter);
            pre = printErrorAndUpdatePre(pre,reporter.errorCount,"条件一致性完整性错误数");
            LogUtils.info("事件一致性分析");
            webSocket.sendProgressMsg("事件一致性分析中...");
            //sleep(500);
            eventCheck.checkEventConsistency(model, reporter);
            pre = printErrorAndUpdatePre(pre,reporter.errorCount,"事件一致性错误数");
        }

        LogUtils.info("输出完整性分析中");
        webSocket.sendProgressMsg("输出完整性分析中...");
        //sleep(500);
        outputCheck.checkOutputIntegrityOfEvent(model, reporter);
        outputCheck.checkOutputIntegrityOfCondition(model, reporter);
        pre = printErrorAndUpdatePre(pre,reporter.errorCount,"输出完整性错误数");
    }

    @Async("AsyncTask")
    public void read(int systemId) {
        webSocket.sendProgressMsg("解析报告中");
        CheckErrorReporter errorReporter = reportHandler.readCheckReport(systemId);
        webSocket.sendObject("vrm-check", errorReporter);
        webSocket.sendProgressMsg("报告解析完毕。打开报告：" + errorReporter.getModelName(), EventLevelEnum.SUCCESS);
    }


    int printErrorAndUpdatePre(int pre, int now, String s){
        if(now>pre){
            webSocket.sendProgressMsg(s+(now-pre),EventLevelEnum.ERROR);
            pre = now;
        }
        return pre;
    }

}
