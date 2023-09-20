package com.nuaa.art.vrmcheck.model.obj;

import com.nuaa.art.vrm.model.ConditionItem;

public class NuclearCondition extends ConditionItem {
    public boolean isTrue = false;
    public boolean isFalse = false;

    public NuclearCondition() {
        super();
    }
    public NuclearCondition(ConditionItem condition) {
        super();
        this.var1 = condition.getVar1();
        this.var2 = condition.getVar2();
        this.operator = condition.getOperator();
    }

    public NuclearCondition(String var1, String operator, String var2) {
        super(var1, operator, var2);
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

}
