package com.nuaa.art.vrm.service.handler;

import com.nuaa.art.vrm.model.EventTable;

public interface EventTableHandler {
    /**
     * 事件表转换为表达式字符串
     *
     * @param eventTable 条件表
     * @return {@link String}
     */
    String ConvertTableToString(EventTable eventTable);

    /**
     * 将表达式字符串转换成事件表
     *
     * @param event 条件
     * @return {@link EventTable}
     */
    EventTable ConvertStringToTable(String event);
}
