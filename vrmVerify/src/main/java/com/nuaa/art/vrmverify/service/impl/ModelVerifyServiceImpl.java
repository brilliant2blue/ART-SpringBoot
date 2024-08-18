package com.nuaa.art.vrmverify.service.impl;

import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.utils.CTLParseUtils;
import com.nuaa.art.vrmverify.common.utils.PathUtils;
import com.nuaa.art.vrmverify.handler.SmvVerifyHandler;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.vo.ReturnVerifyResult;
import com.nuaa.art.vrmverify.service.ModelVerifyService;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 模型验证
 * @author djl
 * @date 2024-04-02
 */
@Service
public class ModelVerifyServiceImpl implements ModelVerifyService {

    /**
     * 检查CTL公式是否合法
     * @param ctlStr
     */
    @Override
    public void checkCTLFormula(String ctlStr) {
        CTLParseUtils.parseCTLStr(ctlStr);
    }

    /**
     * 从文件中读取CTL公式
     * @param fileName
     * @param in
     * @return
     * @throws IOException
     */
    @Override
    public List<String> readCTLFormulasFromFile(String fileName, InputStream in) throws IOException {
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return switch (suffix) {
            case ".txt" -> readCTLFormulasFromTxt(in);
            case ".xlsx", ".xls" -> readCTLFormulasFromXlsx(in);
            default -> throw new IOException(Msg.FILE_TYPE_ERROR);
        };
    }

    /**
     * 从txt文件中读取CTL公式
     * @param in
     * @return
     * @throws IOException
     */
    private List<String> readCTLFormulasFromTxt(InputStream in) throws IOException {
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);
        List<String> ctlFormulaList = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            if(line.trim().isEmpty())
                continue;
            CTLParseUtils.parseCTLStr(line);
            ctlFormulaList.add(line);
        }
        br.close();
        isr.close();
        return ctlFormulaList;
    }

    /**
     * 从xlsx(xls)文件中读取CTL公式
     * @param in
     * @return
     * @throws IOException
     */
    private List<String> readCTLFormulasFromXlsx(InputStream in) throws IOException {
        List<String> ctlFormulaList = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(in);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            String line = row.getCell(0).getStringCellValue();
            if(line.trim().isEmpty())
                continue;
            CTLParseUtils.parseCTLStr(line);
            ctlFormulaList.add(line);
        }
        workbook.close();
        return ctlFormulaList;
    }

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
