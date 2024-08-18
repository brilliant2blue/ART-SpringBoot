package com.nuaa.art.vrmverify.service;

import java.util.List;

/**
 * vrm模型转为smv模型
 * @author djl
 * @date 2024-05-11
 */
public interface Vrm2SmvService {

    /**
     * 将hvrm模型转换为smv模型（不保留层次化信息）
     * @param systemId
     * @param systemName
     * @param user
     * @return
     */
    public String transformVrm2Smv(Integer systemId, String systemName, String user);

    /**
     * 将hvrm模型转换为smv模型（保留层次化信息）
     * @param systemId
     * @return
     */
    public String transformVrm2SmvWithHierarchy(Integer systemId);

    /**
     * 检查并纠正CTL公式
     * @param properties
     * @param systemId
     * @return
     */
    public List<String> checkAndRectifyCTLFormulas(List<String> properties, Integer systemId);
}
