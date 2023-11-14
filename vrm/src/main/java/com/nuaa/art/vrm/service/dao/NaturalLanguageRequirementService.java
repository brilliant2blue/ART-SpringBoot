package com.nuaa.art.vrm.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.vrm.entity.NaturalLanguageRequirement;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_naturallanguagerequirement】的数据库操作Service
* @createDate 2023-06-10 19:04:37
*/
public interface NaturalLanguageRequirementService extends IService<NaturalLanguageRequirement> {
    List<NaturalLanguageRequirement> listNaturalLanguageRequirementBySystemId(Integer systemId);
    NaturalLanguageRequirement getNaturalLanguageRequirementById(Integer id);
    List<NaturalLanguageRequirement> listNaturalLanguageRequirement();
    NaturalLanguageRequirement getNaturalLanguageRequirementByExcelId(Integer systemId,Integer excelId);
    List<NaturalLanguageRequirement> listNaturalLanguageRequirementBySystemIdAndModuleId(Integer systemId, Integer moduleId);
    boolean insertNLR(NaturalLanguageRequirement naturalLanguageRequirement);
    boolean updateNLR(NaturalLanguageRequirement naturalLanguageRequirement);
    boolean deleteNLR(NaturalLanguageRequirement naturalLanguageRequirement);
    boolean deleteNLRById(Integer systemId);
    boolean deleteNLRBySystemIdAndModuleId(Integer systemId, Integer moduleId);
    boolean bindModuleById(Integer id, Integer moduleId);
    boolean bindModuleById(List<Integer> ids, Integer moduleId);
    boolean releaseModuleByReqIdsAndModuleId(List<Integer> ids, Integer moduleId);
    boolean releaseModuleByModuleId(Integer systemId, Integer moduleId);
}
