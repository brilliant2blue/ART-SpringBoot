package com.nuaa.art.vrmverify.service;

import com.nuaa.art.vrmverify.model.send.ReturnVerifyResult;

import java.util.List;

/**
 * 模型验证
 * @author djl
 * @date 2024-04-02
 */
public interface ModelVerifyService {

    /**
     * 读取模型文件并对其进行验证
     * @param smvFilePath
     * @return
     */
    public ReturnVerifyResult verifyModelFromSmvFile(String smvFilePath, boolean addProperties, List<String> properties) ;

    /**
     * 对smv字符串进行验证
     * @param systemName
     * @param smvStr
     * @param addProperties
     * @param properties
     * @return
     */
    public ReturnVerifyResult verifyModelFromSmvStr(String systemName, String smvStr, boolean addProperties, List<String> properties) ;

}
