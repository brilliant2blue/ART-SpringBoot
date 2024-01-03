package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.ConceptRelateRequirement;
import com.nuaa.art.vrm.service.dao.ConceptRelateRequirementService;
import com.nuaa.art.vrm.mapper.ConceptRelateRequirementMapper;
import org.springframework.stereotype.Service;

/**
* @author konsin
* @description 针对表【vrm_conceptrelaterequirement】的数据库操作Service实现
* @createDate 2023-07-10 14:34:19
*/
@Service
public class ConceptRelateRequirementServiceImpl extends ServiceImpl<ConceptRelateRequirementMapper, ConceptRelateRequirement>
    implements ConceptRelateRequirementService{

    @Override
    public String getConceptSourceId(Integer systemId, Integer conceptId) {
        QueryWrapper<ConceptRelateRequirement> wrapper = new QueryWrapper<>();
        wrapper.select("sourceReqId").eq("systemId",systemId).eq("conceptId", conceptId);
        ConceptRelateRequirement ids = getOne(wrapper);
        if(ids != null)
            return ids.getSourceReqId().equals("null")? "": ids.getSourceReqId();
        else return "";
    }

    @Override
    public String getReqRelateConceptId(Integer systemId, Integer sourceReqId) {
        QueryWrapper<ConceptRelateRequirement> wrapper = new QueryWrapper<>();
        wrapper.select("conceptId").eq("systemId",systemId).eq("sourceReqId", sourceReqId);
        ConceptRelateRequirement ids = getOne(wrapper);
        if(ids != null)
            return ids.getSourceReqId().equals("null")? "": ids.getSourceReqId();
        else return "";
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
        String ids = conceptLibrary.getSourceReqId();
        if(ids.equals("null")) {
            ids = "";
        }
        ConceptRelateRequirement item = new ConceptRelateRequirement();

        item.setId(null);
        item.setSystemId(conceptLibrary.getSystemId());
        item.setConceptId(conceptLibrary.getConceptId());
        item.setSourceReqId(ids);
        if(insertRelation(item) < 0){
            deleteBySystemIdAndConceptId(conceptLibrary.getSystemId(),conceptLibrary.getConceptId());
            return false;
        };
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

    @Override
    public String updateRelation(Integer systemId, Integer conceptId, String sourceReqId) {
        LambdaUpdateWrapper<ConceptRelateRequirement> wrapper = new LambdaUpdateWrapper<ConceptRelateRequirement>()
                .set(ConceptRelateRequirement::getSourceReqId, sourceReqId)
                .eq(ConceptRelateRequirement::getSystemId,systemId).eq(ConceptRelateRequirement::getConceptId, conceptId);
        if(update(wrapper)){
            return sourceReqId;
        };
        return "";
    }

    @Override
    public boolean includesItem(Integer systemId, Integer conceptId) {
        QueryWrapper<ConceptRelateRequirement> wrapper = new QueryWrapper<>();
        wrapper.select("sourceReqId").eq("systemId",systemId).eq("conceptId", conceptId);
        return count(wrapper) > 0;
    }
}




