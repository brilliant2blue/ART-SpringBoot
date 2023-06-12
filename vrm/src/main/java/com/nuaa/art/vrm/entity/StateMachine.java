package com.nuaa.art.vrm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName vrm_statemachine
 */
@TableName(value ="vrm_statemachine")
@Data
public class StateMachine implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer stateMachineId;

    /**
     * 
     */
    private String sourceState;

    /**
     * 
     */
    private String endState;

    /**
     * 
     */
    private String event;

    /**
     * 
     */
    private String dependencyModeClass;

    /**
     * 
     */
    private Integer systemId;

    /**
     * 
     */
    private Integer dependencyModeClassId;

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
        StateMachine other = (StateMachine) that;
        return (this.getStateMachineId() == null ? other.getStateMachineId() == null : this.getStateMachineId().equals(other.getStateMachineId()))
            && (this.getSourceState() == null ? other.getSourceState() == null : this.getSourceState().equals(other.getSourceState()))
            && (this.getEndState() == null ? other.getEndState() == null : this.getEndState().equals(other.getEndState()))
            && (this.getEvent() == null ? other.getEvent() == null : this.getEvent().equals(other.getEvent()))
            && (this.getDependencyModeClass() == null ? other.getDependencyModeClass() == null : this.getDependencyModeClass().equals(other.getDependencyModeClass()))
            && (this.getSystemId() == null ? other.getSystemId() == null : this.getSystemId().equals(other.getSystemId()))
            && (this.getDependencyModeClassId() == null ? other.getDependencyModeClassId() == null : this.getDependencyModeClassId().equals(other.getDependencyModeClassId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getStateMachineId() == null) ? 0 : getStateMachineId().hashCode());
        result = prime * result + ((getSourceState() == null) ? 0 : getSourceState().hashCode());
        result = prime * result + ((getEndState() == null) ? 0 : getEndState().hashCode());
        result = prime * result + ((getEvent() == null) ? 0 : getEvent().hashCode());
        result = prime * result + ((getDependencyModeClass() == null) ? 0 : getDependencyModeClass().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
        result = prime * result + ((getDependencyModeClassId() == null) ? 0 : getDependencyModeClassId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", stateMachineId=").append(stateMachineId);
        sb.append(", sourceState=").append(sourceState);
        sb.append(", endState=").append(endState);
        sb.append(", event=").append(event);
        sb.append(", dependencyModeClass=").append(dependencyModeClass);
        sb.append(", systemId=").append(systemId);
        sb.append(", dependencyModeClassId=").append(dependencyModeClassId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}