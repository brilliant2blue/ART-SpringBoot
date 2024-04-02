package com.nuaa.art.vrmverify.model.visualization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 变量表
 * @author djl
 * @date 2024-03-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariableTable {

    private int traceLength;
    private Map<String, List<String>> variableValues = new HashMap<>();

}
