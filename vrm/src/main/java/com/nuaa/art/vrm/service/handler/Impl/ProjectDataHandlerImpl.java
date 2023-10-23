package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrm.service.handler.ProjectDataHandler;
import jakarta.annotation.Resource;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ProjectDataHandlerImpl implements ProjectDataHandler {
    @Resource
    DaoHandler daoHandler;

    @Resource
    WebSocketService webSocketService;

    /**
     * 导入项目
     *
     * @param projectName 项目名称
     * @param fileUrl     文件名称
     * @return int 返回项目id
     */
    @Override
    public int importProjectFromFile(String projectName, String fileUrl) {

        SystemProject sProject = new SystemProject();
        sProject.setSystemName(projectName);
        daoHandler.getDaoService(SystemProjectService.class).insertSystemProject(sProject);
        Integer systemId = sProject.getSystemId();
        try {
            Document doc = FileUtils.readXML(fileUrl);
            Element root = doc.getRootElement();
            Element requirements = root.element("requirements");
            Element variables = root.element("variables");
            Element types = root.element("types");
            Element props = root.element("props");
            Element modeClasses = root.element("modeClasses");
            Element modes = root.element("modes");
            Element standards = root.element("standards");
            Element stms = root.element("stms");
            LogUtils.info("原始需求导入");
            webSocketService.sendMsg(SocketMessage.asText("project","原始需求导入"));
            List<Element> requirementList = requirements.elements("requirement");
            NaturalLanguageRequirement requirement = new NaturalLanguageRequirement();
            for (Element requirementNode : requirementList) {
                requirement.setReqId(null);
                requirement.setSystemId(systemId);
                requirement.setReqExcelId(
                        Integer.valueOf(requirementNode.elementText("reqExcelId")));
                requirement.setReqContent(requirementNode.elementText("reqContent"));
                daoHandler.getDaoService(NaturalLanguageRequirementService.class).insertNLR(requirement);
            }

            Thread.sleep(800);

            LogUtils.info("数据类型导入");
            webSocketService.sendMsg(SocketMessage.asText("project","数据类型导入"));
            List<Element> typeList = types.elements("type");
            Type type = new Type();
            for (Element typeNode : typeList) {
                type.setTypeId(null);
                type.setTypeName(typeNode.elementText("typeName"));
                type.setDataType(typeNode.elementText("dataType"));
                type.setTypeRange(typeNode.elementText("typeRange"));
                type.setTypeAccuracy(typeNode.elementText("typeAccuracy"));
                type.setSystemId(systemId);
                daoHandler.getDaoService(TypeService.class).insertType(type);
            }

            Thread.sleep(800);

            LogUtils.info("专有名词导入");
            webSocketService.sendMsg(SocketMessage.asText("project","专有名词导入"));
            List<Element> propList = props.elements("prop");
            ProperNoun prop = new ProperNoun();
            for (Element propNode : propList) {
                prop.setProperNounId(null);
                prop.setProperNounName(propNode.elementText("properNounName"));
                prop.setProperNounDescription(propNode.elementText("properNounDescription"));
                prop.setSystemId(systemId);
                daoHandler.getDaoService(ProperNounService.class).insertProperNoun(prop);
            }

            Thread.sleep(800);

            LogUtils.info("变量导入");
            webSocketService.sendMsg(SocketMessage.asText("project","变量导入"));
            List<Element> variableList = variables.elements("variable");
            ConceptLibrary variable = new ConceptLibrary();
            for (Element variableNode : variableList) {
                variable.setConceptId(null);
                variable.setConceptName(variableNode.elementText("conceptName"));
                variable.setConceptDatatype(variableNode.elementText("conceptDataType"));
                variable.setConceptValue(variableNode.elementText("conceptValue"));
                variable.setConceptAccuracy(variableNode.elementText("conceptAccuracy"));
                variable.setConceptRange(variableNode.elementText("conceptRange"));
                variable.setConceptType(variableNode.elementText("conceptType"));
                variable.setConceptDependencyModeClass(
                        variableNode.elementText("conceptDependency"));
                variable.setConceptDescription(variableNode.elementText("conceptDescription"));
                variable.setSystemId(systemId);
                String reqID = variableNode.elementText("relateReq");
                if(reqID.equals("null")){
                    variable.setSourceReqId( null);
                } else {
                    for (String s : Collections.singletonList(reqID)){
                        if(s!=""){
                            variable.getSourceReqId().add(Integer.valueOf(s));
                        }
                    }
                }
                daoHandler.getDaoService(ConceptLibraryService.class).insertConcept(variable);
            }

            Thread.sleep(800);

            LogUtils.info("模式集导入");
            webSocketService.sendMsg(SocketMessage.asText("project","模式集导入"));
            List<Element> modeClassList = modeClasses.elements("modeClass");
            Map<String, Integer> modeClassMap = new HashMap<>();
            for (Element modeClassNode : modeClassList) {
                ModeClass modeClass = new ModeClass();
                modeClass.setModeClassName(modeClassNode.elementText("modeClassName"));
                modeClass.setModeClassDescription(
                        modeClassNode.elementText("modeClassDescription"));
                modeClass.setSystemId(systemId);
                daoHandler.getDaoService(ModeClassService.class).insertModeClass(modeClass);
                modeClassMap.put(modeClass.getModeClassName(), modeClass.getModeClassId());
            }

            Thread.sleep(800);

            LogUtils.info("模式导入");
            webSocketService.sendMsg(SocketMessage.asText("project","模式导入"));
            List<Element> modeList = modes.elements("mode");
            for (Element modeNode : modeList) {
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
                String modeClassName = modeNode.elementText("modeClassName");
                Integer dependId = modeClassMap.get(modeClassName);
                mode.setModeClassId(dependId);
                mode.setModeClassName(modeClassName);
                mode.setModeDescription(modeNode.elementText("modeDescription"));
                mode.setSystemId(systemId);
                daoHandler.getDaoService(ModeService.class).insertMode(mode);
            }

            Thread.sleep(800);

            LogUtils.info("规范化需求导入");
            webSocketService.sendMsg(SocketMessage.asText("project","规范化需求导入"));
            List<Element> standardList = standards.elements("standard");
            StandardRequirement standard = new StandardRequirement();
            for (Element standardNode : standardList) {
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
                standard.setSystemId(systemId);
                daoHandler.getDaoService(StandardRequirementService.class).insertStandardRequirement(standard);
            }

            Thread.sleep(800);

            LogUtils.info("模式转换导入");
            webSocketService.sendMsg(SocketMessage.asText("project","模式转换导入"));
            List<Element> stmList = stms.elements("stm");
            for (Element stmNode : stmList) {
                StateMachine stm = new StateMachine();
                stm.setSourceState(stmNode.elementText("sourceState"));
                stm.setEndState(stmNode.elementText("endState"));
                stm.setEvent(stmNode.elementText("event"));
                String modeClassName = stmNode.elementText("dependencyModeClass");
                Integer dependId = modeClassMap.get(modeClassName);
                stm.setDependencyModeClass(modeClassName);
                stm.setSystemId(systemId);
                stm.setDependencyModeClassId(dependId);
                daoHandler.getDaoService(StateMachineService.class).insertStateMachine(stm);
            }

            Thread.sleep(800);
            LogUtils.info("需求工程导入成功！");
            webSocketService.sendMsg(SocketMessage.asText("project","需求工程导入成功！"));
            Thread.sleep(800);
            webSocketService.sendMsg(SocketMessage.asText("project",""));
            return systemId;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error("需求工程导入失败！已经成功的导入环节将被回滚！");
            webSocketService.sendMsg(SocketMessage.asText("project","需求工程导入失败！已经成功的导入环节将被回滚！"));
            if (systemId != null) {
                daoHandler.getDaoService(ConceptLibraryService.class).deleteConceptById(systemId);
                daoHandler.getDaoService(ModeClassService.class).deleteModeClassById(systemId);
                daoHandler.getDaoService(ModeService.class).deleteModeById(systemId);
                daoHandler.getDaoService(NaturalLanguageRequirementService.class).deleteNLRById(systemId);
                daoHandler.getDaoService(ProperNounService.class).deleteProperNounById(systemId);
                daoHandler.getDaoService(StandardRequirementService.class).deleteStandardRequirementBySystemId(systemId);
                daoHandler.getDaoService(StateMachineService.class).deleteStateMachineById(systemId);
                daoHandler.getDaoService(TypeService.class).deleteTypeById(systemId);
                daoHandler.getDaoService(SystemProjectService.class).deleteSystemProject(projectName);
            }
            return -1;
        }
    }

    /**
     * 导出项目
     *
     * @param systemId  系统编号
     * @return {@link String} 导出文件名
     */
    @Override
    public String exportProjectToFile(int systemId) {
            Document systemDocument= DocumentHelper.createDocument();
            //创建根节点systemProject
            Element systemProject = systemDocument.addElement("systemProject");
            systemProject.addAttribute("version", "2.0");
            Element lastSaved = systemProject.addElement("lastSaved");
            Element systemNameElement = lastSaved.addElement("name");

            SystemProject system = daoHandler.getDaoService(SystemProjectService.class)
                    .getSystemProjectById(systemId);

            //保存系统名称
            systemNameElement.addText(system.getSystemName());

            //保存导出时间
            Element updateDateElement =lastSaved.addElement("date");
            SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String dateString = sdfDateFormat.format(new Date());
            updateDateElement.addText(dateString);

            //读取数据库所有属于该需求工程的元素
            List<NaturalLanguageRequirement> requirements=daoHandler.getDaoService(NaturalLanguageRequirementService.class)
                    .listNaturalLanguageRequirementBySystemId(systemId);
            List<ConceptLibrary> variables=daoHandler.getDaoService(ConceptLibraryService.class).listAllConceptBySystemId(systemId);
            List<Type> types=daoHandler.getDaoService(TypeService.class).listTypeBySystemId(systemId);
            List<ProperNoun> props=daoHandler.getDaoService(ProperNounService.class).listProperNounBySystemId(systemId);
            List<ModeClass> modeClasses=daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(systemId);
            List<Mode> modes=daoHandler.getDaoService(ModeService.class).listModeBySystemId(systemId);
            List<StandardRequirement> standards=daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(systemId);
            List<StateMachine> stms=daoHandler.getDaoService(StateMachineService.class).listStateMachineBySystemId(systemId);

            //存储原始需求
            Element requirementsNode=systemProject.addElement("requirements");//添加节点requirements
            for(NaturalLanguageRequirement requirement:requirements) {
                Element requirementNode=requirementsNode.addElement("requirement");//添加节点requirement
                requirementNode.addAttribute("reqId", requirement.getReqId()+"");//添加属性reqId
                Element reqContent=requirementNode.addElement("reqContent");
                Element reqExcelId=requirementNode.addElement("reqExcelId");
                reqContent.setText(requirement.getReqContent());
                System.out.println(requirement.getReqContent());
                reqExcelId.setText(requirement.getReqExcelId()+"");
            }

            //存储变量
            Element variablesNode=systemProject.addElement("variables");//添加节点variables
            for(ConceptLibrary variable:variables) {
                Element variableNode=variablesNode.addElement("variable");//添加节点variable
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
                conceptRelateReq.setText(variable.getSourceReqId()+"");
            }

            //存储数据类型
            Element typesNode=systemProject.addElement("types");//添加节点types
            for(Type type:types) {
                Element typeNode=typesNode.addElement("type");//添加节点type
                typeNode.addAttribute("typeId", type.getTypeId()+"");//添加属性typeId
                Element typeName=typeNode.addElement("typeName");
                Element dataType=typeNode.addElement("dataType");
                Element typeRange=typeNode.addElement("typeRange");
                Element typeAccuracy=typeNode.addElement("typeAccuracy");
                typeName.setText(type.getTypeName()+"");
                dataType.setText(type.getDataType()+"");
                typeRange.setText(type.getTypeRange()+"");
                typeAccuracy.setText(type.getTypeAccuracy()+"");
            }

            //存储专有名词
            Element propsNode=systemProject.addElement("props");//添加节点props
            for(ProperNoun prop:props) {
                Element propNode=propsNode.addElement("prop");//添加节点prop
                propNode.addAttribute("properNounId", prop.getProperNounId()+"");//添加属性properNounId
                Element properNounName=propNode.addElement("properNounName");
                Element properNounDescription=propNode.addElement("properNounDescription");
                properNounName.setText(prop.getProperNounName()+"");
                properNounDescription.setText(prop.getProperNounDescription()+"");
            }

            //存储模式集
            Element modeClassesNode=systemProject.addElement("modeClasses");//添加节点modeClasses
            for(ModeClass modeClass:modeClasses) {
                Element modeClassNode=modeClassesNode.addElement("modeClass");//添加节点modeClass
                modeClassNode.addAttribute("modeClassId", modeClass.getModeClassId()+"");//添加属性modeClassId
                Element modeClassName=modeClassNode.addElement("modeClassName");
                Element modeClassDescription=modeClassNode.addElement("modeClassDescription");
                modeClassName.setText(modeClass.getModeClassName()+"");
                modeClassDescription.setText(modeClass.getModeClassDescription()+"");
            }

            //存储模式
            Element modesNode=systemProject.addElement("modes");//添加节点modes
            for(Mode mode:modes) {
                Element modeNode=modesNode.addElement("mode");//添加节点mode
                modeNode.addAttribute("modeId", mode.getModeId()+"");//添加属性modeId
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
                modeDescription.setText(mode.getModeDescription()+"");
            }

            //存储需求规范化
            Element standardsNode=systemProject.addElement("standards");//添加节点standards
            for(StandardRequirement standard:standards) {
                Element standardNode=standardsNode.addElement("standard");//添加节点standard
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
            }

            //存储模式转换
            Element stmsNode=systemProject.addElement("stms");//添加节点stms
            for(StateMachine stm:stms) {
                Element stmNode=stmsNode.addElement("stm");//添加节点stm
                stmNode.addAttribute("stateMachineId", stm.getStateMachineId()+"");//添加属性stateMachineId
                Element sourceState=stmNode.addElement("sourceState");
                Element endState=stmNode.addElement("endState");
                Element event=stmNode.addElement("event");
                Element dependencyModeClass=stmNode.addElement("dependencyModeClass");
                Element dependencyModeClassId=stmNode.addElement("dependencyModeClassId");
                sourceState.setText(stm.getSourceState());
                endState.setText(stm.getEndState());
                event.setText(stm.getEvent());
                dependencyModeClass.setText(stm.getDependencyModeClass()+"");
                dependencyModeClassId.setText(stm.getDependencyModeClassId()+"");
            }


            String exportUrl = PathUtils.DefaultPath()+system.getSystemName()+"Project.xml";
            if (FileUtils.saveXML(systemDocument, exportUrl)){
                return exportUrl;
            }

        return null;
    }

    /**
     * 删除项目文件
     *
     * @param systemId 系统编号
     * @return boolean
     */
    @Override
    @Transactional
    public boolean deleteProject(int systemId) {
        try {
            daoHandler.getDaoService(SystemProjectService.class).removeById(systemId);
            daoHandler.getDaoService(ConceptLibraryService.class).deleteConceptById(systemId);
            daoHandler.getDaoService(ModeClassService.class).deleteModeClassById(systemId);
            daoHandler.getDaoService(ModeService.class).deleteModeById(systemId);
            daoHandler.getDaoService(NaturalLanguageRequirementService.class).deleteNLRById(systemId);
            daoHandler.getDaoService(StandardRequirementService.class).deleteStandardRequirementBySystemId(systemId);
            daoHandler.getDaoService(ProperNounService.class).deleteProperNounById(systemId);
            daoHandler.getDaoService(StateMachineService.class).deleteStateMachineById(systemId);
            daoHandler.getDaoService(TypeService.class).deleteTypeById(systemId);
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
