package com.nuaa.art.vrmverify.service.impl;

import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrmverify.handler.ModelPartitioningHandler;
import com.nuaa.art.vrmverify.model.ModelPartsWithProperties;
import com.nuaa.art.vrmverify.model.dto.VrmModelWithProperties;
import com.nuaa.art.vrmverify.service.ModelPartitioningService;
import com.nuaa.art.vrmverify.service.Vrm2SmvService;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author djl
 * @date 2024-07-26
 */
@Service
public class ModelPartitioningServiceImpl implements ModelPartitioningService {

    @Resource(name = "vrm-object")
    private ModelCreateHandler modelCreateHandler;

    /**
     * 最小强独立性划分
     * @param systemId
     * @return
     */
    @Override
    public Map<String, HVRM> minimumPartition(Integer systemId) {
        return new ModelPartitioningHandler((HVRM) modelCreateHandler.createModel(systemId)).partitionModel();
    }

    /**
     * 属性驱动最小强独立性划分
     * @param vrmModelWithProperties
     * @return
     */
    @Override
    public List<ModelPartsWithProperties> propertyBasedMinimumPartition(VrmModelWithProperties vrmModelWithProperties) {
        return new ModelPartitioningHandler((HVRM) modelCreateHandler.createModel(vrmModelWithProperties.getSystemId()))
                .partitionModel(vrmModelWithProperties.getProperties());
    }

}
