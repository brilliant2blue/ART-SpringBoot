package com.nuaa.art.vrm.common.utils;

import com.nuaa.art.common.utils.ListUtils;
import com.nuaa.art.vrm.model.hvrm.ModuleTree;
import com.nuaa.art.vrm.model.hvrm.TableOfModule;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VrmModuleXml extends VrmXml{
    /**
     * 输入转XML
     *
     * @param iv
     * @return {@link Element}
     */

    public Element Input(VariableWithPort iv) {
        Element input = super.Input(iv);
        Element inPort = input.addElement("inPort");
        inPort.setText( ListUtils.NumArrayToString(iv.getInPort()));
        return input;
    }

    @Override
    public VariableWithPort Input(Element inputEle) {
        VariableWithPort iv = new VariableWithPort(super.Input(inputEle));
        String ip = inputEle.elementText("inPort");
        iv.setInPort((ArrayList<Integer>) ListUtils.StringToNumArray(ip));
        return iv;
    }

    /**
     * 中间变量转XML
     *
     * @param tv
     * @return {@link Element}
     */
    public Element Term(VariableWithPort tv) {
        Element term =  super.Term(tv);
        term.addElement("inPort").setText(ListUtils.NumArrayToString(tv.getInPort()));
        term.addElement("outPort").setText( ListUtils.NumArrayToString(tv.getOutPort()));
        return term;
    }

    @Override
    public VariableWithPort Term(Element termEle) {
        VariableWithPort tv = new VariableWithPort( super.Term(termEle));
        String ip = termEle.elementText("inPort");
        tv.setInPort((ArrayList<Integer>) ListUtils.StringToNumArray(ip));
        String op = termEle.elementText("outPort");
        tv.setOutPort((ArrayList<Integer>) ListUtils.StringToNumArray(op));
        return tv;
    }

    /**
     * 输出变量转XML
     *
     * @param ov
     * @return {@link Element}
     */
    public Element Output(VariableWithPort ov) {
        Element output =  super.Output(ov);
        output.addElement("inPort").setText( ListUtils.NumArrayToString(ov.getInPort()));
        output.addElement("outPort").setText( ListUtils.NumArrayToString(ov.getOutPort()));
        return output;
    }

    @Override
    public VariableWithPort Output(Element outputEle) {
        VariableWithPort ov = new VariableWithPort( super.Output(outputEle));
        String ip = outputEle.elementText("inPort");
        ov.setInPort((ArrayList<Integer>) ListUtils.StringToNumArray(ip));
        String op = outputEle.elementText("outPort");
        ov.setOutPort((ArrayList<Integer>) ListUtils.StringToNumArray(op));
        return ov;
    }

    /**
     * 条件表xml
     *
     * @param con 反对
     * @return {@link Element}
     */
    public Element Condition(TableOfModule con) {
        Element condition = super.Condition(con);
        condition.addAttribute("moduleId", con.getModuleId()+"");
        condition.addAttribute("module", con.getModule()+"");
        return condition;
    }

    @Override
    public TableOfModule Condition(Element table) {
        TableOfModule condition = new TableOfModule(super.Condition(table));
        String mid = table.attributeValue("moduleId");
        condition.setModuleId(mid.isBlank()? 0: Integer.valueOf(mid));
        condition.setModule(table.attributeValue("module"));
        return condition;
    }

    /**
     * 事件表xml
     *
     * @param ent 事件表
     * @return {@link Element}
     */
    public Element Event(TableOfModule ent) {
        Element event = super.Event(ent);
        event.addAttribute("moduleId", ent.getModuleId()+"");
        event.addAttribute("module", ent.getModule());
        return event;
    }

    @Override
    public TableOfModule Event(Element table) {
        TableOfModule event = new TableOfModule(super.Event(table));
        String mid = table.attributeValue("moduleId");
        event.setModuleId(mid.isBlank()? 0: Integer.valueOf(mid));
        event.setModule(table.attributeValue("module"));
        return event;
    }

    public Element Module(ModuleTree module){
        System.out.println(module.toString());
        Element node = DocumentHelper.createElement("module");
        node.addAttribute("id", module.getId()+"");
        node.addElement("name").setText(module.getName());
        node.addElement("parentId").setText(module.getParentId()+"");
        ArrayList<ModuleTree> children =  module.getChildren();
        if(children!=null && !children.isEmpty()){
            Element sub = node.addElement("children");
            for (ModuleTree child: children) {
                sub.add(Module(child));
            }
        }
        return node;
    }

    public ModuleTree Module(Element node){
        ModuleTree module = new ModuleTree();
        module.setId(Integer.valueOf(node.attributeValue("id")));
        module.setName(node.elementText("name"));
        String pid = node.elementText("parentId");
        module.setParentId(pid.isBlank()? 0 : Integer.valueOf(pid));
        module.setSystemId(null);
        Element children = node.element("children");
        if(children != null){
            for(Element m: (List<Element>)children.elements("module")){
                module.getChildren().add(Module(m));
            }
        }
        return module;
    }

    public ModuleTree Module(Element node, Integer systemId){
        ModuleTree module = new ModuleTree();
        module.setId(Integer.valueOf(node.attributeValue("id")));
        module.setName(node.elementText("name"));
        String pid = node.elementText("parentId");
        module.setParentId(pid.isBlank()? 0 : Integer.valueOf(pid));
        module.setSystemId(systemId);
        Element children = node.element("children");
        if(children != null){
            for(Element m: (List<Element>)children.elements("module")){
                module.getChildren().add(Module(m, systemId));
            }
        }
        return module;
    }

}
