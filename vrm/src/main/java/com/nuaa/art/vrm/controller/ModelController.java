package com.nuaa.art.vrm.controller;


import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.common.utils.ServletUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.common.websocket.WebSocketSessionManager;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.model.VariableRealationModel;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.websocket.server.PathParam;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;

@RestController
@Tag(name = "模型生成")
public class ModelController {
    @Resource(name = "vrm-xml")
    ModelCreateHandler modelXmlCreate;

    @Resource(name = "vrm-object")
    ModelCreateHandler modelObjectCreate;

    @Resource
    SystemProjectService systemProject;

    @GetMapping("vrm/{id}/model/object")
    @Operation(summary = "读取本地文件生成模型")
    public HttpResult<VariableRealationModel> read(@PathVariable(value = "id") Integer systemId, @RequestParam(value = "filename", required = false)String fileName){
        try {
            SystemProject system = systemProject.getSystemProjectById(systemId);
            if(fileName == null || fileName.isBlank()){
                fileName = PathUtils.DefaultPath() + system.getSystemName() + "model.xml";
            }
            VariableRealationModel vrm = (VariableRealationModel) modelObjectCreate.modelFile(systemId, fileName);
            return HttpResult.success(vrm);
        } catch (Exception e){
            e.printStackTrace();
            return HttpResult.fail("模型生成失败");
        }
    }
    @GetMapping("vrm/{id}/model/file")
    @Operation(summary = "检查本地模型是否存在")
    public HttpResult<String> exist(@PathVariable(value = "id") Integer systemId){
        try {
            SystemProject system = systemProject.getSystemProjectById(systemId);
            String fileName = PathUtils.DefaultPath() + system.getSystemName() + "model.xml";
            File file = new File(fileName);
            if(file.exists())
                return HttpResult.success(fileName);
            else return HttpResult.success("");
        } catch (Exception e){
            e.printStackTrace();
            return HttpResult.fail();
        }
    }
    @Resource
    WebSocketService webSocketService;

    @Async("AsyncTask")
    public void creatlocal(Integer systemId) throws IOException {
        try {
        webSocketService.sendMsg(SocketMessage.asText("开始创建模型"));
        LogUtils.info("开始创建模型");
        Thread.sleep(1000);
        VariableRealationModel model = (VariableRealationModel)modelXmlCreate.createModel(systemId);
        LogUtils.info("模型创建结束");
        webSocketService.sendMsg(SocketMessage.asText("模型创建结束"));
        webSocketService.sendMsg(SocketMessage.asText(model.getDate()));
        webSocketService.sendMsg(SocketMessage.asObject("vrm-model", model));
//        ObjectMapper mapper = new ObjectMapper();
//        webSocketService.sendMsg(session, mapper.writeValueAsString(model));
        } catch (InterruptedException e) {
            LogUtils.error(e.getMessage());
        }
    }
    @PostMapping("vrm/{id}/model/xml")
    @Operation(summary = "生成模型的xml文件")
    public HttpResult<String> create(@PathVariable(value = "id") Integer systemId, @PathParam("user")String user){
        try {
            creatlocal(systemId);
            return new HttpResult<>(HttpCodeEnum.SUCCESS,"模型创建成功");
        } catch (Exception e){
            LogUtils.error(e.getMessage());
            e.printStackTrace();
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,"模型创建失败");
        }
    }


    @GetMapping("vrm/{id}/model/xml")
    @Operation(summary = "导出模型的完整xml文件")
    public HttpResult<String> export(@PathVariable(value = "id") Integer systemId, @RequestParam(value = "filename")String fileName){
        try {
            modelXmlCreate.modelFile(systemId, fileName);
            return new HttpResult<>(HttpCodeEnum.SUCCESS,"模型导出："+fileName);
        } catch (Exception e){
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,"模型创建失败");
        }
    }
}
