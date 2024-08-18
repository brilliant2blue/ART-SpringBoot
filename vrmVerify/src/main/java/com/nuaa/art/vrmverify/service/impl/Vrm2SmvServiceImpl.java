package com.nuaa.art.vrmverify.service.impl;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrmverify.common.exception.UnknownVariableException;
import com.nuaa.art.vrmverify.common.utils.CTLParseUtils;
import com.nuaa.art.vrmverify.common.utils.StringUtils;
import com.nuaa.art.vrmverify.handler.Vrm2SmvHandler;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.service.Vrm2SmvService;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * vrm模型转为smv模型
 *
 * @author djl
 * @date 2024-05-11
 */
@Service
public class Vrm2SmvServiceImpl implements Vrm2SmvService {

    @Resource(name = "vrm-object")
    private ModelCreateHandler modelCreateHandler;

    /**
     * 将hvrm模型转换为smv模型（不保留层次化信息）
     *
     * @param systemId
     * @param systemName
     * @param user
     * @return
     */
    @Override
    public String transformVrm2Smv(Integer systemId, String systemName, String user) {
        return new Vrm2SmvHandler((HVRM) modelCreateHandler.createModel(systemId), systemName, user).transferHvrm2Smv();
    }

    /**
     * 将hvrm模型转换为smv模型（保留层次化信息）
     *
     * @param systemId
     * @return
     */
    @Override
    public String transformVrm2SmvWithHierarchy(Integer systemId) {
        return null;
    }

    /**
     * 检查并纠正 CTL公式
     * @param properties
     * @param systemId
     * @return
     */
    @Override
    public List<String> checkAndRectifyCTLFormulas(List<String> properties, Integer systemId) {
        if(properties == null)
            return null;
        HVRM model = (HVRM) modelCreateHandler.createModel(systemId);
        Set<String> varsSet = new HashSet<>();
        for (VariableWithPort term : model.terms)
            varsSet.add(term.getConceptName());
        for (VariableWithPort output : model.outputs)
            varsSet.add(output.getConceptName());
        for (ModeClassOfVRM modeClass : model.modeClass)
            varsSet.add(modeClass.getModeClass().getModeClassName());
        List<String> rectifiedProperties = new ArrayList<>();
        for (String property : properties) {
            CTLFormula ctlFormula = CTLParseUtils.parseCTLStr(property);
            StringBuilder property1 = new StringBuilder(property);
            for (String var : ctlFormula.variableSet()) {
                if(varsSet.contains(var)){
                    for (Integer i : StringUtils.KMP(property1.toString(), var))
                        property1.insert(i, ".result");
                }
//                else
//                    throw new UnknownVariableException(property, var);
            }
            rectifiedProperties.add(property1.toString());
        }
        return rectifiedProperties;
    }

}
