package com.nuaa.art.main.controller;

import com.nuaa.art.vrm.service.dao.ModeService;
import com.nuaa.art.vrm.service.handler.DaoHandler;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
    @Resource
    DaoHandler daoHandler;
    @Resource(name = "modelCreateToXml")
    ModelCreateHandler modelCreateHandler;
    @GetMapping("/test")
    public String test(){
        return daoHandler.getDaoService(ModeService.class).getModeById(339).toString();
    }

    @Parameter(name = "systemId", description = "系统编号")
    @GetMapping("/testCreate")
    public void test2(Integer systemId){
        modelCreateHandler.createModel(systemId);
    }

    @Parameter(name = "systemId", description = "系统编号")
    @Parameter(name = "fileName", description = "文件名")
    @GetMapping("/testExport")
    public void test3(Integer systemId, String fileName){
        modelCreateHandler.exportModel(systemId, fileName);
    }
}
