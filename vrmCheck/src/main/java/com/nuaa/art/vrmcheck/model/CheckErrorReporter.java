package com.nuaa.art.vrmcheck.model;

import java.util.ArrayList;
import java.util.List;

public class CheckErrorReporter {
    public String modelName = "";
    public Integer errorCount = 0;

    /**
     * 基本范式正确
     */
    boolean isBasicRight = true;

    boolean isInputIntegrityRight = true;
    /**
     * 表格函数正确
     */
    boolean isConditionRight = true;
    boolean isEventRight = true;
    /**
     * 模式转换表正确
     */
    boolean isModeTransRight = true;

    /**
     * 输出完整性正确
     */
    boolean isOutputIntegrityRight = true;

    List<CheckErrorInfo> errorList = new ArrayList<>();

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void addErrorCount() {
        this.errorCount++;
    }

    public boolean isBasicRight() {
        return isBasicRight;
    }

    public void setBasicRight(boolean basicRight) {
        isBasicRight = basicRight;
    }

    public boolean isInputIntegrityRight() {
        return isInputIntegrityRight;
    }

    public void setInputIntegrityRight(boolean firstRight) {
        isInputIntegrityRight = firstRight;
    }

    public boolean isConditionRight() {
        return isConditionRight;
    }

    public void setConditionRight(boolean conditionRight) {
        isConditionRight = conditionRight;
    }

    public boolean isEventRight() {
        return isEventRight;
    }

    public void setEventRight(boolean eventRight) {
        isEventRight = eventRight;
    }

    public boolean isModeTransRight() {
        return isModeTransRight;
    }

    public void setModeTransRight(boolean modeTransRight) {
        isModeTransRight = modeTransRight;
    }

    public boolean isOutputIntegrityRight() {
        return isOutputIntegrityRight;
    }

    public void setOutputIntegrityRight(boolean outputIntegrityRight) {
        isOutputIntegrityRight = outputIntegrityRight;
    }

    public List<CheckErrorInfo> getErrorList() {
        return errorList;
    }

    public void addErrorList(CheckErrorInfo error) {
        this.errorList.add(error);
    }
    public void addErrorList(List<CheckErrorInfo> errorList) {
        this.errorList.addAll(errorList);
    }

    @Override
    public String toString() {
        return "CheckErrorReporter{" +
                "errorCount=" + errorCount +
                ", isBasicRight=" + isBasicRight +
                ", isInputIntegrityRight=" + isInputIntegrityRight +
                ", isConditionRight=" + isConditionRight +
                ", isEventRight=" + isEventRight +
                ", isModeTransRight=" + isModeTransRight +
                ", isOutputIntegrityRight=" + isOutputIntegrityRight +
                ", errorList=" + errorList +
                '}';
    }
}
