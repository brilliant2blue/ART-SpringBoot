package com.nuaa.art.vrmverify.controller;

import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.utils.TreeTraverseUtils;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.receive.SmvFileWIthProperties;
import com.nuaa.art.vrmverify.model.send.ReturnExplainResult;
import com.nuaa.art.vrmverify.model.send.ReturnVerifyResult;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import com.nuaa.art.vrmverify.service.CxHandlerService;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.service.async.AsyncVerifyTask;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * smv文件模型检查
     * @return
     */
    @PostMapping("/smvFileVerify")
    @Operation(summary = "smv文件模型检查")
    public HttpResult<String> smvFileVerify(SmvFileWIthProperties smvFileWIthProperties){
        try{
            webSocketService.sendMsg(SocketMessage.asText("model_verify", "模型检查中..."));
            ReturnVerifyResult returnVerifyResult = modelVerifyService.verifyModel(smvFileWIthProperties.getSmvFilePath(),
                    smvFileWIthProperties.getPropertyCount() > 0,
                    smvFileWIthProperties.getProperties());

            webSocketService.sendMsg(SocketMessage.asObject("model_verify", returnVerifyResult));
            return HttpResult.success();
        }
        catch (Exception e){
            e.printStackTrace();
            webSocketService.sendMsg(SocketMessage.asText(e.getMessage()));
            return HttpResult.fail(e.getMessage());
        }
    }

    /**
     * VRM模型检查 TODO
     * @param systemId
     * @param propertyCount
     * @param properties
     * @return
     */
    @PostMapping("/vrmModelVerify")
    @Operation(summary = "vrm模型检查")
    public HttpResult<String> vrmModelVerify(int systemId, Integer propertyCount, @RequestParam("properties") List<String> properties) {
        try{
            // TODO VRM模型转换为smv文件
            webSocketService.sendMsg(SocketMessage.asText("model_verify", "模型检查中..."));
            String smvFilePath = "";
            ReturnVerifyResult returnVerifyResult = modelVerifyService.verifyModel(smvFilePath, propertyCount == 1, properties);
            webSocketService.sendMsg(SocketMessage.asObject("model_verify", returnVerifyResult));
            return HttpResult.success();
        }
        catch (Exception e){
            e.printStackTrace();
            webSocketService.sendMsg(SocketMessage.asText(e.getMessage()));
            return HttpResult.fail(e.getMessage());
        }
    }

    /**
     * 反例处理
     * @param verifyResult
     * @return
     */
    @PostMapping("/handleCx")
    @Operation(summary = "反例处理")
    public HttpResult<String> handleCx(@RequestBody VerifyResult verifyResult){
        try {
            webSocketService.sendMsg(SocketMessage.asText("cx_handle", "反例分析及可视化中..."));
            List<VariableTable> variableTableList = cxHandlerService.computeVariableTables(verifyResult);
            if(variableTableList != null && !variableTableList.isEmpty()){
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
                ReturnExplainResult returnExplainResult = new ReturnExplainResult(variableTableList.size(),
                        ctlFormulaList,
                        highlightedPropertiesList,
                        variableTableList,
                        causeSetList);
                webSocketService.sendMsg(SocketMessage.asObject("cx_handle", returnExplainResult));
            }
            return HttpResult.success();
        }
        catch (Exception e){
            e.printStackTrace();
            webSocketService.sendMsg(SocketMessage.asText(e.getMessage()));
            return HttpResult.fail(e.getMessage());
        }
    }

    /**
     * 获取指定smv文件内容
     * @param smvFilePath
     * @return
     */
    @PostMapping("/getSmvFile")
    @Operation(summary = "获取指定smv文件内容")
    public HttpResult<String> getSmvFile(String smvFilePath){
        try{
            String data = FileUtils.readFileToString(new File(smvFilePath), "UTF-8");
            return HttpResult.success(data);
        }
        catch (Exception e){
            e.printStackTrace();
            return HttpResult.fail(Msg.FILE_NOT_FOUND);
        }
    }
}
