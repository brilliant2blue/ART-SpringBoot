package com.nuaa.art.vrm.model;

import com.nuaa.art.common.utils.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
//todo 待完成模型 各个组件 xml相互转换的功能
public class VRMOfXML {
    public Element typesNode;
    public Element constantsNode;
    public Element stateMachinesNode;
    public Element tablesNode;
    public Element inputsNode;

    public VRMOfXML(Document modelDoc){
            Element root = modelDoc.getRootElement();
            typesNode = root.element("types");
            constantsNode = root.element("constants");
            stateMachinesNode = root.element("stateMachines");
            inputsNode = root.element("inputs");
            tablesNode = root.element("tables");

    }
}
