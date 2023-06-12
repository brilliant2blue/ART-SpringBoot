package com.nuaa.art.vrm.controller;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.model.ConditionTable;
import com.nuaa.art.vrm.model.EventTable;
import com.nuaa.art.vrm.service.handler.ConditionTableHandler;
import com.nuaa.art.vrm.service.handler.EventTableHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 表格控制层
 *
 * @author konsin
 * @date 2023/07/07
 */
@RestController
@Tag(name = "表格形式与字符串互相转化的接口")
public class TableConvertController {
    @Resource(name = "conditionForTable")
    ConditionTableHandler conditionTableHandler;
    @PostMapping("tool/condition/table")
    public String tcs(@RequestBody ConditionTable c){
        String res =  conditionTableHandler.ConvertTableToString(c);
        LogUtils.info("condition:" + res);;
        return res;
    }

    @PostMapping("tool/condition/string")
    public ConditionTable tsc(@Parameter(required = true)@RequestBody String s){
        LogUtils.info("condition:" + s);
        return conditionTableHandler.ConvertStringToTable(s);
    }

    @Resource
    EventTableHandler eventTableHandler;
    @PostMapping("tool/event/table")
    public String tes(@RequestBody EventTable c){
        String res =  eventTableHandler.ConvertTableToString(c);
        LogUtils.info("event:" + res);
        return res;
    }

    @PostMapping("tool/event/string")
    public EventTable tse(@Parameter(required = true)@RequestBody String s){
        LogUtils.info("event:" + s);
        return eventTableHandler.ConvertStringToTable(s);
    }
}
