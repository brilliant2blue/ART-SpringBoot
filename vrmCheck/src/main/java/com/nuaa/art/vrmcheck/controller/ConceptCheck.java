package com.nuaa.art.vrmcheck.controller;

import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrmcheck.controller.asynctask.AsyncTaskHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConceptCheck {

    @Resource
    WebSocketService webSocket;

    @Resource
    SystemProjectService systemProjectService;

    @Resource(name = "vrmCheck")
    AsyncTaskHandler asyncTaskHandler;

    @PostMapping("/vrm/{id}/concept/check")
    @Operation(summary = "模型的领域概念库分析")
    @Parameter(name = "id", description = "系统工程编号")
    public HttpResult checkConcepts(@PathVariable(value = "id") Integer systemId){
        SystemProject sys = systemProjectService.getSystemProjectById(systemId);
        webSocket.sendMsg(SocketMessage.asText("concept-check", "检查领域概念库："+sys.getSystemName()+"的语法错误中..."));
        try{
            asyncTaskHandler.asyncConceptCheck(systemId);
        } catch (Exception e){
            webSocket.sendMsg(SocketMessage.asText("concept-check", "发生异常..."));
            webSocket.sendMsg(SocketMessage.asText("concept-check", ""));
        }

        return HttpResult.success();
    }


}
