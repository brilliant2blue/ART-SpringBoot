package com.nuaa.art.vrmcheck.common.utils;

import java.util.ArrayList;

public class OutputUtils {
    // 生成违反范式的场景集合的表头（变量的列表），提供给分析的输出信息
    public static String getVariableSetHeader(ArrayList<String> continualVariables, ArrayList<String> discreteVariables) {
        int maximum = 0;
        for (String continual : continualVariables) {
            if (continual.length() / 15 > maximum)
                maximum = continual.length() / 15;
        }
        for (String discrete : discreteVariables) {
            if (discrete.length() / 15 > maximum)
                maximum = discrete.length() / 15;
        }
        String variableSet = "";
        for (int i = 0; i <= maximum; i++) {
            variableSet += "|";
            for (String continual : continualVariables) {
                if (continual.length() > 15 + i * 15)
                    variableSet += String.format("%-15s", continual.substring(0 + i * 15, 15 + i * 15)) + "|";
                else if (continual.length() > 0 + i * 15)
                    variableSet += String.format("%-15s", continual.substring(0 + i * 15)) + "|";
                else
                    variableSet += String.format("%-15s", "") + "|";
            }
            for (String discrete : discreteVariables) {
                if (discrete.length() > 15 + i * 15)
                    variableSet += String.format("%-15s", discrete.substring(0 + i * 15, 15 + i * 15)) + "|";
                else if (discrete.length() > 0 + i * 15)
                    variableSet += String.format("%-15s", discrete.substring(0 + i * 15)) + "|";
                else
                    variableSet += String.format("%-15s", "") + "|";
            }
            variableSet += "\n";
        }
        variableSet += "|";
        for (int i = 0; i < continualVariables.size() + discreteVariables.size(); i++) {
            variableSet += "---------------|";
        }
        variableSet += "\n";
        return variableSet;
    }
}
