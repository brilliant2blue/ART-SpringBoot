package com.nuaa.art.vrm.controller;

import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.service.dao.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "领域概念库 各元素集合的控制")
public class ConceptLibraryController {
    @Resource
    DaoHandler daoHandler;

    @GetMapping("vrm/{id}/inputs")
    @Operation(summary = "获取项目的所有输入变量", description = "根据系统编号获取系统所有输入变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<List<ConceptLibrary>> getAllInputVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listInputVariableBySystemId(systemId);
        if(data != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, data);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @GetMapping("vrm/{id}/outputs")
    @Operation(summary = "获取项目的所有输出变量",description = "根据系统编号获取系统所有输出变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<List<ConceptLibrary>> getAllOutputVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listOutputVariableBySystemId(systemId);
        if(data != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, data);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @GetMapping("vrm/{id}/terms")
    @Operation(summary = "获取项目的所有中间变量",description = "根据系统编号获取系统所有中间变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<List<ConceptLibrary>> getAllTermVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listTermVariableBySystemId(systemId);
        if(data != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, data);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @GetMapping("vrm/{id}/consts")
    @Operation(summary = "获取项目的所有常量",description = "根据系统编号获取系统所有中间变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<List<ConceptLibrary>> getAllConstVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listConstVariableBySystemId(systemId);
        if(data != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, data);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @GetMapping("vrm/{id}/vars")
    @Operation(summary = "获取项目所有变量",description = "根据系统编号获取所有变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<List<ConceptLibrary>> getAllVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listAllConceptBySystemId(systemId);
        if(data != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, data);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @GetMapping("vrm/{id}/propernouns")
    @Operation(summary = "获取项目下专有名词信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<List<ProperNoun>> getProperNoun(@PathVariable("id")int systemId){
        List<ProperNoun> data = daoHandler.getDaoService(ProperNounService.class).listProperNounBySystemId(systemId);
        if(data != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, data);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @GetMapping("vrm/{id}/modeclasses")
    @Operation(summary = "获取项目下所有模式集")
    @Parameter(name = "id", description = "项目号")
    public HttpResult<List<ModeClass>> getAll(int id) {
        List<ModeClass> mc = daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(id);
        if (mc != null) {
            return new HttpResult<>(HttpCodeEnum.SUCCESS, mc);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @GetMapping("vrm/{id}/types")
    @Operation(summary = "获取项目的所有基本类型")
    @Parameter(name = "id", description = "项目号")
    public HttpResult<List<Type>> getAllType(@PathVariable("id") int systemId){
        List<Type> types = daoHandler.getDaoService(TypeService.class).listTypeBySystemId(systemId);
        if(types != null) {
            return new HttpResult<>(HttpCodeEnum.SUCCESS, types);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }


    @DeleteMapping("vrm/{id}/inputs")
    @Operation(summary = "删除项目的所有输入变量", description = "根据系统编号删除输入变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<Integer> delAllInputVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listInputVariableBySystemId(systemId);
        if(data != null){
            if(daoHandler.getDaoService(ConceptLibraryService.class).removeBatchByIds(data)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, data.size());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        }
        return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
    }

    @DeleteMapping("vrm/{id}/outputs")
    @Operation(summary = "删除项目的所有输出变量",description = "根据系统编号删除输出变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<Integer> delAllOutputVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listOutputVariableBySystemId(systemId);
        if(data != null){
            if(daoHandler.getDaoService(ConceptLibraryService.class).removeBatchByIds(data)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, data.size());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        }
        return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
    }

    @DeleteMapping("vrm/{id}/terms")
    @Operation(summary = "删除项目的所有中间变量",description = "根据系统编号删除中间变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<Integer> delAllTermVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listTermVariableBySystemId(systemId);
        if(data != null){
            if(daoHandler.getDaoService(ConceptLibraryService.class).removeBatchByIds(data)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, data.size());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        }
        return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
    }

    @DeleteMapping("vrm/{id}/consts")
    @Operation(summary = "删除项目的所有常量",description = "根据系统编号删除中间变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<Integer> delAllConstVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listConstVariableBySystemId(systemId);
        if(data != null){
            if(daoHandler.getDaoService(ConceptLibraryService.class).removeBatchByIds(data)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, data.size());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        }
        return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
    }

    @DeleteMapping("vrm/{id}/vars")
    @Operation(summary = "删除项目所有变量",description = "根据系统编号删除所有变量信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<Integer> delAllVariable(@PathVariable("id")int systemId){
        List<ConceptLibrary> data = daoHandler.getDaoService(ConceptLibraryService.class).listAllConceptBySystemId(systemId);
        if(data != null){
            if(daoHandler.getDaoService(ConceptLibraryService.class).removeBatchByIds(data)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, data.size());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        }
        return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
    }

    @DeleteMapping("vrm/{id}/propernouns")
    @Operation(summary = "删除项目下专有名词信息")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<Integer> delAllProperNoun(@PathVariable("id")int systemId){
        List<ProperNoun> data = daoHandler.getDaoService(ProperNounService.class).listProperNounBySystemId(systemId);
        if(data != null){
            if(daoHandler.getDaoService(ProperNounService.class).deleteProperNounById(systemId)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, data.size());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        }
        return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
    }

    @DeleteMapping("vrm/{id}/modeclasses")
    @Operation(summary = "删除项目下所有模式集")
    @Parameter(name = "id", description = "项目号")
    public HttpResult<Integer> delAll(int id) {
        List<ModeClass> data = daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(id);
        if(data != null){
            if(daoHandler.getDaoService(ModeClassService.class).deleteModeClassById(id)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, data.size());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        }
        return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
    }

    @DeleteMapping("vrm/{id}/types")
    @Operation(summary = "删除项目的所有基本类型")
    @Parameter(name = "id", description = "项目号")
    public HttpResult<Integer> delAllType(@PathVariable("id") int systemId){

        List<Type> types = daoHandler.getDaoService(TypeService.class).listTypeBySystemId(systemId);

        if(types != null){
            if(daoHandler.getDaoService(ModeClassService.class).deleteModeClassById(systemId)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, types.size());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        }
        return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
    }

    @GetMapping("vrm/{id}/req/standards")
    @Operation(summary = "获取项目下所有规范化需求")
    public HttpResult<List<StandardRequirement>> getAllStandard(Integer id){
        List<StandardRequirement> sReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(id);
        if(sReq != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,sReq);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @DeleteMapping("vrm/{id}/req/standards")
    @Operation(summary = "删除项目下所有规范化需求")
    public HttpResult<Integer> delStandards(Integer id){
        if(daoHandler.getDaoService(StandardRequirementService.class)
                .listStandardRequirementBySystemId(id) == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }

        if(daoHandler.getDaoService(StandardRequirementService.class).deleteStandardRequirementBySystemId(id)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,id);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @GetMapping("vrm/{id}/req")
    @Operation(summary = "获取项目下所有原始需求")
    public HttpResult<List<NaturalLanguageRequirement>> getAllNatural(Integer id){
        List<NaturalLanguageRequirement> sReq = daoHandler.getDaoService(NaturalLanguageRequirementService.class).listNaturalLanguageRequirementBySystemId(id);
        if(sReq != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,sReq);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

//    @DeleteMapping("/concept/{id}/req")
//    @Operation(summary = "删除项目下所有原始需求")
//    public HttpResult<Integer> delNatural(Integer id){
//        List<NaturalLanguageRequirement> sReq = daoHandler.getDaoService(NaturalLanguageRequirementService.class).listNaturalLanguageRequirementBySystemId(id);
//        if(sReq == null){
//            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
//        }
//
//        if(daoHandler.getDaoService(NaturalLanguageRequirementService.class).deleteNLRById(id)){
//            return new HttpResult<>(HttpCodeEnum.SUCCESS, sReq.size());
//        } else {
//            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
//        }
//    }


}
