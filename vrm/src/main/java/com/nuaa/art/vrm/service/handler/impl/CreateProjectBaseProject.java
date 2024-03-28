package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.NaturalLanguageRequirement;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.service.dao.ConceptLibraryService;
import com.nuaa.art.vrm.service.dao.NaturalLanguageRequirementService;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("createProjectBaseProject")
public class CreateProjectBaseProject extends CreateProjectBaseConceptLibrary {
    /**
     * 新建项目
     *
     * @param project 项目
     * @param baseId  基于项目Id
     * @return int
     */
    @Override
    @Transactional
    public int newProject(SystemProject project, Integer baseId) {
        project.setSystemId(null);
        if (daoHandler.getDaoService(SystemProjectService.class).insertSystemProject(project)) {
            try {
                copyType(baseId, project.getSystemId());
                copyProperNoun(baseId, project.getSystemId());
                copyConceptItem(baseId, project.getSystemId());
                copyModelClass(baseId, project.getSystemId());
                copyNaturalLanguageRequirement(baseId, project.getSystemId());
                return 1;
            } catch (Exception e) {
                LogUtils.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return -1;

    }

    public void copyNaturalLanguageRequirement(Integer oldsysId, Integer newsysId){
        List<NaturalLanguageRequirement> nLanguageRequirements = daoHandler
                .getDaoService(NaturalLanguageRequirementService.class).listNaturalLanguageRequirementBySystemId(oldsysId);
        if (nLanguageRequirements != null) {
            NaturalLanguageRequirement nl = new NaturalLanguageRequirement();
            for (NaturalLanguageRequirement n : nLanguageRequirements) {
                nl.setReqId(null);
                nl.setReqContent(n.getReqContent());
                nl.setSystemId(newsysId);
                nl.setReqExcelId(n.getReqExcelId());
                daoHandler.getDaoService(NaturalLanguageRequirementService.class).insertNLR(nl);
            }
        }
    }

    @Override
    public void copyConceptItem(Integer oldsysId, Integer newsysId) {
        // 领域概念元素插入
        List<ConceptLibrary> conceptItems = daoHandler.getDaoService(ConceptLibraryService.class).listConstVariableBySystemId(oldsysId);
        if (conceptItems != null) {
            ConceptLibrary variable = new ConceptLibrary();
            for (ConceptLibrary item : conceptItems) {
                variable.setConceptId(null);
                variable.setConceptName(item.getConceptName());
                variable.setConceptDatatype(item.getConceptDatatype());
                variable.setConceptRange(item.getConceptRange());
                variable.setConceptAccuracy(item.getConceptAccuracy());
                variable.setConceptValue(item.getConceptValue());
                variable.setConceptDependencyModeClass(item.getConceptDependencyModeClass());
                variable.setConceptDescription(item.getConceptDescription());
                variable.setConceptType(item.getConceptType());
                variable.setSystemId(newsysId);
                variable.setSourceReqId(item.getSourceReqId()); //复制源需求的映射
                daoHandler.getDaoService(ConceptLibraryService.class).insertConcept(variable);
            }
        }
        conceptItems = null;
    }
}
