package com.nuaa.art.vrm.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_conceptlibrary】的数据库操作Service
* @createDate 2023-06-10 19:05:02
*/
public interface ConceptLibraryService extends IService<ConceptLibrary> {
    List<ConceptLibrary> listAllConceptBySystemId(Integer systemId);
    List<ConceptLibrary> listConceptByLikeNameAndSystemId(@Param("likeName")String likeName, @Param("systemId")Integer systemId);
    ConceptLibrary getConceptById(Integer id);
    List<ConceptLibrary> listInputVariableBySystemId(Integer systemId);
    List<ConceptLibrary> listOutputVariableBySystemId(Integer systemId);
    List<ConceptLibrary> listTermVariableBySystemId(Integer systemId);
    List<ConceptLibrary> listConstVariableBySystemId(Integer systemId);
    ConceptLibrary getConceptByNameandId(@Param("conceptName")String name,@Param("systemId")Integer systemId);
    boolean insertConcept(ConceptLibrary conceptLibrary);
    boolean updateConcept(ConceptLibrary conceptLibrary);
    boolean deleteConcept(Integer conceptLibraryId);
    boolean deleteConceptById(Integer systemId);
    boolean deleteConceptByProId(Integer proId);

    boolean switchValidStatus(Integer id);

}
