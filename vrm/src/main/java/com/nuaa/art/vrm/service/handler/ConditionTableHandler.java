package com.nuaa.art.vrm.service.handler;

import com.nuaa.art.vrm.model.ConditionTable;

/**
 * 条件表处理
 *
 * @author konsin
 * @date 2023/07/02
 */
public interface ConditionTableHandler {

    /**
     * 条件AndOr表转换为表达式字符串
     *
     * @param conditionTable 条件表
     * @return {@link String}
     */
    String ConvertTableToString(ConditionTable conditionTable);

    /**
     * 将表达式字符串转换成条件表
     *
     * @param condition 条件
     * @return {@link ConditionTable}
     */
    ConditionTable ConvertStringToTable(String condition);

}
