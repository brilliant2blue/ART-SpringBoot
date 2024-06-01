package com.nuaa.art.vrmverify.service.async;

import com.nuaa.art.common.EventLevelEnum;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.receive.SmvFileWIthProperties;
import com.nuaa.art.vrmverify.model.receive.VrmModelWithProperties;
import com.nuaa.art.vrmverify.model.send.ReturnExplainResult;
import com.nuaa.art.vrmverify.model.send.ReturnVerifyResult;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import com.nuaa.art.vrmverify.service.CxHandlerService;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import com.nuaa.art.vrmverify.service.Vrm2SmvService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private boolean isInterrupted = false;

    /**
     * 异步模型检查（smv文件）
     * @param smvFileWIthProperties
     */
    @Async("AsyncTask")
    public void asyncSmvFileVerify(SmvFileWIthProperties smvFileWIthProperties) {
        try {
            webSocketService.sendProgressMsg("模型验证中...");
            ReturnVerifyResult returnVerifyResult = modelVerifyService.verifyModelFromSmvFile(
                    smvFileWIthProperties.getSmvFilePath(),
                    smvFileWIthProperties.getPropertyCount() > 0,
                    smvFileWIthProperties.getProperties());
            if(isInterrupted){
                isInterrupted = false;
                return;
            }
            webSocketService.sendProgressMsg("模型验证完成！",EventLevelEnum.SUCCESS);
            webSocketService.sendMsg(SocketMessage.asObject("model_verify", returnVerifyResult));
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            webSocketService.sendDialogMsg(e.getMessage(), EventLevelEnum.ERROR);
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
            webSocketService.sendProgressMsg("vrm模型转为smv模型中...");
            String smvStr = vrm2SmvService.transformVrm2Smv(
                    vrmModelWithProperties.getSystemId(),
                    vrmModelWithProperties.getSystemName(),
                    user);
            webSocketService.sendProgressMsg("模型转换完成！",EventLevelEnum.SUCCESS);
            webSocketService.sendProgressMsg("模型验证中...");
            ReturnVerifyResult returnVerifyResult = modelVerifyService.verifyModelFromSmvStr(
                    vrmModelWithProperties.getSystemName(),
                    smvStr,
                    vrmModelWithProperties.getPropertyCount() > 0,
                    vrmModelWithProperties.getProperties());
            if(isInterrupted){
                isInterrupted = false;
                return;
            }
            webSocketService.sendProgressMsg("模型验证完成！",EventLevelEnum.SUCCESS);
            webSocketService.sendMsg(SocketMessage.asObject("model_verify", returnVerifyResult));
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            webSocketService.sendDialogMsg(e.getMessage(), EventLevelEnum.ERROR);
        }
    }

    /**
     * 异步反例处理
     * @param verifyResult
     * @param type
     * @param name
     */
    @Async("AsyncTask")
    public void asyncHandleCx(VerifyResult verifyResult, Integer type, String name){
        try{
            webSocketService.sendProgressMsg("反例分析及可视化中...");
            List<VariableTable> variableTableList = cxHandlerService.computeVariableTables(verifyResult);
            if(variableTableList != null && !variableTableList.isEmpty()) {
                List<CTLFormula> ctlFormulaList = new ArrayList<>();
                List<List<String>> highlightedPropertiesList = new ArrayList<>();
                List<Set<Cause>> causeSetList = new ArrayList<>();
                for (int i = 0; i < variableTableList.size(); i++) {
                    VariableTable variableTable = variableTableList.get(i);
                    CTLFormula f = cxHandlerService.parseCTLFormula(
                            variableTable.getProperty(),
                            variableTable.getVariableValues(),
                            false);
                    ctlFormulaList.add(f);
                    cxHandlerService.computeFormulaValues(f, variableTable);
                    Set<Cause> causeSet = cxHandlerService.explainCx(variableTable, f);
                    causeSetList.add(causeSet);
                    highlightedPropertiesList.add(cxHandlerService.genHighlightedProperty(f, causeSet));
                }
                ReturnExplainResult returnExplainResult = new ReturnExplainResult(
                        type,
                        name,
                        variableTableList.size(),
                        ctlFormulaList,
                        highlightedPropertiesList,
                        variableTableList,
                        causeSetList);
                webSocketService.sendProgressMsg("反例解析完成！",EventLevelEnum.SUCCESS);
                webSocketService.sendMsg(SocketMessage.asObject("cx_handle", returnExplainResult));
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            webSocketService.sendDialogMsg(e.getMessage(), EventLevelEnum.ERROR);
        }
    }

    public void setInterrupted(){
        this.isInterrupted = true;
    }

    public void cancelInterrupted(){
        this.isInterrupted = false;
    }

}
