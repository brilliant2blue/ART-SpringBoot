package com.nuaa.art.vrm.service.handler.impl;


import com.nuaa.art.common.utils.LogicSymbolToString;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;

import jakarta.annotation.Resource;

import java.io.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Service;

/**
 * 模型实例化创建程序
 *
 * @author konsin
 * @date 2023/06/13
 */
@Service("modelCreateToXml")
public class ModelCreateToXML implements ModelCreateHandler {
    @Resource
    DaoHandler daoHandler;

    /**
     * 创建模型
     *
     * @param systemId 工程编号
     */
    @Override
    public void createModel(Integer systemId) {

        Document vrmDocument = DocumentHelper.createDocument();
        //创建根节点vrmModel
        Element vrmModel = vrmDocument.addElement("vrmModel");
        vrmModel.addAttribute("version", "2.0");
        Element lastSaved = vrmModel.addElement("lastSaved");
        Element modelNamElement = lastSaved.addElement("name");

        modelNamElement.addText(daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId).getSystemName());

        Element updateDateElement = lastSaved.addElement("date");
        SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateString = sdfDateFormat.format(new Date());
        updateDateElement.addText(dateString);
        //将领域概念原始自然语言转换为vrm模型中的自然语言需求并存储到xml文件中
        List<NaturalLanguageRequirement> domainConceptReq = daoHandler.getDaoService(NaturalLanguageRequirementService.class)
                .listNaturalLanguageRequirementBySystemId(systemId);
        //添加节点requirements
        Element requirements = vrmModel.addElement("requirements");
        if (domainConceptReq.isEmpty() == false) {
            for (NaturalLanguageRequirement req : domainConceptReq) {
                //添加节点requirement
                Element requirement = requirements.addElement("requirement");
                //设置requirement属性 ID
                if (req.getReqId() != null) {
                    requirement.addAttribute("ID", req.getReqId() + "");
                }
                //添加requirement属性 content
                Element content = requirement.addElement("content");
                if (req.getReqContent() != null) {
                    content.setText(req.getReqContent());
                }
            }
        }

        //将领域概念常量转换为数据字典常量并存储到xml文件中
        List<ConceptLibrary> domainConceptConst = daoHandler.getDaoService(ConceptLibraryService.class).listConstVariableBySystemId(systemId);
        //添加节点constants
        Element constants = vrmModel.addElement("constants");
        if (domainConceptConst.isEmpty() == false) {
            for (ConceptLibrary conLibrary : domainConceptConst) {
                //添加节点constant
                Element constant = constants.addElement("constant");
                //设置constant属性 name
                if (conLibrary.getConceptName() != null) {
                    constant.addAttribute("name", conLibrary.getConceptName());
                }
                //添加constant属性 constantType
                Element constantType = constant.addElement("constantType");
                if (conLibrary.getConceptDatatype() != null) {
                    constantType.setText(conLibrary.getConceptDatatype());
                }
                //添加constant属性 value
                Element value = constant.addElement("value");
                if (conLibrary.getConceptValue() != null) {
                    value.setText(conLibrary.getConceptValue());
                }
                //添加constant属性 description
                Element description = constant.addElement("description");
                if (conLibrary.getConceptDescription() != null) {
                    description.setText(conLibrary.getConceptDescription());
                }
            }
        }

        //将领域概念数据类型转换为数据字典数据类型并存储到xml文件中
        List<Type> domainConceptTypes = daoHandler.getDaoService(TypeService.class).listTypeBySystemId(systemId);
        //添加节点types
        Element types = vrmModel.addElement("types");
        if (domainConceptTypes.isEmpty() == false) {
            for (Type t : domainConceptTypes) {
                //添加节点type
                Element type = types.addElement("type");
                //设置type属性 name
                if (t.getTypeName() != null) {
                    type.addAttribute("name", t.getTypeName());
                }
                //添加type属性 baseType
                Element baseType = type.addElement("baseType");
                if (t.getDataType() != null) {
                    baseType.setText(t.getDataType());
                }
                //添加type属性 range
                Element range = type.addElement("range");
                if (t.getTypeRange() != null) {
                    range.setText(t.getTypeRange());
                }
                //添加type属性 accuracy
                Element accuracy = type.addElement("accuracy");
                if (t.getTypeAccuracy() != null) {
                    accuracy.setText(t.getTypeAccuracy());
                }
            }
        }

        //将领域概念输入变量转换为数据字典输入变量并输出到xml文件中
        List<ConceptLibrary> domainConceptInput = daoHandler.getDaoService(ConceptLibraryService.class).listInputVariableBySystemId(systemId);
        //添加节点 inputs
        Element inputs = vrmModel.addElement("inputs");
        if (domainConceptInput.isEmpty() == false) {
            for (ConceptLibrary iv : domainConceptInput) {
                //添加节点input
                Element input = inputs.addElement("input");
                //设置input属性 name
                if (iv.getConceptName() != null) {
                    input.addAttribute("name", iv.getConceptName());
                }
                //添加input属性 inputType
                Element inputType = input.addElement("inputType");
                if (iv.getConceptDatatype() != null) {
                    inputType.setText(iv.getConceptDatatype());
                }
                //添加input属性 initialValue
                Element initialValue = input.addElement("initialValue");
                if (iv.getConceptValue() != null) {
                    initialValue.setText(iv.getConceptValue());
                }
                //添加input属性 range
                Element range = input.addElement("range");
                if (iv.getConceptRange() != null) {
                    range.setText(iv.getConceptRange());
                }
                //添加input属性accuracy
                Element accuracy = input.addElement("accuracy");
                if (iv.getConceptAccuracy() != null) {
                    accuracy.setText(iv.getConceptAccuracy());
                }
            }
        }

        //将领域概念中间变量、输出变量、规范化需求 转换为 中间变量条件表、中间变量事件表、输出变量条件表和输出变量事件表
        List<ConceptLibrary> domainConceptTerm = daoHandler.getDaoService(ConceptLibraryService.class).listTermVariableBySystemId(systemId);
        List<ConceptLibrary> domainConceptOutput = daoHandler.getDaoService(ConceptLibraryService.class).listOutputVariableBySystemId(systemId);
        List<StandardRequirement> domainConceptStandardReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(systemId);
        StandardRequirement reqIsCorD = null;

        //添加节点tables
        Element tables = vrmModel.addElement("tables");
        if (domainConceptTerm.isEmpty() == false) {
            for (ConceptLibrary term : domainConceptTerm) {
                //添加table节点前进行判定 当前standardReq是否为空
                for (StandardRequirement standardReq : domainConceptStandardReq) {
                    if (standardReq.getStandardReqVariable().equals(term.getConceptName())) {
                        reqIsCorD = standardReq;
                        break;
                    }
                }

                //添加节点table
                Element table = tables.addElement("table");
                //设置table属性 name
                if (term.getConceptName() != null) {
                    table.addAttribute("name", term.getConceptName());
                    table.addAttribute("isOutput", "0");
                }

                //添加table属性referencedStateMachine
                Element referencedStateMachine = table.addElement("referencedStateMachine");
                if (term.getConceptDependencyModeClass() != null) {
                    referencedStateMachine.addAttribute("name", term.getConceptDependencyModeClass());
                }

                //判断当前规范化需求是否为空,不为空则添加规范化需求
                if (reqIsCorD != null) {
                    if (reqIsCorD.getTemplateType().contains("Condition")) {
                        //添加table节点conditionTable
                        Element conditionTable = table.addElement("conditionTable");
                        for (StandardRequirement standardReq : domainConceptStandardReq) {
                            if (standardReq.getStandardReqVariable().equals(term.getConceptName())) {
                                //添加conditionTable节点row
                                Element row = conditionTable.addElement("row");
                                Element assignment = row.addElement("assignment");
                                if (standardReq.getStandardReqValue() != null) {
                                    assignment.setText(standardReq.getStandardReqValue());
                                }
                                Element condition = row.addElement("condition");
                                if (standardReq.getStandardReqCondition() != null) {
                                    condition.setText(standardReq.getStandardReqCondition());
                                }
                                Element state = row.addElement("state");
                                if (standardReq.getMode() != null) {
                                    state.setText(standardReq.getMode());
                                }
                                Element relateSReq = row.addElement("relateSReq");
                                if (standardReq.getStandardRequirementId() != null) {
                                    relateSReq.setText(standardReq.getStandardRequirementId() + "");
                                }
                            }
                        }
                    } else if (reqIsCorD.getTemplateType().contains("Event")) {
                        //添加table节点eventTable
                        Element eventTable = table.addElement("eventTable");
                        for (StandardRequirement standardReq : domainConceptStandardReq) {
                            if (standardReq.getStandardReqVariable().equals(term.getConceptName())) {
                                Element row = eventTable.addElement("row");
                                Element assignment = row.addElement("assignment");
                                if (standardReq.getStandardReqValue() != null) {
                                    assignment.setText(standardReq.getStandardReqValue());
                                }
                                Element Event = row.addElement("event");
                                if (standardReq.getStandardReqEvent() != null) {
                                    Event.setText(standardReq.getStandardReqEvent());
                                }
                                Element state = row.addElement("state");
                                if (standardReq.getMode() != null) {
                                    state.setText(standardReq.getMode());
                                }
                                Element relateSReq = row.addElement("relateSReq");
                                if (standardReq.getStandardRequirementId() != null) {
                                    relateSReq.setText(standardReq.getStandardRequirementId() + "");
                                }
                            }
                        }
                    }
                }

                //添加table属性 tableVariableType
                Element tableVariableType = table.addElement("tableVariableType");
                if (term.getConceptDatatype() != null) {
                    tableVariableType.setText(term.getConceptDatatype());
                }
                //添加table属性 initialValue
                Element initialValue = table.addElement("initialValue");
                if (term.getConceptValue() != null) {
                    initialValue.setText(term.getConceptValue());
                }
                //添加table属性 range
                Element range = table.addElement("range");
                if (term.getConceptRange() != null) {
                    range.setText(term.getConceptRange());
                }
                //添加table属性accuracy
                Element accuracy = table.addElement("accuracy");
                if (term.getConceptAccuracy() != null) {
                    accuracy.setText(term.getConceptAccuracy());
                }
            }
        }

        //output
        if (domainConceptOutput.isEmpty() == false) {
            for (ConceptLibrary output : domainConceptOutput) {
                //添加table节点前，对当前变量的条件表事件表是否为空进行判定
                for (StandardRequirement standardReq : domainConceptStandardReq) {
                    if (standardReq.getStandardReqVariable().equals(output.getConceptName())) {
                        reqIsCorD = standardReq;
                        break;
                    }
                }


                //添加节点table
                Element table = tables.addElement("table");
                //设置table属性 name
                if (output.getConceptName() != null) {
                    table.addAttribute("name", output.getConceptName());
                    table.addAttribute("isOutput", "1");
                }

                //添加table属性referencedStateMachine
                Element referencedStateMachine = table.addElement("referencedStateMachine");
                if (output.getConceptDependencyModeClass() != null) {
                    referencedStateMachine.addAttribute("name", output.getConceptDependencyModeClass());
                }

                //判断规范化需求是否为空，不为空则添加规范化需求
                if (reqIsCorD != null) {
                    if (reqIsCorD.getTemplateType().contains("Condition")) {
                        Element conditionTable = table.addElement("conditionTable");
                        for (StandardRequirement standardReq : domainConceptStandardReq) {
                            if (standardReq.getStandardReqVariable().equals(output.getConceptName())) {
                                Element row = conditionTable.addElement("row");
                                Element assignment = row.addElement("assignment");
                                if (standardReq.getStandardReqValue() != null) {
                                    assignment.setText(standardReq.getStandardReqValue());
                                }
                                Element condition = row.addElement("condition");
                                if (standardReq.getStandardReqCondition() != null) {
                                    condition.setText(standardReq.getStandardReqCondition());
                                }
                                Element state = row.addElement("state");
                                if (standardReq.getMode() != null) {
                                    state.setText(standardReq.getMode());
                                }
                                Element relateSReq = row.addElement("relateSReq");
                                if (standardReq.getStandardRequirementId() != null) {
                                    relateSReq.setText(standardReq.getStandardRequirementId() + "");
                                }
                            }
                        }
                    } else if (reqIsCorD.getTemplateType().contains("Event")) {
                        Element eventTable = table.addElement("eventTable");
                        for (StandardRequirement standardReq : domainConceptStandardReq) {
                            if (standardReq.getStandardReqVariable().equals(output.getConceptName())) {
                                Element row = eventTable.addElement("row");
                                Element assignment = row.addElement("assignment");
                                if (standardReq.getStandardReqValue() != null) {
                                    assignment.setText(standardReq.getStandardReqValue());
                                }
                                Element Event = row.addElement("event");
                                if (standardReq.getStandardReqEvent() != null) {
                                    Event.setText(standardReq.getStandardReqEvent());
                                }
                                Element state = row.addElement("state");
                                if (standardReq.getMode() != null) {
                                    state.setText(standardReq.getMode());
                                }
                                Element relateSReq = row.addElement("relateSReq");
                                if (standardReq.getStandardRequirementId() != null) {
                                    relateSReq.setText(standardReq.getStandardRequirementId() + "");
                                }
                            }
                        }
                    }
                }


                //添加table属性 tableVariableType
                Element tableVariableType = table.addElement("tableVariableType");
                if (output.getConceptDatatype() != null) {
                    tableVariableType.setText(output.getConceptDatatype());
                }
                //添加table属性 initialValue
                Element initialValue = table.addElement("initialValue");
                if (output.getConceptValue() != null) {
                    initialValue.setText(output.getConceptValue());
                }
                //添加table属性 range
                Element range = table.addElement("range");
                if (output.getConceptRange() != null) {
                    range.setText(output.getConceptRange());
                }
                //添加table属性accuracy
                Element accuracy = table.addElement("accuracy");
                if (output.getConceptAccuracy() != null) {
                    accuracy.setText(output.getConceptAccuracy());
                }
            }
        }

        //将领域概念模式集、模式和模式转换信息 转换为 模式转换表
        List<ModeClass> domainConceptMC = daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(systemId);
        List<Mode> domainConceptMode = daoHandler.getDaoService(ModeService.class).listModeBySystemId(systemId);
        List<StateMachine> domainConceptStateM = daoHandler.getDaoService(StateMachineService.class).listStateMachineBySystemId(systemId);

        //添加节点stateMachines
        Element stateMachines = vrmModel.addElement("stateMachines");
        if (domainConceptMC.isEmpty() == false) {
            for (ModeClass mc : domainConceptMC) {
                //添加节点stateMachine
                Element stateMachine = stateMachines.addElement("stateMachine");
                if (mc.getModeClassName() != null) {
                    stateMachine.addAttribute("name", mc.getModeClassName());
                }
                //添加节点description
                Element description = stateMachine.addElement("description");
                if (mc.getModeClassDescription() != null) {
                    description.setText(mc.getModeClassDescription());
                }

                //添加节点stateList
                Element stateList = stateMachine.addElement("stateList");
                for (Mode m : domainConceptMode) {
                    if (m.getModeClassName().equals(mc.getModeClassName())) {
                        //添加节点state
                        Element state = stateList.addElement("state");
                        //添加四个属性name，initial，final，enum；
                        if (m.getModeName() != null) {
                            state.addAttribute("name", m.getModeName());
                        }
                        if (m.getInitialStatus() != null) {
                            state.addAttribute("initial", m.getInitialStatus().toString());
                        }
                        if (m.getFinalStatus() != null) {
                            state.addAttribute("final", m.getFinalStatus().toString());
                        }
                        if (m.getValue() != null) {
                            state.addAttribute("enum", m.getValue().toString());
                        }
                    }
                }

                //添加节点stateTransition
                Element stateTransition = stateMachine.addElement("stateTransition");
                for (StateMachine stateM : domainConceptStateM) {
                    if (stateM.getDependencyModeClass().equals(mc.getModeClassName())) {
                        //添加节点row  stateM.getDependencyModeClass().equals(mc.getModeClassName())
                        Element row = stateTransition.addElement("row");
                        Element source = row.addElement("source");
                        if (stateM.getSourceState() != null) {
                            source.setText(stateM.getSourceState());
                        }
                        Element Event = row.addElement("event");
                        if (stateM.getEvent() != null) {
                            Event.setText(stateM.getEvent());
                        }
                        Element destinate = row.addElement("destination");
                        if (stateM.getEndState() != null) {
                            destinate.setText(stateM.getEndState());
                        }
                    }
                }
            }
        }


        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");

        String url = "";

        url = PathUtils.DefaultPath();


        String fileName = "";
        if (null != daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId)) {
            fileName = url + daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId).getSystemName() + "model.xml";
        } else {
            fileName = url + "model.xml";
        }

        XMLWriter writer = null;
        try {
            File file = new File(fileName);

            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            writer = new XMLWriter(fos, format);
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            writer.write(vrmDocument);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(fileName);
    }

    /**
     * 导出模型
     *
     * @param systemId 系统标识
     * @param fileName 文件名称
     */
    //todo: 并不完善，后面还是需要考虑导出地址的方式，是web的下载方式，还是客户端的保存方式
    @Override
    public void exportModel(Integer systemId, String fileName) {
        Document vrmDocument = DocumentHelper.createDocument();
        //创建根节点vrmModel
        Element vrmModel = vrmDocument.addElement("vrmModel");
        vrmModel.addAttribute("version", "2.0");
        Element lastSaved = vrmModel.addElement("lastSaved");
        Element modelNamElement = lastSaved.addElement("name");

        modelNamElement.addText(daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId).getSystemName());

        Element updateDateElement =lastSaved.addElement("date");
        SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateString = sdfDateFormat.format(new Date());
        updateDateElement.addText(dateString);
        //将领域概念原始自然语言转换为vrm模型中的自然语言需求并存储到xml文件中
        List<NaturalLanguageRequirement> domainConceptReq = daoHandler.getDaoService(NaturalLanguageRequirementService.class).listNaturalLanguageRequirementBySystemId(systemId);
        //添加节点requirements
        Element requirements = vrmModel.addElement("requirements");
        if(domainConceptReq.isEmpty() == false) {
            for(NaturalLanguageRequirement req : domainConceptReq) {
                //添加节点requirement
                Element requirement = requirements.addElement("requirement");
                //设置requirement属性 ID
                if(req.getReqId() != null) {
                    requirement.addAttribute("ID", req.getReqId() + "");
                }
                //添加requirement属性 content
                Element content = requirement.addElement("content");
                if(req.getReqContent() != null) {
                    content.setText(req.getReqContent());
                }
            }
        }

        //将规范化需求输出到XML文件中
        List<StandardRequirement> domainConStandardReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(systemId);
        //添加节点standardRequirements
        Element standardRequirements = vrmModel.addElement("standardRequirements");
        if(domainConStandardReq.isEmpty() == false) {
            for(StandardRequirement sReq : domainConStandardReq) {
                //添加节点standardRequirement
                Element standardRequirement = standardRequirements.addElement("standardRequirement");
                //设置standardRequirement属性 ID
                if(sReq.getStandardRequirementId() != null) {
                    standardRequirement.addAttribute("ID", sReq.getStandardRequirementId() + "");
                }

                //添加requirement属性 content
                Element content = standardRequirement.addElement("content");
                if(sReq.getStandardReqContent() != null) {
                    content.setText(sReq.getStandardReqContent());
                }
                Element sourceReqID = standardRequirement.addElement("sourceReqID");
                if(sReq.getNaturalLanguageReqId() != null) {
                    sourceReqID.setText(sReq.getNaturalLanguageReqId() + "");
                }
                Element function=standardRequirement.addElement("function");
                if(sReq.getStandardReqFunction() != null) {
                    function.setText(sReq.getStandardReqFunction()+"");
                }
                Element templateType=standardRequirement.addElement("templateType");
                if(sReq.getTemplateType() != null) {
                    templateType.setText(sReq.getTemplateType()+"");
                }
            }

        }

        //将专有名词输出到XML文件中
        List<ProperNoun> domainConProperNoun=daoHandler.getDaoService(ProperNounService.class).listProperNounBySystemId(systemId);
        //添加节点properNouns
        Element properNouns=vrmModel.addElement("properNouns");
        if(domainConProperNoun.isEmpty()==false) {
            for(ProperNoun prop:domainConProperNoun) {
                //添加节点properNoun
                Element properNoun=properNouns.addElement("properNoun");
                //设置properNoun属性 ID
                if(prop.getProperNounId() != null) {
                    properNoun.addAttribute("ID", prop.getProperNounId() + "");
                }
                //添加properNoun属性
                Element name = properNoun.addElement("name");
                if(prop.getProperNounName() != null) {
                    name.setText(prop.getProperNounName());
                }
                Element description = properNoun.addElement("description");
                if(prop.getProperNounDescription() != null) {
                    description.setText(prop.getProperNounDescription() + "");
                }
            }
        }


        //将领域概念常量转换为数据字典常量并存储到xml文件中
        List<ConceptLibrary> domainConceptConst = daoHandler.getDaoService(ConceptLibraryService.class).listConstVariableBySystemId(systemId);
        //添加节点constants
        Element constants = vrmModel.addElement("constants");
        if(domainConceptConst.isEmpty() == false) {
            for(ConceptLibrary conLibrary : domainConceptConst) {
                //添加节点constant
                Element constant = constants.addElement("constant");
                //设置constant属性 name
                if(conLibrary.getConceptName() != null) {
                    constant.addAttribute("name", conLibrary.getConceptName());
                }
                //添加constant属性 constantType
                Element constantType = constant.addElement("constantType");
                if(conLibrary.getConceptDatatype() != null) {
                    constantType.setText(conLibrary.getConceptDatatype());
                }
                //添加constant属性 value
                Element value = constant.addElement("value");
                if(conLibrary.getConceptValue() != null) {
                    value.setText(conLibrary.getConceptValue());
                }
                //添加constant属性 description
                Element description = constant.addElement("description");
                if(conLibrary.getConceptDescription() != null) {
                    description.setText(conLibrary.getConceptDescription());
                }
            }
        }

        //将领域概念数据类型转换为数据字典数据类型并存储到xml文件中
        List<Type> domainConceptTypes = daoHandler.getDaoService(TypeService.class).listTypeBySystemId(systemId);
        //添加节点types
        Element types = vrmModel.addElement("types");
        if(domainConceptTypes.isEmpty() == false) {
            for (Type t : domainConceptTypes) {
                //添加节点type
                Element type = types.addElement("type");
                //设置type属性 name
                if(t.getTypeName() != null) {
                    type.addAttribute("name", t.getTypeName());
                }
                //添加type属性 baseType
                Element baseType = type.addElement("baseType");
                if(t.getDataType() != null) {
                    baseType.setText(t.getDataType());
                }
                //添加type属性 range
                Element range = type.addElement("range");
                if(t.getTypeRange() !=  null) {
                    range.setText(t.getTypeRange());
                }
                //添加type属性 accuracy
                Element accuracy = type.addElement("accuracy");
                if(t.getTypeAccuracy() !=  null) {
                    accuracy.setText(t.getTypeAccuracy());
                }
            }
        }

        //将领域概念输入变量转换为数据字典输入变量并输出到xml文件中
        List<ConceptLibrary> domainConceptInput = daoHandler.getDaoService(ConceptLibraryService.class).listInputVariableBySystemId(systemId);
        //添加节点 inputs
        Element inputs = vrmModel.addElement("inputs");
        if(domainConceptInput.isEmpty() == false) {
            for(ConceptLibrary iv : domainConceptInput) {
                //添加节点input
                Element input = inputs.addElement("input");
                //设置input属性 name
                if(iv.getConceptName() != null) {
                    input.addAttribute("name", iv.getConceptName());
                }
                //添加input属性 inputType
                Element inputType = input.addElement("inputType");
                if(iv.getConceptDatatype() != null) {
                    inputType.setText(iv.getConceptDatatype());
                }
                //添加input属性 initialValue
                Element  initialValue = input.addElement("initialValue");
                if(iv.getConceptValue() != null) {
                    initialValue.setText(iv.getConceptValue());
                }
                //添加input属性 range
                Element range = input.addElement("range");
                if(iv.getConceptRange() != null) {
                    range.setText(iv.getConceptRange());
                }
                //添加input属性accuracy
                Element accuracy = input.addElement("accuracy");
                if(iv.getConceptAccuracy() != null) {
                    accuracy.setText(iv.getConceptAccuracy());
                }
            }
        }

        //将领域概念中间变量、输出变量、规范化需求 转换为 中间变量条件表、中间变量事件表、输出变量条件表和输出变量事件表
        List<ConceptLibrary> domainConceptTerm = daoHandler.getDaoService(ConceptLibraryService.class).listTermVariableBySystemId(systemId);
        List<ConceptLibrary> domainConceptOutput = daoHandler.getDaoService(ConceptLibraryService.class).listOutputVariableBySystemId(systemId);
        List<StandardRequirement> domainConceptStandardReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(systemId);
        StandardRequirement reqIsCorD = null;

        //添加节点tables
        Element tables = vrmModel.addElement("tables");
        if(domainConceptTerm.isEmpty() == false) {
            for(ConceptLibrary term : domainConceptTerm) {
                //添加table节点前进行判定 当前standardReq是否为空
                for(StandardRequirement standardReq : domainConceptStandardReq) {
                    if(standardReq.getStandardReqVariable().equals(term.getConceptName())) {
                        reqIsCorD = standardReq;
                        break;
                    }
                }

                //添加节点table
                Element table = tables.addElement("table");
                //设置table属性 name
                if(term.getConceptName() != null) {
                    table.addAttribute("name", term.getConceptName());
                    table.addAttribute("isOutput", "0");
                }

                //添加table属性referencedStateMachine

                if(term.getConceptDependencyModeClass() != null && !term.getConceptDependencyModeClass().equals("")) {
                    Element referencedStateMachine = table.addElement("referencedStateMachine");
                    referencedStateMachine.addAttribute("name", term.getConceptDependencyModeClass());
                }

                //判空
                if(reqIsCorD != null) {
                    if(reqIsCorD.getTemplateType().contains("Condition")) {
                        //添加table节点conditionTable
                        Element conditionTable = table.addElement("conditionTable");
                        for(StandardRequirement standardReq : domainConceptStandardReq) {
                            if(standardReq.getStandardReqVariable().equals(term.getConceptName())) {
                                //添加conditionTable节点row
                                Element row = conditionTable.addElement("row");
                                Element assignment = row.addElement("assignment");
                                if(standardReq.getStandardReqValue() != null) {
                                    assignment.setText(standardReq.getStandardReqValue());
                                }
                                Element condition = row.addElement("condition");
                                if(standardReq.getStandardReqCondition() != null) {
                                    condition.setText(LogicSymbolToString.processString(standardReq.getStandardReqCondition()));
                                }

                                if(standardReq.getMode() != null && standardReq.getMode() != "") {
                                    Element state = row.addElement("state");
                                    state.setText(standardReq.getMode());
                                }
                                Element relateSReq = row.addElement("relateSReq");
                                if(standardReq.getStandardRequirementId() != null) {
                                    relateSReq.setText(standardReq.getStandardRequirementId() + "");
                                }
                            }
                        }
                    }
                    else if(reqIsCorD.getTemplateType().contains("Event")) {
                        //添加table节点eventTable
                        Element eventTable = table.addElement("eventTable");
                        for(StandardRequirement standardReq : domainConceptStandardReq) {
                            if(standardReq.getStandardReqVariable().equals(term.getConceptName())) {
                                Element row = eventTable.addElement("row");
                                Element assignment = row.addElement("assignment");
                                if(standardReq.getStandardReqValue() != null) {
                                    assignment.setText(standardReq.getStandardReqValue());
                                }
                                Element Event = row.addElement("event");
                                if(standardReq.getStandardReqEvent() != null ) {
                                    Event.setText(LogicSymbolToString.processString(standardReq.getStandardReqEvent()));
                                }

                                if(standardReq.getMode() != null && standardReq.getMode() != "") {
                                    Element state = row.addElement("state");
                                    state.setText(standardReq.getMode());
                                }
                                Element relateSReq = row.addElement("relateSReq");
                                if(standardReq.getStandardRequirementId() != null) {
                                    relateSReq.setText(standardReq.getStandardRequirementId() + "");
                                }
                            }
                        }
                    }

                }


                //添加table属性 tableVariableType
                Element tableVariableType = table.addElement("tableVariableType");
                if(term.getConceptDatatype() != null) {
                    tableVariableType.setText(term.getConceptDatatype());
                }
                //添加table属性 initialValue
                Element  initialValue = table.addElement("initialValue");
                if(term.getConceptValue() != null) {
                    initialValue.setText(term.getConceptValue());
                }
                //添加table属性 range
                Element range = table.addElement("range");
                if(term.getConceptRange() != null) {
                    range.setText(term.getConceptRange());
                }
                //添加table属性accuracy
                Element accuracy = table.addElement("accuracy");
                if(term.getConceptAccuracy() != null) {
                    accuracy.setText(term.getConceptAccuracy());
                }

            }
        }

        //output
        if(domainConceptOutput.isEmpty() == false) {
            for(ConceptLibrary output : domainConceptOutput) {
                //添加table节点前，对当前变量的条件表事件表是否为空进行判定
                for(StandardRequirement standardReq : domainConceptStandardReq) {
                    if(standardReq.getStandardReqVariable().equals(output.getConceptName())) {
                        reqIsCorD = standardReq;
                        break;
                    }
                }


                //添加节点table
                Element table = tables.addElement("table");
                //设置table属性 name
                if(output.getConceptName() != null) {
                    table.addAttribute("name", output.getConceptName());
                    table.addAttribute("isOutput", "1");
                }

                //添加table属性referencedStateMachine

                if(output.getConceptDependencyModeClass() != null && !output.getConceptDependencyModeClass().equals("")) {
                    Element referencedStateMachine = table.addElement("referencedStateMachine");
                    referencedStateMachine.addAttribute("name", output.getConceptDependencyModeClass());
                }

                if(reqIsCorD != null) {
                    if(reqIsCorD.getTemplateType().contains("Condition")) {
                        Element conditionTable = table.addElement("conditionTable");
                        for(StandardRequirement standardReq : domainConceptStandardReq) {
                            if(standardReq.getStandardReqVariable().equals(output.getConceptName())) {
                                Element row = conditionTable.addElement("row");
                                Element assignment = row.addElement("assignment");
                                if(standardReq.getStandardReqValue() != null) {
                                    assignment.setText(standardReq.getStandardReqValue());
                                }
                                Element condition = row.addElement("condition");
                                if(standardReq.getStandardReqCondition() != null) {
                                    condition.setText(LogicSymbolToString.processString(standardReq.getStandardReqCondition()));
                                }

                                if(standardReq.getMode() != null && standardReq.getMode() != "") {
                                    Element state = row.addElement("state");
                                    state.setText(standardReq.getMode());
                                }
                                Element relateSReq = row.addElement("relateSReq");
                                if(standardReq.getStandardRequirementId() != null) {
                                    relateSReq.setText(standardReq.getStandardRequirementId() + "");
                                }
                            }
                        }
                    }
                    else if(reqIsCorD.getTemplateType().contains("Event")) {
                        Element eventTable = table.addElement("eventTable");
                        for(StandardRequirement standardReq : domainConceptStandardReq) {
                            if(standardReq.getStandardReqVariable().equals(output.getConceptName())) {
                                Element row = eventTable.addElement("row");
                                Element assignment = row.addElement("assignment");
                                if(standardReq.getStandardReqValue() != null) {
                                    assignment.setText(standardReq.getStandardReqValue());
                                }
                                Element Event = row.addElement("event");
                                if(standardReq.getStandardReqEvent() != null) {
                                    Event.setText(LogicSymbolToString.processString(standardReq.getStandardReqEvent()));
                                }

                                if(standardReq.getMode() != null && standardReq.getMode() != "") {
                                    Element state = row.addElement("state");
                                    state.setText(standardReq.getMode());
                                }
                                Element relateSReq = row.addElement("relateSReq");
                                if(standardReq.getStandardRequirementId() != null) {
                                    relateSReq.setText(standardReq.getStandardRequirementId() + "");
                                }
                            }
                        }
                    }

                }


                //添加table属性 tableVariableType
                Element tableVariableType = table.addElement("tableVariableType");
                if(output.getConceptDatatype() != null) {
                    tableVariableType.setText(output.getConceptDatatype());
                }
                //添加table属性 initialValue
                Element  initialValue = table.addElement("initialValue");
                if(output.getConceptValue() != null) {
                    initialValue.setText(output.getConceptValue());
                }
                //添加table属性 range
                Element range = table.addElement("range");
                if(output.getConceptRange() != null) {
                    range.setText(output.getConceptRange());
                }
                //添加table属性accuracy
                Element accuracy = table.addElement("accuracy");
                if(output.getConceptAccuracy() != null) {
                    accuracy.setText(output.getConceptAccuracy());
                }

            }
        }

        //将领域概念模式集、模式和模式转换信息 转换为 模式转换表
        List<ModeClass> domainConceptMC = daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(systemId);
        List<Mode> domainConceptMode = daoHandler.getDaoService(ModeService.class).listModeBySystemId(systemId);
        List<StateMachine> domainConceptStateM = daoHandler.getDaoService(StateMachineService.class).listStateMachineBySystemId(systemId);

        //添加节点stateMachines
        Element stateMachines = vrmModel.addElement("stateMachines");
        if(domainConceptMC.isEmpty() == false) {
            for(ModeClass mc : domainConceptMC) {
                //添加节点stateMachine
                Element stateMachine = stateMachines.addElement("stateMachine");
                if(mc.getModeClassName() != null) {
                    stateMachine.addAttribute("name", mc.getModeClassName());
                }
                //添加节点description
                Element description = stateMachine.addElement("description");
                if(mc.getModeClassDescription() != null) {
                    description.setText(mc.getModeClassDescription());
                }

                //添加节点stateList
                Element stateList = stateMachine.addElement("stateList");
                for(Mode m : domainConceptMode) {
                    if(m.getModeClassName().equals(mc.getModeClassName())) {
                        //添加节点state
                        Element state = stateList.addElement("state");
                        //添加四个属性name，initial，final，enum；
                        if(m.getModeName() != null) {
                            state.addAttribute("name", m.getModeName());
                        }
                        if(m.getInitialStatus() != null) {
                            state.addAttribute("initial", m.getInitialStatus().toString());
                        }
                        if(m.getFinalStatus() != null) {
                            state.addAttribute("final", m.getFinalStatus().toString());
                        }
                        if(m.getValue() != null) {
                            state.addAttribute("enum", m.getValue().toString());
                        }
                    }
                }

                //添加节点stateTransition
                Element stateTransition = stateMachine.addElement("stateTransition");
                for(StateMachine stateM : domainConceptStateM) {
                    if(stateM.getDependencyModeClass().equals(mc.getModeClassName())) {
                        //添加节点row  stateM.getDependencyModeClass().equals(mc.getModeClassName())
                        Element row = stateTransition.addElement("row");
                        Element source = row.addElement("source");
                        if(stateM.getSourceState() != null) {
                            source.setText(stateM.getSourceState());
                        }
                        Element Event = row.addElement("event");
                        if(stateM.getEvent() != null) {
                            Event.setText(LogicSymbolToString.processString(stateM.getEvent()));
                        }
                        Element destinate = row.addElement("destination");
                        if(stateM.getEndState() != null) {
                            destinate.setText(stateM.getEndState());
                        }
                    }
                }
            }
        }


        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");

            String url =PathUtils.ProjectPath()+File.separator;
            fileName = url.concat(fileName).concat(".xml");

            XMLWriter writer = null;
            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                writer = new XMLWriter(fos, format);
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                writer.write(vrmDocument);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}

