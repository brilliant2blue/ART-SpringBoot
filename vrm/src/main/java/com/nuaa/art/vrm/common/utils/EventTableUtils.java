package com.nuaa.art.vrm.common.utils;

import com.nuaa.art.vrm.model.EventItem;
import com.nuaa.art.vrm.model.EventTable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class EventTableUtils {
    @Resource
    ConditionTableUtils conditionTableUtils;

    /**
     * 将合法的事件表转换为表达式字符串
     *
     * @param eventTable 条件表
     * @return {@link String}
     */

    public String ConvertTableToString(EventTable eventTable) {
        if ((eventTable.getAndNum() == 0) || (eventTable.getAndNum() == 1 &&
                eventTable.getEvents().get(0).getEventOperator().isBlank() &&
                eventTable.getEvents().get(0).getGuardOperator().isBlank() )){
            return "never";
        }
        String eventResult = "";
        for (int i = 0; i < eventTable.getAndNum(); i++) { //循环读取每一行事件
            String result = "";
            if(!eventTable.getEvents().get(i).getEventOperator().equals("")
                    && eventTable.getEvents().get(i).getEventCondition() != null) {
                result += eventTable.getEvents().get(i).getEventOperator();
                String events = conditionTableUtils.ConvertTableToString(eventTable.getEvents().get(i).getEventCondition());
                if (events.contains("true")||eventTable.getEvents().get(i).getEventCondition().getOrNum() > 1) {
                    result += "(" + events + ")";
                }
                else {
                    result += events;
                }
            }
            if(!eventTable.getEvents().get(i).getGuardOperator().equals("")
                    && eventTable.getEvents().get(i).getGuardCondition() != null) {
                result += eventTable.getEvents().get(i).getGuardOperator();
                String guards = conditionTableUtils.ConvertTableToString(eventTable.getEvents().get(i).getGuardCondition());
                if (guards.contains("true") || eventTable.getEvents().get(i).getGuardCondition().getOrNum() > 1) {
                    result += "(" + guards + ")";
                }
                else {
                    result += guards;
                }
            }
            if(result.equals("")) continue;
            result = "{" + result + "}";
            if(i == 0 || eventResult.equals("")){
                eventResult += result;
            } else {
                eventResult += "&&" + result;
            }
        }
        return eventResult;
    }

    /**
     * 将合法的表达式字符串转换成事件表
     *
     * @param event 条件
     * @return {@link EventTable}
     */
    public EventTable ConvertStringToTable(String event) {
        EventTable eventResult = new EventTable();
        if(event.isBlank()||event.equalsIgnoreCase("never")){
            EventItem subEvent = new EventItem();
            eventResult.getEvents().add(subEvent);
            eventResult.setAndNum(1);
            return eventResult;
        }
        String[] events = event.split("}&&\\{");
        eventResult.setAndNum(events.length);

        for (int x = 0; x < eventResult.getAndNum(); x++) {
            String singleEvent = events[x];
            String eventOp = "";
            String eventCondition = "";
            String guardOp = "";
            String guardCondition = "";
            if (x == 0) {
                singleEvent = singleEvent.substring(1);
            }
            if (x == eventResult.getAndNum() - 1) {
                singleEvent = singleEvent.substring(0, singleEvent.length() - 1);
            }

            if (singleEvent.contains("@T")) {
                eventOp = "@T";
            } else if (singleEvent.contains("@F")) {
                eventOp = "@F";
            } else if (singleEvent.contains("@C")) {
                eventOp = "@C";
            }

            if (singleEvent.contains("_")) {
                eventCondition = singleEvent.substring(2, singleEvent.indexOf("_")).replace("(", "")
                        .replace(")", "");
                guardOp = "_";
                guardCondition = singleEvent.substring(singleEvent.indexOf("_") + 1).replace("(", "")
                        .replace(")", "");
            } else if (singleEvent.contains("WHERE")) {
                eventCondition = singleEvent.substring(2, singleEvent.indexOf("WHERE")).replace("(", "")
                        .replace(")", "");
                guardOp = "WHERE";
                guardCondition = singleEvent.substring(singleEvent.indexOf("WHERE") + 5).replace("(", "")
                        .replace(")", "");
            } else if (singleEvent.contains("WHEN")) {
                eventCondition = singleEvent.substring(2, singleEvent.indexOf("WHEN")).replace("(", "")
                        .replace(")", "");
                //System.out.println(eventCondition);
                guardOp = "WHEN";
                guardCondition = singleEvent.substring(singleEvent.indexOf("WHEN") + 4).replace("(", "")
                        .replace(")", "");
                //System.out.println(guardCondition);
            } else if (singleEvent.contains("WHILE")) {
                eventCondition = singleEvent.substring(2, singleEvent.indexOf("WHILE")).replace("(", "")
                        .replace(")", "");

                guardOp = "WHILE";
                guardCondition = singleEvent.substring(singleEvent.indexOf("WHILE") + 5).replace("(", "")
                        .replace(")", "");
            } else {
                eventCondition = singleEvent.substring(2).replace("(", "")
                        .replace(")", "");
                //System.out.println(eventCondition);
            }

            EventItem subEvent = new EventItem();
            subEvent.setEventOperator(eventOp);
            subEvent.setGuardOperator(guardOp);
            try {
                subEvent.setEventCondition(conditionTableUtils.ConvertStringToTable(eventCondition));
                subEvent.setGuardCondition(conditionTableUtils.ConvertStringToTable(guardCondition));
            } catch (Exception e){
                throw new RuntimeException("condition");
            }
            eventResult.getEvents().add(subEvent);
        }
        return eventResult;
    }
}
