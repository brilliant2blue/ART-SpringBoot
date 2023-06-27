package com.nuaa.art.vrmcheck.controller;

import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.vrm.service.handler.DaoHandler;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.model.VRMOfXML;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrmcheck.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

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

    @GetMapping("/vrm/check")
    @Operation(description = "模型分析， 花费时间很长，应为异步响应操作")
    @Parameter(name = "id", description = "系统工程编号")
    public CheckErrorReporter Check(@RequestParam(value = "id") Integer systemId){
        String url = "";
        url = PathUtils.DefaultPath();
        String fileName = "";
        if (null != daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId)) {
            fileName = url + daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId).getSystemName() + "model.xml";
        } else {
            fileName = url + "model.xml";
        }
        VRMOfXML vrmModel = new VRMOfXML(fileName);
        CheckErrorReporter reporter = new CheckErrorReporter();

        // 基本语法分析
        basicHandler.checkTypeBasic(vrmModel,reporter);
        basicHandler.checkConstantBasic(vrmModel, reporter);
        basicHandler.checkInputBasic(vrmModel, reporter);
        basicHandler.checkVariableBasic(vrmModel,reporter);
        if(reporter.isBasicRight()){
            basicHandler.checkTableBasic(vrmModel, reporter);
            basicHandler.checkModeClassBasic(vrmModel, reporter);
        }
        // 输入完整性分析
        if(reporter.isBasicRight()){
            inputHandler.checkInputIntegrityOfCondition(vrmModel, reporter);
            inputHandler.checkInputIntegrityOfEvent(vrmModel, reporter);
            inputHandler.checkInputIntegrityOfModeTrans(vrmModel, reporter);

            if(reporter.isInputIntegrityRight()){
                // 表函数分析
                System.out.println("事件一致性检查");
                eventHandler.checkEvent(vrmModel, reporter);
                System.out.println("条件一致性完整性检查");
                conditionHandler.checkCondition(vrmModel, reporter);
                System.out.println("模式转换一致性检查");
                modeTransHandler.checkModeTrans(vrmModel,reporter);

            }
        }

        outputHandler.checkOutputIntegrityOfEvent(vrmModel, reporter);
        outputHandler.checkOutputIntegrityOfCondition(vrmModel, reporter);

        System.out.println(reporter.toString());
        return reporter;
    }

}
