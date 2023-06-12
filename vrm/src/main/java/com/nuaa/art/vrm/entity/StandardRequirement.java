package com.nuaa.art.vrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName vrm_standardrequirement
 */
@TableName(value ="vrm_standardrequirement")
@Data
public class StandardRequirement implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer standardrequirementId;

    /**
     * 
     */
    private Integer naturalLangugeReqId;

    /**
     * 
     */
    private String standardReqVariable;

    /**
     * 
     */
    private String standardReqFunction;

    /**
     * 
     */
    private String standardReqValue;

    /**
     * 
     */
    private String standardReqCondition;

    /**
     * 
     */
    private String standardReqEvent;

    /**
     * 
     */
    private String standardReqContent;

    /**
     * 
     */
    private String templateType;

    /**
     * 
     */
    private String mode;

    /**
     * 
     */
    private Integer systemId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        StandardRequirement other = (StandardRequirement) that;
        return (this.getStandardrequirementId() == null ? other.getStandardrequirementId() == null : this.getStandardrequirementId().equals(other.getStandardrequirementId()))
            && (this.getNaturalLangugeReqId() == null ? other.getNaturalLangugeReqId() == null : this.getNaturalLangugeReqId().equals(other.getNaturalLangugeReqId()))
            && (this.getStandardReqVariable() == null ? other.getStandardReqVariable() == null : this.getStandardReqVariable().equals(other.getStandardReqVariable()))
            && (this.getStandardReqFunction() == null ? other.getStandardReqFunction() == null : this.getStandardReqFunction().equals(other.getStandardReqFunction()))
            && (this.getStandardReqValue() == null ? other.getStandardReqValue() == null : this.getStandardReqValue().equals(other.getStandardReqValue()))
            && (this.getStandardReqCondition() == null ? other.getStandardReqCondition() == null : this.getStandardReqCondition().equals(other.getStandardReqCondition()))
            && (this.getStandardReqEvent() == null ? other.getStandardReqEvent() == null : this.getStandardReqEvent().equals(other.getStandardReqEvent()))
            && (this.getStandardReqContent() == null ? other.getStandardReqContent() == null : this.getStandardReqContent().equals(other.getStandardReqContent()))
            && (this.getTemplateType() == null ? other.getTemplateType() == null : this.getTemplateType().equals(other.getTemplateType()))
            && (this.getMode() == null ? other.getMode() == null : this.getMode().equals(other.getMode()))
            && (this.getSystemId() == null ? other.getSystemId() == null : this.getSystemId().equals(other.getSystemId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getStandardrequirementId() == null) ? 0 : getStandardrequirementId().hashCode());
        result = prime * result + ((getNaturalLangugeReqId() == null) ? 0 : getNaturalLangugeReqId().hashCode());
        result = prime * result + ((getStandardReqVariable() == null) ? 0 : getStandardReqVariable().hashCode());
        result = prime * result + ((getStandardReqFunction() == null) ? 0 : getStandardReqFunction().hashCode());
        result = prime * result + ((getStandardReqValue() == null) ? 0 : getStandardReqValue().hashCode());
        result = prime * result + ((getStandardReqCondition() == null) ? 0 : getStandardReqCondition().hashCode());
        result = prime * result + ((getStandardReqEvent() == null) ? 0 : getStandardReqEvent().hashCode());
        result = prime * result + ((getStandardReqContent() == null) ? 0 : getStandardReqContent().hashCode());
        result = prime * result + ((getTemplateType() == null) ? 0 : getTemplateType().hashCode());
        result = prime * result + ((getMode() == null) ? 0 : getMode().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", standardrequirementId=").append(standardrequirementId);
        sb.append(", naturalLangugeReqId=").append(naturalLangugeReqId);
        sb.append(", standardReqVariable=").append(standardReqVariable);
        sb.append(", standardReqFunction=").append(standardReqFunction);
        sb.append(", standardReqValue=").append(standardReqValue);
        sb.append(", standardReqCondition=").append(standardReqCondition);
        sb.append(", standardReqEvent=").append(standardReqEvent);
        sb.append(", standardReqContent=").append(standardReqContent);
        sb.append(", templateType=").append(templateType);
        sb.append(", mode=").append(mode);
        sb.append(", systemId=").append(systemId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}