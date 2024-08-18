package com.nuaa.art.vrmverify.common.utils;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.VariableWithPort;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrmverify.model.ModelPartsWithProperties;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

/**
 * 将信息记录到文件中
 * @author djl
 * @date 2024-07-27
 */
public class RecordsUtils {

    /**
     * 将信息记录到文件中
     * @param fileName
     * @param records
     */
    public static void writeRecords2File(String fileName, String records) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(PathUtils.getRecordsPath(fileName), StandardCharsets.UTF_8))) {
            bw.write(records);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage());
        }
    }

    public static String HVRMs2Str(Map<String, HVRM> parts) {
        StringBuilder str = new StringBuilder();
        for (String name : parts.keySet()) {
            str.append(name).append("\n");
            HVRM hvrm = parts.get(name);
            str.append("\t输入变量个数：")
                    .append(hvrm.inputs.size())
                    .append("\n\t输入变量列表：")
                    .append(hvrm.inputs.stream().map(VariableWithPort::getConceptName).toList())
                    .append("\n\t中间变量个数：")
                    .append(hvrm.terms.size())
                    .append("\n\t中间变量列表：")
                    .append(hvrm.terms.stream().map(VariableWithPort::getConceptName).toList())
                    .append("\n\t输出变量个数：")
                    .append(hvrm.outputs.size())
                    .append("\n\t输出变量列表：")
                    .append(hvrm.outputs.stream().map(VariableWithPort::getConceptName).toList())
                    .append("\n\t模式集个数：")
                    .append(hvrm.modeClass.size())
                    .append("\n\t模式集列表：")
                    .append(hvrm.modeClass.stream().map(mc -> mc.getModeClass().getModeClassName()).toList())
                    .append("\n\n");
        }
        return str.toString();
    }

    public static String HVRMs2Str(List<ModelPartsWithProperties> parts) {
        StringBuilder str = new StringBuilder();
        for (ModelPartsWithProperties part : parts) {
            str.append(part.getName()).append("\n");
            HVRM hvrm = part.getModel();
            str.append("\t输入变量个数：")
                    .append(hvrm.inputs.size())
                    .append("\n\t输入变量列表：")
                    .append(hvrm.inputs.stream().map(VariableWithPort::getConceptName).toList())
                    .append("\n\t中间变量个数：")
                    .append(hvrm.terms.size())
                    .append("\n\t中间变量列表：")
                    .append(hvrm.terms.stream().map(VariableWithPort::getConceptName).toList())
                    .append("\n\t输出变量个数：")
                    .append(hvrm.outputs.size())
                    .append("\n\t输出变量列表：")
                    .append(hvrm.outputs.stream().map(VariableWithPort::getConceptName).toList())
                    .append("\n\t模式集个数：")
                    .append(hvrm.modeClass.size())
                    .append("\n\t模式集列表：")
                    .append(hvrm.modeClass.stream().map(mc -> mc.getModeClass().getModeClassName()).toList())
                    .append("\n\t属性公式：")
                    .append(part.getProperties())
                    .append("\n\n");
        }
        return str.toString();
    }


}
