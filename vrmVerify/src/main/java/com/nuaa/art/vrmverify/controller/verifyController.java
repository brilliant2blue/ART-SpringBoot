package com.nuaa.art.vrmverify.controller;

import com.nuaa.art.common.EventLevelEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.model.receive.SmvFileWIthProperties;
import com.nuaa.art.vrmverify.model.receive.VrmModelWithProperties;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.service.async.AsyncVerifyTask;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;


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
    private ModelVerifyService modelVerifyService;
    @Resource
    private WebSocketService webSocketService;

    /**
     * smv文件模型检查
     * @return
     */
    @PostMapping("/smvFileVerify")
    @Operation(summary = "smv文件模型检查")
    public HttpResult<String> smvFileVerify(SmvFileWIthProperties smvFileWIthProperties){
        asyncVerifyTask.setInterrupted();
        if(killCurVerifyProcess())
            webSocketService.sendNormalMsg("当前模型验证终止！", EventLevelEnum.WARN);
        else
            asyncVerifyTask.cancelInterrupted();
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
        asyncVerifyTask.setInterrupted();
        if(killCurVerifyProcess())
            webSocketService.sendNormalMsg("当前模型验证终止！", EventLevelEnum.WARN);
        else
            asyncVerifyTask.cancelInterrupted();
        asyncVerifyTask.asyncSmvStrVerify(vrmModelWithProperties, user);
        return HttpResult.success();
    }

    /**
     * 终止当前模型验证
     * @return
     */
    @PostMapping("/stopModelVerify")
    @Operation(summary = "终止当前模型验证")
    public HttpResult<String> stopModelVerify(){
        asyncVerifyTask.setInterrupted();
        if(killCurVerifyProcess())
            webSocketService.sendNormalMsg("当前模型验证终止！", EventLevelEnum.WARN);
        else{
            asyncVerifyTask.cancelInterrupted();
            webSocketService.sendDialogMsg("当前无模型在验证中！", EventLevelEnum.WARN);
        }
        return HttpResult.success();
    }

    /**
     * 若有模型正在验证中，则停止验证
     * @return
     */
    private boolean killCurVerifyProcess(){
        try {
            return modelVerifyService.killCurVerifyProcess();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            webSocketService.sendDialogMsg(e.getMessage(), EventLevelEnum.ERROR);
            return false;
        }
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
