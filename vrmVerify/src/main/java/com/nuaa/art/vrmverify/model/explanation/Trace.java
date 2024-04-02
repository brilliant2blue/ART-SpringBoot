package com.nuaa.art.vrmverify.model.explanation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 反例路径
 * @author djl
 * @date 2024-03-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trace {

    private int traceLength;    // 反例路径长度
    private boolean existLoop;  // 是否存在环
    private int loopStartsState;    // 环开始的状态
    private List<Map<String, String>> assignments;    // 赋值列表

}
