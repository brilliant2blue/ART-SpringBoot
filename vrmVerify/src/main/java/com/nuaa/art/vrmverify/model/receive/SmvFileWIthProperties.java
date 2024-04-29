package com.nuaa.art.vrmverify.model.receive;

import lombok.Data;

import java.util.List;

/**
 * 接受前端传递的smv验证文件
 * @author djl
 * @date 2024-04-15
 */
@Data
public class SmvFileWIthProperties {

    private String smvFilePath;
    private int propertyCount;
    private List<String> properties;

}
