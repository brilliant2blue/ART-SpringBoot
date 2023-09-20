package com.nuaa.art.main.controller;

import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.vrm.model.ConditionTable;
import com.nuaa.art.vrm.model.EventTable;
import com.nuaa.art.vrm.model.VRMOfXML;
import com.nuaa.art.vrm.service.dao.ModeService;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.common.utils.ConditionTableUtils;
import com.nuaa.art.vrm.common.utils.EventTableUtils;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrmcheck.service.*;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.dom4j.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
    @Resource
    DaoHandler daoHandler;
    @Resource(name = "vrm-xml")
    ModelCreateHandler modelCreateHandler;
    @GetMapping("/test")
    public String test(){
        return daoHandler.getDaoService(ModeService.class).getModeById(339).toString();
    }
    @PostMapping("/test/login")
    public HttpResult<String> login(){
        return new HttpResult<>(HttpCodeEnum.SUCCESS, "成功");
    }

    @Parameter(name = "systemId", description = "系统编号")
    @GetMapping("/testCreate")
    public void test2(Integer systemId){
        modelCreateHandler.createModel(systemId);
    }

    @Parameter(name = "systemId", description = "系统编号")
    @Parameter(name = "fileName", description = "文件名")
    @GetMapping("/testExport")
    public void test3(Integer systemId, String fileName){
        modelCreateHandler.modelFile(systemId, fileName);
    }

    @Resource(name = "conditionForTable")
    ConditionTableUtils conditionTableUtils;
    @PostMapping("/test/and-or-condition")
    public String tcs(@RequestBody ConditionTable c){
        System.out.println(c.toString());
        String res =  conditionTableUtils.ConvertTableToString(c);
        System.out.println(res);
        return res;
    }

    @GetMapping("/test/and-or-condition")
    public ConditionTable tsc(String s){
        System.out.println(s);
        return conditionTableUtils.ConvertStringToTable(s);
    }

    @Resource
    EventTableUtils eventTableUtils;
    @PostMapping("/test/event")
    public String tes(@RequestBody EventTable c){
        System.out.println(c.toString());
        String res =  eventTableUtils.ConvertTableToString(c);
        System.out.println(res);
        return res;
    }

    @GetMapping("/test/event")
    public EventTable tse(String s){
        System.out.println(s);
        return eventTableUtils.ConvertStringToTable(s);
    }


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


    @GetMapping("/test/newcheck")
    public CheckErrorReporter testcheck(){
        String fileName = "D:\\CodePath\\Code\\VRM\\variable-relation-model-generate-tool\\Formodel.xml";
        Document model = FileUtils.readXML(fileName);
        VRMOfXML vrmModel = new VRMOfXML(model);
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
