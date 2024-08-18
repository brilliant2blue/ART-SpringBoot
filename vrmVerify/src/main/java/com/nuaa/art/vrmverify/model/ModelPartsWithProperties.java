package com.nuaa.art.vrmverify.model;

import com.nuaa.art.vrm.model.hvrm.HVRM;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 分配了验证属性的子模型
 * @author djl
 * @date 2024-07-27
 */
@Data
@AllArgsConstructor
public class ModelPartsWithProperties {

    private String name;
    private HVRM model;
    private List<String> properties;

}
