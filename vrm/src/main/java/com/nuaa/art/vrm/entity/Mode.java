package com.nuaa.art.vrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName vrm_mode
 */
@TableName(value ="vrm_mode")
@Data
public class Mode implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer modeId;

    /**
     * 
     */
    private String modeName;

    /**
     * 
     */
    private Integer initialStatus;

    /**
     * 
     */
    private Integer finalStatus;

    /**
     * 
     */
    private Integer value;

    /**
     * 
     */
    private Integer modeClassId;

    /**
     * 
     */
    private String modeClassName;

    /**
     * 
     */
    private String modeDescription;

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
        Mode other = (Mode) that;
        return (this.getModeId() == null ? other.getModeId() == null : this.getModeId().equals(other.getModeId()))
            && (this.getModeName() == null ? other.getModeName() == null : this.getModeName().equals(other.getModeName()))
            && (this.getInitialStatus() == null ? other.getInitialStatus() == null : this.getInitialStatus().equals(other.getInitialStatus()))
            && (this.getFinalStatus() == null ? other.getFinalStatus() == null : this.getFinalStatus().equals(other.getFinalStatus()))
            && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()))
            && (this.getModeClassId() == null ? other.getModeClassId() == null : this.getModeClassId().equals(other.getModeClassId()))
            && (this.getModeClassName() == null ? other.getModeClassName() == null : this.getModeClassName().equals(other.getModeClassName()))
            && (this.getModeDescription() == null ? other.getModeDescription() == null : this.getModeDescription().equals(other.getModeDescription()))
            && (this.getSystemId() == null ? other.getSystemId() == null : this.getSystemId().equals(other.getSystemId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getModeId() == null) ? 0 : getModeId().hashCode());
        result = prime * result + ((getModeName() == null) ? 0 : getModeName().hashCode());
        result = prime * result + ((getInitialStatus() == null) ? 0 : getInitialStatus().hashCode());
        result = prime * result + ((getFinalStatus() == null) ? 0 : getFinalStatus().hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        result = prime * result + ((getModeClassId() == null) ? 0 : getModeClassId().hashCode());
        result = prime * result + ((getModeClassName() == null) ? 0 : getModeClassName().hashCode());
        result = prime * result + ((getModeDescription() == null) ? 0 : getModeDescription().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", modeId=").append(modeId);
        sb.append(", modeName=").append(modeName);
        sb.append(", initialStatus=").append(initialStatus);
        sb.append(", finalStatus=").append(finalStatus);
        sb.append(", value=").append(value);
        sb.append(", modeClassId=").append(modeClassId);
        sb.append(", modeClassName=").append(modeClassName);
        sb.append(", modeDescription=").append(modeDescription);
        sb.append(", systemId=").append(systemId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}