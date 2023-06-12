package com.nuaa.art.vrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName vrm_conceptrelaterequirement
 */
@TableName(value ="vrm_conceptrelaterequirement")
@Data
public class ConceptRelateRequirement implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer systemId;

    /**
     * 
     */
    private Integer conceptId;

    /**
     * 
     */
    private Integer sourceReqId;

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
        ConceptRelateRequirement other = (ConceptRelateRequirement) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSystemId() == null ? other.getSystemId() == null : this.getSystemId().equals(other.getSystemId()))
            && (this.getConceptId() == null ? other.getConceptId() == null : this.getConceptId().equals(other.getConceptId()))
            && (this.getSourceReqId() == null ? other.getSourceReqId() == null : this.getSourceReqId().equals(other.getSourceReqId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
        result = prime * result + ((getConceptId() == null) ? 0 : getConceptId().hashCode());
        result = prime * result + ((getSourceReqId() == null) ? 0 : getSourceReqId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", systemId=").append(systemId);
        sb.append(", conceptId=").append(conceptId);
        sb.append(", sourceReqId=").append(sourceReqId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}