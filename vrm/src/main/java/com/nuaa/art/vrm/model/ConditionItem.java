package com.nuaa.art.vrm.model;

import lombok.Data;

/**
 * 条件表辅助类
 *
 * @author konsin
 * @date 2023/07/02
 */
@Data
public class ConditionItem {
    String var1;
    String operator;
    String var2;
    String symbol;

    public ConditionItem(){
        super();
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

    public String conditionString() {
        return var1 + operator + var2;
    }

}
