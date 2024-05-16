package com.nuaa.art.vrmverify.service.impl;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrmverify.handler.Vrm2SmvHandler;
import com.nuaa.art.vrmverify.service.Vrm2SmvService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * vrm模型转为smv模型
 * @author djl
 * @date 2024-05-11
 */
@Service
public class Vrm2SmvServiceImpl implements Vrm2SmvService {

    @Resource(name = "vrm-object")
    private ModelCreateHandler modelCreateHandler;

    /**
     * 将hvrm模型转换为smv模型（不保留层次化信息）
     * @param systemId
     * @param systemName
     * @param user
     * @return
     */
    @Override
    public String transformVrm2Smv(Integer systemId, String systemName, String user) {
        HVRM model = (HVRM) modelCreateHandler.createModel(systemId);
        Vrm2SmvHandler vrm2SmvHandler = new Vrm2SmvHandler(model, systemName, user);
        return vrm2SmvHandler.transferHvrm2Smv();
    }

    /**
     * 将hvrm模型转换为smv模型（保留层次化信息）
     * @param systemId
     * @return
     */
    @Override
    public String transformVrm2SmvWithHierarchy(Integer systemId) {
        return null;
    }
}
