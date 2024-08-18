package com.nuaa.art.vrmverify.controller;

import com.nuaa.art.common.EventLevelEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.exception.InvalidCTLException;
import com.nuaa.art.vrmverify.common.utils.RecordsUtils;
import com.nuaa.art.vrmverify.model.ModelPartsWithProperties;
import com.nuaa.art.vrmverify.model.dto.SmvFileWIthProperties;
import com.nuaa.art.vrmverify.model.dto.VrmModelWithProperties;
import com.nuaa.art.vrmverify.service.ModelPartitioningService;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.service.async.AsyncVerifyTask;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;



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
    private ModelPartitioningService modelPartitioningService;
    @Resource
    private WebSocketService webSocketService;

    /**
     * 检查CTL公式是否合法
     *
     * @param ctlStr
     * @return
     */
    @PostMapping("/checkCTLFormula")
    @Operation(summary = "检查CTL公式是否合法")
    public HttpResult<Boolean> checkCTLFormula(@RequestParam("formula") String ctlStr) {
        try {
            modelVerifyService.checkCTLFormula(ctlStr);
        } catch (InvalidCTLException ice) {
            LogUtils.warn(ice.getMessage());
            return HttpResult.fail(ice.getMessage());
        }
        return HttpResult.success();
    }

    /**
     * 通过文件批量导入CTL公式
     *
     * @param file
     * @return
     */
    @PostMapping("/importCTLFormulas")
    @Operation(summary = "通过文件批量导入CTL公式")
    public HttpResult<List<String>> importCTLFormulas(MultipartFile file) {
        try {
            List<String> ctlFormulaList = modelVerifyService.readCTLFormulasFromFile(file.getOriginalFilename(), file.getInputStream());
            return HttpResult.success(ctlFormulaList);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            return HttpResult.fail();
        } catch (InvalidCTLException ice) {
            LogUtils.warn(ice.getMessage());
            return HttpResult.fail(ice.getMessage());
        }
    }

    /**
     * smv文件模型检查
     *
     * @return
     */
    @PostMapping("/smvFileVerify")
    @Operation(summary = "smv模型验证")
    public HttpResult<String> smvFileVerify(@RequestBody SmvFileWIthProperties smvFileWIthProperties) {
        asyncVerifyTask.setInterrupted();
        if (killCurVerifyProcess())
            webSocketService.sendNormalMsg("当前模型验证终止！", EventLevelEnum.WARN);
        else
            asyncVerifyTask.cancelInterrupted();
        asyncVerifyTask.asyncSmvFileVerify(smvFileWIthProperties);
        return HttpResult.success();
    }

    /**
     * vrm模型检查
     *
     * @param vrmModelWithProperties
     * @param user
     * @return
     */
    @PostMapping("/vrmModelVerify")
    @Operation(summary = "vrm模型验证")
    public HttpResult<String> vrmModelVerify(@RequestBody VrmModelWithProperties vrmModelWithProperties, String user) {
        asyncVerifyTask.setInterrupted();
        if (killCurVerifyProcess())
            webSocketService.sendNormalMsg("当前模型验证终止！", EventLevelEnum.WARN);
        else
            asyncVerifyTask.cancelInterrupted();
        asyncVerifyTask.asyncSmvStrVerify(vrmModelWithProperties, user);
        return HttpResult.success();
    }

    /**
     * 终止当前模型验证
     *
     * @return
     */
    @PostMapping("/stopModelVerify")
    @Operation(summary = "终止当前模型验证")
    public HttpResult<String> stopModelVerify() {
        asyncVerifyTask.setInterrupted();
        if (killCurVerifyProcess())
            webSocketService.sendNormalMsg("当前模型验证终止！", EventLevelEnum.WARN);
        else {
            asyncVerifyTask.cancelInterrupted();
            webSocketService.sendDialogMsg("当前无模型在验证中！", EventLevelEnum.WARN);
        }
        return HttpResult.success();
    }

    /**
     * 若有模型正在验证中，则停止验证
     *
     * @return
     */
    private boolean killCurVerifyProcess() {
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
     *
     * @param verifyResult
     * @return
     */
    @PostMapping("/handleCx")
    @Operation(summary = "反例处理")
    public HttpResult<String> handleCx(@RequestBody VerifyResult verifyResult, Integer type, String name) {
        asyncVerifyTask.asyncHandleCx(verifyResult, type, name);
        return HttpResult.success();
    }

    /**
     * 获取指定smv文件内容
     *
     * @param smvFilePath
     * @return
     */
    @PostMapping("/getSmvFile")
    @Operation(summary = "获取指定smv文件内容")
    public HttpResult<String> getSmvFile(String smvFilePath) {
        try {
            String data = FileUtils.readFileToString(new File(smvFilePath), "UTF-8");
            return HttpResult.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            return HttpResult.fail(Msg.FILE_NOT_FOUND);
        }
    }

    /**
     * 最小强独立性划分
     *
     * @param systemId
     * @return
     */
    @PostMapping("/minimumPartition")
    @Operation(summary = "最小强独立性划分")
    public HttpResult<Map<String, HVRM>> minimumPartitionModel(Integer systemId) {
        try {
            Map<String, HVRM> map = modelPartitioningService.minimumPartition(systemId);
            RecordsUtils.writeRecords2File(systemId + ".txt", RecordsUtils.HVRMs2Str(map));
            return HttpResult.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            return HttpResult.fail();
        }
    }

    /**
     * 属性驱动最小强独立性划分
     * @param vrmModelWithProperties
     * @return
     */
    @PostMapping("/propertyBasedMinimumPartition")
    @Operation(summary = "属性驱动最小强独立性划分")
    public HttpResult<List<ModelPartsWithProperties>> propertyBasedMinimumPartition(@RequestBody VrmModelWithProperties vrmModelWithProperties) {
        try {
            List<ModelPartsWithProperties> modelParts = modelPartitioningService.propertyBasedMinimumPartition(vrmModelWithProperties);
            RecordsUtils.writeRecords2File(vrmModelWithProperties.getSystemId() + ".txt", RecordsUtils.HVRMs2Str(modelParts));
            return HttpResult.success(modelParts);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage());
            return HttpResult.fail(e.getMessage());
        }
    }

}
