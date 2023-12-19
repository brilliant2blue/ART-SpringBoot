package com.nuaa.art.vrmcheck.controller;

import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrmcheck.controller.asynctask.AsyncTaskHandler;
import com.nuaa.art.vrmcheck.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.io.File;

import static java.lang.Thread.sleep;

@RestController
public class ModelCheck {
    @Resource
    WebSocketService webSocket;

    @Resource
    ReportHandler reportHandler;

    @Resource
    DaoHandler daoHandler;

    @Resource(name = "vrm-object")
    ModelCreateHandler modelObjectCreate;

    @Resource(name = "vrmCheck")
    AsyncTaskHandler asyncTaskHandler;



    @PostMapping("/vrm/{id}/check")
    @Operation(summary = "模型分析", description = "花费时间很长，应为异步响应操作")
    @Parameter(name = "id", description = "系统工程编号")
    public HttpResult<Boolean> checkmodel(@PathVariable(value = "id") Integer systemId) {

        SystemProject systemProject = daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId);
        String fileName = PathUtils.DefaultPath() + systemProject.getSystemName() + "model.xml";
        HVRM vrm = null;
        try {
            webSocket.sendMsg(SocketMessage.asText("vrm-check","开始解析模型文件"));
            sleep(500);
            vrm = (HVRM) modelObjectCreate.modelFile(systemId, fileName);
            webSocket.sendMsg(SocketMessage.asText("vrm-check","模型文件解析成功, 开始进行模型分析。"));
//            webSocket.sendMsg(SocketMessage.asObject("test", vrm));
        } catch (Exception e){
            e.printStackTrace();
            return HttpResult.fail("模型文件解析失败。");
        }


        if (vrm != null) {
            try {
                asyncTaskHandler.checkProcess(vrm);
            } catch (Exception e) {
                e.printStackTrace();
                webSocket.sendMsg(SocketMessage.asText("vrm-model","模型分析失败"));
            }

        }
        return HttpResult.success();
    }



    @GetMapping("/vrm/{id}/check")
    @Operation(summary = "获取分析报告")
    public HttpResult getCheckReport(@PathVariable("id") Integer systemId) throws InterruptedException {
            asyncTaskHandler.read(systemId);
            //sleep(1000);
            return HttpResult.success();
    }



    @PostMapping("/vrm/{id}/check/file")
    @Operation(summary = "导出分析报告到指定文件")
    public HttpResult<String> exportCheckReport(@PathVariable("id") Integer systemId, @RequestParam("url")String fileUrl){
        if(reportHandler.exportCheckReport(systemId,fileUrl)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, fileUrl);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, "");
        }
    }

    @GetMapping("vrm/{id}/check/report")
    @Operation(summary = "检查本地报告是否存在")
    public HttpResult<String> exist(@PathVariable(value = "id") Integer systemId){
        try {
            SystemProject system = daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId);
            String fileName = PathUtils.DefaultPath() + system.getSystemName() + "CheckReport.xml";
            File file = new File(fileName);
            if(file.exists())
                return HttpResult.success(fileName);
            else return HttpResult.success("");
        } catch (Exception e){
            e.printStackTrace();
            return HttpResult.fail();
        }
    }

}
