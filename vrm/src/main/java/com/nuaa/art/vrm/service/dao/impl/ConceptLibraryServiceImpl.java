package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.mapper.ConceptLibraryMapper;
import com.nuaa.art.vrm.service.dao.ConceptLibraryService;
import com.nuaa.art.vrm.service.dao.StandardRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_conceptlibrary】的数据库操作Service实现
* @createDate 2023-06-10 19:05:02
*/
@Service
@Transactional
public class ConceptLibraryServiceImpl extends ServiceImpl<ConceptLibraryMapper, ConceptLibrary>
    implements ConceptLibraryService {
    @Autowired
    StandardRequirementService standardRequirementService;

    @Override
    public List<ConceptLibrary> listAllConceptBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId ", systemId);
        return list(wrapper);
    }

    @Override
    public List<ConceptLibrary> listConceptByLikeNameAndSystemId(String likeName, Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.like("conceptName",likeName).eq("systemId",systemId);

        return list(wrapper);
    }

    @Override
    public ConceptLibrary getConceptById(Integer id) {
        return getById(id);
    }

    @Override
    public List<ConceptLibrary> listInputVariableBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptType","InputVariable").eq("systemId",systemId);
        return list(wrapper);
    }

    @Override
    public List<ConceptLibrary> listOutputVariableBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptType","OutputVariable").eq("systemId",systemId);
        return list(wrapper);
    }

    @Override
    public List<ConceptLibrary> listTermVariableBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptType","TermVariable").eq("systemId",systemId);
        return list(wrapper);
    }

    @Override
    public List<ConceptLibrary> listConstVariableBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptType","ConstVariable").eq("systemId",systemId);
        return list(wrapper);
    }

    @Override
    public ConceptLibrary getConceptByNameandId(String name, Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptName",name).eq("systemId",systemId);
        return getOne(wrapper, true);
    }

    @Override
    public void insertConcept(ConceptLibrary conceptLibrary) {
        save(conceptLibrary);
    }

    @Override
    public void updateConcept(ConceptLibrary conceptLibrary) {
        ConceptLibrary tempConceptLibrary = getConceptById(conceptLibrary.getConceptId());
        updateById(conceptLibrary);
        for(StandardRequirement standardRequirement : standardRequirementService
                .listStandardRequirementBySystemId(conceptLibrary.getSystemId())){
            if(standardRequirement.getStandardReqVariable() != null) {
                if(standardRequirement.getStandardReqVariable().contains(tempConceptLibrary.getConceptName())) {
                    standardRequirement.setStandardReqVariable(conceptLibrary.getConceptName());
                }
            }
            standardRequirementService.updateStandardRequirement(standardRequirement);
        }
    }

    @Override
    public void deleteConcept(Integer conceptLibraryId) {
        removeById(conceptLibraryId);
    }

    @Override
    public void deleteConceptById(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId",systemId);
        remove(wrapper);
    }

    @Override
    public void deleteConceptByProId(Integer proId) {
        removeById(proId);
    }
}




