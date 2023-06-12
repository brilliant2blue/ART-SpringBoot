package com.nuaa.art.vrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName vrm_propernoun
 */
@TableName(value ="vrm_propernoun")
@Data
public class ProperNoun implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer properNounId;

    /**
     * 
     */
    private String properNounName;

    /**
     * 
     */
    private String properNounDescription;

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
        ProperNoun other = (ProperNoun) that;
        return (this.getProperNounId() == null ? other.getProperNounId() == null : this.getProperNounId().equals(other.getProperNounId()))
            && (this.getProperNounName() == null ? other.getProperNounName() == null : this.getProperNounName().equals(other.getProperNounName()))
            && (this.getProperNounDescription() == null ? other.getProperNounDescription() == null : this.getProperNounDescription().equals(other.getProperNounDescription()))
            && (this.getSystemId() == null ? other.getSystemId() == null : this.getSystemId().equals(other.getSystemId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getProperNounId() == null) ? 0 : getProperNounId().hashCode());
        result = prime * result + ((getProperNounName() == null) ? 0 : getProperNounName().hashCode());
        result = prime * result + ((getProperNounDescription() == null) ? 0 : getProperNounDescription().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", properNounId=").append(properNounId);
        sb.append(", properNounName=").append(properNounName);
        sb.append(", properNounDescription=").append(properNounDescription);
        sb.append(", systemId=").append(systemId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}