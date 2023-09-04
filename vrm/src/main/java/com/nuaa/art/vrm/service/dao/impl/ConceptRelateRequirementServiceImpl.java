package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.ConceptRelateRequirement;
import com.nuaa.art.vrm.service.dao.ConceptRelateRequirementService;
import com.nuaa.art.vrm.mapper.ConceptRelateRequirementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author konsin
* @description 针对表【vrm_conceptrelaterequirement】的数据库操作Service实现
* @createDate 2023-07-10 14:34:19
*/
@Service
public class ConceptRelateRequirementServiceImpl extends ServiceImpl<ConceptRelateRequirementMapper, ConceptRelateRequirement>
    implements ConceptRelateRequirementService{
    @Override
    public List<Integer> getConceptSourceId(Integer systemId, Integer conceptId) {
        QueryWrapper<ConceptRelateRequirement> wrapper = new QueryWrapper<>();
        wrapper.select("sourceReqId").eq("systemId",systemId).eq("conceptId", conceptId);
        List<ConceptRelateRequirement> ids = list(wrapper);
        return ids.stream().map(ConceptRelateRequirement::getSourceReqId).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getReqRelateConceptId(Integer systemId, Integer sourceReqId) {
        QueryWrapper<ConceptRelateRequirement> wrapper = new QueryWrapper<>();
        wrapper.select("conceptId").eq("systemId",systemId).eq("sourceReqId", sourceReqId);
        List<ConceptRelateRequirement> ids = list(wrapper);
        return ids.stream().map(ConceptRelateRequirement::getConceptId).collect(Collectors.toList());
    }

    @Override
    public boolean deleteBySystemId(Integer systemId) {
        QueryWrapper<ConceptRelateRequirement> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId", systemId);
        return remove(wrapper);
    }

    @Override
    public boolean deleteByConceptId(Integer conceptId) {
        QueryWrapper<ConceptRelateRequirement> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptId", conceptId);
        return remove(wrapper);
    }

    @Override
    public boolean deleteBySystemIdAndConceptId(Integer systemId, Integer conceptId) {
        QueryWrapper<ConceptRelateRequirement> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId", systemId).eq("conceptId",conceptId);
        return remove(wrapper);
    }

    @Override
    public boolean deleteBySystemIdAndReqId(Integer systemId, Integer sourceReqId) {
        QueryWrapper<ConceptRelateRequirement> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId", systemId).eq("sourceReqId",sourceReqId);
        return remove(wrapper);
    }

    @Override
    public int insertRelation(ConceptRelateRequirement item) {
        item.setId(null);
        if(save(item)){
            return item.getId();
        } else {
            return -1;
        }
    }

    @Override
    public boolean insertRelationOfConcept(ConceptLibrary conceptLibrary) {
        if(conceptLibrary.getSourceReqId() == null) {
            return true;
        }
        ConceptRelateRequirement item = new ConceptRelateRequirement();
        for(Integer reqId: conceptLibrary.getSourceReqId()){
            item.setId(null);
            item.setSystemId(conceptLibrary.getSystemId());
            item.setConceptId(conceptLibrary.getConceptId());
            item.setSourceReqId(reqId);
            if(insertRelation(item) < 0){
                deleteBySystemIdAndConceptId(conceptLibrary.getSystemId(),conceptLibrary.getConceptId());
                return false;
            };
        }
        return true;
    }

    @Override
    public boolean deleteRelationOfConcept(ConceptLibrary conceptLibrary) {
        return deleteBySystemIdAndConceptId(conceptLibrary.getSystemId(),conceptLibrary.getConceptId());
    }

    @Override
    public boolean updateRelation(ConceptRelateRequirement item) {
        return updateById(item);
    }
}




