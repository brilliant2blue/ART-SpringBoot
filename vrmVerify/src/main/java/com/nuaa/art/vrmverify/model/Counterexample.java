package com.nuaa.art.vrmverify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 反例类
 * @author djl
 * @date 2024-03-25
 */
@Data
@NoArgsConstructor
public class Counterexample {

    private String property;    // 验证属性
    private boolean passed;     // 验证是否通过
    private int traceLength;    // 反例路径长度
    private boolean existLoop;  // 是否存在环
    private int loopStartsState;    // 环开始的状态
    private List<List<Map<String, String>>> traceList;    // 反例路径

}
