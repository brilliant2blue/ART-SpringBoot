package com.nuaa.art.vrm.controller;


import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.model.VariableRealationModel;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("vrm/{id}/model/xml")
    @Operation(summary = "生成模型的xml文件")
    public HttpResult<String> create(@PathVariable(value = "id") Integer systemId){
        try {
            modelXmlCreate.createModel(systemId);
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
