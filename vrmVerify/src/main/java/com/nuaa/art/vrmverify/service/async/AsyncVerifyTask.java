package com.nuaa.art.vrmverify.service.async;

import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import com.nuaa.art.vrmverify.service.CxHandlerService;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 异步验证任务
 * @author djl
 * @date 2024-04-02
 */
@Service
public class AsyncVerifyTask {

    @Resource
    private ModelVerifyService modelVerifyService;

    @Resource
    private CxHandlerService cxHandlerService;

    @Resource
    private WebSocketService webSocketService;

    /**
     * 异步模型检查
     * @param smvFilePath
     * @param addProperties
     * @param properties
     */
    @Async("AsyncTask")
    public void asyncModelVerify(String smvFilePath, boolean addProperties, List<String> properties) {
        webSocketService.sendMsg(SocketMessage.asText("model_verify", "模型检查中..."));
        VerifyResult verifyResult = modelVerifyService.verifyModel(smvFilePath, addProperties, properties);
        webSocketService.sendMsg(SocketMessage.asObject("model_verify", verifyResult));
    }

    /**
     * 异步反例处理
     * @param propertyIndex
     */
    @Async("AsyncTask")
    public void asyncHandleCx(VerifyResult verifyResult, int propertyIndex){
        webSocketService.sendMsg(SocketMessage.asText("cx_handle", "反例分析及可视化中..."));
        VariableTable variableTable = cxHandlerService.computeVariableTable(verifyResult, propertyIndex);
        webSocketService.sendMsg(SocketMessage.asObject("cx_handle", variableTable));
        if(variableTable != null){
            CTLFormula f = cxHandlerService.parseCTLFormula(verifyResult, propertyIndex, false);
            webSocketService.sendMsg(SocketMessage.asObject("cx_handle", f));
            Set<Cause> causeSet = cxHandlerService.explainCx(variableTable, f);
            webSocketService.sendMsg(SocketMessage.asObject("cx_handle", causeSet));
        }
    }

}
