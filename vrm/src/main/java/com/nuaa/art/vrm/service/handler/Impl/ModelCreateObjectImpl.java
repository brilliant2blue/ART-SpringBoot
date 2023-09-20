package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.vrm.common.utils.VrmXml;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.model.*;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import jakarta.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



import java.util.ArrayList;

import java.util.List;

/**
 * 模型对象与模型文件的转换
 *
 * @author konsin
 * @date 2023/09/04
 */
@Service("vrm-object")
public class ModelCreateObjectImpl implements ModelCreateHandler {

    @Resource
    DaoHandler daoHandler;

    @Resource
    VrmXml vrmTool;

    /**
     * 创建模型对象
     *
     * @param systemId 系统ID
     * @return {@link VariableRealationModel}
     */
    @Override
    public VariableRealationModel createModel(Integer systemId) {
        // 设定系统信息
        VariableRealationModel vrm = new VariableRealationModel();
        vrm.setSystem(daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId));

        // 设定类型信息
        List<Type> types = daoHandler.getDaoService(TypeService.class).listTypeBySystemId(systemId);
        vrm.setTypes((ArrayList<Type>) types);

        //设定常量信息
        List<ConceptLibrary> constants =
                daoHandler.getDaoService(ConceptLibraryService.class).listConstVariableBySystemId(systemId);
        vrm.setConstants((ArrayList<ConceptLibrary>) constants);

        // 设定输入信息
        List<ConceptLibrary> inputs =
                daoHandler.getDaoService(ConceptLibraryService.class).listInputVariableBySystemId(systemId);
        vrm.setInputs((ArrayList<ConceptLibrary>) inputs);

        // 设定中间变量信息
        List<ConceptLibrary> terms =
                daoHandler.getDaoService(ConceptLibraryService.class).listTermVariableBySystemId(systemId);
        vrm.setTerms((ArrayList<ConceptLibrary>) terms);

        //设定输出变量信息
        List<ConceptLibrary> outputs =
                daoHandler.getDaoService(ConceptLibraryService.class).listOutputVariableBySystemId(systemId);
        vrm.setOutputs((ArrayList<ConceptLibrary>) outputs);


        List<StandardRequirement> SReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(systemId);
        //处理生成中间变量相关表函数
        extractTable(vrm, terms, SReq);
        //处理生成输出变量相关表函数
        extractTable(vrm, outputs, SReq);

        //将领域概念模式集、模式和模式转换信息 转换为 模式转换表
        List<ModeClass> modeClasses = daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(systemId);
        List<Mode> modes = daoHandler.getDaoService(ModeService.class).listModeBySystemId(systemId);
        List<StateMachine> modeTrans = daoHandler.getDaoService(StateMachineService.class).listStateMachineBySystemId(systemId);

        for (ModeClass mc : modeClasses) {
            ModeClassOfVRM MC = new ModeClassOfVRM();
            MC.setModeClass(mc);

            //添加节点stateList
            for (Mode m : modes) {
                if (m.getModeClassName().equals(mc.getModeClassName())) {
                    MC.getModes().add(m);
                }
            }

            //添加节点stateTransition
            for (StateMachine stateM : modeTrans) {
                if (stateM.getDependencyModeClass().equals(mc.getModeClassName())) {
                    MC.getModeTrans().add(stateM);
                }
            }
            vrm.getModeClass().add(MC);
        }

        return vrm;
    }

    /**
     * 从模型文件得到模型实例
     *
     * @param systemId 系统ID
     * @param fileName 文件名文件名
     * @return {@link VariableRealationModel}
     */
    @Override
    public VariableRealationModel modelFile(Integer systemId, String fileName) {
        VariableRealationModel model = new VariableRealationModel();
        Document modelXML = FileUtils.readXML(fileName);
        Element root = null;
        if (modelXML != null) {
            root = modelXML.getRootElement();
        }

        Element sysNode = root.element("lastSaved");
        model.getSystem().setSystemName(sysNode.elementText("name"));
        model.setDate(sysNode.elementText("date"));

        Element typesNode = root.element("types");
        List<Element> typeList = typesNode.elements("type");
        for(Element item : typeList){
            model.getTypes().add(vrmTool.Type(item));
        }
        Element constantsNode = root.element("constants");
        List<Element> constantList = constantsNode.elements("constant");
        for(Element item : constantList){
            model.getConstants().add(vrmTool.Constant(item));
        }

        Element inputsNode = root.element("inputs");
        List<Element> inputs = inputsNode.elements("input");
        for(Element item : inputs) {
            model.getInputs().add(vrmTool.Input(item));
        }

        Element termsNode = root.element("terms");
        List<Element> terms = termsNode.elements("term");
        for(Element item : terms) {
            model.getTerms().add(vrmTool.Term(item));
        }

        Element outputsNode = root.element("outputs");
        List<Element> outputs = outputsNode.elements("output");
        for(Element item : outputs) {
            model.getOutputs().add(vrmTool.Output(item));
        }

        Element tablesNode = root.element("tables");
        List<Element> tables = tablesNode.elements("table");
        for(Element item: tables) {
            if(item.selectSingleNode("conditionTable") != null) {
                model.getConditions().add(vrmTool.Condition(item));
            } else {
                model.getEvents().add(vrmTool.Event(item));
            }
        }

        Element modeClassNode = root.element("stateMachines");
        List<Element> modeClassList = modeClassNode.elements("stateMachine");
        for(Element item : modeClassList){
            model.getModeClass().add(vrmTool.ModeClassVRM(item));
        }
        return model;
    }


    private void extractTable(VariableRealationModel vrm, List<ConceptLibrary> concept, List<StandardRequirement> SReq) {
        // todo 不确定是否只在一个规范化需求里，所以目前先不break
        for (ConceptLibrary item : concept) {
            ArrayList<TableRow> conditions = new ArrayList<>();
            ArrayList<TableRow> events = new ArrayList<>();
            for (StandardRequirement standardReq : SReq) {
                if (standardReq.getStandardReqVariable().equals(item.getConceptName())) {
                    TableRow row = new TableRow(standardReq);
                    if(standardReq.getTemplateType().contains("Condition"))
                        conditions.add(row);
                    else events.add(row);
                    //break;
                }
            }
            if(!conditions.isEmpty()) {
                TableOfVRM ct = new TableOfVRM();
                ct.setName(item.getConceptName());
                ct.setRelateVar(item);
                ct.setRows(conditions);
                vrm.getConditions().add(ct);
            }
            if(!events.isEmpty()) {
                TableOfVRM et = new TableOfVRM();
                et.setName(item.getConceptName());
                et.setRelateVar(item);
                et.setRows(events);
                vrm.getEvents().add(et);
            }
        }
    }
    
}
