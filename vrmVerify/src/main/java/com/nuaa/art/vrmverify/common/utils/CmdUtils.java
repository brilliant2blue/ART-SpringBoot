package com.nuaa.art.vrmverify.common.utils;

import java.io.*;

/**
 * 命令行工具类
 * @author djl
 * @date 2024-05-25
 */
public class CmdUtils {

    /**
     * 执行cmd命令，并返回结果
     * @param cmd
     * @return
     * @throws IOException
     */
    public static String execute(String cmd) throws IOException {
        if(cmd == null)
            return null;

        Process process = Runtime.getRuntime().exec(cmd);

        // 用于读取执行结果流
        InputStream is = process.getInputStream();
        InputStream es = process.getErrorStream();
        SequenceInputStream sis = new SequenceInputStream(is, es);
        BufferedInputStream bis = new BufferedInputStream(sis);
        Reader reader = new InputStreamReader(bis, getDefaultEncoding());
        BufferedReader bufReader = new BufferedReader(reader);

        // 读取执行结果
        StringBuilder execRes = new StringBuilder();
        String line;
        while((line = bufReader.readLine()) != null)
            execRes.append(line).append("\n");
        return execRes.toString();
    }

    /**
     * 根据操作系统获取默认编码
     * @return
     */
    private static String getDefaultEncoding(){
        if (getOS().trim().toLowerCase().startsWith("win"))
            return "GBK";
        else
            return "UTF-8";
    }

    /**
     * 获取操作系统名称
     * @return
     */
    private static String getOS(){
        return System.getProperty("os.name");
    }
}
