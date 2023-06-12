package com.nuaa.art.vrm.model;

import com.nuaa.art.vrm.entity.Type;

public class VerificationType extends Type {
    private String typeName;
    private String baseType;
    private String range;
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public String getBaseType() {
        return baseType;
    }
    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }
    public String getRange() {
        return range;
    }
    public void setRange(String range) {
        this.range = range;
    }
    public VerificationType(String typeName, String baseType, String range) {

        this.typeName = typeName;
        this.baseType = baseType;
        this.range = range;
    }
    public VerificationType() {
    }

}
