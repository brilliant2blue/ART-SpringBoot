package com.nuaa.art.vrmverify.controller;

import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import com.nuaa.art.vrmverify.service.CxHandlerService;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.service.async.AsyncVerifyTask;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static java.lang.Thread.sleep;

/**
 * 验证视图层
 * @author djl
 * @date 2024-04-02
 */
@RestController
@EnableAsync
@RequestMapping("/vrm/verify")
public class verifyController {

    @Resource
    private AsyncVerifyTask asyncVerifyTask;
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private ModelVerifyService modelVerifyService;
    @Resource
    private CxHandlerService cxHandlerService;


    /**
     * 模型检查
     * @param smvFilePath
     * @param addProperties
     * @param properties
     * @return
     */
    @PostMapping("/modelVerify")
    @Operation(summary = "模型检查")
    public HttpResult<String> modelVerify(String smvFilePath, int addProperties, @RequestParam("properties") List<String> properties){
        try{
            webSocketService.sendMsg(SocketMessage.asText("model_verify", "模型检查中..."));
            VerifyResult verifyResult = modelVerifyService.verifyModel(smvFilePath, addProperties == 1, properties);
            webSocketService.sendMsg(SocketMessage.asObject("model_verify", verifyResult));
            return HttpResult.success();
        }
        catch (Exception e){
            e.printStackTrace();
            return HttpResult.fail(e.getMessage());
        }
    }

    /**
     * 反例处理
     * @param verifyResult
     * @param propertyIndex
     * @return
     */
    @PostMapping("/handleCx")
    @Operation(summary = "反例处理")
    public HttpResult<String> handleCx(@RequestBody VerifyResult verifyResult, int propertyIndex){
        try {
            webSocketService.sendMsg(SocketMessage.asText("cx_handle", "反例分析及可视化中..."));
            VariableTable variableTable = cxHandlerService.computeVariableTable(verifyResult, propertyIndex);
            webSocketService.sendMsg(SocketMessage.asObject("cx_handle", variableTable));
            if(variableTable != null){
                CTLFormula f = cxHandlerService.parseCTLFormula(verifyResult, propertyIndex, false);
                webSocketService.sendMsg(SocketMessage.asObject("cx_handle", f));
                Set<Cause> causeSet = cxHandlerService.explainCx(variableTable, f);
                webSocketService.sendMsg(SocketMessage.asObject("cx_handle", causeSet));
            }
            return HttpResult.success();
        }
        catch (Exception e){
            e.printStackTrace();
            return HttpResult.fail(e.getMessage());
        }
    }


}
