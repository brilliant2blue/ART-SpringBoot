package com.nuaa.art.vrm.model;

/**
 * 验证表
 *
 * @author konsin
 * @date 2023/06/10
 */
public class VerificationTable {
    String assignment;
    String condition;
    String event;
    String conditionString;
    String eventString;

    public String getConditionString() {
        return conditionString;
    }
    public void setConditionString(String conditionString) {
        this.conditionString = conditionString;
    }
    public String getEventString() {
        return eventString;
    }
    public void setEventString(String eventString) {
        this.eventString = eventString;
    }
    public String getAssignment() {
        return assignment;
    }
    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }
    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
    public VerificationTable(String assignment, String condition, String event) {
        this.assignment = assignment;
        this.condition = condition;
        this.event = event;
    }
    public VerificationTable() {
    }


}
