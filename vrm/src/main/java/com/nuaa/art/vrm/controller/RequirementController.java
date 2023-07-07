package com.nuaa.art.vrm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.NaturalLanguageRequirement;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.service.dao.ConceptLibraryService;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.dao.NaturalLanguageRequirementService;
import com.nuaa.art.vrm.service.dao.StandardRequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Delete;
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

    @GetMapping("vrm/{id}/req/{reqid}/standards")
    @Operation(summary = "获取项目下某一自然需求的规范化需求")
    @Parameter(name = "reqid",description = "是自然语言需求的excelId")
    public HttpResult<List<StandardRequirement>> getStandardOfReq(Integer reqid){
        List<StandardRequirement> sReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementByReqId(reqid);
        if(sReq != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,sReq);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @PostMapping("vrm/{id}/req/{reqid}/standard")
    @Operation(summary = "新建一个规范化需求")
    public HttpResult<Integer> newStandardOfReq(@RequestBody StandardRequirement standard, Integer id, Integer reqid){
        standard.setStandardRequirementId(null);
        standard.setSystemId(id);
        standard.setNaturalLanguageReqId(reqid);

        if(daoHandler.getDaoService(StandardRequirementService.class).insertStandardRequirement(standard)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, standard.getStandardRequirementId());
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @PutMapping("vrm/{id}/req/{reqid}/standard/{standardid}")
    @Operation(summary = "更新一个规范化需求")
    public HttpResult<Integer> updateStandardOfReq(@RequestBody StandardRequirement standard, Integer standardid){
        if(daoHandler.getDaoService(StandardRequirementService.class)
                .getStandardRequirementById(standard.getStandardRequirementId()) == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }
        if(daoHandler.getDaoService(StandardRequirementService.class).updateStandardRequirement(standard)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,standard.getStandardRequirementId());
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @DeleteMapping("vrm/{id}/req/{reqid}/standard/{standardid}")
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

    @DeleteMapping("vrm/{id}/req/{reqid}/standards")
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



    @GetMapping("vrm/{id}/req/{reqid}")
    @Operation(summary = "获取一个原始需求的内容")
    public HttpResult<NaturalLanguageRequirement> getOneReq(int id, int reqid){
        NaturalLanguageRequirement requirement = daoHandler.getDaoService(NaturalLanguageRequirementService.class)
                .getNaturalLanguageRequirementByExcelId(id, reqid);
        if(requirement != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, requirement);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

//    @PostMapping("/concept/{id}/req")
//    @Operation(summary = "新建一个原始需求的内容，不应当自行创建")
//    public HttpResult<Integer> updateOneReq(@RequestBody NaturalLanguageRequirement requirement){
//        requirement.setReqId(null);
//        if(daoHandler.getDaoService(NaturalLanguageRequirementService.class).insertNLR(requirement)){
//            return new HttpResult<>(HttpCodeEnum.SUCCESS,requirement.getReqId());
//        } else {
//            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED,0);
//        }
//
//    }

    @PutMapping("vrm/{id}/req/{reqid}")
    @Operation(summary = "更新一个原始需求的内容")
    public HttpResult<Integer> updateOneReq(@RequestBody NaturalLanguageRequirement requirement, int id, int reqid){
        NaturalLanguageRequirement req =  daoHandler.getDaoService(NaturalLanguageRequirementService.class)
                .getNaturalLanguageRequirementByExcelId(id, reqid);
        if(req == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }

        requirement.setReqId(req.getReqId());

        if(daoHandler.getDaoService(NaturalLanguageRequirementService.class).updateNLR(requirement)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, reqid);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @DeleteMapping("vrm/{id}/req/{reqid}")
    @Operation(summary = "更新一个原始需求的内容")
    public HttpResult<Integer> delOneReq(int id, int reqid){
        NaturalLanguageRequirement req =  daoHandler.getDaoService(NaturalLanguageRequirementService.class)
                .getNaturalLanguageRequirementByExcelId(id, reqid);
        if(req == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("sorceId",req.getReqExcelId());
        if(daoHandler.getDaoService(ConceptLibraryService.class).list(wrapper) != null){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"存在由此需求建立的领域概念元素，无法删除！",-1);
        }

        if(daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementByReqId(req.getReqExcelId()) != null){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"存在由此需求建立的模板化需求，无法删除！",-1);
        }


        if(daoHandler.getDaoService(NaturalLanguageRequirementService.class).deleteNLR(req)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, reqid);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

}
