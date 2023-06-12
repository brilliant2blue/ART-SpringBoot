package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.NaturalLanguageRequirement;
import com.nuaa.art.vrm.mapper.NaturalLanguageRequirementMapper;
import com.nuaa.art.vrm.service.dao.NaturalLanguageRequirementService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_naturallanguagerequirement】的数据库操作Service实现
* @createDate 2023-06-10 19:04:37
*/
@Service
public class NaturalLanguageRequirementServiceImpl extends ServiceImpl<NaturalLanguageRequirementMapper, NaturalLanguageRequirement>
    implements NaturalLanguageRequirementService {
    @Override
    public List<NaturalLanguageRequirement> listNaturalLanguageRequirementBySystemId(Integer systemId) {
        return list(new QueryWrapper<NaturalLanguageRequirement>().eq("systemId",systemId));
    }

    @Override
    public NaturalLanguageRequirement getNaturalLanguageRequirementById(Integer id) {
        return getById(id);
    }

    @Override
    public List<NaturalLanguageRequirement> listNaturalLanguageRequirement() {
        return list();
    }

    @Override
    public NaturalLanguageRequirement getNaturalLanguageRequirementByExcelId(Integer systemId, Integer excelId) {
        return getOne(new QueryWrapper<NaturalLanguageRequirement>()
                .eq("systemId",systemId)
                .eq("reqExcelId",excelId));
    }

    @Override
    public void insertNLR(NaturalLanguageRequirement naturalLanguageRequirement) {
        save(naturalLanguageRequirement);
    }

    @Override
    public void updateNLR(NaturalLanguageRequirement naturalLanguageRequirement) {
        update(new QueryWrapper<NaturalLanguageRequirement>().eq("systemId",naturalLanguageRequirement.getSystemId()));
    }

    @Override
    public void deleteNLR(NaturalLanguageRequirement naturalLanguageRequirement) {
        remove(new QueryWrapper<NaturalLanguageRequirement>()
                .eq("systemId",naturalLanguageRequirement.getSystemId())
                .eq("reqExcelId",naturalLanguageRequirement.getReqExcelId()));
    }

    @Override
    public void deleteNLRById(Integer systemId) {
        remove(new QueryWrapper<NaturalLanguageRequirement>().eq("systemId",systemId));
    }
}




