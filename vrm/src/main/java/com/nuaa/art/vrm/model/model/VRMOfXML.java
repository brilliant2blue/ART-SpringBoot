package com.nuaa.art.vrm.model.model;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.MalformedURLException;

public class VRMOfXML {
    public Element typesNode;
    public Element constantsNode;
    public Element stateMachinesNode;
    public Element tablesNode;
    public Element inputsNode;

    public VRMOfXML(String modelFileName){
        SAXReader reader = new SAXReader();
        Document modelDoc = null;
        try {
            modelDoc = reader.read(new File(modelFileName));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Element root = modelDoc.getRootElement();
        typesNode = root.element("types");
        constantsNode = root.element("constants");
        stateMachinesNode = root.element("stateMachines");
        inputsNode = root.element("inputs");
        tablesNode = root.element("tables");
    }
}
