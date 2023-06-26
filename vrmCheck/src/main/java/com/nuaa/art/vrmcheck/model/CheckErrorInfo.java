package com.nuaa.art.vrmcheck.model;

import com.nuaa.art.vrmcheck.common.CheckErrorType;

import java.util.ArrayList;
import java.util.List;

public class CheckErrorInfo {
    /**
     * 错误条目序号
     */
    int id;
    /**
     * 错误类型枚举序号
     */
    int errorTypeId;

    /**
     * 错误类型
     */
    String errorType;

    /**
     * 错误对象类型
     */
    String relateType;

    /**
     * 错误对象名
     */
    String relateName;

    /**
     *  原始需求Id, xml中为规范化需求,需要进行一次查询
     */
    List<Integer> requirementId;

    /**
     * 依赖模式类
     */
    String modeClass;

    /**
     * 错误细节, 表函数错误要说明情况
     */
    String details;




    public CheckErrorInfo(int id, int errorTypeId, String errorType, String relateType, String relateName, List<Integer> requirementId, String modeClass, String details) {
        this.id = id;
        this.errorTypeId = errorTypeId;
        this.errorType = errorType;
        this.relateType = relateType;
        this.relateName = relateName;
        this.requirementId = new ArrayList<>(requirementId);
        this.modeClass = modeClass;
        this.details = details;
    }

    public CheckErrorInfo(int id, CheckErrorType e, String relateName, List<Integer> requirementId, String modeClass, String details) {
        this.id = id;
        this.errorTypeId = e.ordinal();
        this.errorType = e.getErrorType();
        this.relateType = e.getRelateType();
        this.relateName = relateName;
        this.requirementId = new ArrayList<>(requirementId);
        this.modeClass = modeClass;
        this.details = details;
    }

    public CheckErrorInfo(int id, CheckErrorType e, String relateName, List<Integer> requirementId, String details) {
        this.id = id;
        this.errorTypeId = e.ordinal();
        this.errorType = e.getErrorType();
        this.relateType = e.getRelateType();
        this.relateName = relateName;
        this.requirementId = new ArrayList<>(requirementId);
        this.modeClass = "";
        this.details = details;
    }
    public CheckErrorInfo(int id, CheckErrorType e, String relateName, String modeClass, String details) {
        this.id = id;
        this.errorTypeId = e.ordinal();
        this.errorType = e.getErrorType();
        this.relateType = e.getRelateType();
        this.relateName = relateName;
        this.requirementId = new ArrayList<>();
        this.modeClass = modeClass;
        this.details = details;
    }
    public CheckErrorInfo(int id, CheckErrorType e, String relateName,  String details) {
        this.id = id;
        this.errorTypeId = e.ordinal();
        this.errorType = e.getErrorType();
        this.relateType = e.getRelateType();
        this.relateName = relateName;
        this.requirementId = new ArrayList<>();
        this.modeClass = "";
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getErrorTypeId() {
        return errorTypeId;
    }

    public void setErrorTypeId(int errorTypeId) {
        this.errorTypeId = errorTypeId;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getRelateType() {
        return relateType;
    }

    public void setRelateType(String relateType) {
        this.relateType = relateType;
    }

    public String getRelateName() {
        return relateName;
    }

    public void setRelateName(String relateName) {
        this.relateName = relateName;
    }

    public List<Integer> getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(List<Integer> requirementId) {
        this.requirementId = requirementId;
    }

    public String getModeClass() {
        return modeClass;
    }

    public void setModeClass(String modeClass) {
        this.modeClass = modeClass;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "CheckErrorInfo{" +
                "id=" + id +
                ", errorTypeId=" + errorTypeId +
                ", errorType='" + errorType + '\'' +
                ", relateType='" + relateType + '\'' +
                ", relateName='" + relateName + '\'' +
                ", requirementId=" + requirementId +
                ", modeClass='" + modeClass + '\'' +
                ", details='" + details + '\'' +
                '}';
    }



}
