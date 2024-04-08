package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.vrm.common.utils.ConditionTableUtils;
import com.nuaa.art.vrm.common.utils.EventTableUtils;
import com.nuaa.art.vrm.common.utils.VrmModuleXml;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.entity.Module;
import com.nuaa.art.vrm.model.ConditionItem;
import com.nuaa.art.vrm.model.ConditionTable;
import com.nuaa.art.vrm.model.EventItem;
import com.nuaa.art.vrm.model.EventTable;
import com.nuaa.art.vrm.model.hvrm.ModuleTree;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.hvrm.TableOfModule;
import com.nuaa.art.vrm.model.vrm.TableRow;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrm.service.handler.ModuleHandler;
import jakarta.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;



import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
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
    ModuleHandler moduleHandler;

    @Resource
    VrmModuleXml vrmTool;

    @Resource
    ConditionTableUtils ctool;
    @Resource
    EventTableUtils etool;

    /**
     * 创建模型对象
     *
     * @param systemId 系统ID
     * @return {@link HVRM}
     */
    @Override
    public HVRM createModel(Integer systemId) {
        // 设定系统信息
        HVRM vrm = new HVRM();
        vrm.creatDate();

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
        //vrm.setInputs((ArrayList<VariableWithPort>) inputs.stream().map(VariableWithPort::new).collect(Collectors.toList()));

        // 设定中间变量信息
        List<ConceptLibrary> terms =
                daoHandler.getDaoService(ConceptLibraryService.class).listTermVariableBySystemId(systemId);
        //vrm.setTerms((ArrayList<VariableWithPort>) terms.stream().map(VariableWithPort::new).collect(Collectors.toList()));

        //设定输出变量信息
        List<ConceptLibrary> outputs =
                daoHandler.getDaoService(ConceptLibraryService.class).listOutputVariableBySystemId(systemId);
        //vrm.setOutputs((ArrayList<VariableWithPort>) outputs.stream().map(VariableWithPort::new).collect(Collectors.toList()));

        List<Module> modules = daoHandler.getDaoService(ModuleService.class).listModulesBySystemId(systemId);
        vrm.setModules((ArrayList<ModuleTree>) moduleHandler.ModulesToModuleTree(modules));

        List<StandardRequirement> SReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(systemId);

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

        HashMap<String, HashSet<Integer>> inPort = new HashMap<>();
        HashMap<String, HashSet<Integer>> outPort = new HashMap<>();
        for (Module md: modules) {
            //处理生成中间变量相关表函数
            extractTable(vrm, md, terms, SReq, inPort, outPort);
            //处理生成输出变量相关表函数
            extractTable(vrm, md,outputs, SReq, inPort, outPort);
        }

        //处理未分配给模块的需求
        extractTable(vrm, null, terms, SReq, inPort, outPort);
        extractTable(vrm, null, outputs, SReq, inPort, outPort);

        getValidVariableFromModeTrans(vrm, terms, inPort);
        getValidVariableFromModeTrans(vrm, outputs, inPort);




        //填充有效的变量元素
        ArrayList<VariableWithPort> inputVar = new ArrayList<>();
        for(ConceptLibrary v : inputs){
            String name = v.getConceptName();
            if(inPort.containsKey(name)){
                VariableWithPort i = new VariableWithPort(v);
                i.setInPort(new ArrayList<>(inPort.get(name)));
                inputVar.add(i);
            }else if(v.isValid()){
                inputVar.add(new VariableWithPort(v));
            }
        }
        inputs = null;
        vrm.setInputs(inputVar);

        ArrayList<VariableWithPort> termVar = new ArrayList<>();
        for(ConceptLibrary v : terms){
            String name = v.getConceptName();
            boolean fi = inPort.containsKey(name);
            boolean fo = outPort.containsKey(name);
            if(fi || fo){
                VariableWithPort i = new VariableWithPort(v);
                if(fi) i.setInPort(new ArrayList<>(inPort.get(name)));
                if(fo) i.setOutPort(new ArrayList<>(outPort.get(name)));
                termVar.add(i);
            } else if(v.isValid()){
                termVar.add(new VariableWithPort(v));
            }
        }
        terms = null;
        vrm.setTerms(termVar);

        ArrayList<VariableWithPort> outputVar = new ArrayList<>();
        for(ConceptLibrary v : outputs){
            String name = v.getConceptName();
            boolean fi = inPort.containsKey(name);
            boolean fo = outPort.containsKey(name);
            if(fi || fo){
                VariableWithPort i = new VariableWithPort(v);
                if(fi) i.setInPort(new ArrayList<>(inPort.get(name)));
                if(fo) i.setOutPort(new ArrayList<>(outPort.get(name)));
                outputVar.add(i);
            } else if(v.isValid()){
                outputVar.add(new VariableWithPort(v));
            }
        }
        outputs = null;
        vrm.setOutputs(outputVar);



        return vrm;
    }

    /**
     * 从模型文件得到模型实例
     *
     * @param systemId 系统ID
     * @param fileName 文件名文件名
     * @return {@link HVRM}
     */
    @Override
    public HVRM modelFile(Integer systemId, String fileName) {
        HVRM model = new HVRM();
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

        Element modulesNode = root.element("modules");
        List<Element> modules = modulesNode.elements("module");
        for(Element item : modules) {
            model.getModules().add(vrmTool.Module(item));
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

    private void MapSetAction(HashMap<String, HashSet<Integer>> map, String key, Integer value){
        if(!map.containsKey(key)){
            map.put(key, new HashSet<>());
        }
        map.get(key).add(value);
    }

    private void getValidVariableFromModeTrans(HVRM vrm, List<ConceptLibrary> concept, HashMap<String, HashSet<Integer>> inPort) {
        for (ConceptLibrary item : concept) {
            ArrayList<TableRow> conditions = new ArrayList<>();
            ArrayList<TableRow> events = new ArrayList<>();
            Integer moduleId = -1; //表示 模块中使用
            for(ModeClassOfVRM mc : vrm.getModeClass()){
                for (StateMachine sm: mc.getModeTrans()) {
                    // 填充输入端口
                    EventTable es = etool.ConvertStringToTable(sm.getEvent());
                    for(EventItem ei: es.getEvents()){
                        if(ei.getEventCondition() != null)
                            for(ConditionItem ci: ei.getEventCondition().getConditionItems()){
                                MapSetAction(inPort, ci.getVar1(), moduleId);
                                MapSetAction(inPort, ci.getVar2(), moduleId);
                            }
                        if(ei.getGuardCondition() != null)
                            for(ConditionItem ci: ei.getGuardCondition().getConditionItems()){
                                MapSetAction(inPort, ci.getVar1(), moduleId);
                                MapSetAction(inPort, ci.getVar2(), moduleId);
                            }
                    }
                }
            }
        }
    }

    // 读取数据库生成表函数。同时读取每一行，填充变量的输入输出端口。
    private void extractTable(HVRM vrm, Module module, List<ConceptLibrary> concept, List<StandardRequirement> SReq,
                              HashMap<String, HashSet<Integer>> inPort, HashMap<String, HashSet<Integer>> outPort) {
        Integer moduleId  = 0;
        String moduleName = "";
        if(module != null){
             moduleId = module.getId();
             moduleName = module.getName();
        }
        for (ConceptLibrary item : concept) {
            ArrayList<TableRow> conditions = new ArrayList<>();
            ArrayList<TableRow> events = new ArrayList<>();
            for (StandardRequirement standardReq : SReq) {
                if (standardReq.getStandardReqVariable().equals(item.getConceptName())&&
                        (
                                (standardReq.getModuleId() != null && standardReq.getModuleId().equals(moduleId))
                                        || (standardReq.getModuleId() == null && moduleId == 0)
                        )) {
                    MapSetAction(outPort, item.getConceptName(), moduleId);
                    TableRow row = new TableRow(standardReq);
                    if(standardReq.getTemplateType().contains("Condition")){
                        conditions.add(row);
                        // 填充输入端口
                        ConditionTable cs = ctool.ConvertStringToTable(standardReq.getStandardReqCondition());
                        for(ConditionItem ci: cs.getConditionItems()){
                            MapSetAction(inPort, ci.getVar1(), moduleId);
                            MapSetAction(inPort, ci.getVar2(), moduleId); //不考虑是变量还是取值，空间换时间. 注意，这条是只考虑原子条件不包含表达式的情况
                        }
                    }
                    else {
                        events.add(row);
                        // 填充输入端口
                        EventTable es = etool.ConvertStringToTable(standardReq.getStandardReqEvent());
                        for(EventItem ei: es.getEvents()){
                            if(ei.getEventCondition() != null)
                                for(ConditionItem ci: ei.getEventCondition().getConditionItems()){
                                    MapSetAction(inPort, ci.getVar1(), moduleId);
                                    MapSetAction(inPort, ci.getVar2(), moduleId);
                                }
                            if(ei.getGuardCondition() != null)
                                for(ConditionItem ci: ei.getGuardCondition().getConditionItems()){
                                    MapSetAction(inPort, ci.getVar1(), moduleId);
                                    MapSetAction(inPort, ci.getVar2(), moduleId);
                                }
                        }
                    }
                    //break;
                }
            }
            if(!conditions.isEmpty()) {
                TableOfModule ct = new TableOfModule();
                ct.setName(item.getConceptName());
                ct.setModuleId(moduleId);
                ct.setModule(moduleName);
                ct.setRelateVar(item);
                ct.setRows(conditions);
                vrm.getConditions().add(ct);
            }
            if(!events.isEmpty()) {
                TableOfModule et = new TableOfModule();
                et.setName(item.getConceptName());
                et.setModuleId(moduleId);
                et.setModule(moduleName);
                et.setRelateVar(item);
                et.setRows(events);
                vrm.getEvents().add(et);
            }
        }
    }
    
}
