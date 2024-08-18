package com.nuaa.art.vrmverify.service;

import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrmverify.model.ModelPartsWithProperties;
import com.nuaa.art.vrmverify.model.dto.VrmModelWithProperties;

import java.util.List;
import java.util.Map;

/**
 * 模型划分
 * @author djl
 * @date 2024-07-26
 */
public interface ModelPartitioningService {

    /**
     * 最小强独立性划分
     * @param systemId
     * @return
     */
    public Map<String, HVRM> minimumPartition(Integer systemId);

    /**
     * 属性驱动最小强独立性划分
     * @param vrmModelWithProperties
     * @return
     */
    public List<ModelPartsWithProperties> propertyBasedMinimumPartition(VrmModelWithProperties vrmModelWithProperties);

}
