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

    /**
     * smv文件模型检查
     * @return
     */
    @PostMapping("/smvFileVerify")
    @Operation(summary = "smv文件模型检查")
    public HttpResult<String> smvFileVerify(SmvFileWIthProperties smvFileWIthProperties){
        asyncVerifyTask.asyncSmvFileVerify(smvFileWIthProperties);
        return HttpResult.success();
    }

    /**
     * vrm模型检查
     * @param vrmModelWithProperties
     * @param user
     * @return
     */
    @PostMapping("/vrmModelVerify")
    @Operation(summary = "vrm模型检查")
    public HttpResult<String> vrmModelVerify(VrmModelWithProperties vrmModelWithProperties, String user) {
        asyncVerifyTask.asyncSmvStrVerify(vrmModelWithProperties, user);
        return HttpResult.success();
    }

    /**
     * 反例处理
     * @param verifyResult
     * @return
     */
    @PostMapping("/handleCx")
    @Operation(summary = "反例处理")
    public HttpResult<String> handleCx(@RequestBody VerifyResult verifyResult, Integer type, String name){
        asyncVerifyTask.asyncHandleCx(verifyResult, type, name);
        return HttpResult.success();
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
