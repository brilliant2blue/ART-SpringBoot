package com.nuaa.art.vrm.common.utils;

import com.nuaa.art.common.utils.ListUtils;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.entity.Module;
import org.apache.xmlbeans.xml.stream.events.ElementTypeNames;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

@Component
public class EntityXmlConvert {
    public <T> String  notNullString(T value){
        if(value==null) return "";
        else return String.valueOf(value);
    }

    public Integer  toInt(String value){
        if(value==null||value.isBlank()||value.equals("null")) return null;
        else return Integer.valueOf(value);
    }

    public Element NReq(NaturalLanguageRequirement nreq){
        Element requirementNode= DocumentHelper.createElement("requirement");//添加节点requirement
        requirementNode.addAttribute("id", String.valueOf(nreq.getReqId()));
        Element reqContent=requirementNode.addElement("reqContent");
        Element reqExcelId=requirementNode.addElement("reqExcelId");
        Element reqModuleId=requirementNode.addElement("reqModuleId");
        reqContent.setText(nreq.getReqContent());
        //System.out.println(nreq.getReqContent());
        reqExcelId.setText(notNullString(nreq.getReqExcelId()));
        reqModuleId.setText(notNullString(nreq.getModuleId()));
        return requirementNode;
    }

    public NaturalLanguageRequirement NReq(Element node){
        NaturalLanguageRequirement requirement = new NaturalLanguageRequirement();
        requirement.setReqId( Integer.valueOf(node.attributeValue("id")));
        requirement.setSystemId(null);
        requirement.setReqExcelId(
                Integer.valueOf(node.elementText("reqExcelId")));

        String moduleId = node.elementText("reqModuleId");
        requirement.setModuleId(toInt(moduleId));

        requirement.setReqContent(node.elementText("reqContent"));
        return  requirement;
    }

    public NaturalLanguageRequirement NReq(Element node, Integer systemId){
        NaturalLanguageRequirement r = NReq(node);
        r.setSystemId(systemId);
        return r;
    }

    public Element Variable(ConceptLibrary variable){
        Element variableNode=DocumentHelper.createElement("variable");//添加节点variable
        variableNode.addAttribute("id", String.valueOf(variable.getConceptId()));//添加属性conceptId
        Element conceptName=variableNode.addElement("conceptName");
        Element conceptDataType=variableNode.addElement("conceptDataType");
        Element conceptRange=variableNode.addElement("conceptRange");
        Element conceptValue=variableNode.addElement("conceptValue");
        Element conceptAccuracy=variableNode.addElement("conceptAccuracy");
        Element conceptDependency=variableNode.addElement("conceptDependency");
        Element conceptType=variableNode.addElement("conceptType");
        Element conceptDescription=variableNode.addElement("conceptDescription");
        Element conceptRelateReq=variableNode.addElement("relateReq");
        conceptName.setText(variable.getConceptName());
        conceptDataType.setText(notNullString(variable.getConceptDatatype()));
        conceptRange.setText(notNullString(variable.getConceptRange()));
        conceptValue.setText(notNullString(variable.getConceptValue()));
        conceptAccuracy.setText(notNullString(variable.getConceptAccuracy()));
        conceptDependency.setText(notNullString(variable.getConceptDependencyModeClass()));
        conceptType.setText(notNullString(variable.getConceptType()));
        conceptDescription.setText(notNullString(variable.getConceptDescription()));
        conceptRelateReq.setText(notNullString(variable.getSourceReqId()));
        return variableNode;
    }

    public ConceptLibrary Variable(Element variableNode) {
        ConceptLibrary variable = new ConceptLibrary();
        variable.setConceptId(Integer.valueOf(variableNode.attributeValue("id")));
        variable.setSystemId(null);
        variable.setConceptName(variableNode.elementText("conceptName"));
        variable.setConceptDatatype(variableNode.elementText("conceptDataType"));
        variable.setConceptValue(variableNode.elementText("conceptValue"));
        variable.setConceptAccuracy(variableNode.elementText("conceptAccuracy"));
        variable.setConceptRange(variableNode.elementText("conceptRange"));
        variable.setConceptType(variableNode.elementText("conceptType"));
        variable.setConceptDependencyModeClass(
                variableNode.elementText("conceptDependency"));
        variable.setConceptDescription(variableNode.elementText("conceptDescription"));
       variable.setSourceReqId(variableNode.elementText("relateReq"));

        return variable;
    }

    public ConceptLibrary Variable(Element variableNode, Integer systemId) {
        ConceptLibrary variable = Variable(variableNode);
        variable.setSystemId(systemId);
        return variable;
    }

    public Element Type(Type type) {
        Element typeNode = DocumentHelper.createElement("type");//添加节点type
        Element typeName=typeNode.addElement("name");
        Element dataType=typeNode.addElement("baseType");
        Element typeRange=typeNode.addElement("range");
        Element typeAccuracy=typeNode.addElement("accuracy");
        typeName.setText(notNullString(type.getTypeName()));
        dataType.setText(notNullString(type.getDataType()));
        typeRange.setText(notNullString(type.getTypeRange()));
        typeAccuracy.setText(notNullString(type.getTypeAccuracy()));
        return typeNode;
    }

    public Type Type(Element typeEle) {
        Type t = new Type();
        t.setTypeId(null);
        t.setSystemId(null);
        t.setTypeName(typeEle.elementText("name"));
        t.setDataType(typeEle.elementText("baseType"));
        t.setTypeRange(typeEle.elementText("range"));
        t.setTypeAccuracy(typeEle.elementText("accuracy"));
        return t;
    }

    public Type Type(Element typeEle, Integer systemId) {
        Type t = Type(typeEle);
        t.setSystemId(systemId);
        return t;
    }

    /**
     * 专有名词转换为xml
     *
     * @param prop 道具
     * @return {@link Element}
     */
    public Element ProperNoun(ProperNoun prop) {
        //添加节点properNoun
        Element properNoun = DocumentHelper.createElement("properNoun");
        //添加properNoun属性
        Element name = properNoun.addElement("name");
        name.setText(notNullString(prop.getProperNounName()));

        Element description = properNoun.addElement("description");
        description.setText(notNullString(prop.getProperNounDescription()));

        return properNoun;
    }

    public ProperNoun ProperNoun(Element PropEle) {
        ProperNoun prop = new ProperNoun();
        prop.setProperNounId(null);
        prop.setProperNounName(PropEle.elementText("name"));
        prop.setProperNounDescription(PropEle.elementText("description"));
        prop.setSystemId(null);
        return prop;
    }

    public ProperNoun ProperNoun(Element PropEle, Integer systemId) {
        ProperNoun prop = ProperNoun(PropEle);
        prop.setSystemId(systemId);
        return prop;
    }

    /**
     * 模式集转换XML
     *
     * @param modeClass 模式类
     * @return {@link Element}
     */
    public Element ModeClass(ModeClass modeClass){
        Element modeClassNode=DocumentHelper.createElement("modeClass");//添加节点modeClass
        modeClassNode.addAttribute("id", notNullString(modeClass.getModeClassId()));//添加属性modeClassId
        Element modeClassName=modeClassNode.addElement("modeClassName");
        Element modeClassDescription=modeClassNode.addElement("modeClassDescription");
        modeClassName.setText(notNullString(modeClass.getModeClassName()));
        modeClassDescription.setText(modeClass.getModeClassDescription());
        Element parentMode = modeClassNode.addElement("parentMode");
        parentMode.addAttribute("modeClass", notNullString(modeClass.getParentName()));
        parentMode.addAttribute("mode", notNullString(modeClass.getParentModeName()));
        return modeClassNode;
    }

    public ModeClass ModeClass(Element modeClassNode){
        ModeClass modeClass = new ModeClass();
        modeClass.setModeClassId(Integer.valueOf(modeClassNode.attributeValue("id")));
        modeClass.setModeClassName(modeClassNode.elementText("modeClassName"));
        modeClass.setModeClassDescription(
                modeClassNode.elementText("modeClassDescription"));
        Element parent = modeClassNode.element("parentMode");
        modeClass.setParentName(parent.attributeValue("modeClass"));
        modeClass.setParentModeName(parent.attributeValue("mode"));
        return modeClass;
    }
    public ModeClass ModeClass(Element modeClassNode, Integer systemId){
        ModeClass modeClass = ModeClass(modeClassNode);
        modeClass.setSystemId(systemId);
        return modeClass;
    }

    /**
     * 模式转XML
     * dependId， systemId都为空置
     *
     * @param mode 模式
     * @return {@link Mode}
     */
    public Element Mode(Mode mode){
        Element modeNode=DocumentHelper.createElement("mode");//添加节点mode
        Element modeName=modeNode.addElement("modeName");
        Element initialStatus=modeNode.addElement("initialStatus");
        Element finalStatus=modeNode.addElement("finalStatus");
        Element value=modeNode.addElement("value");
        Element modeClassId=modeNode.addElement("modeClassId");
        Element modeClassName=modeNode.addElement("modeClassName");
        Element modeDescription=modeNode.addElement("modeDescription");
        modeName.setText(notNullString(mode.getModeName()));
        initialStatus.setText(notNullString(mode.getInitialStatus()));
        finalStatus.setText(notNullString(mode.getFinalStatus()));
        value.setText(notNullString(mode.getValue()));
        modeClassId.setText(notNullString(mode.getModeClassId()));
        modeClassName.setText(notNullString(mode.getModeClassName()));
        modeDescription.setText(notNullString(mode.getModeDescription()));
        return modeNode;
    }

    /**
     * XML转模式
     * dependId， systemId都为空置
     *
     * @param modeNode 模式节点
     * @return {@link Mode}
     */
    public Mode  Mode(Element modeNode){
        Mode mode = new Mode();
        mode.setModeName(modeNode.elementText("modeName"));
        if (modeNode.elementText("initialStatus") != "")
            mode.setInitialStatus(
                    Integer.valueOf(modeNode.elementText("initialStatus")));
        else
            mode.setInitialStatus(0);
        if (modeNode.elementText("finalStatus") != "")
            mode.setFinalStatus(Integer.valueOf(modeNode.elementText("finalStatus")));
        else
            mode.setFinalStatus(0);
        mode.setValue(Integer.valueOf(modeNode.elementText("value")));
        mode.setModeClassName(modeNode.elementText("modeClassName"));
        mode.setModeClassId(null);
        mode.setModeDescription(modeNode.elementText("modeDescription"));
        return mode;
    }

    public Mode  Mode(Element modeNode, Integer systemId){
        Mode mode = Mode(modeNode);
        mode.setSystemId(systemId);
        return mode;
    }

    public Element ModeTrans(StateMachine stm){
        Element stmNode=DocumentHelper.createElement("stm");//添加节点stm
        Element sourceState=stmNode.addElement("sourceState");
        Element endState=stmNode.addElement("endState");
        Element event=stmNode.addElement("event");
        Element dependencyModeClass=stmNode.addElement("dependencyModeClass");
        Element dependencyModeClassId=stmNode.addElement("dependencyModeClassId");
        sourceState.setText(stm.getSourceState());
        endState.setText(stm.getEndState());
        event.setText(stm.getEvent());
        dependencyModeClass.setText(notNullString(stm.getDependencyModeClass()));
        dependencyModeClassId.setText(notNullString(stm.getDependencyModeClassId()));
        return stmNode;
    }

    /**
     * 模式转换
     * dependId， systemId都为空置
     *
     * @param stmNode STM节点
     * @return {@link StateMachine}
     */
    public StateMachine ModeTrans(Element stmNode){
        StateMachine stm = new StateMachine();
        stm.setSourceState(stmNode.elementText("sourceState"));
        stm.setEndState(stmNode.elementText("endState"));
        stm.setEvent(stmNode.elementText("event"));
        stm.setDependencyModeClass(stmNode.elementText("dependencyModeClass"));
        return stm;
    }

    public StateMachine ModeTrans(Element stmNode, Integer systemId){
        StateMachine stm = ModeTrans(stmNode);
        stm.setSystemId(systemId);
        return stm;
    }

    /**
     * 规范化需求转XML
     *
     * @param standard 规范化需求
     * @return {@link Element}
     */
    public Element SReq(StandardRequirement standard){
        Element standardNode=DocumentHelper.createElement("standard");//添加节点standard
        standardNode.addAttribute("id", notNullString(standard.getStandardRequirementId()));//添加属性standardrequirementId
        Element naturalLanguageReqId=standardNode.addElement("naturalLanguageReqId");
        Element standardReqVariable=standardNode.addElement("standardReqVariable");
        Element standardReqFunction=standardNode.addElement("standardReqFunction");
        Element standardReqValue=standardNode.addElement("standardReqValue");
        Element standardReqCondition=standardNode.addElement("standardReqCondition");
        Element standardReqEvent=standardNode.addElement("standardReqEvent");
        Element standardReqContent=standardNode.addElement("standardReqContent");
        Element templateType=standardNode.addElement("templateType");
        Element mode=standardNode.addElement("mode");
        Element moduleId = standardNode.addElement("moduleId");
        naturalLanguageReqId.setText(notNullString(standard.getNaturalLanguageReqId()));
        standardReqVariable.setText(notNullString(standard.getStandardReqVariable()));
        standardReqFunction.setText(notNullString(standard.getStandardReqFunction()));
        standardReqValue.setText(notNullString(standard.getStandardReqValue()));
        standardReqCondition.setText(notNullString(standard.getStandardReqCondition()));
        standardReqEvent.setText(notNullString(standard.getStandardReqEvent()));
        standardReqContent.setText(notNullString(standard.getStandardReqContent()));
        templateType.setText(notNullString(standard.getTemplateType()));
        mode.setText(notNullString(standard.getMode()));
        moduleId.setText(notNullString(standard.getModuleId()));
        return standardNode;
    }

    public StandardRequirement SReq(Element standardNode){
        StandardRequirement standard = new StandardRequirement();
        String sReqId = standardNode.attributeValue("id");

        standard.setStandardRequirementId(sReqId.isBlank() ? null : Integer.valueOf(sReqId));
        //这里现在修改为了对应的需求excelId
        standard.setNaturalLanguageReqId(toInt(standardNode.elementText("naturalLanguageReqId")));
        standard.setStandardReqVariable(
                standardNode.elementText("standardReqVariable"));
        standard.setStandardReqFunction(
                standardNode.elementText("standardReqFunction"));
        standard.setStandardReqValue(standardNode.elementText("standardReqValue"));
        standard.setStandardReqCondition(
                standardNode.elementText("standardReqCondition"));
        standard.setStandardReqEvent(standardNode.elementText("standardReqEvent"));
        standard.setStandardReqContent(standardNode.elementText("standardReqContent"));
        standard.setTemplateType(standardNode.elementText("templateType"));
        standard.setMode(standardNode.elementText("mode"));

        String moduleId = standardNode.elementText("moduleId");
        standard.setModuleId(toInt(moduleId));

        return standard;
    }

    public StandardRequirement SReq(Element standardNode, Integer systemId){
        StandardRequirement standard = SReq(standardNode);
        standard.setSystemId(systemId);
        return standard;
    }


    public Element Module(Module module){
        Element moduleNode=DocumentHelper.createElement("module");
        moduleNode.addAttribute("id", notNullString(module.getId()));
        Element name = moduleNode.addElement("name");
        Element parent = moduleNode.addElement("parent");
        name.setText(notNullString(module.getName()));
        parent.setText(notNullString(module.getParentId()));
        return moduleNode;
    }

    public  Module Module(Element node){
        Module module = new Module();
        module.setSystemId(null);
        module.setId(toInt(node.attributeValue("id")));
        module.setName(node.elementText("name"));
        module.setParentId(toInt(node.elementText("parent")));
        return module;
    }

}
