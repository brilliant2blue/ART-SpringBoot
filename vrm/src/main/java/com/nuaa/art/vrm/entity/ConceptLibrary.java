package com.nuaa.art.vrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @TableName vrm_conceptlibrary
 */
@TableName(value ="vrm_conceptlibrary")
@Data
public class ConceptLibrary implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer conceptId;

    /**
     * 
     */
    private String conceptName;

    /**
     * 
     */
    private String conceptDatatype;

    /**
     * 
     */
    private String conceptRange;

    /**
     * 
     */
    private String conceptValue;

    /**
     * 
     */
    private String conceptAccuracy;

    /**
     * 
     */
    private String conceptDependencyModeClass;

    /**
     * 
     */
    private String conceptType;

    /**
     * 
     */
    private String conceptDescription;

    /**
     * 
     */
    private Integer systemId;


    @TableField(exist = false)
    private String sourceReqId;

    @TableField(exist = false)
    private boolean valid;

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
        ConceptLibrary other = (ConceptLibrary) that;
        return (this.getConceptId() == null ? other.getConceptId() == null : this.getConceptId().equals(other.getConceptId()))
            && (this.getConceptName() == null ? other.getConceptName() == null : this.getConceptName().equals(other.getConceptName()))
            && (this.getConceptDatatype() == null ? other.getConceptDatatype() == null : this.getConceptDatatype().equals(other.getConceptDatatype()))
            && (this.getConceptRange() == null ? other.getConceptRange() == null : this.getConceptRange().equals(other.getConceptRange()))
            && (this.getConceptValue() == null ? other.getConceptValue() == null : this.getConceptValue().equals(other.getConceptValue()))
            && (this.getConceptAccuracy() == null ? other.getConceptAccuracy() == null : this.getConceptAccuracy().equals(other.getConceptAccuracy()))
            && (this.getConceptDependencyModeClass() == null ? other.getConceptDependencyModeClass() == null : this.getConceptDependencyModeClass().equals(other.getConceptDependencyModeClass()))
            && (this.getConceptType() == null ? other.getConceptType() == null : this.getConceptType().equals(other.getConceptType()))
            && (this.getConceptDescription() == null ? other.getConceptDescription() == null : this.getConceptDescription().equals(other.getConceptDescription()))
            && (this.getSystemId() == null ? other.getSystemId() == null : this.getSystemId().equals(other.getSystemId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getConceptId() == null) ? 0 : getConceptId().hashCode());
        result = prime * result + ((getConceptName() == null) ? 0 : getConceptName().hashCode());
        result = prime * result + ((getConceptDatatype() == null) ? 0 : getConceptDatatype().hashCode());
        result = prime * result + ((getConceptRange() == null) ? 0 : getConceptRange().hashCode());
        result = prime * result + ((getConceptValue() == null) ? 0 : getConceptValue().hashCode());
        result = prime * result + ((getConceptAccuracy() == null) ? 0 : getConceptAccuracy().hashCode());
        result = prime * result + ((getConceptDependencyModeClass() == null) ? 0 : getConceptDependencyModeClass().hashCode());
        result = prime * result + ((getConceptType() == null) ? 0 : getConceptType().hashCode());
        result = prime * result + ((getConceptDescription() == null) ? 0 : getConceptDescription().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", conceptId=").append(conceptId);
        sb.append(", conceptName=").append(conceptName);
        sb.append(", conceptDatatype=").append(conceptDatatype);
        sb.append(", conceptRange=").append(conceptRange);
        sb.append(", conceptValue=").append(conceptValue);
        sb.append(", conceptAccuracy=").append(conceptAccuracy);
        sb.append(", conceptDependencyModeClass=").append(conceptDependencyModeClass);
        sb.append(", conceptType=").append(conceptType);
        sb.append(", conceptDescription=").append(conceptDescription);
        sb.append(", systemId=").append(systemId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}