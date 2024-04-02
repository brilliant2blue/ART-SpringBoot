package com.nuaa.art.vrmverify.handler;

import com.nuaa.art.vrmverify.model.Counterexample;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;

import java.util.*;

/**
 * 处理反例可视化
 *
 * @author djl
 * @date 2024-03-30
 */
public class CxVisualizationHandler {

    /**
     * 根据反例计算变量表
     *
     * @param cx
     * @return
     */
    public static VariableTable computeVariableTable(Counterexample cx) {
        if (cx.isPassed())
            return null;

        int traceLength = cx.getTraceLength();
        List<Map<String, String>> assignments = cx.getAssignments();
        Map<String, List<String>> variable2Values = new HashMap<>();

        for (int i = 0; i < traceLength; i++) {
            Set<Map.Entry<String, String>> curAssignmentSet = assignments.get(i).entrySet();
            // 初始状态赋值
            if(i == 0){
                for (Map.Entry<String, String> assignment : curAssignmentSet) {
                    List<String> values = new ArrayList<>();
                    values.add(assignment.getValue());
                    variable2Values.put(assignment.getKey(), values);
                }
                continue;
            }
            // 之后状态赋值
            int j = i - 1;
            variable2Values.forEach((variable, values) -> {
                values.add(values.get(j));
            });
            for (Map.Entry<String, String> assignment : curAssignmentSet) {
                List<String> values = variable2Values.get(assignment.getKey());
                // 更新变量值
                values.remove(values.size() - 1);
                values.add(assignment.getValue());
            }
        }

        return new VariableTable(cx.getTraceLength(), variable2Values);
    }
}
