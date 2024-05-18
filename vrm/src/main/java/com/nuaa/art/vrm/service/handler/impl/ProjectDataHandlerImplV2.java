package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.ListUtils;
import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.common.websocket.WebSocketService;
import com.nuaa.art.vrm.common.utils.EntityXmlConvert;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.entity.Module;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.handler.ProjectDataHandler;
import jakarta.annotation.Resource;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("projectV2")
public class ProjectDataHandlerImplV2 implements ProjectDataHandler {
    @Resource
    DaoHandler daoHandler;

    @Resource(name = "entityXmlConvert")
    EntityXmlConvert xmlConvert;

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
            Element modules = root.element("modules");
            Element requirements = root.element("requirements");
            Element variables = root.element("variables");
            Element types = root.element("types");
            Element props = root.element("props");
            Element modeClasses = root.element("modeClasses");
            Element modes = root.element("modes");
            Element standards = root.element("standards");
            Element stms = root.element("stms");

            webSocketService.sendMsg(SocketMessage.asText("project","模块导入..."));
            List<Element> moduleList = modules.elements("module");
            HashMap<Integer,Integer> moduleIdMap  = new HashMap<>();
            Queue<Module> moduleQueue = new LinkedList<>();
            for(Element element: moduleList){
                Module tmp = xmlConvert.Module(element);
                if(tmp.getParentId()==null||tmp.getParentId().equals(0)){
                    int pre = tmp.getId();
                    tmp.setId(null);
                    tmp.setSystemId(systemId);
                    daoHandler.getDaoService(ModuleService.class).insertModule(tmp);
                    moduleIdMap.put(pre,tmp.getId());
                } else {
                    moduleQueue.offer(tmp);
                }
            }

            while(!moduleQueue.isEmpty()){
                Module tmp = moduleQueue.poll();
                if(moduleIdMap.containsKey(tmp.getParentId())){
                    int pre = tmp.getId();
                    tmp.setId(null);
                    tmp.setSystemId(systemId);
                    tmp.setParentId(moduleIdMap.get(tmp.getParentId()));
                    daoHandler.getDaoService(ModuleService.class).insertModule(tmp);
                    moduleIdMap.put(pre,tmp.getId());
                } else {
                    moduleQueue.offer(tmp);
                }
            }
            moduleQueue = null;


            LogUtils.info("原始需求导入");
            webSocketService.sendMsg(SocketMessage.asText("project","原始需求导入..."));
            List<Element> requirementList = requirements.elements("requirement");
            HashMap<Integer,Integer> reqIdMap  = new HashMap<>();
            for (Element requirementNode : requirementList) {
                NaturalLanguageRequirement requirement = xmlConvert.NReq(requirementNode);
                int old = requirement.getReqId();
                Integer oldm = requirement.getModuleId();
                requirement.setReqId(null);
                requirement.setSystemId(systemId);
                if(oldm!=null){
                    requirement.setModuleId(moduleIdMap.get(oldm));
                }
                daoHandler.getDaoService(NaturalLanguageRequirementService.class).insertNLR(requirement);
                reqIdMap.put(old,requirement.getReqId());
            }

            Thread.sleep(800);

            LogUtils.info("数据类型导入");
            webSocketService.sendMsg(SocketMessage.asText("project","数据类型导入..."));
            List<Element> typeList = types.elements("type");
            for (Element typeNode : typeList) {
                Type type = xmlConvert.Type(typeNode);
                type.setTypeId(null);
                type.setSystemId(systemId);
                daoHandler.getDaoService(TypeService.class).insertType(type);
            }

            Thread.sleep(800);

            LogUtils.info("专有名词导入");
            webSocketService.sendMsg(SocketMessage.asText("project","专有名词导入..."));
            List<Element> propList = props.elements("prop");
            for (Element propNode : propList) {
                ProperNoun prop = xmlConvert.ProperNoun(propNode);
                prop.setProperNounId(null);
                prop.setSystemId(systemId);
                daoHandler.getDaoService(ProperNounService.class).insertProperNoun(prop);
            }

            Thread.sleep(800);

            LogUtils.info("变量导入");
            webSocketService.sendMsg(SocketMessage.asText("project","变量导入..."));
            List<Element> variableList = variables.elements("variable");

            for (Element variableNode : variableList) {
                ConceptLibrary variable = xmlConvert.Variable(variableNode);
                variable.setConceptId(null);
                variable.setSystemId(systemId);
                if(!variable.getSourceReqId().isBlank()){
                    List<Integer> old = ListUtils.StringToNumArray(variable.getSourceReqId());
                    ArrayList<Integer> news = new ArrayList<>(old.size());
                    for(Integer id:old){
                        news.add(reqIdMap.get(id));
                    }
                    variable.setSourceReqId(ListUtils.NumArrayToString(news));
                }
                daoHandler.getDaoService(ConceptLibraryService.class).insertConcept(variable);
            }

            Thread.sleep(800);

            LogUtils.info("模式集导入");
            webSocketService.sendMsg(SocketMessage.asText("project","模式集导入..."));
            List<Element> modeClassList = modeClasses.elements("modeClass");
            Map<String, Integer> modeClassMap = new HashMap<>();
            for (Element modeClassNode : modeClassList) {
                ModeClass modeClass = xmlConvert.ModeClass(modeClassNode);
                modeClass.setModeClassId(null);
                modeClass.setSystemId(systemId);
                daoHandler.getDaoService(ModeClassService.class).insertModeClass(modeClass);
                modeClassMap.put(modeClass.getModeClassName(), modeClass.getModeClassId());
            }

            Thread.sleep(800);

            LogUtils.info("模式导入");
            webSocketService.sendMsg(SocketMessage.asText("project","模式导入..."));
            List<Element> modeList = modes.elements("mode");
            for (Element modeNode : modeList) {
                Mode mode = xmlConvert.Mode(modeNode);
                mode.setModeId(null);
                String modeClassName = mode.getModeClassName();
                Integer dependId = modeClassMap.get(modeClassName);
                mode.setModeClassId(dependId);
                mode.setSystemId(systemId);
                daoHandler.getDaoService(ModeService.class).insertMode(mode);
            }

            Thread.sleep(800);

            LogUtils.info("规范化需求导入");
            webSocketService.sendMsg(SocketMessage.asText("project","规范化需求导入..."));
            List<Element> standardList = standards.elements("standard");
            for (Element standardNode : standardList) {
                StandardRequirement standard = xmlConvert.SReq(standardNode);
                standard.setStandardRequirementId(null);
                standard.setModuleId(moduleIdMap.get(standard.getModuleId()));
                standard.setNaturalLanguageReqId(reqIdMap.get(standard.getNaturalLanguageReqId()));

                standard.setSystemId(systemId);
                daoHandler.getDaoService(StandardRequirementService.class).insertStandardRequirement(standard);
            }

            Thread.sleep(800);

            LogUtils.info("模式转换导入");
            webSocketService.sendMsg(SocketMessage.asText("project","模式转换导入..."));
            List<Element> stmList = stms.elements("stm");
            for (Element stmNode : stmList) {
                StateMachine stm = xmlConvert.ModeTrans(stmNode);
                stm.setStateMachineId(null);
                String modeClassName = stm.getDependencyModeClass();
                Integer dependId = modeClassMap.get(modeClassName);
                stm.setSystemId(systemId);
                stm.setDependencyModeClassId(dependId);
                daoHandler.getDaoService(StateMachineService.class).insertStateMachine(stm);
            }

            Thread.sleep(800);
            LogUtils.info("需求工程导入成功！");
            webSocketService.sendMsg(SocketMessage.asText("project", String.format("需求工程:%s, 导入成功！", projectName)));
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
                daoHandler.getDaoService(ModuleService.class).deleteModulesBySystemId(systemId);
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

            // 导出模块
            List<Module> modules = daoHandler.getDaoService(ModuleService.class).listModulesBySystemId(systemId);
            Element modulesNode=systemProject.addElement("modules");//添加节点requirements
            for(Module module: modules) {
                modulesNode.add(xmlConvert.Module(module));
            }

            //读取数据库所有属于该需求工程的元素
            List<NaturalLanguageRequirement> requirements=daoHandler.getDaoService(NaturalLanguageRequirementService.class)
                    .listNaturalLanguageRequirementBySystemId(systemId);
            //存储原始需求
            Element requirementsNode=systemProject.addElement("requirements");//添加节点requirements
            for(NaturalLanguageRequirement requirement:requirements) {
                requirementsNode.add(xmlConvert.NReq(requirement));
            }
            requirements = null;

            //存储变量
            List<ConceptLibrary> variables=daoHandler.getDaoService(ConceptLibraryService.class).listAllConceptBySystemId(systemId);
            Element variablesNode=systemProject.addElement("variables");//添加节点variables
            for(ConceptLibrary variable:variables) {
                variablesNode.add(xmlConvert.Variable(variable));
            }
            variables=null;

            //存储数据类型
            List<Type> types=daoHandler.getDaoService(TypeService.class).listTypeBySystemId(systemId);
            Element typesNode=systemProject.addElement("types");//添加节点types
            for(Type type:types) {
                typesNode.add(xmlConvert.Type(type));
            }
            types=null;

            //存储专有名词
            List<ProperNoun> props=daoHandler.getDaoService(ProperNounService.class).listProperNounBySystemId(systemId);
            Element propsNode=systemProject.addElement("props");//添加节点props
            for(ProperNoun prop:props) {
                propsNode.add(xmlConvert.ProperNoun(prop));
            }
            props=null;

            //存储模式集
            List<ModeClass> modeClasses=daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(systemId);
            Element modeClassesNode=systemProject.addElement("modeClasses");//添加节点modeClasses
            for(ModeClass modeClass:modeClasses) {
                modeClassesNode.add(xmlConvert.ModeClass(modeClass));
            }
            modeClasses=null;

            //存储模式
            List<Mode> modes=daoHandler.getDaoService(ModeService.class).listModeBySystemId(systemId);
            Element modesNode=systemProject.addElement("modes");//添加节点modes
            for(Mode mode:modes) {
               modesNode.add(xmlConvert.Mode(mode));
            }
            modes=null;

            //存储需求规范化
            List<StandardRequirement> standards=daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(systemId);
            Element standardsNode=systemProject.addElement("standards");//添加节点standards
            for(StandardRequirement standard:standards) {
                standardsNode.add(xmlConvert.SReq(standard));
            }
            standards=null;

            //存储模式转换
            List<StateMachine> stms=daoHandler.getDaoService(StateMachineService.class).listStateMachineBySystemId(systemId);
            Element stmsNode=systemProject.addElement("stms");//添加节点stms
            for(StateMachine stm:stms) {
                stmsNode.add(xmlConvert.ModeTrans(stm));
            }
            stms=null;


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
            daoHandler.getDaoService(ModuleService.class).deleteModulesBySystemId(systemId);

            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
