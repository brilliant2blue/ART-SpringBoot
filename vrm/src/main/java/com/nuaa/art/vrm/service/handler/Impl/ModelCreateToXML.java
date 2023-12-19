package com.nuaa.art.vrm.service.handler.impl;


import com.nuaa.art.common.utils.FileUtils;
import com.nuaa.art.common.utils.PathUtils;
import com.nuaa.art.vrm.common.utils.VrmModuleXml;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.model.hvrm.ModuleTree;
import com.nuaa.art.vrm.model.hvrm.TableOfModule;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.dao.DaoHandler;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;

import jakarta.annotation.Resource;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

/**
 * 模型实例化创建程序
 *
 * @author konsin
 * @date 2023/06/13
 */
@Service("vrm-xml")
public class ModelCreateToXML implements ModelCreateHandler {
    @Resource
    DaoHandler daoHandler;

    @Resource(name = "vrm-object")
    ModelCreateHandler createHandler;

    @Resource
    VrmModuleXml vrmTool;


    /**
     * 创建模型
     * 模型文件为精简文件，在本机可直接使用
     *
     * @param systemId 工程编号
     * @return {@link String} 导出文件名
     */
    @Override
    public HVRM createModel(Integer systemId) {
        HVRM vrm = (HVRM) createHandler.createModel(systemId);

        Document vrmDocument = DocumentHelper.createDocument();
        //创建根节点vrmModel
        Element vrmModel = vrmDocument.addElement("vrmModel");
        vrmModel.addAttribute("version", "2.0");
        Element lastSaved = vrmModel.addElement("lastSaved");
        Element modelNamElement = lastSaved.addElement("name");

        modelNamElement.addText(vrm.getSystem().getSystemName());

        Element updateDateElement = lastSaved.addElement("date");
        updateDateElement.addText(vrm.getDate());
//        //将领域概念原始自然语言转换为vrm模型中的自然语言需求并存储到xml文件中
//        List<NaturalLanguageRequirement> domainConceptReq = daoHandler.getDaoService(NaturalLanguageRequirementService.class)
//                .listNaturalLanguageRequirementBySystemId(systemId);
//        //添加节点requirements
//        Element requirements = vrmModel.addElement("requirements");
//        if (!domainConceptReq.isEmpty()) {
//            for (NaturalLanguageRequirement req : domainConceptReq) {
//                //添加节点requirement
//                requirements.add(vrmTool.NReq(req));
//            }
//        }

        //将领域概念常量转换为数据字典常量并存储到xml文件中
        //添加节点constants
        Element constants = vrmModel.addElement("constants");

            for (ConceptLibrary conLibrary : vrm.getConstants()) {
                //添加节点constant
               constants.add(vrmTool.Constant(conLibrary));
            }


        //将领域概念数据类型转换为数据字典数据类型并存储到xml文件中
        //添加节点types
        Element types = vrmModel.addElement("types");
        for (Type t : vrm.getTypes()) {
            //添加节点type
            types.add(vrmTool.Type(t));
        }

        //将领域概念输入变量转换为数据字典输入变量并输出到xml文件中
        //添加节点 inputs
        Element inputs = vrmModel.addElement("inputs");

        for (VariableWithPort iv : vrm.getInputs()) {
            //添加节点input
            inputs.add(vrmTool.Input(iv));
        }

        //将领域概念中间变量转换为数据字典中间变量并输出到xml文件中
        //添加节点 terms
        Element terms = vrmModel.addElement("terms");

        for (VariableWithPort tv : vrm.getTerms()) {
            //添加节点input
            terms.add(vrmTool.Term(tv));
        }

        //将领域概念输出变量转换为数据字典输出变量并输出到xml文件中
        //添加节点 outputs
        Element outputs = vrmModel.addElement("outputs");

        for (VariableWithPort ov : vrm.getOutputs()) {
            //添加节点input
            outputs.add(vrmTool.Output(ov));
        }

        Element modules = vrmModel.addElement("modules");
        for(ModuleTree module: vrm.getModules()){
            modules.add(vrmTool.Module(module));
        }

        //添加节点tables
        Element tables = vrmModel.addElement("tables");
        for(TableOfModule t : vrm.getConditions()){
            tables.add(vrmTool.Condition(t));
        }
        for(TableOfModule t : vrm.getEvents()){
            tables.add(vrmTool.Event(t));
        }

        //添加节点stateMachines
        Element stateMachines = vrmModel.addElement("stateMachines");
        for(ModeClassOfVRM mc : vrm.getModeClass()) {
            stateMachines.add(vrmTool.ModeClassVRM(mc));
        }

        String fileName = PathUtils.DefaultPath() + vrm.getSystem().getSystemName() + "model.xml";

        if(FileUtils.saveXML(vrmDocument,fileName)){
            return vrm;
        } else {
            return null;
        }

    }


    /**
     * 导出模型
     * 模型文件为完全文件，可分享其他设备使用
     *
     * @param systemId 系统标识
     * @param fileUrl 文件名称
     * @return {@link String} 导出文件名
     */
    @Override
    public String modelFile(Integer systemId, String fileUrl) {
        HVRM vrm = (HVRM) createHandler.createModel(systemId);

        Document vrmDocument = DocumentHelper.createDocument();
        //创建根节点vrmModel
        Element vrmModel = vrmDocument.addElement("vrmModel");
        vrmModel.addAttribute("version", "2.0");
        Element lastSaved = vrmModel.addElement("lastSaved");
        Element modelNamElement = lastSaved.addElement("name");

        modelNamElement.addText(vrm.getSystem().getSystemName());

        Element updateDateElement = lastSaved.addElement("date");
        updateDateElement.addText(vrm.getDate());
        //将领域概念原始自然语言转换为vrm模型中的自然语言需求并存储到xml文件中
        List<NaturalLanguageRequirement> domainConceptReq = daoHandler.getDaoService(NaturalLanguageRequirementService.class)
                .listNaturalLanguageRequirementBySystemId(systemId);
        //添加节点requirements
        Element requirements = vrmModel.addElement("requirements");
        if (!domainConceptReq.isEmpty()) {
            for (NaturalLanguageRequirement req : domainConceptReq) {
                //添加节点requirement
                requirements.add(vrmTool.NReq(req));
            }
        }

        //将规范化需求输出到XML文件中
        List<StandardRequirement> domainConStandardReq = daoHandler.getDaoService(StandardRequirementService.class).listStandardRequirementBySystemId(systemId);
        //添加节点standardRequirements
        Element standardRequirements = vrmModel.addElement("standardRequirements");

        for(StandardRequirement sReq : domainConStandardReq) {
            standardRequirements.add(vrmTool.SReq(sReq));

        }

        //将专有名词输出到XML文件中
        List<ProperNoun> domainConProperNoun=daoHandler.getDaoService(ProperNounService.class).listProperNounBySystemId(systemId);
        //添加节点properNouns
        Element properNouns=vrmModel.addElement("properNouns");

        for(ProperNoun prop:domainConProperNoun) {
                properNouns.add(vrmTool.ProperNoun(prop));
            }


        //将领域概念常量转换为数据字典常量并存储到xml文件中
        //添加节点constants
        Element constants = vrmModel.addElement("constants");

        for (ConceptLibrary conLibrary : vrm.getConstants()) {
            //添加节点constant
            constants.add(vrmTool.Constant(conLibrary));
        }


        //将领域概念数据类型转换为数据字典数据类型并存储到xml文件中
        //添加节点types
        Element types = vrmModel.addElement("types");
        for (Type t : vrm.getTypes()) {
            //添加节点type
            types.add(vrmTool.Type(t));
        }

        //将领域概念输入变量转换为数据字典输入变量并输出到xml文件中
        //添加节点 inputs
        Element inputs = vrmModel.addElement("inputs");

        for (VariableWithPort iv : vrm.getInputs()) {
            //添加节点input
            inputs.add(vrmTool.Input(iv));
        }

        //将领域概念中间变量转换为数据字典中间变量并输出到xml文件中
        //添加节点 terms
        Element terms = vrmModel.addElement("terms");

        for (VariableWithPort tv : vrm.getTerms()) {
            //添加节点input
            terms.add(vrmTool.Term(tv));
        }

        //将领域概念输出变量转换为数据字典输出变量并输出到xml文件中
        //添加节点 outputs
        Element outputs = vrmModel.addElement("outputs");

        for (VariableWithPort ov : vrm.getOutputs()) {
            //添加节点input
            outputs.add(vrmTool.Output(ov));
        }

        Element modules = vrmModel.addElement("modules");
        for(ModuleTree module: vrm.getModules()){
            modules.add(vrmTool.Module(module));
        }


        //添加节点tables
        Element tables = vrmModel.addElement("tables");
        for(TableOfModule t : vrm.getConditions()){
            tables.add(vrmTool.Condition(t));
        }
        for(TableOfModule t : vrm.getEvents()){
            tables.add(vrmTool.Event(t));
        }

        //添加节点stateMachines
        Element stateMachines = vrmModel.addElement("stateMachines");
        for(ModeClassOfVRM mc : vrm.getModeClass()) {
            stateMachines.add(vrmTool.ModeClassVRM(mc));
        }

        if(FileUtils.saveXML(vrmDocument,fileUrl)){
            return fileUrl;
        } else {
            return null;
        }

    }

}

