package com.nuaa.art.vrm.controller.asynctask;

import com.nuaa.art.common.EventLevelEnum;
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
        webSocketService.sendProgressMsg("解析模型文件中");
        Thread.sleep(1000);
        try{
            HVRM vrm = (HVRM) modelObjectCreate.modelFile(systemId, fileName);
            webSocketService.sendObject("vrm-model", vrm);
            webSocketService.sendProgressMsg("打开模型文件："+vrm.getSystem().getSystemName(), EventLevelEnum.SUCCESS);
        } catch (Exception e){
            webSocketService.sendProgressMsg("打开模型文件失败"+fileName, EventLevelEnum.ERROR);
        }
    }

    @Async("AsyncTask")
    public void createlocal(Integer systemId) {
        try {
            webSocketService.sendProgressMsg("开始创建模型");
            LogUtils.info("开始创建模型");
            Thread.sleep(1000);
            HVRM model = (HVRM)modelXmlCreate.createModel(systemId);
            LogUtils.info("模型创建结束");
            webSocketService.sendProgressMsg("模型%s创建结束".formatted(model.getSystem().getSystemName()), EventLevelEnum.SUCCESS);
            webSocketService.sendObject("vrm-model", model);
        } catch (InterruptedException e) {
            LogUtils.error(e.getMessage());
            webSocketService.sendProgressMsg("模型创建失败", EventLevelEnum.ERROR);
        }
    }

    @Async("AsyncTask")
    public void creatFull(Integer systemId) {
        try {
            SystemProject system = daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId);
            String fileName = PathUtils.DefaultPath() + system.getSystemName() + "ExportModel.xml";
            webSocketService.sendProgressMsg("可导出文件生成中");
            LogUtils.info("开始创建模型");
            Thread.sleep(1000);
            String fileUrl = (String) modelXmlCreate.modelFile(systemId, fileName);
            LogUtils.info("模型创建结束");
            if (fileUrl != null || fileUrl.isBlank()) {
                webSocketService.sendProgressMsg("可导出模型文件生成完毕", EventLevelEnum.SUCCESS);
                webSocketService.sendObject("file", fileUrl);
            } else {
                webSocketService.sendProgressMsg("可导出模型文件生成失败", EventLevelEnum.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            webSocketService.sendProgressMsg("可导出模型文件生成失败", EventLevelEnum.ERROR);
        }
    }

    @Resource(name = "projectV2")
    ProjectDataHandler projectDataHandler;

    @Async("AsyncTask")
    public void creatFile(int systemId){
        try {
            webSocketService.sendProgressMsg("生成可导出的工程文件中...");
            String fileUrl = projectDataHandler.exportProjectToFile(systemId);
            if(fileUrl != null){
                webSocketService.sendProgressMsg("可导出的工程文件生成完毕",EventLevelEnum.SUCCESS);
                webSocketService.sendObject("file", fileUrl); //返回对应工程文件的地址
            } else {
                webSocketService.sendProgressMsg("可导出的工程文件生成失败",EventLevelEnum.ERROR);
            }
        } catch (Exception e){
            e.printStackTrace();
            webSocketService.sendProgressMsg("可导出的工程文件生成失败",EventLevelEnum.ERROR);
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
            webSocketService.sendProgressMsg("生成可导出的工程文件中...");
            sleep(1000);
            int count = requirementDataHandler.exportToFile(systemId, fileUrl);
            if (count > 0){
                webSocketService.sendProgressMsg("可导出的工程文件生成完毕");
                webSocketService.sendObject("file", fileUrl); //返回对应工程文件的地址
            } else {
                webSocketService.sendProgressMsg("可导出的工程文件生成失败",EventLevelEnum.ERROR);
            }
        } catch (Exception e){
            e.printStackTrace();
            webSocketService.sendProgressMsg("可导出的工程文件生成失败",EventLevelEnum.ERROR);
        }
    }
}
