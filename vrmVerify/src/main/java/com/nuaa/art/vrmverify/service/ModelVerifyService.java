package com.nuaa.art.vrmverify.service;

import com.nuaa.art.vrmverify.model.vo.ReturnVerifyResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 模型验证
 * @author djl
 * @date 2024-04-02
 */
public interface ModelVerifyService {

    /**
     * 检查CTL公式是否合法
     * @param ctlStr
     */
    public void checkCTLFormula(String ctlStr);

    /**
     * 从文件中读取CTL公式
     * @param fileName
     * @param in
     * @return
     * @throws IOException
     */
    public List<String> readCTLFormulasFromFile(String fileName, InputStream in) throws IOException;

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

    /**
     * 中止当前验证进程
     * @return
     */
    public boolean killCurVerifyProcess() throws IOException;

}
