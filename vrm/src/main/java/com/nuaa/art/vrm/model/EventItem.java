package com.nuaa.art.vrm.model;

import lombok.Data;

import java.util.ArrayList;

/**
 * 原子事件
 *
 * @author konsin
 * @date 2023/09/14
 */
@Data
public class EventItem {
    public String eventOperator = "";
    public ConditionTable eventCondition;
    public String guardOperator = "";
    public ConditionTable guardCondition;
}
