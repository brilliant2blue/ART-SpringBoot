package com.nuaa.art.vrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName vrm_modeclass
 */
@TableName(value ="vrm_modeclass")
@Data
public class ModeClass implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer modeClassId;

    /**
     * 
     */
    private String modeClassName;

    /**
     * 
     */
    private String modeClassDescription;

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
        ModeClass other = (ModeClass) that;
        return (this.getModeClassId() == null ? other.getModeClassId() == null : this.getModeClassId().equals(other.getModeClassId()))
            && (this.getModeClassName() == null ? other.getModeClassName() == null : this.getModeClassName().equals(other.getModeClassName()))
            && (this.getModeClassDescription() == null ? other.getModeClassDescription() == null : this.getModeClassDescription().equals(other.getModeClassDescription()))
            && (this.getSystemId() == null ? other.getSystemId() == null : this.getSystemId().equals(other.getSystemId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getModeClassId() == null) ? 0 : getModeClassId().hashCode());
        result = prime * result + ((getModeClassName() == null) ? 0 : getModeClassName().hashCode());
        result = prime * result + ((getModeClassDescription() == null) ? 0 : getModeClassDescription().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", modeClassId=").append(modeClassId);
        sb.append(", modeClassName=").append(modeClassName);
        sb.append(", modeClassDescription=").append(modeClassDescription);
        sb.append(", systemId=").append(systemId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}