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
    public List<NaturalLanguageRequirement> listNaturalLanguageRequirementBySystemId(Integer systemId);
    public NaturalLanguageRequirement getNaturalLanguageRequirementById(Integer id);
    public List<NaturalLanguageRequirement> listNaturalLanguageRequirement();
    public NaturalLanguageRequirement getNaturalLanguageRequirementByExcelId(Integer systemId,Integer excelId);
    public boolean insertNLR(NaturalLanguageRequirement naturalLanguageRequirement);
    public boolean updateNLR(NaturalLanguageRequirement naturalLanguageRequirement);
    public boolean deleteNLR(NaturalLanguageRequirement naturalLanguageRequirement);
    public boolean deleteNLRById(Integer systemId);
}
