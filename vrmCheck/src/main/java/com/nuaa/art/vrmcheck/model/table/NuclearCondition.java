package com.nuaa.art.vrmcheck.model.table;

import com.nuaa.art.vrm.model.ConditionItem;

public class NuclearCondition extends ConditionItem {
    public boolean isTrue = false;
    public boolean isFalse = false;

    public boolean isDefault = false;

    public NuclearCondition() {
        super();
        isTrue = false;
        isFalse = false;
        isDefault = false;
    }

    //深拷贝构造
    public NuclearCondition(ConditionItem condition) {
        super(condition);
        isTrue = false;
        isFalse = false;
        isDefault = false;
    }

    public NuclearCondition(String var1, String operator, String var2) {
        super(var1, operator, var2);
        isTrue = false;
        isFalse = false;
        isDefault = false;
    }

    public void setTrue(){
        isTrue = true;
    }
    public void setFalse(){
        isFalse = true;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public boolean isFalse() {
        return isFalse;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault( ){
        isDefault = true;
    }
}
