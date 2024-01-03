package com.nuaa.art.vrm.model;

import lombok.Data;

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

    public EventItem() {
        super();
    }

    public EventItem(String eventOperator, ConditionTable eventCondition, String guardOperator, ConditionTable guardCondition) {
        this.eventOperator = eventOperator;
        this.eventCondition = new ConditionTable(eventCondition);
        this.guardOperator = guardOperator;
        this.guardCondition = new ConditionTable(guardCondition);
    }

    public EventItem(EventItem item){
        this(item.getEventOperator(),item.getEventCondition(),item.getGuardOperator(),item.getGuardCondition());
    }

    public boolean whetherEmpty(){
        return eventOperator.isBlank() && guardOperator.isBlank();
    }
}
