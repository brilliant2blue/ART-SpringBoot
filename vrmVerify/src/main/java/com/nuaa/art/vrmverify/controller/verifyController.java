package com.nuaa.art.vrmverify.controller;

import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.utils.TreeTraverseUtils;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.receive.SmvFileWIthProperties;
import com.nuaa.art.vrmverify.model.receive.VrmModelWithProperties;
import com.nuaa.art.vrmverify.model.send.ReturnExplainResult;
import com.nuaa.art.vrmverify.model.send.ReturnVerifyResult;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import com.nuaa.art.vrmverify.service.CxHandlerService;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.service.Vrm2SmvService;
import com.nuaa.art.vrmverify.service.async.AsyncVerifyTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.Resource;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private Vrm2SmvService vrm2SmvService;
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
            ReturnVerifyResult returnVerifyResult = modelVerifyService.verifyModelFromSmvFile(
                    smvFileWIthProperties.getSmvFilePath(),
                    smvFileWIthProperties.getPropertyCount() > 0,
                    smvFileWIthProperties.getProperties());
            webSocketService.sendMsg(SocketMessage.asObject("model_verify", returnVerifyResult));
            return HttpResult.success();
        }
        catch (Exception e){
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            return HttpResult.fail(e.getMessage());
        }
    }

    /**
     * vrm模型检查
     * @param vrmModelWithProperties
     * @return
     */
    @PostMapping("/vrmModelVerify")
    @Operation(summary = "vrm模型检查")
    public HttpResult<String> vrmModelVerify(VrmModelWithProperties vrmModelWithProperties, String user) {
        try{
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
            return HttpResult.success();
        }
        catch (Exception e){
            e.printStackTrace();
            LogUtils.error(e.getMessage());
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
            LogUtils.error(e.getMessage());
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
            LogUtils.error(e.getMessage());
            return HttpResult.fail(Msg.FILE_NOT_FOUND);
        }
    }
}
