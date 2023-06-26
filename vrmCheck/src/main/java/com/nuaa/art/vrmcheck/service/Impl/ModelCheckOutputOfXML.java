package com.nuaa.art.vrmcheck.service.Impl;

import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.model.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.model.VRMOfXML;
import com.nuaa.art.vrmcheck.service.ModelCheckOutputHandler;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

@Service
public class ModelCheckOutputOfXML implements ModelCheckOutputHandler   {
    /**
     * 检查条件表输出完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkOutputIntegrityOfCondition(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        Iterator tableIterator = vrmModel.tablesNode.elementIterator();
        while (tableIterator.hasNext()) {
            Element table = (Element) tableIterator.next();
            if(table.element("conditionTable") != null){
                checkOutputIntegrity(table,errorReporter,"conditionTable");
            }
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
    public boolean checkOutputIntegrityOfEvent(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        Iterator tableIterator = vrmModel.tablesNode.elementIterator();
        while (tableIterator.hasNext()) {
            Element table = (Element) tableIterator.next();
            if(table.element("eventTable") != null){
                checkOutputIntegrity(table,errorReporter,"eventTable");
            }
        }
        return errorReporter.isOutputIntegrityRight();
    }

    void checkOutputIntegrity(Element table, CheckErrorReporter errorReporter, String type){
        String range = table.elementText("range");
        String[] ranges = range.split(",");
        ArrayList<String> formalRanges = new ArrayList<String>();
        ArrayList<String> consideredValues = new ArrayList<String>();
        ArrayList<String> informalValues = new ArrayList<String>();
        ArrayList<String> inconsideredValues = new ArrayList<String>();
        boolean isValueInconsidered = false;
        boolean isValueInformal = false;
        boolean isFourthRight = true;
        for (int i = 0; i < ranges.length; i++) {
            String singleRange = ranges[i];
            if (singleRange.contains("=")) {
                singleRange = singleRange.substring(0, singleRange.indexOf("="));
            }
            ranges[i] = singleRange;
            formalRanges.add(singleRange);
        }

            Iterator rowIterator = table.element(type).elementIterator();
            while (rowIterator.hasNext()) {
                Element row = (Element) rowIterator.next();
                String behavior = row.elementText("assignment");
                if (!consideredValues.contains(behavior))
                    consideredValues.add(behavior);
            }

        for (String formalRange : formalRanges) {
            if (!consideredValues.contains(formalRange)) {
                isValueInconsidered = true;
                inconsideredValues.add(formalRange);
            }
        }
        for (String consideredValue : consideredValues) {
            if (!formalRanges.contains(consideredValue)) {
                isValueInformal = true;
                informalValues.add(consideredValue);
            }
        }
        if (isValueInconsidered == true) {
            OutputErrorRefresh(errorReporter);
            String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：该表格变量值域中的值";
            for (String inconsideredValue : inconsideredValues) {
                outputString += inconsideredValue + "、";
            }
            outputString = outputString.substring(0, outputString.length() - 1);
            outputString += "未在表中任何一行取得";
            if(type.equals("eventTable"))
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityEventValueMiss,table.attributeValue("name"), outputString));
            else
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityConditionValueMiss,table.attributeValue("name"), outputString));
        }
        if (isValueInformal == true) {
            OutputErrorRefresh(errorReporter);
            String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：该表格的输出值";
            for (String informalValue : informalValues) {
                outputString += informalValue + "、";
            }
            outputString = outputString.substring(0, outputString.length() - 1);
            outputString += "不属于该变量的值域";
            if(type.equals("eventTable"))
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityEventOutRange,table.attributeValue("name"), outputString));
            else
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.OutputIntegrityConditionOutRange,table.attributeValue("name"), outputString));
        }
    }

    void OutputErrorRefresh(CheckErrorReporter errorReporter){
        errorReporter.setOutputIntegrityRight(false);
        errorReporter.addErrorCount();
    }
}
