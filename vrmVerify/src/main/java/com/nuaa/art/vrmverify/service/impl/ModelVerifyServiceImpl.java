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
import java.io.IOException;
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
    public ReturnVerifyResult verifyModelFromSmvFile(String smvFilePath, boolean addProperties, List<String> properties) {
        try {
            if(!isSmvFile(smvFilePath)){
                String errorTxt = Msg.FILE_NOT_FOUND + "或" + Msg.FILE_TYPE_ERROR;
                throw new RuntimeException(errorTxt);
            }
            String verifyResultStr = SmvVerifyHandler.doCMDFromSmvFile(smvFilePath, addProperties, properties);
            VerifyResult verifyResult = SmvVerifyHandler.handleVerifyRes(verifyResultStr);
            if(verifyResult == null)
                throw new RuntimeException(Msg.UNKNOWN_ERROR);
            if(verifyResult.isHasError())
                throw new RuntimeException(verifyResult.getErrMsg() + " " + verifyResult.getDetails());
            String fileName = new File(smvFilePath).getName();
            return new ReturnVerifyResult(
                    ReturnVerifyResult.SMV_FILE,
                    fileName,
                    PathUtils.getSmvFilePath(fileName),
                    verifyResultStr,
                    verifyResult);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 对smv字符串进行验证
     * @param systemName
     * @param smvStr
     * @param addProperties
     * @param properties
     * @return
     */
    @Override
    public ReturnVerifyResult verifyModelFromSmvStr(String systemName, String smvStr, boolean addProperties, List<String> properties) {
        try {
            String verifyResultStr = SmvVerifyHandler.doCMDFromSmvStr(systemName, smvStr, addProperties, properties);
            VerifyResult verifyResult = SmvVerifyHandler.handleVerifyRes(verifyResultStr);
            if(verifyResult.isHasError())
                throw new RuntimeException(verifyResult.getErrMsg() + " " + verifyResult.getDetails());
            return new ReturnVerifyResult(
                    ReturnVerifyResult.VRM_MODEL,
                    systemName,
                    PathUtils.getSmvFilePath(systemName + ".smv"),
                    verifyResultStr,
                    verifyResult);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 中止当前验证进程
     * @return
     */
    @Override
    public boolean killCurVerifyProcess() throws IOException {
        // 中止nuxmv.exe进程
        if(SmvVerifyHandler.killNuxmvProcess())
            return true;
        // 中止异步线程

        return false;
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
