package com.nuaa.art.vrmcheck.model;

import java.util.ArrayList;
import java.util.List;

public class CheckErrorReporter {
    public
    int errorCount = 0;

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
    boolean isModeConvertRight = true;

    /**
     * 输出完整性正确
     */
    boolean isOutputIntegrityRight = true;

    List<CheckErrorInfo> errorList = new ArrayList<>();

    public int getErrorCount() {
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

    public boolean isModeConvertRight() {
        return isModeConvertRight;
    }

    public void setModeConvertRight(boolean modeConvertRight) {
        isModeConvertRight = modeConvertRight;
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
                ", isModeConvertRight=" + isModeConvertRight +
                ", isOutputIntegrityRight=" + isOutputIntegrityRight +
                ", errorList=" + errorList +
                '}';
    }
}
