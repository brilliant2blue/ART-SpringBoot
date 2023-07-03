package com.nuaa.art.vrm.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class EventItem {
    public String eventOperator = "";
    public ConditionTable eventCondition;
    public String guardOperator = "";
    public ConditionTable guardCondition;
}
