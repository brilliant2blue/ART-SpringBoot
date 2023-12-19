package com.nuaa.art.vrm.controller;

import com.nuaa.art.common.model.HttpResult;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.entity.StandardRequirement;
import com.nuaa.art.vrm.model.ConditionTable;
import com.nuaa.art.vrm.model.EventTable;
import com.nuaa.art.vrm.common.utils.ConditionTableUtils;
import com.nuaa.art.vrm.common.utils.EventTableUtils;
import com.nuaa.art.vrm.service.handler.StandardRequirementHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 各种转换工具控制层
 *
 * @author konsin
 * @date 2023/07/07
 */
@RestController
@Tag(name = "工具API接口")
public class ToolController {
    @Resource(name = "conditionForTable")
    ConditionTableUtils conditionTableUtils;
    @Resource
    StandardRequirementHandler content;
    @PostMapping("tool/condition/table")
    @Operation(summary = "条件表格转为字符串")
    public HttpResult<String> tcs(@RequestBody ConditionTable c){
        String res =  conditionTableUtils.ConvertTableToString(c);
        LogUtils.info("condition:" + res);
        return HttpResult.success(res);
    }

    @PostMapping("tool/condition/string")
    @Operation(summary = "字符串转为条件表格")
    public HttpResult<ConditionTable> tsc(@Parameter(required = true)@RequestBody String s){
        LogUtils.info("condition:" + s);
        return HttpResult.success(conditionTableUtils.ConvertStringToTable(s));
    }

    @Resource
    EventTableUtils eventTableUtils;
    @PostMapping("tool/event/table")
    @Operation(summary = "事件表格转为字符串")
    public HttpResult<String> tes(@RequestBody EventTable c){
        String res =  eventTableUtils.ConvertTableToString(c);
        LogUtils.info("event:" + res);
        return HttpResult.success(res);
    }

    @PostMapping("tool/event/string")
    @Operation(summary = "字符串转为事件表格")
    public HttpResult<EventTable> tse(@Parameter(required = true)@RequestBody String s){
        LogUtils.info("event:" + s);
        return HttpResult.success(eventTableUtils.ConvertStringToTable(s));
    }

    // 需求规范化模板
    @Operation(summary = "根据模板ID生成规范化需求语句")
    @PostMapping("tool/sreq/content")
    public HttpResult<String> reqStr(@RequestBody StandardRequirement s, Integer templeteId) {
        return HttpResult.success(content.createContent(s,templeteId));
    }
}
