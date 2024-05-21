package com.nuaa.art.vrm.controller;


import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.vrm.controller.asynctask.AsyncTaskHandler;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

import java.io.File;

import static java.lang.Thread.sleep;

@RestController
@Tag(name = "模型生成")
public class ModelController {
    @Resource
    SystemProjectService systemProject;

    @Resource(name = "vrm")
    AsyncTaskHandler asyncTaskHandler;

    @GetMapping("vrm/{id}/model/object")
    @Operation(summary = "读取本地文件生成模型")
    public HttpResult read(@PathVariable(value = "id") Integer systemId, @RequestParam(value = "filename", required = false)String fileName){
        try {
            SystemProject system = systemProject.getSystemProjectById(systemId);
            if(fileName == null || fileName.isBlank()){
                fileName = PathUtils.DefaultPath() + system.getSystemName() + "model.xml";
            }
            asyncTaskHandler.readLocal(systemId, fileName);
            return HttpResult.success();
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


    @PostMapping("vrm/{id}/model/xml")
    @Operation(summary = "生成模型的xml文件")
    public HttpResult<String> create(@PathVariable(value = "id") Integer systemId, @PathParam("user")String user){

            asyncTaskHandler.createlocal(systemId);
            return new HttpResult<>(HttpCodeEnum.SUCCESS);

    }


    @GetMapping("vrm/{id}/model/xml")
    @Operation(summary = "导出模型的完整xml文件")
    public HttpResult<String> export(@PathVariable(value = "id") Integer systemId){
        asyncTaskHandler.creatFull(systemId);
        return HttpResult.success();

    }
}
