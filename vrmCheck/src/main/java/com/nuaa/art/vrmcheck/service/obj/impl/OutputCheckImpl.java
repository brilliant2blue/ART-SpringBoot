package com.nuaa.art.vrmcheck.service.obj.impl;

import com.nuaa.art.vrm.common.BasicDataType;
import com.nuaa.art.vrm.common.utils.DataTypeUtils;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.model.*;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.model.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrmcheck.service.obj.OutputCheck;
import jakarta.annotation.Resource;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 输出完整性实现
 *
 * @author konsin
 * @date 2023/09/12
 */
@Service
public class OutputCheckImpl implements OutputCheck {
    /**
     * 检查条件表输出完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkOutputIntegrityOfCondition(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
        for(TableOfVRM table: vrmModel.getConditions()) {
            checkOutputIntegrity(vrmModel, table,false, errorReporter);
        }
        return errorReporter.isOutputIntegrityRight();
    }

    /**
     * 检查事件表输出完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkOutputIntegrityOfEvent(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
        for(TableOfVRM table: vrmModel.getEvents()) {
            checkOutputIntegrity(vrmModel, table,true, errorReporter);
        }
        return errorReporter.isOutputIntegrityRight();
    }

    @Resource
    DataTypeUtils dataTypeUtils;
    /**
     * 检查输出完整性
     * 离散型变量要保证所有可取值要能取到，且不可以越界
     * 连续型变量要保证不能越界。
     *
     * @param vrmModel      vrm模型
     * @param table         表格
     * @param isEvent       是否是事件
     * @param errorReporter 错误报告器
     */
    void checkOutputIntegrity(VariableRealationModel vrmModel, TableOfVRM table, Boolean isEvent, CheckErrorReporter errorReporter){
        Type type = dataTypeUtils.FindVariableType(table.getRelateVar(), vrmModel.getTypes());
        //System.out.println(type.toString());
        boolean isContinues = dataTypeUtils.WhetherContinuousRange(type);
        // 由于字符串输出类型没有值域限制，故不判断。
        boolean isStringValue = type.getDataType().equalsIgnoreCase(BasicDataType.StringType.getTypeName());
        boolean isValueInconsidered = false;
        boolean isValueInformal = false;
        if(isStringValue){
            return;
        } else if(isContinues){
            List<String> informalValues = new ArrayList<>();
            for(TableRow row : table.getRows()) { // 遍历获取可输出的值
                String assignment = row.getAssignment();

                if (type.getDataType().equalsIgnoreCase(BasicDataType.IntegerType.getTypeName())) {
                    // 分离区间边界
                    List<Integer> ranges = dataTypeUtils.GetRangeList(type);
                    int asciiOfChar = Integer.valueOf(assignment);
                    if (asciiOfChar < ranges.get(0) || asciiOfChar > ranges.get(1))
                        informalValues.add(assignment);
                } else if (type.getDataType().equalsIgnoreCase(BasicDataType.DoubleType.getTypeName())) {
                    List<Double> ranges = dataTypeUtils.GetRangeList(type);
                    double vValue = Double.parseDouble(assignment);
                    if (vValue < ranges.get(0) || vValue > ranges.get(1))
                        informalValues.add(assignment);
                } else if (type.getDataType().equalsIgnoreCase(BasicDataType.FloatType.getTypeName())) {
                    List<Float> ranges = dataTypeUtils.GetRangeList(type);
                    double vValue = Float.parseFloat(assignment);
                    if (vValue < ranges.get(0) || vValue > ranges.get(1))
                        informalValues.add(assignment);
                }
            }
            isValueInformal = !informalValues.isEmpty();
            if (isValueInformal) {
                OutputErrorRefresh(errorReporter);
                String outputString = "错误定位：表格" + table.getName() + "\n错误内容：该表格的输出值";
                for (String informalValue : informalValues) {
                    outputString += informalValue + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "不属于该变量的值域";
                if(isEvent)
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityEventOutRange,table.getName(), outputString));
                else
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityConditionOutRange,table.getName(), outputString));
            }
        } else {
            List<String> ranges = dataTypeUtils.GetRangeList(type);
            List<String> inconsideredValues = new ArrayList<>();
            List<String> consideredValues = new ArrayList<>();
            List<String> informalValues = new ArrayList<>();
            for(TableRow row : table.getRows()) { // 遍历获取可输出的值
                String assignment = row.getAssignment();
                if (!consideredValues.contains(assignment))
                    consideredValues.add(assignment);
            }
            inconsideredValues = ranges.stream().filter(item -> !consideredValues.contains(item)).collect(Collectors.toList());
            isValueInconsidered = !inconsideredValues.isEmpty();
            // 检查是否再值域内
            informalValues = consideredValues.stream().filter(item -> !ranges.contains(item)).collect(Collectors.toList());
            isValueInformal = !informalValues.isEmpty();

            if (isValueInconsidered) {
                OutputErrorRefresh(errorReporter);
                String outputString = "错误定位：表格" + table.getName() + "\n错误内容：该表格变量值域中的值";
                for (String inconsideredValue : inconsideredValues) {
                    outputString += inconsideredValue + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "未在表中任何一行取得";
                if(isEvent)
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityEventValueMiss,table.getName(), outputString));
                else
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityConditionValueMiss,table.getName(), outputString));
            }
            if (isValueInformal == true) {
                OutputErrorRefresh(errorReporter);
                String outputString = "错误定位：表格" + table.getName() + "\n错误内容：该表格的输出值";
                for (String informalValue : informalValues) {
                    outputString += informalValue + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "不属于该变量的值域";
                if(isEvent)
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityEventOutRange,table.getName(), outputString));
                else
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityConditionOutRange,table.getName(), outputString));
            }
        }

    }

    void OutputErrorRefresh(CheckErrorReporter errorReporter){
        errorReporter.setOutputIntegrityRight(false);
        errorReporter.addErrorCount();
    }
}
