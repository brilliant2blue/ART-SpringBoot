package com.nuaa.art.vrmverify.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 接受前端传递的需要验证的vrm模型及属性
 * @author djl
 * @date 2024-05-15
 */
@Data
public class VrmModelWithProperties {

    private Integer systemId;
    private String systemName;
    private int propertyCount;
    private List<String> properties;

}
