package com.nuaa.art.vrm.model;

/**
 * VRM验证输入
 *
 * @author konsin
 * @date 2023/06/10
 */

public class VerificationInput {
    private String inputName;
    private String initValue;
    private String typeName;
    private String rangeInSmv;
    private boolean listFlag=false;

    public boolean getListFlag() {
        return listFlag;
    }

    public void setListFlag(boolean listFlag) {
        this.listFlag = listFlag;
    }

    public String getRangeInSmv() {
        return rangeInSmv;
    }

    public void setRangeInSmv(String rangeInSmv) {
        this.rangeInSmv = rangeInSmv;
    }
    public String getInputName() {
        return inputName;
    }
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }
    public String getInitValue() {
        return initValue;
    }
    public void setInitValue(String initValue) {
        this.initValue = initValue;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public VerificationInput(String inputName, String initValue, String typeName) {
        this.inputName = inputName;
        this.initValue = initValue;
        this.typeName = typeName;
    }

    public VerificationInput() {

    }



}
