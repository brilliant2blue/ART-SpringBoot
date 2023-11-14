package com.nuaa.art.vrm.controller;

import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.vrm.entity.Module;
import com.nuaa.art.vrm.entity.NaturalLanguageRequirement;
import com.nuaa.art.vrm.model.ModuleTree;
import com.nuaa.art.vrm.service.dao.ModuleService;
import com.nuaa.art.vrm.service.dao.NaturalLanguageRequirementService;
import com.nuaa.art.vrm.service.handler.ModuleHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Tag(name="模块管理")
public class ModuleController {

    @Resource
    ModuleHandler moduleHandler;
    @Resource
    ModuleService moduleService;

    @GetMapping("/vrm/{systemid}/modules")
    @Operation(summary = "获取模块树列表")
    public HttpResult<List<ModuleTree>> getModuleTree(@PathVariable("systemid") Integer id){
        List<Module> modules = moduleService.listModulesBySystemId(id);
        System.out.println(modules.toString());
        return HttpResult.success(moduleHandler.ModulesToModuleTree(modules));
    }

    @PostMapping("/vrm/{systemid}/modules")
    @Operation(summary = "新建模块")
    public HttpResult<Integer> newModule(@RequestBody Module module , @PathVariable("systemid") Integer systemId){
        if(moduleService.insertModule(module))
            return HttpResult.success(module.getId());
        else
            return HttpResult.fail();
    }

    @Resource
    NaturalLanguageRequirementService requirementService;

    @PostMapping("/vrm/{systemid}/modules/{moduleid}/nreq")
    @Operation(summary = "分配原始需求给模块")
    public HttpResult assignNReqToModule(@RequestBody List<Integer> reqIds , @PathVariable("systemid") Integer systemId, @PathVariable("moduleid") Integer moduleId){
        if(requirementService.bindModuleById(reqIds, moduleId))
            return HttpResult.success();
        else return  HttpResult.fail();
    }

    @DeleteMapping("/vrm/{systemid}/modules/{moduleid}/nreq")
    @Operation(summary = "释放模块下原始需求")
    public HttpResult releaseNReqOfModule(@RequestBody List<Integer> reqIds , @PathVariable("systemid") Integer systemId, @PathVariable("moduleid") Integer moduleId){
        if(requirementService.releaseModuleByReqIdsAndModuleId(reqIds, moduleId))
            return HttpResult.success();
        else return  HttpResult.fail();
    }

    @PutMapping("/vrm/{systemid}/modules/{moduleid}")
    @Operation(summary = "更新模块")
    public HttpResult updateModuleTree(@RequestBody Module module , @PathVariable("systemid") Integer systemId, @PathVariable("moduleid") Integer moduleId){
        if(moduleService.updateModule(module))
            return HttpResult.success();
        else return HttpResult.fail();
    }

    @DeleteMapping("/vrm/{systemid}/modules/{moduleid}")
    @Operation(summary = "删除模块")
    public HttpResult deleteModuleTree(@PathVariable("systemid") Integer systemId, @PathVariable("moduleid") Integer moduleId){
        if (moduleService.deleteChildModulesBySystemIdAndId(systemId,moduleId) && moduleService.deleteModuleById(moduleId))
            return HttpResult.success();
        else
            return HttpResult.fail();
    }
}
