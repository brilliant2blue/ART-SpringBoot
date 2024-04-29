package com.nuaa.art.vrmverify.service.impl;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.utils.PathUtils;
import com.nuaa.art.vrmverify.handler.SmvVerifyHandler;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.send.ReturnVerifyResult;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * 模型验证
 * @author djl
 * @date 2024-04-02
 */
@Service
public class ModelVerifyServiceImpl implements ModelVerifyService {

    /**
     * 读取模型文件并对其进行验证
     * @param smvFilePath
     * @return
     */
    @Override
    public ReturnVerifyResult verifyModel(String smvFilePath, boolean addProperties, List<String> properties) {
        try {
            if(!isSmvFile(smvFilePath)){
                String errorTxt = Msg.FILE_NOT_FOUND + "或" + Msg.FILE_TYPE_ERROR;
                throw new RuntimeException(errorTxt);
            }
            String verifyResultStr = SmvVerifyHandler.doCMD(smvFilePath, addProperties, properties);
            VerifyResult verifyResult = SmvVerifyHandler.handleVerifyRes(verifyResultStr);
            if(verifyResult.isHasError())
                throw new RuntimeException(verifyResult.getErrMsg() + " " + verifyResult.getDetails());
            return new ReturnVerifyResult(PathUtils.getSmvFilePath(new File(smvFilePath).getName()),
                    verifyResultStr,
                    verifyResult);
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 判断是否为 smv 文件
     * @param smvFilePath
     * @return
     */
    private boolean isSmvFile(String smvFilePath){
        File file = new File(smvFilePath);
        if(!file.isFile() || !file.exists())
            return false;
        String extension = FilenameUtils.getExtension(file.getName());
        return extension.equals("smv");
    }

}
