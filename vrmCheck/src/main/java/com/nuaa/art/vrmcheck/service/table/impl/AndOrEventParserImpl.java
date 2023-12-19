package com.nuaa.art.vrmcheck.service.table.impl;

import com.nuaa.art.vrm.common.utils.ConditionTableUtils;
import com.nuaa.art.vrm.common.utils.DataTypeUtils;
import com.nuaa.art.vrm.common.utils.EventTableUtils;
import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.model.ConditionItem;
import com.nuaa.art.vrm.model.EventItem;
import com.nuaa.art.vrm.model.EventTable;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import com.nuaa.art.vrm.model.vrm.TableRow;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.model.table.*;
import com.nuaa.art.vrmcheck.service.table.ConditionParser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AndOrEventParserImpl {

    public AndOrEventsInformation emptyEventsInformationFactory() {
        return new AndOrEventsInformation();
    }

    @Resource
    EventTableUtils tableUtils;

    @Resource
    ConditionTableUtils conditionUtils;

    @Resource
    ConditionParser conditionParser;

    @Resource
    DataTypeUtils typeUtils;

    // 不仅可以设置离散型，也应当设置连续型， 连续型按有赋值的进行设置
    // todo 获取每行的赋值，如果后续支持表达式的处理，需要在此进行逻辑修改
    public void setParentRangeAndValueOfEachRow(VRM vrmModel, TableOfVRM table, AndOrEventsInformation ei){
        Type thisType = typeUtils.FindVariableType(table.getRelateVar(), vrmModel.getTypes());
        // 获取每行的赋值
        ei.assignmentForEachRow = (ArrayList<String>) table.getRows().stream().map(TableRow::getAssignment).collect(Collectors.toList());
        if(typeUtils.WhetherContinuousRange(thisType)){
            ei.outputRanges = (ArrayList<String>) ei.assignmentForEachRow.clone();
        } else {
            ei.outputRanges = (ArrayList<String>) typeUtils.GetRangeList(thisType);
        }
        // 获取每行的赋值
        ei.assignmentForEachRow = (ArrayList<String>) table.getRows().stream().map(TableRow::getAssignment).collect(Collectors.toList());
    }

    public void setParentRangeAndValueOfEachRow(VRM vrmModel, ModeClassOfVRM MC, List<TableRow> tableRows, AndOrEventsInformation ei){

        ei.outputRanges = (ArrayList<String>) MC.getModes().stream().map(Mode::getModeName).collect(Collectors.toList());
        // 获取每行的赋值
        ei.assignmentForEachRow = (ArrayList<String>) tableRows.stream().map(TableRow::getAssignment).collect(Collectors.toList());
    }

    public void praserInformationInTables(VRM vrm, List<TableRow> tableRows, AndOrEventsInformation ei) {
        for(TableRow row : tableRows) { //获取事件行， 是一个OR事件
            EventTable thisEvent = tableUtils.ConvertStringToTable(row.getDetails()); // 处理为表格形式
            ArrayList<CoreEvent> coreEventTrees =
                    praserCoreEventToTreeAndGetKeyValueAndGetCriticalVariable(vrm, thisEvent.getEvents(),ei);
            ArrayList<ArrayList<CoreEvent>> orTree = praserOrTree(thisEvent, coreEventTrees);
            ei.nuclearTreeForEachRow.add(orTree);
        }

        // 将收集的每个关键连续变量的关键值从小到大排序。
        ei.criticalVariables.sortContinualValues();
        //值域离散化
        ei.variableRanges = ei.criticalVariables.rangeDiscretization();

    }

    ArrayList<ArrayList<CoreEvent>> praserOrTree(EventTable eventTable, ArrayList<CoreEvent> coreEventTrees){
        ArrayList<ArrayList<CoreEvent>> orTree = new ArrayList<>();
        ArrayList<EventItem> orignCoreEvents = eventTable.getEvents();
        ArrayList<ArrayList<String>> orRelation = eventTable.getOrList(); // 每个orlist是每个原子条件在or树中的取值正反情况
        for (int i = 0; i < eventTable.getOrNum(); i++) { // 遍历一列orList，这列orList形成一组and条件
            ArrayList<CoreEvent> andTree = new ArrayList<>();
            for(int j = 0; j < eventTable.getAndNum(); j++) {
                String symbol = orRelation.get(j).get(i); //获取or类型
                var thisTree = coreEventTrees.get(j);
                if(symbol.equals("T")){
                    if(orignCoreEvents.get(j).whetherEmpty()){
                        CoreEvent ce = new CoreEvent();
                        ce.setTrue();
                        andTree.add(ce);
                    } else {
                        if(thisTree.eventOperator.equals("@C")){ //处理@C类型的核事件
                            CoreEvent ce1 = new CoreEvent("@T",
                                    thisTree.eventCondition, thisTree.guardOperator,  thisTree.guardCondition);
                            andTree.add(ce1);
                            CoreEvent ce2 = new CoreEvent("@F",
                                    thisTree.eventCondition, thisTree.guardOperator,  thisTree.guardCondition);
                            andTree.add(ce2);
                        } else {
                            CoreEvent ce = new CoreEvent(thisTree.eventOperator,
                                    thisTree.eventCondition, thisTree.guardOperator,  thisTree.guardCondition);
                            andTree.add(ce);
                        }

                    }
                    //todo 在建模时未提供 否事件的建模方式，故暂不分析
//                } else if (symbol.equals("F")) {
//                    if(orignCoreEvents.get(j).whetherEmpty()){
//                        CoreEvent ce = new CoreEvent();
//                        ce.setFalse();
//                        andTree.add(ce);
//                    } else { //需要解析获取关键变量，关键值。
//                        CoreEvent ce = new CoreEvent(orignCoreEvents.get(j));
//                        andTree.add(ce);
//                    }
                } else if (symbol.equals(".")) { // 整个事件为default的情况
                    if(i == 0 && j == 0 && orignCoreEvents.get(j).whetherEmpty()){
                        CoreEvent ce = new CoreEvent();
                        ce.setDefault();
                        andTree.add(ce);
                        break;
                    }
                }
            }
            orTree.add(andTree);
        }

        return orTree;
    }

    /**
     * 将核心事件分解为树，并获取关键值和关键变量
     *
     * @param vrm        模型
     * @param eventItems 事件项
     * @param ei         事件信息记录
     */
    ArrayList<CoreEvent> praserCoreEventToTreeAndGetKeyValueAndGetCriticalVariable(VRM vrm, ArrayList<EventItem> eventItems, AndOrEventsInformation ei){
        ArrayList<CoreEvent> coreEventsTrees = new ArrayList<>();
        AndOrConditionsInformation cie = conditionParser.emptyInformationFactory();
        // 生成语法树
        for(EventItem event: eventItems){
            CoreEvent coreEvent = new CoreEvent();
            coreEvent.eventOperator= event.getEventOperator();
            coreEvent.eventCondition = conditionParser.praserConditionTree(event.getEventCondition());
            if(event.getGuardOperator().isBlank()){
                coreEvent.guardOperator = "WHEN";
                coreEvent.guardCondition = conditionParser.praserConditionTree(conditionUtils.ConvertStringToTable("true"));
            } else {
                coreEvent.guardOperator = event.getGuardOperator();
                coreEvent.guardCondition = conditionParser.praserConditionTree(event.getGuardCondition());
            }

            for(ConditionItem c: event.getEventCondition().getConditionItems())
                conditionParser.findCriticalVariableAndKeyValues(vrm,cie, c);
            for(ConditionItem c: event.getGuardCondition().getConditionItems())
                conditionParser.findCriticalVariableAndKeyValues(vrm,cie, c);

            coreEventsTrees.add(coreEvent);
        }
        // 将分析的条件中的属性并入事件属性中
        ei.criticalVariables = ei.criticalVariables.merge(cie.criticalVariables);

        return coreEventsTrees;
    }


}
