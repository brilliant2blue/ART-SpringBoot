package com.nuaa.art.vrmverify.service;

import com.nuaa.art.vrmverify.model.VerifyResult;

import java.io.IOException;
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
    public VerifyResult verifyModel(String smvFilePath, boolean addProperties, List<String> properties) ;

}
