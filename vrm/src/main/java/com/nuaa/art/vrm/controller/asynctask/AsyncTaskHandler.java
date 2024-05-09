package com.nuaa.art.vrm.controller.asynctask;

import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrm.service.handler.ProjectDataHandler;
import com.nuaa.art.vrm.service.handler.RequirementDataHandler;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * 异步任务处理程序
 * 受springboot限制，异步函数不能和调用函数写在一个类中。
 * 故单独创建一个用于处理异步函数的类
 *
 * @author konsin
 * @date 2023/12/18
 */
@Component("vrm")
public class AsyncTaskHandler {
    @Resource(name = "vrm-xml")
    ModelCreateHandler modelXmlCreate;

    @Resource(name = "vrm-object")
    ModelCreateHandler modelObjectCreate;


    @Resource
    WebSocketService webSocketService;

    @Resource
    DaoHandler daoHandler;

    @Async("AsyncTask")
    public void readLocal(Integer systemId, String fileName) throws IOException, InterruptedException {
        webSocketService.sendMsg(SocketMessage.asText("vrm-model","解析模型文件中"));
        Thread.sleep(1000);
        HVRM vrm = (HVRM) modelObjectCreate.modelFile(systemId, fileName);
        webSocketService.sendMsg(SocketMessage.asObject("vrm-model", vrm));
        webSocketService.sendMsg(SocketMessage.asText("打开模型文件："+vrm.getSystem().getSystemName()));
    }

    @Async("AsyncTask")
    public void createlocal(Integer systemId) throws IOException {
        try {
            webSocketService.sendMsg(SocketMessage.asText("vrm-model","开始创建模型"));
            LogUtils.info("开始创建模型");
            Thread.sleep(1000);
            HVRM model = (HVRM)modelXmlCreate.createModel(systemId);
            LogUtils.info("模型创建结束");
            webSocketService.sendMsg(SocketMessage.asText("vrm-model","模型创建结束"));
            webSocketService.sendMsg(SocketMessage.asText(model.getDate()));
            webSocketService.sendMsg(SocketMessage.asObject("vrm-model", model));
        } catch (InterruptedException e) {
            LogUtils.error(e.getMessage());
        }
    }

    @Async("AsyncTask")
    public void creatFull(Integer systemId) {
        try {
            SystemProject system = daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId);
            String fileName = PathUtils.DefaultPath() + system.getSystemName() + "ExportModel.xml";
            webSocketService.sendMsg(SocketMessage.asText("model-file", "可导出文件生成中"));
            LogUtils.info("开始创建模型");
            Thread.sleep(1000);
            String fileUrl = (String) modelXmlCreate.modelFile(systemId, fileName);
            LogUtils.info("模型创建结束");
            if (fileUrl != null || fileUrl.isBlank()) {
                webSocketService.sendMsg(SocketMessage.asText("model-file", "可导出文件生成完毕"));
                webSocketService.sendMsg(SocketMessage.asText("model-file", ""));
                webSocketService.sendMsg(SocketMessage.asObject("file", fileUrl));
            } else {
                webSocketService.sendMsg(SocketMessage.asText("model-file", "可导出文件生成失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            webSocketService.sendMsg(SocketMessage.asText("model-file", "可导出文件生成失败"));
        }
    }

    @Resource(name = "projectV2")
    ProjectDataHandler projectDataHandler;

    @Async("AsyncTask")
    public void creatFile(int systemId){
        try {
            webSocketService.sendMsg(SocketMessage.asText("project", "生成可导出的工程文件中..."));
            String fileUrl = projectDataHandler.exportProjectToFile(systemId);
            if(fileUrl != null){
                webSocketService.sendMsg(SocketMessage.asText("project", "可导出的工程文件生成完毕"));
                webSocketService.sendMsg(SocketMessage.asText("project", "")); //发送空消息，关闭前端进度条
                webSocketService.sendMsg(SocketMessage.asObject("file", fileUrl)); //返回对应工程文件的地址
            } else {
                webSocketService.sendMsg(SocketMessage.asText("project", "可导出的工程文件生成失败"));
            }
        } catch (Exception e){
            e.printStackTrace();
            webSocketService.sendMsg(SocketMessage.asText("project", "可导出的工程文件生成失败"));
        }
    }

    @Async("AsyncTask")
    public void importProject(String systemName, String fileUrl){
        projectDataHandler.importProjectFromFile(systemName,fileUrl);
    }

    @Resource
    RequirementDataHandler requirementDataHandler;

    @Async("AsyncTask")
    public void creatExcelFile(int systemId){
        try {
            String system = daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId).getSystemName();
            String fileUrl = PathUtils.DefaultPath()+system+"Requirement.xlsx";
            webSocketService.sendMsg(SocketMessage.asText("requirement", "生成可导出的工程文件中..."));
            sleep(1000);
            int count = requirementDataHandler.exportToFile(systemId, fileUrl);
            if (count > 0){
                webSocketService.sendMsg(SocketMessage.asText("requirement", "可导出的工程文件生成完毕"));
                webSocketService.sendMsg(SocketMessage.asText("requirement", "")); //发送空消息，关闭前端进度条
                webSocketService.sendMsg(SocketMessage.asObject("file", fileUrl)); //返回对应工程文件的地址
            } else {
                webSocketService.sendMsg(SocketMessage.asText("requirement", "可导出的工程文件生成失败"));
            }
        } catch (Exception e){
            e.printStackTrace();
            webSocketService.sendMsg(SocketMessage.asText("requirement", "可导出的工程文件生成失败"));
        }
    }
}
