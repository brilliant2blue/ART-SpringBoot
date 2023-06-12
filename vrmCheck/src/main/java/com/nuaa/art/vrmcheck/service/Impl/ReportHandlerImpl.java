package com.nuaa.art.vrmcheck.service.impl;

import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.vrm.entity.SystemProject;
import com.nuaa.art.vrm.service.dao.SystemProjectService;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.model.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrmcheck.service.ReportHandler;
import jakarta.annotation.Resource;
import org.apache.juli.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReportHandlerImpl implements ReportHandler {
    /**
     * 读取检查报告
     *
     * @param systemId 系统标识
     * @return {@link CheckErrorReporter}
     */
    @Override
    public CheckErrorReporter readCheckReport(int systemId) {
        String url = PathUtils.DefaultPath();
        String fileName = "";
        SystemProject systemProject = service.getSystemProjectById(systemId);
        if (null != systemProject) {
            fileName = url + systemProject.getSystemName() + "CheckReport.xml";
        } else {
            fileName = url + "CheckReport.xml";
        }
        try {


        Document reportdoc = FileUtils.readXML(fileName);
        Element report = reportdoc.getRootElement();
        CheckErrorReporter reporter = new CheckErrorReporter();

        reporter.setModelName(report.element("lastSaved").elementText("name"));

        reporter.setErrorCount(Integer.valueOf(report.elementText("errorCount")));

        Iterator errors = report.element("errorList").elementIterator();
        while (errors.hasNext()) {
            Element error = (Element) errors.next();
            reporter.addErrorList(checkErrorInfoFromXml(error));
        }
        return reporter;
        } catch (Exception e){
            LogUtils.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    /**
     * 导出检查报告
     *
     * @param reporter
     * @param fileUrl
     * @return boolean
     */
    @Override
    public boolean saveCheckReport(CheckErrorReporter reporter, String fileUrl) {
        Document root = DocumentHelper.createDocument();
        Element report = root.addElement("CheckReport");
        Element lastSaved = report.addElement("lastSaved");
        Element modelNamElement = lastSaved.addElement("name");

        modelNamElement.addText(reporter.getModelName());

        Element updateDateElement = lastSaved.addElement("date");
        SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateString = sdfDateFormat.format(new Date());
        updateDateElement.addText(dateString);

        Element errorCount = report.addElement("errorCount");
        errorCount.addText(reporter.getErrorCount().toString());

        boolean isBasicRight = true;

        boolean isInputIntegrityRight = true;

        boolean isConditionRight = true;
        boolean isEventRight = true;

        boolean isModeConvertRight = true;



        Element errorList = report.addElement("errorList");
        for(CheckErrorInfo e: reporter.getErrorList()){
           errorList.add(checkErrorInfoToXml(e));
        }

        if(FileUtils.saveXML(root,fileUrl)){
            return true;
        } else {
            return false;
        }


    }

    @Resource
    SystemProjectService service;

    /**
     * 导出检查报告
     *
     * @param systemId
     * @param fileUrl
     * @return boolean
     */
    @Override
    public boolean exportCheckReport(int systemId, String fileUrl) {
        String url = PathUtils.DefaultPath();
        String fileName = "";
        SystemProject systemProject = service.getSystemProjectById(systemId);
        if (null != systemProject) {
            fileName = url + systemProject.getSystemName() + "CheckReport.xml";
        } else {
            fileName = url + "CheckReport.xml";
        }

        try(FileChannel in = new FileInputStream(fileUrl).getChannel();
            FileChannel out = new FileOutputStream(fileName, true).getChannel()){
            out.transferFrom(in, 0, in.size());
            return true;
        } catch (IOException e) {
            LogUtils.error(e.getMessage());
            return false;
        }
    }

    public Element checkErrorInfoToXml(CheckErrorInfo e){
        Element error = DocumentHelper.createElement("error");
        error.addElement("id").addText(e.getId().toString());
        error.addElement("type").addText(e.getErrorType());
        error.addElement("typeId").addText(String.valueOf(e.getErrorTypeId()));
        error.addElement("relateType").addText(e.getRelateType());
        error.addElement("relateItem").addText(e.getRelateName());

        String reqId = "";
        for (Integer id: e.getRequirementId()) {
            reqId += id.toString() + ",";
        }

        error.addElement("relateReq").addText(reqId);

        error.addElement("modelClass").addText(e.getModeClass());
        error.addElement("details").addText(e.getDetails());
        return error;
    }

    public CheckErrorInfo checkErrorInfoFromXml(Element error){
        List<Integer> reqId = new ArrayList<>();
        //System.out.println(Collections.singletonList(error.element("relateReq").getText()));
        for (String s : Collections.singletonList(error.element("relateReq").getText())){
            if(s!=""){
                reqId.add(Integer.valueOf(s));
            }
        }
        return new CheckErrorInfo(
                Integer.valueOf(error.elementText("id")),
                Integer.valueOf(error.elementText("typeId")),
                error.element("type").getText(),
                error.element("relateType").getText(),
                error.element("relateItem").getText(),
                reqId,
                error.element("modelClass").getText(),
                error.element("details").getText()
        );

    }
}
