package com.nuaa.art.vrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName vrm_naturallanguagerequirement
 */
@TableName(value ="vrm_naturallanguagerequirement")
@Data
public class NaturalLanguageRequirement implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer reqId;

    /**
     * 
     */
    private Integer systemId;

    /**
     * 
     */
    private String reqContent;

    /**
     * 
     */
    private Integer reqExcelId;

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
        NaturalLanguageRequirement other = (NaturalLanguageRequirement) that;
        return (this.getReqId() == null ? other.getReqId() == null : this.getReqId().equals(other.getReqId()))
            && (this.getSystemId() == null ? other.getSystemId() == null : this.getSystemId().equals(other.getSystemId()))
            && (this.getReqContent() == null ? other.getReqContent() == null : this.getReqContent().equals(other.getReqContent()))
            && (this.getReqExcelId() == null ? other.getReqExcelId() == null : this.getReqExcelId().equals(other.getReqExcelId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getReqId() == null) ? 0 : getReqId().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
        result = prime * result + ((getReqContent() == null) ? 0 : getReqContent().hashCode());
        result = prime * result + ((getReqExcelId() == null) ? 0 : getReqExcelId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", reqId=").append(reqId);
        sb.append(", systemId=").append(systemId);
        sb.append(", reqContent=").append(reqContent);
        sb.append(", reqExcelId=").append(reqExcelId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}