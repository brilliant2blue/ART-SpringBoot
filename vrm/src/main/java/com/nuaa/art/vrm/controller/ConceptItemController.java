package com.nuaa.art.vrm.controller;

import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.ProperNoun;
import com.nuaa.art.vrm.service.dao.ConceptLibraryService;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.dao.ProperNounService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 领域概念库 各类变量控制类
 * 各类变量
 *
 * @author konsin
 * {@code @date} 2023/06/28
 */
@RestController
@Tag(name = "领域概念库 各类变量控制类", description = "包括了 输入变量， 输出变量， 中间变量， 常量， 专有名词")
public class ConceptItemController {
    @Resource
    DaoHandler daoHandler;
    @GetMapping("vrm/{id}/vars/{name}")
    @Operation(summary = "获取一个变量",description = "根据变量名和系统编号获取一个变量信息(输入、输出、中间变量以及常量通用)")
    @Parameter(name = "name",description = "变量名")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<ConceptLibrary> getVariable(@PathVariable("name") String variablName, @PathVariable("id")int systemId){
        ConceptLibrary data = daoHandler.getDaoService(ConceptLibraryService.class).getConceptByNameandId(variablName, systemId);
        if(data != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, data);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }


    @PutMapping("vrm/{id}/vars/{varid}")
    @Operation(summary = "更新一个变量信息",description = "更新一个变量信息(输入、输出、中间变量以及常量通用)")
    @Parameter(name = "id", description = "项目号")
    @Parameter(name = "varid",description = "领域元素id")
    public HttpResult<Integer> updateVariable(@RequestBody ConceptLibrary variable, @PathVariable("id") Integer systemId, @PathVariable("varid")Integer varId){
//        if(variable.getConceptName() == null || variable.getConceptName() == "") {
//            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"属性不完整",false);
//        }
        ConceptLibrary var = daoHandler.getDaoService(ConceptLibraryService.class).getById(varId);
        if(var != null) {
            ConceptLibrary _var = daoHandler.getDaoService(ConceptLibraryService.class).getConceptByNameandId(var.getConceptName(), systemId);
            if(_var!=null && !_var.getConceptId().equals(varId)){
                return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"同名资源已存在，请重新填写！",-1);
            }
            if( daoHandler.getDaoService(ConceptLibraryService.class).updateConcept(variable)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS,variable.getConceptId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }
    }

    @PostMapping("vrm/{id}/vars/{name}")
    @Operation(summary = "插入一个变量信息",description = "插入一个新的变量，最好前端判断是否已经存在以及所需属性是否完整(输入、输出、中间变量以及常量通用)")
    public HttpResult<Integer> newVariable(@RequestBody ConceptLibrary variable, @PathVariable("id") int systemId, @PathVariable("name") String name){
//        if(variable.getConceptName() == null || variable.getConceptName() == "") {
//            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"属性不完整",false);
//        }
        if(daoHandler.getDaoService(ConceptLibraryService.class).getConceptByNameandId(name, systemId)!= null ){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "同名资源已存在，请重新填写！",-1);
        }
        variable.setConceptId(null);
        if( daoHandler.getDaoService(ConceptLibraryService.class).insertConcept(variable)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, variable.getConceptId());
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,0);
        }
    }

    @DeleteMapping("vrm/{id}/vars/{name}")
    @Operation(summary = "删除一个变量")
    @Parameter(name = "id", description = "系统id")
    @Parameter(name = "name", description = "变量名")
    public HttpResult<Integer> deleteVariable(@PathVariable("id") int systemId, @PathVariable("name") String name){
        ConceptLibrary var = daoHandler.getDaoService(ConceptLibraryService.class).getConceptByNameandId(name,systemId);
        if(var != null) {
            if (daoHandler.getDaoService(ConceptLibraryService.class).deleteConcept(var.getConceptId())) {
                return new HttpResult<>(HttpCodeEnum.SUCCESS, var.getConceptId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "领域概念元素不存在",-1);
        }
    }


    @GetMapping("vrm/{id}/propernoun/{name}")
    @Operation(summary = "获取指定专有名词信息")
    @Parameter(name = "name",description = "变量名")
    @Parameter(name = "id",description = "系统编号")
    public HttpResult<ProperNoun> getProperNoun(@PathVariable("name") String name, @PathVariable("id")int systemId){
        ProperNoun data = daoHandler.getDaoService(ProperNounService.class).getProperNounByNameandId(name,systemId);
        if(data != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, data);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }
    @PostMapping("vrm/{id}/propernoun/{name}")
    @Operation(summary = "新建专有名词")
    public HttpResult<Integer> newProperNoun(@RequestBody ProperNoun properNoun,@PathVariable("id")Integer systemId, @PathVariable("name")String name){
//        if(properNoun.getProperNounName() == null || properNoun.getProperNounName() =="") {
//            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"属性不完整",false);
//        }
        if(daoHandler.getDaoService(ProperNounService.class).getProperNounByNameandId(name, systemId)!= null ){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "同名变量已存在，请重新填写！",-1);
        }
        properNoun.setProperNounId(null);   //防止主键冲突
        if( daoHandler.getDaoService(ProperNounService.class).insertProperNoun(properNoun)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, properNoun.getProperNounId());
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,0);
        }
    }
    @PutMapping("vrm/{id}/propernoun/{itemid}")
    @Operation(summary = "更新专有名词")
    @Parameter(name = "id", description = "系统id")
    @Parameter(name = "itemid", description = "专有名词id")
    public HttpResult<Integer> updateProperNoun(@RequestBody ProperNoun properNoun, @PathVariable("id") int systemId, @PathVariable("itemid") Integer id){
//        if(properNoun.getProperNounName() == null || properNoun.getProperNounName() == "") {
//            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"属性不完整",false);
//        }
        ProperNoun noun = daoHandler.getDaoService(ProperNounService.class).getById(id);

        if(noun!= null){
            ProperNoun _noun = daoHandler.getDaoService(ProperNounService.class).getProperNounByNameandId(properNoun.getProperNounName(),systemId);
            if(_noun != null && !_noun.getProperNounId().equals(id)){
                return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"同名资源已存在，请重新填写！",_noun.getProperNounId());
            }
            if( daoHandler.getDaoService(ProperNounService.class).updateProperNoun(properNoun)){
                return new HttpResult<>(HttpCodeEnum.SUCCESS, properNoun.getProperNounId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,0);
            }
        } else {
            return  new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
        }
    }
    @DeleteMapping("vrm/{id}/propernoun/{name}")
    @Operation(summary = "删除一个专有名词")
    @Parameter(name = "id", description = "系统id")
    @Parameter(name = "name", description = "变量名")
    public HttpResult<Integer> deleteProperNoun(@PathVariable("id") int systemId, @PathVariable("name") String name){
        ProperNoun var = daoHandler.getDaoService(ProperNounService.class).getProperNounByNameandId(name,systemId);
        if(var != null) {
            if (daoHandler.getDaoService(ProperNounService.class).deleteProperNoun(var)) {
                return new HttpResult<>(HttpCodeEnum.SUCCESS, var.getProperNounId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "领域概念元素不存在",-1);
        }
    }

}
