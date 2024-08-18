package com.nuaa.art.vrmverify.common.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * 条件/事件表工具类
 * @author djl
 * @date 2024-07-23
 */
public class TableUtils {

    public static final String EVENT_SIGNAL = "@T|@F|@C|WHEN|WHILE|WHERE";

    /**
     * 从条件中获取变量列表
     * @param condition
     */
    public static Set<String> getVarsFromCondition(String condition){
        String[] exprs = condition
                .replaceAll("!", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .split("&|\\|\\|");
        Set<String> vars = new HashSet<>();
        for (String expr : exprs) {
            String var = expr.split(">|=|<")[0];
            if(var.equalsIgnoreCase("True") || var.equalsIgnoreCase("False"))
                continue;
            vars.add(var);
        }
        return vars;
    }

    /**
     * 从事件中获取变量列表
     * @param event
     * @return
     */
    public static Set<String> getVarsFromEvent(String event){
        String[] conditions = event
                .replaceAll("\\{", "")
                .replaceAll("}", "")
                .split(EVENT_SIGNAL);
        Set<String> vars = new HashSet<>();
        for (int i = 1; i < conditions.length; i++) {
            vars.addAll(getVarsFromCondition(conditions[i]));
        }
        return vars;
    }

}
