package com.nuaa.art.vrm.common.utils;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.model.EventItem;
import com.nuaa.art.vrm.model.EventTable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventTableUtils {
    @Resource
    ConditionTableUtils conditionTableUtils;

    /**
     * 将合法的事件表转换为表达式字符串
     * 永真则表有一个子事件和Or列，但是子事件为空，OR为 T
     * 假则有一个子事件和Or列，但是子事件为空，OR为 F
     * 其余情况返回空值，即默认
     *
     * @param eventTable 条件表
     * @return {@link String}
     */

    public String ConvertTableToString(EventTable eventTable) {
        if (eventTable.getAndNum() == 0) return "";
        if (eventTable.getAndNum() == 1 && eventTable.getOrNum() == 1 && eventTable.getEvents().get(0).whetherEmpty()){
            String OR = eventTable.getOrList().get(0).get(0);
            if(OR.equals(".")) return "default";
            else if(OR.equals("T")) return "true";
            else return "never";
        }
        String eventResult = "";

        ArrayList<String> headList = new ArrayList<String>();
        for (int i = 0; i < eventTable.getOrNum() ; i++) { //为每个or子条件创建条件头
            headList.add("{");
        }

        for (int i = 0; i < eventTable.getAndNum(); i++) { //循环读取每一行子事件
            String event = "";
            if(!eventTable.getEvents().get(i).getEventOperator().equals("")
                    && eventTable.getEvents().get(i).getEventCondition() != null) {
                event += eventTable.getEvents().get(i).getEventOperator() + '(';
                String events = conditionTableUtils.ConvertTableToString(eventTable.getEvents().get(i).getEventCondition());
                event += events;
                event += ')';
            }
            if(!eventTable.getEvents().get(i).getGuardOperator().equals("")
                    && eventTable.getEvents().get(i).getGuardCondition() != null) {
                event += eventTable.getEvents().get(i).getGuardOperator() + '(';
                String guards = conditionTableUtils.ConvertTableToString(eventTable.getEvents().get(i).getGuardCondition());
                event += guards;
                event += ')';
            }
            for (int j = 0; j < eventTable.getOrNum(); j++) {   //按列解析or列
                if (eventTable.orList.get(i).get(j).equals("T")) {
                    String string = headList.get(j);
                    if (!headList.get(j).equals("{")) {
                        string = string + "&&";
                    }
                    string += event;
                    headList.set(j, string);
                } else if (eventTable.orList.get(i).get(j).equals("F")) {
                    String string = headList.get(j);
                    if (!headList.get(j).equals("{")) {
                        string = string + "&&";
                    }
                    string += "!" + event;
                    headList.set(j, string);
                } else if (eventTable.orList.get(i).get(j).equals(".")) {

                }
            }
        }
        int emptyLines = 0;
        for (int i = 0; i < eventTable.getOrNum(); i++) {  //排除空结果
            if (!headList.get(i).equals("{")) {
                headList.set(i, headList.get(i) + "}");
            } else {
                headList.set(i, "");
                emptyLines++;
            }
        }

        if (emptyLines == eventTable.getOrNum()) { //设置了条件，但是解析后未填充条件，则认为是永真式
            eventResult = "true";
        } else {
            eventResult = headList.stream().filter(item -> !item.isBlank()).collect(Collectors.joining("||"));
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
        if(event.isBlank() || event.equalsIgnoreCase("default")){
            EventItem subEvent = new EventItem();
            eventResult.getEvents().add(subEvent);
            eventResult.setAndNum(1);
            eventResult.setOrNum(1);
            ArrayList<String> orList = new ArrayList<>();
            orList.add(".");
            eventResult.getOrList().add(orList);
            return eventResult;
        } else if(event.equalsIgnoreCase("true")){
            EventItem subEvent = new EventItem();
            eventResult.getEvents().add(subEvent);
            eventResult.setAndNum(1);
            eventResult.setOrNum(1);
            ArrayList<String> orList = new ArrayList<>();
            orList.add("T");
            eventResult.getOrList().add(orList);
            return eventResult;
        }else if(event.equalsIgnoreCase("never")){
            EventItem subEvent = new EventItem();
            eventResult.getEvents().add(subEvent);
            eventResult.setAndNum(1);
            eventResult.setOrNum(1);
            ArrayList<String> orList = new ArrayList<>();
            orList.add("F");
            eventResult.getOrList().add(orList);
            return eventResult;
        }
        ArrayList<String> subEvents = new ArrayList<String>(); //存储解析的子事件字符串
        String[] orEvents = event.split("}\\|\\|\\{");
        eventResult.setOrNum(orEvents.length);

        for (int col = 0; col < eventResult.getOrNum(); col++) {
            String s = orEvents[col];
            List<Integer> trueIntegers = new ArrayList<Integer>(); //暂存为真的子事件 行号
            List<Integer> falseIntegers = new ArrayList<Integer>(); //暂存为假的原子条件 行号
            String[] andContents = s.split("&&"); //划分合取式
            for (String s1 : andContents) {
                String subEvent = s1.replaceAll("\\{", "").replaceAll("}", "");
                boolean subEventTrue = true;
                if (subEvent.contains("!")) {
                    subEventTrue = false;
                    subEvent = subEvent.replaceAll("!", "");
                }
                if(!subEvents.contains(subEvent)){
                    ArrayList<String> orList = new ArrayList<>(eventResult.getOrNum()); //新建or行
                    for (int i = 0; i < eventResult.getOrNum(); i++) {    //初始化orList行
                        orList.add(".");
                    }
                    subEvents.add(subEvent);
                    eventResult.getEvents().add(psrserSubEvent(subEvent));
                    eventResult.getOrList().add(orList); //添加新的空or行
                    eventResult.addAndNum(); //变量数增加
                    if (!subEventTrue)
                        falseIntegers.add(eventResult.getAndNum());
                    else
                        trueIntegers.add(eventResult.getAndNum());
                } else {
                    if (!subEventTrue)
                        falseIntegers.add(subEvents.indexOf(subEvent) + 1);
                    else
                        trueIntegers.add(subEvents.indexOf(subEvent) + 1);
                }

                // 填充or列
                for (int i = 0; i < eventResult.getAndNum(); i++) {
                    for (Integer integer : trueIntegers) {
                        if (integer - 1 == i) {
                            eventResult.getOrList().get(i).set(col, "T");
                        }
                    }
                    for (Integer integer : falseIntegers) {
                        if (integer - 1 == i) {
                            eventResult.getOrList().get(i).set(col, "F");
                        }
                    }
                }
            }

        }
        return eventResult;
    }

    public EventItem psrserSubEvent(String singleEvent){
        String eventOp = "";
        String eventCondition = "";
        String guardOp = "";
        String guardCondition = "";

        System.out.println("完整事件："+singleEvent);

        if (singleEvent.contains("@T")) {
            eventOp = "@T";
        } else if (singleEvent.contains("@F")) {
            eventOp = "@F";
        } else if (singleEvent.contains("@C")) {
            eventOp = "@C";
        }
        int guardid = 2;
        if(eventOp.isBlank()) guardid = 0;
        if (singleEvent.contains("WHERE")) {
            eventCondition = singleEvent.substring(guardid, singleEvent.indexOf("WHERE")).replace("{", "")
                    .replace("}", "");
            guardOp = "WHERE";
            guardCondition = singleEvent.substring(singleEvent.indexOf("WHERE") + 5).replace("{", "")
                    .replace("}", "");
        } else if (singleEvent.contains("WHEN")) {
            eventCondition = singleEvent.substring(guardid, singleEvent.indexOf("WHEN")).replace("{", "")
                    .replace("}", "");
            //System.out.println(eventCondition);
            guardOp = "WHEN";
            guardCondition = singleEvent.substring(singleEvent.indexOf("WHEN") + 4).replace("{", "")
                    .replace("}", "");
            //System.out.println(guardCondition);
        } else if (singleEvent.contains("WHILE")) {
            eventCondition = singleEvent.substring(guardid, singleEvent.indexOf("WHILE")).replace("{", "")
                    .replace("}", "");

            guardOp = "WHILE";
            guardCondition = singleEvent.substring(singleEvent.indexOf("WHILE") + 5).replace("{", "")
                    .replace("}", "");
        } else {
            eventCondition = singleEvent.substring(guardid).replace("{", "")
                    .replace("}", "");
            //System.out.println(eventCondition);
        }

        EventItem subEvent = new EventItem();
        subEvent.setEventOperator(eventOp);
        subEvent.setGuardOperator(guardOp);
        try {
            // 去除首尾的小括号
            if (eventCondition.charAt(0) == '(') eventCondition = eventCondition.substring(1);
            if (eventCondition.charAt(eventCondition.length() - 1) == ')')
                eventCondition = eventCondition.substring(0, eventCondition.length() - 1);
            subEvent.setEventCondition(conditionTableUtils.ConvertStringToTable(eventCondition));
            if(!guardOp.isBlank()){ // 为保持数据结构完整，即便guard为空也要填充永真
                // 去除首尾的小括号
                if(guardCondition.charAt(0) == '(') guardCondition = guardCondition.substring(1);
                if(guardCondition.charAt(guardCondition.length()-1) == ')') guardCondition =guardCondition.substring(0, guardCondition.length()-1);
                subEvent.setGuardCondition(conditionTableUtils.ConvertStringToTable(guardCondition));
            } else {
                subEvent.setGuardCondition(conditionTableUtils.ConvertStringToTable("true"));
            }

            return subEvent;
        } catch (Exception e){
            throw new RuntimeException("condition");
        }
    }
}
