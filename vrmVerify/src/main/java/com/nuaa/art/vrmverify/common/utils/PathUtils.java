package com.nuaa.art.vrmverify.common.utils;

import java.net.URL;

/**
 * 获取资源路径
 * @author djl
 * @date 2024-03-25
 */
public class PathUtils {

    private static final String NUXMV = "engines/nuxmv.exe";
    private static final String SMV_FILE = "smvFile";
    private static final String CX_FILE = "cxFile";

    /**
     * 获取 nuxmv 的绝对路径
     * @return
     */
    public static String getNuxmvPath(){
        URL nuxmvURL = PathUtils.class.getClassLoader().getResource(NUXMV);
        if(nuxmvURL != null)
            return nuxmvURL.getPath().substring(1);
        else
            return null;
    }

    /**
     * 获取 smv 文件的绝对路径
     * @param fileName
     * @return
     */
    public static String getSmvFilePath(String fileName){
        String rootPath = System.getProperty("user.dir");
        return rootPath + "\\vrmVerify\\src\\main\\resources\\" + SMV_FILE + "\\" + fileName;
    }

    /**
     * 获取验证结果文件的绝对路径
     * @param fileName
     * @return
     */
    public static String getCxFilePath(String fileName){
        String rootPath = System.getProperty("user.dir");
        return rootPath + "\\vrmVerify\\src\\main\\resources\\" + CX_FILE + "\\" + fileName;
    }

}
