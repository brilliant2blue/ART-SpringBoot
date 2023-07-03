package com.nuaa.art.vrm.controller;

import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.EnumUtils;
import com.nuaa.art.vrm.common.BasicDataType;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.service.dao.ConceptLibraryService;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.dao.TypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 领域概念库 数据类型控制器
 *
 * @author konsin
 * @date 2023/06/29
 */
@RestController
@Tag(name = "领域概念库 数据类型控制器")
public class DataTypeController {
    @Resource
    DaoHandler daoHandler;

    @GetMapping("concept/basetypes")
    @Operation(summary = "获取基本类型列表")
    public HttpResult<List> getBaseTypeList(){
        return new HttpResult<>(HttpCodeEnum.SUCCESS, EnumUtils.enumToListMap(BasicDataType.class));
    }

    @GetMapping("concept/basetypes/{name}")
    @Operation(summary = "获取指定基本类型")
    @Parameter(name = "name", description = "基本类型的名称")
    public HttpResult<BasicDataType> getBaseTypeInfo(String name){
        return new HttpResult<>(HttpCodeEnum.SUCCESS, BasicDataType.findTypeByName(name));
    }

    @GetMapping("/concpet/{id}/types")
    @Operation(summary = "获取项目的所有基本类型")
    @Parameter(name = "id", description = "项目号")
    public HttpResult<List<Type>> getAllType(@PathVariable("id") int systemId){
        return new HttpResult<>(HttpCodeEnum.SUCCESS,
                daoHandler.getDaoService(TypeService.class).listTypeBySystemId(systemId));
    }

    @PostMapping("concept/{id}/types/{name}")
    @Operation(summary = "新建一个类型",description = "新建一个类型，最好前端判断是否已经存在")
    public HttpResult<Integer> newType(@RequestBody Type type,  @PathVariable("id") int systemId, @PathVariable("name") String name){
//        if(daoHandler.getDaoService(TypeService.class).getTypeByNameandId(type.getTypeName(), type.getSystemId())!= null ){
//            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "同名变量已存在，请重新填写！",false);
//        }
        Type t = daoHandler.getDaoService(TypeService.class).getTypeByNameandId(name,systemId);
        if(t == null) {
            type.setTypeId(null);
            if (daoHandler.getDaoService(TypeService.class).insertType(type)) {
                return new HttpResult(HttpCodeEnum.SUCCESS, type.getTypeId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "同名资源已存在，请重新填写！",t.getTypeId());
        }
    }

    @PutMapping("concept/{id}/types/{name}")
    @Operation(summary = "更新类型信息")
    @Parameter(name = "id", description = "系统工程号")
    @Parameter(name = "name", description = "类型名")
    public HttpResult<Integer> updateType(@RequestBody Type type, @PathVariable("id") int systemId, @PathVariable("name") String name){
        Type t = daoHandler.getDaoService(TypeService.class).getTypeByNameandId(name,systemId);
        if(t != null) {
            if(!t.getTypeId().equals(type.getTypeId())){
                return new HttpResult(HttpCodeEnum.BAD_REQUEST, "同名资源已存在，请重新填写！",t.getTypeId());
            }
            if (daoHandler.getDaoService(TypeService.class).updateType(type)) {
                return new HttpResult(HttpCodeEnum.SUCCESS, type.getTypeId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, -1);
        }
    }

    @DeleteMapping("concept/{id}/types/{name}")
    @Operation(summary = "删除指定类型")
    @Parameter(name = "id", description = "系统工程号")
    @Parameter(name = "name", description = "类型名")
    public HttpResult<Integer> deleteType(@PathVariable("id") int systemId, @PathVariable("name") String name){
        Type type = daoHandler.getDaoService(TypeService.class).getTypeByNameandId(name,systemId);
        if(type != null) {
            if (daoHandler.getDaoService(TypeService.class).deleteType(type)) {
                return new HttpResult<>(HttpCodeEnum.SUCCESS, type.getTypeId());
            } else {
                return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
            }
        } else {
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST, "领域概念元素不存在",-1);
        }
    }

    //todo 删除项目下所有类型

}
