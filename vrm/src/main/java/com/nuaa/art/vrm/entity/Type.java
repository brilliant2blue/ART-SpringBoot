package com.nuaa.art.vrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName vrm_type
 */
@TableName(value ="vrm_type")
@Data
public class Type implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer typeId;

    /**
     * 
     */
    private String typeName;

    /**
     * 
     */
    private String dataType;

    /**
     * 
     */
    private String typeRange;

    /**
     * 
     */
    private String typeAccuracy;

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
        Type other = (Type) that;
        return (this.getTypeId() == null ? other.getTypeId() == null : this.getTypeId().equals(other.getTypeId()))
            && (this.getTypeName() == null ? other.getTypeName() == null : this.getTypeName().equals(other.getTypeName()))
            && (this.getDataType() == null ? other.getDataType() == null : this.getDataType().equals(other.getDataType()))
            && (this.getTypeRange() == null ? other.getTypeRange() == null : this.getTypeRange().equals(other.getTypeRange()))
            && (this.getTypeAccuracy() == null ? other.getTypeAccuracy() == null : this.getTypeAccuracy().equals(other.getTypeAccuracy()))
            && (this.getSystemId() == null ? other.getSystemId() == null : this.getSystemId().equals(other.getSystemId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTypeId() == null) ? 0 : getTypeId().hashCode());
        result = prime * result + ((getTypeName() == null) ? 0 : getTypeName().hashCode());
        result = prime * result + ((getDataType() == null) ? 0 : getDataType().hashCode());
        result = prime * result + ((getTypeRange() == null) ? 0 : getTypeRange().hashCode());
        result = prime * result + ((getTypeAccuracy() == null) ? 0 : getTypeAccuracy().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", typeId=").append(typeId);
        sb.append(", typeName=").append(typeName);
        sb.append(", dataType=").append(dataType);
        sb.append(", typeRange=").append(typeRange);
        sb.append(", typeAccuracy=").append(typeAccuracy);
        sb.append(", systemId=").append(systemId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}