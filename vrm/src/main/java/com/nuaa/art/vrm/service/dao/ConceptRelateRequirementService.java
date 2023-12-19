package com.nuaa.art.vrm.service.dao;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.ConceptRelateRequirement;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.sql.Wrapper;
import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_conceptrelaterequirement】的数据库操作Service
* @createDate 2023-07-10 14:34:19
*/
public interface ConceptRelateRequirementService extends IService<ConceptRelateRequirement> {
    String getConceptSourceId(@Param("systemId") Integer systemId, @Param("conceptId") Integer conceptId);

    String getReqRelateConceptId(@Param("systemId") Integer systemId, @Param("sourceReqId") Integer sourceReqId);

    boolean deleteBySystemId(Integer systemId);
    boolean deleteByConceptId(Integer conceptId);

    boolean deleteRelationOfConcept(ConceptLibrary item);

    boolean deleteBySystemIdAndConceptId(@Param("systemId") Integer systemId, @Param("conceptId") Integer conceptId);

    boolean deleteBySystemIdAndReqId(@Param("systemId") Integer systemId, @Param("sourceReqId") Integer sourceReqId);

    int insertRelation(ConceptRelateRequirement item);

    boolean insertRelationOfConcept(ConceptLibrary item);

    boolean updateRelation(ConceptRelateRequirement item);

    String updateRelation(@Param("systemId") Integer systemId, @Param("conceptId") Integer conceptId, @Param("sourceReqId") String sourceReqId);

    boolean includesItem(@Param("systemId") Integer systemId, @Param("conceptId") Integer conceptId);
}
