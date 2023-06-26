package com.nuaa.art.vrm.controller;


import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelCreate {
    @Resource(name = "modelCreateToXml")
    ModelCreateHandler modelCreateForXml;

    @GetMapping("/vrm/create")
    public boolean create(@RequestParam(value = "id") Integer systemId){
        try {
            modelCreateForXml.createModel(systemId);
            return true;
        } catch (Exception e){
            return false;
        }
    }


    @GetMapping("/vrm/export")
    public boolean export(@RequestParam(value = "id") Integer systemId, @RequestParam(value = "filename")String fileName){
        try {
            modelCreateForXml.exportModel(systemId, fileName);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
