package com.nuaa.art.vrm.common.utils;

import com.nuaa.art.common.utils.ListUtils;
import com.nuaa.art.vrm.entity.*;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

@Component
public class EntityXmlConvert {
    public Element NReq(NaturalLanguageRequirement nreq){
        Element requirementNode= DocumentHelper.createElement("requirement");//添加节点requirement
        Element reqContent=requirementNode.addElement("reqContent");
        Element reqExcelId=requirementNode.addElement("reqExcelId");
        reqContent.setText(nreq.getReqContent());
        System.out.println(nreq.getReqContent());
        reqExcelId.setText(nreq.getReqExcelId()+"");
        return requirementNode;
    }

    public NaturalLanguageRequirement NReq(Element node){
        NaturalLanguageRequirement requirement = new NaturalLanguageRequirement();
        requirement.setReqId(null);
        requirement.setSystemId(null);
        requirement.setReqExcelId(
                Integer.valueOf(node.elementText("reqExcelId")));
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
        variableNode.addAttribute("conceptId", variable.getConceptId()+"");//添加属性conceptId
        Element conceptName=variableNode.addElement("conceptName");
        Element conceptDataType=variableNode.addElement("conceptDataType");
        Element conceptRange=variableNode.addElement("conceptRange");
        Element conceptValue=variableNode.addElement("conceptValue");
        Element conceptAccuracy=variableNode.addElement("conceptAccuracy");
        Element conceptDependency=variableNode.addElement("conceptDependency");
        Element conceptType=variableNode.addElement("conceptType");
        Element conceptDescription=variableNode.addElement("conceptDescription");
        Element conceptRelateReq=variableNode.addElement("relateReq");
        conceptName.setText(variable.getConceptName()+"");
        conceptDataType.setText(variable.getConceptDatatype()+"");
        conceptRange.setText(variable.getConceptRange()+"");
        conceptValue.setText(variable.getConceptValue()+"");
        conceptAccuracy.setText(variable.getConceptAccuracy()+"");
        conceptDependency.setText(variable.getConceptDependencyModeClass()+"");
        conceptType.setText(variable.getConceptType()+"");
        conceptDescription.setText(variable.getConceptDescription()+"");
        conceptRelateReq.setText(ListUtils.NumArrayToString(variable.getSourceReqId())+"");
        return variableNode;
    }

    public ConceptLibrary Variable(Element variableNode) {
        ConceptLibrary variable = new ConceptLibrary();
        variable.setConceptId(null);
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
       variable.setSourceReqId(ListUtils.StringToNumArray(variableNode.elementText("relateReq")));

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
        typeName.setText(type.getTypeName()+"");
        dataType.setText(type.getDataType()+"");
        typeRange.setText(type.getTypeRange()+"");
        typeAccuracy.setText(type.getTypeAccuracy()+"");
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
        if (prop.getProperNounName() != null) {
            name.setText(prop.getProperNounName());
        }
        Element description = properNoun.addElement("description");
        if (prop.getProperNounDescription() != null) {
            description.setText(prop.getProperNounDescription() + "");
        }
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
        modeClassNode.addAttribute("modeClassId", modeClass.getModeClassId()+"");//添加属性modeClassId
        Element modeClassName=modeClassNode.addElement("modeClassName");
        Element modeClassDescription=modeClassNode.addElement("modeClassDescription");
        modeClassName.setText(modeClass.getModeClassName()+"");
        modeClassDescription.setText(modeClass.getModeClassDescription());
        return modeClassNode;
    }

    public ModeClass ModeClass(Element modeClassNode){
        ModeClass modeClass = new ModeClass();
        modeClass.setModeClassName(modeClassNode.elementText("modeClassName"));
        modeClass.setModeClassDescription(
                modeClassNode.elementText("modeClassDescription"));
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
        modeName.setText(mode.getModeName()+"");
        if(mode.getInitialStatus()!=null)
            initialStatus.setText(mode.getInitialStatus()+"");
        else
            initialStatus.setText("");
        if(mode.getFinalStatus()!=null)
            finalStatus.setText(mode.getFinalStatus()+"");
        else
            finalStatus.setText("");
        value.setText(mode.getValue()+"");
        modeClassId.setText(mode.getModeClassId()+"");
        modeClassName.setText(mode.getModeClassName()+"");
        modeDescription.setText(mode.getModeDescription());
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
        sourceState.setText(stm.getSourceState());
        endState.setText(stm.getEndState());
        event.setText(stm.getEvent());
        dependencyModeClass.setText(stm.getDependencyModeClass()+"");
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
        standardNode.addAttribute("standardrequirementId", standard.getStandardRequirementId()+"");//添加属性standardrequirementId
        Element naturalLanguageReqId=standardNode.addElement("naturalLanguageReqId");
        Element standardReqVariable=standardNode.addElement("standardReqVariable");
        Element standardReqFunction=standardNode.addElement("standardReqFunction");
        Element standardReqValue=standardNode.addElement("standardReqValue");
        Element standardReqCondition=standardNode.addElement("standardReqCondition");
        Element standardReqEvent=standardNode.addElement("standardReqEvent");
        Element standardReqContent=standardNode.addElement("standardReqContent");
        Element templateType=standardNode.addElement("templateType");
        Element mode=standardNode.addElement("mode");
        naturalLanguageReqId.setText(standard.getNaturalLanguageReqId()+"");
        standardReqVariable.setText(standard.getStandardReqVariable()+"");
        standardReqFunction.setText(standard.getStandardReqFunction()+"");
        standardReqValue.setText(standard.getStandardReqValue()+"");
        standardReqCondition.setText(standard.getStandardReqCondition()+"");
        standardReqEvent.setText(standard.getStandardReqEvent()+"");
        standardReqContent.setText(standard.getStandardReqContent()+"");
        templateType.setText(standard.getTemplateType()+"");
        if(standard.getMode()!=null)
            mode.setText(standard.getMode()+"");
        else
            mode.setText("");
        return standardNode;
    }

    public StandardRequirement SReq(Element standardNode){
        StandardRequirement standard = new StandardRequirement();
        standard.setStandardRequirementId(null);
        //这里现在修改为了对应的需求excelId
        standard.setNaturalLanguageReqId(Integer.valueOf(standardNode.elementText("naturalLanguageReqId")));
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
        return standard;
    }

    public StandardRequirement SReq(Element standardNode, Integer systemId){
        StandardRequirement standard = SReq(standardNode);
        standard.setSystemId(systemId);
        return standard;
    }

}
