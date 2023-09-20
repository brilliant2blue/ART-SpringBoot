package com.nuaa.art.vrm.model;

import lombok.Data;

/**
 * 原子条件
 *
 * @author konsin
 * @date 2023/07/02
 */
@Data
public class ConditionItem {
    protected String var1;
    protected String operator;
    protected String var2;
    protected String symbol;

    public ConditionItem() {
        this.var1 = "";
        this.operator = "";
        this.var2 = "";
        this.symbol = null;
    }

    public ConditionItem(String var1, String operator, String var2) {
        this.var1 = var1;
        this.operator = operator;
        this.var2 = var2;
        this.symbol = "";
    }

    public ConditionItem(String var1, String operator, String var2, String symbol) {
        this.var1 = var1;
        this.operator = operator;
        this.var2 = var2;
        this.symbol = symbol;
    }

    public boolean isEmpty(){
        return (var1 == null || var1.isBlank()) && (operator == null || operator.isBlank()) && (var2 == null || var2.isBlank());
    }

    public String conditionString() {
        return var1 + operator + var2;
    }

}
