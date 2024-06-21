package com.nuaa.art.vrmverify.service.impl;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrmverify.common.utils.CTLParseUtils;
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
        HVRM model = (HVRM) modelCreateHandler.createModel(systemId);
        Vrm2SmvHandler vrm2SmvHandler = new Vrm2SmvHandler(model, systemName, user);
        return vrm2SmvHandler.transferHvrm2Smv();
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
     * 纠正CTL公式
     * @param properties
     * @param systemId
     * @return
     */
    @Override
    public List<String> rectifyCTLFormulas(List<String> properties, Integer systemId) {
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
                    for (Integer i : KMP(property1.toString(), var))
                        property1.insert(i, ".result");
                }
            }
            rectifiedProperties.add(property1.toString());
        }
        return rectifiedProperties;
    }

    /**
     * KMP模式匹配算法
     *
     * @param str
     * @param target
     * @return
     */
    private List<Integer> KMP(String str, String target) {
        char[] t = str.toCharArray(), p = target.toCharArray();
        int i = 0, j = 0;
        int[] next = getNext(target);
        List<Integer> res = new ArrayList<>();
        int n = 0;
        while(i < t.length){
            while (i < t.length && j < p.length) {
                if (j == -1 || t[i] == p[j]) {
                    i++;
                    j++;
                } else
                    j = next[j];
            }
            if(j == p.length){
                res.add(i - j + p.length + n * 7);
                j = 0;
                n++;
            }
        }
        return res;
    }

    private int[] getNext(String target) {
        char[] p = target.toCharArray();
        int[] next = new int[p.length];
        next[0] = -1;
        int j = 0, k = -1;
        while (j < p.length - 1) {
            if (k == -1 || p[j] == p[k]) {
                if (p[++j] == p[++k])
                    next[j] = next[k];
                else
                    next[j] = k;

            } else
                k = next[k];
        }
        return next;
    }

}
