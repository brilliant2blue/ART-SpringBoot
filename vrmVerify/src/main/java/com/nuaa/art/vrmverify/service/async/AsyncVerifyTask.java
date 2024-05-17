package com.nuaa.art.vrmverify.service.async;

import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.receive.SmvFileWIthProperties;
import com.nuaa.art.vrmverify.model.receive.VrmModelWithProperties;
import com.nuaa.art.vrmverify.model.send.ReturnVerifyResult;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import com.nuaa.art.vrmverify.service.CxHandlerService;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import com.nuaa.art.vrmverify.service.Vrm2SmvService;
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
    private Vrm2SmvService vrm2SmvService;

    @Resource
    private CxHandlerService cxHandlerService;

    @Resource
    private WebSocketService webSocketService;

    /**
     * 异步模型检查（smv文件）
     * @param smvFileWIthProperties
     */
    @Async("AsyncTask")
    public void asyncSmvFileVerify(SmvFileWIthProperties smvFileWIthProperties) {
        try {
            webSocketService.sendMsg(SocketMessage.asText("model_verify", "模型检查中..."));
            ReturnVerifyResult returnVerifyResult = modelVerifyService.verifyModelFromSmvFile(
                    smvFileWIthProperties.getSmvFilePath(),
                    smvFileWIthProperties.getPropertyCount() > 0,
                    smvFileWIthProperties.getProperties());
            webSocketService.sendMsg(SocketMessage.asObject("model_verify", returnVerifyResult));
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            webSocketService.sendMsg(SocketMessage.asError(e.getMessage()));
        }
    }

    /**
     * 异步模型检查（smv字符串）
     * @param vrmModelWithProperties
     * @param user
     */
    @Async("AsyncTask")
    public void asyncSmvStrVerify(VrmModelWithProperties vrmModelWithProperties, String user){
        try {
            webSocketService.sendMsg(SocketMessage.asText("model_verify", "vrm模型转为smv模型中..."));
            String smvStr = vrm2SmvService.transformVrm2Smv(
                    vrmModelWithProperties.getSystemId(),
                    vrmModelWithProperties.getSystemName(),
                    user);
            webSocketService.sendMsg(SocketMessage.asText("model_verify", "模型检查中..."));
            ReturnVerifyResult returnVerifyResult = modelVerifyService.verifyModelFromSmvStr(
                    vrmModelWithProperties.getSystemName(),
                    smvStr,
                    vrmModelWithProperties.getPropertyCount() > 0,
                    vrmModelWithProperties.getProperties());
            webSocketService.sendMsg(SocketMessage.asObject("model_verify", returnVerifyResult));
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            webSocketService.sendMsg(SocketMessage.asError(e.getMessage()));
        }
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
            CTLFormula f = cxHandlerService.parseCTLFormula(
                    verifyResult.getCxList().get(propertyIndex).getProperty(),
                    variableTable.getVariableValues(),
                    false);
            webSocketService.sendMsg(SocketMessage.asObject("cx_handle", f));
            Set<Cause> causeSet = cxHandlerService.explainCx(variableTable, f);
            webSocketService.sendMsg(SocketMessage.asObject("cx_handle", causeSet));
        }
    }

}
