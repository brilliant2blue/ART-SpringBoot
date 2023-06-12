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
    public List<ConceptLibrary> listAllConceptBySystemId(Integer systemId);
    public List<ConceptLibrary> listConceptByLikeNameAndSystemId(@Param("likeName")String likeName, @Param("systemId")Integer systemId);
    public ConceptLibrary getConceptById(Integer id);
    public List<ConceptLibrary> listInputVariableBySystemId(Integer systemId);
    public List<ConceptLibrary> listOutputVariableBySystemId(Integer systemId);
    public List<ConceptLibrary> listTermVariableBySystemId(Integer systemId);
    public List<ConceptLibrary> listConstVariableBySystemId(Integer systemId);
    public ConceptLibrary getConceptByNameandId(@Param("conceptName")String name,@Param("systemId")Integer systemId);
    public void insertConcept(ConceptLibrary conceptLibrary);
    public void updateConcept(ConceptLibrary conceptLibrary);
    public void deleteConcept(Integer conceptLibraryId);
    public void deleteConceptById(Integer systemId);
    public void deleteConceptByProId(Integer proId);

}
