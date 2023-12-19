package com.nuaa.art.vrm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrm.controller.asynctask.AsyncTaskHandler;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.NaturalLanguageRequirement;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.handler.RequirementDataHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.Thread.sleep;

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

    @GetMapping("vrm/{systemid}/req/{reqid}/standards")
    @Operation(summary = "获取项目下某一自然需求的规范化需求")
    @Parameter(name = "reqid",description = "是自然语言需求的excelId")
    public HttpResult<List<StandardRequirement>> getStandardOfReq(@PathVariable Integer systemid, @PathVariable Integer reqid){
        List<StandardRequirement> sReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementByReqIdAndSystemId(systemid, reqid);
        if(sReq != null){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,sReq);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND, null);
        }
    }

    @PostMapping("vrm/{id}/req/{reqid}/standard")
    @Operation(summary = "新建一个规范化需求")
    @Parameter(name = "reqid",description = "是自然语言需求的excelId")
    public HttpResult<Integer> newStandardOfReq(@RequestBody StandardRequirement standard, @PathVariable("id") Integer id, @PathVariable("reqid") Integer reqid){
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
    public HttpResult<Integer> updateStandardOfReq(@RequestBody StandardRequirement standard, @PathVariable("standardid")Integer standardid){
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
    public HttpResult<Integer> delStandard(@PathVariable("reqid")Integer reqid, @PathVariable("standardid")Integer standardid){
        if(daoHandler.getDaoService(StandardRequirementService.class)
                .getStandardRequirementById(standardid) == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }

        if(daoHandler.getDaoService(StandardRequirementService.class).deleteOneStandardRequirement(standardid)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,standardid);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @DeleteMapping("vrm/{systemid}/req/{reqid}/standards")
    @Operation(summary = "删除自然语言需求下的所有规范化需求")
    public HttpResult<Integer> delStandardOfReq(@PathVariable Integer systemid,@PathVariable("reqid")Integer reqid){
        if(daoHandler.getDaoService(StandardRequirementService.class)
                .listStandardRequirementByReqIdAndSystemId(systemid, reqid).size() == 0){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, 0);
        }

        if(daoHandler.getDaoService(StandardRequirementService.class).deleteStandardRequirementByReqIdAndSystemId(systemid, reqid)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS,reqid);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }



    @GetMapping("vrm/{id}/req/{reqid}")
    @Operation(summary = "获取一个原始需求的内容")
    public HttpResult<NaturalLanguageRequirement> getOneReq(@PathVariable("id") int id, @PathVariable("reqid") int excelid){
        NaturalLanguageRequirement requirement = daoHandler.getDaoService(NaturalLanguageRequirementService.class)
                .getNaturalLanguageRequirementByExcelId(id, excelid);
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
    public HttpResult<Integer> updateOneReq(@RequestBody NaturalLanguageRequirement requirement, @PathVariable("id")int id, @PathVariable("reqid")int reqid){
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
    @Operation(summary = "删除一个原始需求的内容")
    public HttpResult<Integer> delOneReq(@PathVariable("id")int id, @PathVariable("reqid")int reqid){
        NaturalLanguageRequirement req =  daoHandler.getDaoService(NaturalLanguageRequirementService.class)
                .getNaturalLanguageRequirementByExcelId(id, reqid);
        if(req == null){
            return new HttpResult<>(HttpCodeEnum.NOT_FOUND,-1);
        }
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("sorceId",req.getReqId());
        if(daoHandler.getDaoService(ConceptLibraryService.class).list(wrapper) != null){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"存在由此需求建立的领域概念元素，无法删除！",-1);
        }

        if(daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementByReqIdAndSystemId(id, req.getReqExcelId()) != null){
            return new HttpResult<>(HttpCodeEnum.BAD_REQUEST,"存在由此需求建立的模板化需求，无法删除！",-1);
        }


        if(daoHandler.getDaoService(NaturalLanguageRequirementService.class).deleteNLR(req)){
            return new HttpResult<>(HttpCodeEnum.SUCCESS, reqid);
        } else {
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
        }
    }

    @Resource
    RequirementDataHandler requirementDataHandler;

    @PostMapping("vrm/{id}/req")
    @Operation(summary = "从文件导入原始需求")
    public HttpResult<Integer> importReqs(@PathVariable("id")int id, @RequestBody String fileUrl){
        int count = requirementDataHandler.importFromFile(id, fileUrl);
        if (count > 0)
            return new HttpResult<>(HttpCodeEnum.SUCCESS, count);
        else
            return new HttpResult<>(HttpCodeEnum.NOT_MODIFIED, 0);
    }


    @Resource(name = "vrm")
    AsyncTaskHandler asyncTaskHandler;

    @GetMapping("vrm/{id}/sreq")
    @Operation(summary = "导出规范化需求为Excel")
    public HttpResult<String> exportReqs(@PathVariable("id")int id){
        asyncTaskHandler.creatExcelFile(id);
        return HttpResult.success();
    }







}
