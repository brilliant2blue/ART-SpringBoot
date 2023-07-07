package com.nuaa.art.vrm.controller;


import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "模型生成")
public class ModelCreateController {
    @Resource(name = "modelCreateToXml")
    ModelCreateHandler modelCreateForXml;

    @PostMapping("vrm/{id}/model")
    @Operation(summary = "生成模型的xml文件")
    public HttpResult<String> create(@PathVariable(value = "id") Integer systemId){
        try {
            modelCreateForXml.createModel(systemId);
            return new HttpResult<>(HttpCodeEnum.SUCCESS,"模型创建成功");
        } catch (Exception e){
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,"模型创建失败");
        }
    }


    @GetMapping("vrm/{id}/model")
    @Operation(summary = "导出模型的xml文件")
    public HttpResult<String> export(@RequestParam(value = "id") Integer systemId, @RequestParam(value = "filename")String fileName){
        try {
            modelCreateForXml.exportModel(systemId, fileName);
            return new HttpResult<>(HttpCodeEnum.SUCCESS,"模型导出："+fileName);
        } catch (Exception e){
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,"模型创建失败");
        }
    }
}
