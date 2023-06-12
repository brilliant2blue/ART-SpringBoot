package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.ConceptRelateRequirement;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.mapper.ConceptLibraryMapper;
import com.nuaa.art.vrm.service.dao.ConceptLibraryService;
import com.nuaa.art.vrm.service.dao.ConceptRelateRequirementService;
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

    @Autowired
    ConceptRelateRequirementService relateRequirement;

    @Override
    public List<ConceptLibrary> listAllConceptBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId ", systemId);
        return refreshRelation(list(wrapper));
    }

    @Override
    public List<ConceptLibrary> listConceptByLikeNameAndSystemId(String likeName, Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.like("conceptName",likeName).eq("systemId",systemId);

        return refreshRelation(list(wrapper));
    }

    @Override
    public ConceptLibrary getConceptById(Integer id) {
        return refreshRelation(getById(id));
    }

    @Override
    public List<ConceptLibrary> listInputVariableBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptType","InputVariable").eq("systemId",systemId);
        return  refreshRelation(list(wrapper));
    }

    @Override
    public List<ConceptLibrary> listOutputVariableBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptType","OutputVariable").eq("systemId",systemId);
        return  refreshRelation(list(wrapper));
    }

    @Override
    public List<ConceptLibrary> listTermVariableBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptType","TermVariable").eq("systemId",systemId);
        return refreshRelation(list(wrapper));
    }

    @Override
    public List<ConceptLibrary> listConstVariableBySystemId(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptType","ConstVariable").eq("systemId",systemId);
        return  refreshRelation(list(wrapper));
    }

    @Override
    public ConceptLibrary getConceptByNameandId(String name, Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("conceptName",name).eq("systemId",systemId);
        ConceptLibrary concept= getOne(wrapper, true);
        if(concept!=null){
            return refreshRelation(concept);
        } else {
            return null;
        }
    }

    @Override
    public boolean insertConcept(ConceptLibrary conceptLibrary) {
        boolean res = save(conceptLibrary);
        if(res){
            return relateRequirement.insertRelationOfConcept(conceptLibrary);
        } else {
            return res;
        }
    }

    @Override
    public boolean updateConcept(ConceptLibrary conceptLibrary) {
        boolean res = true;
        ConceptLibrary tempConceptLibrary = getConceptById(conceptLibrary.getConceptId());
        res=updateById(conceptLibrary);
        relateRequirement.deleteRelationOfConcept(conceptLibrary);
        res = relateRequirement.insertRelationOfConcept(conceptLibrary);

        if(tempConceptLibrary.equals(conceptLibrary)){
            return res;
        }

        for(StandardRequirement standardRequirement : standardRequirementService
                .listStandardRequirementBySystemId(conceptLibrary.getSystemId())){
            if(standardRequirement.getStandardReqVariable() != null) {
                if(standardRequirement.getStandardReqVariable().contains(tempConceptLibrary.getConceptName())) {
                    standardRequirement.setStandardReqVariable(conceptLibrary.getConceptName());
                }
            }
            res = standardRequirementService.updateStandardRequirement(standardRequirement);
        }
        return res;
    }

    @Override
    public boolean deleteConcept(Integer conceptLibraryId) {
        relateRequirement.deleteByConceptId(conceptLibraryId);
        return removeById(conceptLibraryId);
    }

    @Override
    public boolean deleteConceptById(Integer systemId) {
        QueryWrapper<ConceptLibrary> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId",systemId);
        relateRequirement.deleteBySystemId(systemId);
        return remove(wrapper);
    }

    @Override
    public boolean deleteConceptByProId(Integer proId) {
        return removeById(proId);
    }


    public List<ConceptLibrary> refreshRelation(List<ConceptLibrary> items){
        for(ConceptLibrary item : items){
            item.setSourceReqId(relateRequirement.getConceptSourceId(item.getSystemId(),item.getConceptId()));
        }
        return items;
    }

    public ConceptLibrary refreshRelation(ConceptLibrary item){
        item.setSourceReqId(relateRequirement.getConceptSourceId(item.getSystemId(),item.getConceptId()));
        return item;
    }

    public boolean removeRelation(List<ConceptLibrary> items){
        for(ConceptLibrary item : items){
            if(!relateRequirement.deleteBySystemIdAndConceptId(item.getSystemId(),item.getConceptId())){
                return false;
            };
        }
        return true;
    }

    public boolean removeRelation(ConceptLibrary item){
            if(!relateRequirement.deleteBySystemIdAndConceptId(item.getSystemId(),item.getConceptId())){
                return false;
            };
        return true;
    }

}




