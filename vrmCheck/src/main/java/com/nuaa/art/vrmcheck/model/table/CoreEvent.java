package com.nuaa.art.vrmcheck.model.table;

import java.util.ArrayList;

public class CoreEvent {

    public String eventOperator = "";
    public ArrayList<ArrayList<NuclearCondition>> eventCondition;
    public String guardOperator = "";
    public ArrayList<ArrayList<NuclearCondition>> guardCondition;

    public boolean isTrue = false;
    public boolean isFalse = false;

    public boolean isDefault = false;

    public CoreEvent() {
        super();
        isTrue = false;
        isFalse = false;
        isDefault = false;
    }

    public CoreEvent(String eventOperator, ArrayList<ArrayList<NuclearCondition>> eventCondition, String guardOperator, ArrayList<ArrayList<NuclearCondition>> guardCondition) {
        this.eventOperator = eventOperator;
        this.eventCondition = eventCondition;
        this.guardOperator = guardOperator;
        this.guardCondition = guardCondition;
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
