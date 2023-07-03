package com.nuaa.art.vrm.controller;

import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.dao.StandardRequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 需求管理，原始需求和规范化需求
 *
 * @author konsin
 * @date 2023/07/03
 */
@RestController
@Tag(name = "领域概念库 需求条目管理")
public class RequirementController {
    @Resource
    DaoHandler daoHandler;

    @GetMapping("/concept/{id}/req/standards")
    @Operation(summary = "获取项目下所有规范化需求")
    public HttpResult<List<StandardRequirement>> getAllStandard(Integer id){
        List<StandardRequirement> sReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(id);
        if(sReq != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,sReq);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @GetMapping("/concept/{id}/req/{reqid}/standards")
    @Operation(summary = "获取项目下某一自然需求的规范化需求")
    public HttpResult<List<StandardRequirement>> getStandardOfReq(Integer reqid){
        List<StandardRequirement> sReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementByReqId(reqid);
        if(sReq != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,sReq);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @PostMapping("/concept/{id}/req/{reqid}/standard")
    @Operation(summary = "新建一个规范化需求")
    public HttpResult<Integer> newStandardOfReq(@RequestBody StandardRequirement standard, Integer id, Integer reqid){
        standard.setStandardRequirementId(null);
        standard.setSystemId(id);
        standard.setNaturalLanguageReqId(reqid);

        daoHandler.getDaoService(StandardRequirementService.class).insertStandardRequirement(standard);
        Integer sReqID = standard.getStandardRequirementId();
        if(sReqID!= null && sReqID > 0){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,sReqID);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @PutMapping("/concept/{id}/req/{reqid}/standard")
    @Operation(summary = "更新一个规范化需求")
    public HttpResult<Integer> updateStandardOfReq(@RequestBody StandardRequirement standard, Integer id, Integer reqid){
        if(daoHandler.getDaoService(StandardRequirementService.class)
                .getStandardRequirementById(standard.getStandardRequirementId()) == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }

        standard.setSystemId(id);
        standard.setNaturalLanguageReqId(reqid);

        if(daoHandler.getDaoService(StandardRequirementService.class).updateStandardRequirement(standard)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,standard.getStandardRequirementId());
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @DeleteMapping("/concept/{id}/req/{reqid}/standard/{standardid}")
    @Operation(summary = "删除一个规范化需求")
    public HttpResult<Integer> delStandard(Integer reqid,Integer standardid){
        if(daoHandler.getDaoService(StandardRequirementService.class)
                .getStandardRequirementById(standardid) == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }

        if(daoHandler.getDaoService(StandardRequirementService.class).deleteOneStandardRequirement(reqid,standardid)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,standardid);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @DeleteMapping("/concept/{id}/req/{reqid}/standards")
    @Operation(summary = "删除自然语言需求下的所有规范化需求")
    public HttpResult<Integer> delStandardOfReq(Integer reqid){
        if(daoHandler.getDaoService(StandardRequirementService.class)
                .listStandardRequirementByReqId(reqid) == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }

        if(daoHandler.getDaoService(StandardRequirementService.class).deleteStandardRequirement(reqid)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,reqid);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @DeleteMapping("/concept/{id}/req//standards")
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









}
