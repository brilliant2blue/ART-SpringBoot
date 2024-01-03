package com.nuaa.art.vrmcheck.service.table.impl;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.ModuleTree;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;
import com.nuaa.art.vrmcheck.service.table.ModuleCheck;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleCheckImpl implements ModuleCheck {

    /**
     * 检查模块完整性和一致性
     * 模块完整性：引入层次化语义后，为保持建模元素保持原有的语义而做出的约束。
     * 有效输入变量必须享有输入端口。
     * 有效中间变量必须享有输入端口和输出端口。
     * 有效输出变量必须享有输出端口。
     *
     * 模块一致性：为避免引入层次化语义带来的错误，应对建模元素的使用上做出的约束。
     * 有效输入变量的输出端口为空
     * 有效中间变量的输出端口唯一
     * 有效输出变量的输出端口唯一
     *
     * @param vrm      变量关系模型
     * @param reporter 错误报告记录
     */
    @Override
    public void checkModuleIntegrityAndConsistency(HVRM vrm, CheckErrorReporter reporter) {
        for(VariableWithPort iv: vrm.getInputs()){
            if(iv.getInPort().isEmpty()){
                ErrorRefresh(reporter);
                LogUtils.info("输入变量"+iv.getConceptName()+"违反模块完整性");
                String outputString = "错误定位：输入变量" + iv.getConceptName() + "\n错误内容：违反模块完整性。"
                       + "有效的输入变量未关联任何模块的输入端口！";
                reporter.addErrorList(new CheckErrorInfo(reporter.errorCount, CheckErrorType.ModuleIntegrityInputInPort,iv.getConceptName(),outputString));
            }
            if(!iv.getOutPort().isEmpty()){
                ErrorRefresh(reporter);
                LogUtils.info("输入变量"+iv.getConceptName()+"违反模块一致性。\n 享有了模块"+iv.getOutPort()+"的输出端口");
                String outputString = "错误定位：输入变量" + iv.getConceptName() + "\n错误内容：违反模块一致性。\n"
                        + "享有了模块";
                for(Integer i: iv.getOutPort()){
                    outputString += getModuleNameByModuleId(vrm.getModules(), i);
                }
                outputString += "的输出端口！";
                reporter.addErrorList(new CheckErrorInfo(reporter.errorCount, CheckErrorType.ModuleConsistencyInputOutPort,iv.getConceptName(),outputString));
            }
        }

        for(VariableWithPort tv: vrm.getTerms()){
            if(tv.getInPort().isEmpty()){
                ErrorRefresh(reporter);
                LogUtils.info("中间变量"+tv.getConceptName()+"违反模块完整性， 未享有输入端口");
                String outputString = "错误定位：中间变量" + tv.getConceptName() + "\n错误内容：违反模块完整性。"
                        + "有效的中间变量未享有任何模块的输入端口！";
                reporter.addErrorList(new CheckErrorInfo(reporter.errorCount, CheckErrorType.ModuleIntegrityTermInPort,tv.getConceptName(),outputString));
            }
            if(tv.getOutPort().isEmpty()){
                ErrorRefresh(reporter);
                LogUtils.info("中间变量"+tv.getConceptName()+"违反模块完整性。\n 未享有输出端口");
                String outputString = "错误定位：中间变量" + tv.getConceptName() + "\n错误内容：违反模块完整性。"
                        + "有效的中间变量未享有任何模块的输出端口！";
                reporter.addErrorList(new CheckErrorInfo(reporter.errorCount, CheckErrorType.ModuleIntegrityTermOutPort,tv.getConceptName(),outputString));
            } else if(tv.getOutPort().size() > 1){
                ErrorRefresh(reporter);
                LogUtils.info("中间变量"+tv.getConceptName()+"违反模块一致性。\n 享有输出端口不唯一");
                String outputString = "错误定位：中间变量" + tv.getConceptName() + "\n错误内容：违反模块一致性。\n"
                        + "同时享有了模块";
                for(Integer i: tv.getOutPort()){
                    outputString += getModuleNameByModuleId(vrm.getModules(), i);
                }
                outputString += "的输出端口！";
                reporter.addErrorList(new CheckErrorInfo(reporter.errorCount, CheckErrorType.ModuleConsistencyTermOutPort,tv.getConceptName(),outputString));
            }
        }
        for(VariableWithPort ov: vrm.getOutputs()){
            if(ov.getOutPort().isEmpty()){
                ErrorRefresh(reporter);
                LogUtils.info("输出变量"+ov.getConceptName()+"违反模块完整性。\n 未享有输出端口");
                String outputString = "错误定位：输出变量" + ov.getConceptName() + "\n错误内容：违反模块完整性。"
                        + "有效的输出变量未享有任何模块的输出端口！";
                reporter.addErrorList(new CheckErrorInfo(reporter.errorCount, CheckErrorType.ModuleIntegrityOutputOutPort,ov.getConceptName(),outputString));
            } else if(ov.getOutPort().size() > 1){
                ErrorRefresh(reporter);
                LogUtils.info("输出变量"+ov.getConceptName()+"违反模块一致性。\n 享有输出端口不唯一");
                String outputString = "错误定位：输出变量" + ov.getConceptName() + "\n错误内容：违反模块一致性。\n"
                        + "同时享有了模块";
                for(Integer i: ov.getOutPort()){
                    outputString += getModuleNameByModuleId(vrm.getModules(), i);
                }
                outputString += "的输出端口！";
                reporter.addErrorList(new CheckErrorInfo(reporter.errorCount, CheckErrorType.ModuleConsistencyOutputOutPort,ov.getConceptName(),outputString));
            }
        }
    }

    String getModuleNameByModuleId(List<? extends ModuleTree> moduleTree, Integer id){
        if(moduleTree == null || moduleTree.isEmpty()) return "";
        if(id.equals(0)) return "默认模块";
        else{
            for(ModuleTree module: moduleTree){
                if(module.getId().equals(id)) return module.getName();
                String s =  getModuleNameByModuleId(module.getChildren(), id);
                if(!s.isBlank()) return s;
            }
        }
        return "";
    }

    void ErrorRefresh(CheckErrorReporter errorReporter){
        errorReporter.setModuleRight(false);
        errorReporter.addErrorCount();
    }
}
