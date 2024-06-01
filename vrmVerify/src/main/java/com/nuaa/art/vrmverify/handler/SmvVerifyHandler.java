package com.nuaa.art.vrmverify.handler;

import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.utils.CmdUtils;
import com.nuaa.art.vrmverify.common.utils.PathUtils;
import com.nuaa.art.vrmverify.model.Counterexample;
import com.nuaa.art.vrmverify.model.VerifyResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理smv模型验证
 * @author djl
 * @date 2024-03-25
 */
public class SmvVerifyHandler {

    public static String VERIFY_OPTIONS = " -coi -df ";
    public static String PROPERTY_TYPE = "CTLSPEC ";
    public static String KILL_NUXMV_CMD_WIN = "taskkill /f /t /im nuxmv.exe";

    /**
     * 若存在nuxmv进程，则终止
     * @return
     */
    public static boolean killNuxmvProcess() throws IOException {
        String res = CmdUtils.execute(KILL_NUXMV_CMD_WIN);
        return !res.contains("错误: 没有找到进程");
    }

    /**
     * 对验证结果进行处理，并返回结果对象
     * @param result
     * @return
     */
    public static VerifyResult handleVerifyRes(String result){
        if(result == null || result.isEmpty())
            return null;

        String[] lines = result.split("\n");

        int propertyCount = 0;  // 记录验证属性的个数
        List<Counterexample> cxList = new ArrayList<>();
        VerifyResult vr = new VerifyResult();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(line.startsWith("***"))
                continue;

            // 报错
            if(line.contains("file")){
                vr.setHasError(true);
                vr.setDetails(line);
                // 1.文件找不到
                String t = line.toLowerCase();
                if(t.contains("cannot open input file"))
                    vr.setErrMsg(Msg.FILE_NOT_FOUND);
                // 2.文件中存在语法错误
                else if(t.contains("syntax error"))
                    vr.setErrMsg(Msg.PARSE_ERROR);
                // 3.存在变量类型错误
                else if(t.contains("type error"))
                    vr.setErrMsg(Msg.TYPE_ERROR);
                break;
            }
            // 超出内存异常
            if(line.contains("Out of memory allocating")){
                vr.setHasError(true);
                vr.setDetails(line);
                vr.setErrMsg(Msg.OUT_OF_MEMORY);
                break;
            }
            if(line.contains("batch mode is not available with models containing infinite domain variables")){
                vr.setHasError(true);
                vr.setDetails(line);
                vr.setErrMsg(Msg.INFINITE_DOMAIN);
                break;
            }
            // 定位到验证结果
            if(line.startsWith("-- specification")){
                vr.setHasError(false);
                String propertyRes = line;
                Counterexample cx = new Counterexample();
                while(++i < lines.length && !lines[i].startsWith("-- "))
                    propertyRes += lines[i];
                // 验证通过
                if(propertyRes.contains(" is true")){
                    String property = propertyRes.substring("-- specification".length(), propertyRes.length() - " is true".length());
                    cx.setProperty(property);
                    cx.setPassed(true);
                }
                // 验证不通过
                else if(propertyRes.contains(" is false")){
                    String property = line.substring("-- specification".length(), propertyRes.length() - " is false".length());
                    int traceLength = 0;    // 反例路径长度
                    List<Map<String, String>> assignments = new ArrayList<>();   // 反例路径
                    while(i < lines.length
                            && !lines[i].startsWith("-- specification")
                            && !lines[i].startsWith("WARNING")){
                        if(lines[i].startsWith("  -> ")){
                            traceLength++;
                            i++;
                            Map<String, String> oneState = new HashMap<>();
                            while(i < lines.length
                                    && !lines[i].startsWith("  -> ")
                                    && !lines[i].startsWith("-- specification")
                                    && !lines[i].startsWith("  -- Loop starts here")
                                    && !lines[i].startsWith("WARNING")){
                                String[] variableAndValue = lines[i].split(" = ");
                                oneState.put(variableAndValue[0].substring(4), variableAndValue[1]);
                                i++;
                            }
                            assignments.add(oneState);
                        }
                        else if(lines[i].startsWith("  -- Loop starts here")){
                            cx.setExistLoop(true);
                            cx.setLoopStartsState(traceLength);
                            i++;
                        }
                        else
                            i++;
                    }
                    cx.setProperty(property);
                    cx.setPassed(false);
                    cx.setTraceLength(traceLength);
                    cx.setAssignments(assignments);
                }
                else {
                    vr.setHasError(true);
                    vr.setErrMsg(Msg.UNKNOWN_ERROR);
                    break;
                }
                propertyCount++;
                cxList.add(cx);
                i--;
            }
        }

        vr.setPropertyCount(propertyCount);
        vr.setCxList(cxList);

        return vr;
    }

    /**
     * 通过命令行执行验证（smv文件路径）
     * @param originalFilePath
     * @param addProperties
     * @param properties
     * @return
     * @throws IOException
     */
    public static String doCMDFromSmvFile(String originalFilePath, boolean addProperties, List<String> properties) throws IOException {
        return CmdUtils.execute(generateVerifyCmdFromSmvFile(originalFilePath, addProperties, properties));
    }

    /**
     * 通过命令行执行验证（smv字符串）
     * @param systemName
     * @param smvStr
     * @param addProperties
     * @param properties
     * @return
     * @throws IOException
     */
    public static String doCMDFromSmvStr(String systemName, String smvStr, boolean addProperties, List<String> properties) throws IOException {
        return CmdUtils.execute(generateVerifyCmdFromSmvStr(systemName, smvStr, addProperties, properties));
    }

    /**
     * 从文件中读取验证结果
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String getResultFromFile(String filePath) throws IOException {
        return Files.readString(new File(filePath).toPath(), StandardCharsets.UTF_8);
    }

    /**
     * 拼接验证命令（smv文件路径）
     * @param originalFilePath
     * @param addProperties
     * @param properties
     * @return
     */
    private static String generateVerifyCmdFromSmvFile(String originalFilePath, boolean addProperties, List<String> properties){
        String smvFilePath = copySmvFileFromFile(originalFilePath, addProperties, properties);
        if(smvFilePath == null)
            return null;
        return PathUtils.getNuxmvPath() + " " + VERIFY_OPTIONS + " " + smvFilePath;
    }

    /**
     * 拼接验证命令（smv字符串）
     * @param systemName
     * @param smvStr
     * @param addProperties
     * @param properties
     * @return
     */
    private static String generateVerifyCmdFromSmvStr(String systemName, String smvStr, boolean addProperties, List<String> properties){
        String smvFilePath = copySmvFileFromStr(systemName, smvStr, addProperties, properties);
        if(smvFilePath == null)
            return null;
        return PathUtils.getNuxmvPath() + " " + VERIFY_OPTIONS + " " + smvFilePath;
    }

    /**
     * 根据文件路径将smv文件复制到指定文件夹，并添加验证属性
     * @param originalFilePath
     * @param addProperties
     * @param properties
     * @return
     */
    private static String copySmvFileFromFile(String originalFilePath, boolean addProperties, List<String> properties) {
        File originalSmvFile = new File(originalFilePath);
        if(originalSmvFile.isFile() && originalSmvFile.exists()){
            String copiedSmvFilePath = PathUtils.getSmvFilePath(originalSmvFile.getName());
            File copiedSmvFile = new File(copiedSmvFilePath);
            try {
                // 复制smv文件到指定文件夹
                Files.copy(originalSmvFile.toPath(), copiedSmvFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                copiedSmvFile = new File(copiedSmvFilePath);
                // 给文件追加验证属性
                if(addProperties){
                    FileOutputStream fos = new FileOutputStream(copiedSmvFile, true);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                    for (String property : properties)
                        osw.write("\r\n" + PROPERTY_TYPE + property);
                    osw.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return copiedSmvFilePath;
        }
        else
            return null;
    }

    /**
     * 将smv字符串复制到指定文件夹，并添加验证属性
     * @param systemName
     * @param smvStr
     * @param addProperties
     * @param properties
     * @return
     */
    private static String copySmvFileFromStr(String systemName, String smvStr, boolean addProperties, List<String> properties) {
        String copiedSmvFilePath = PathUtils.getSmvFilePath(systemName + ".smv");
        File copiedSmvFile = new File(copiedSmvFilePath);
        try (FileOutputStream fos = new FileOutputStream(copiedSmvFile, false)) {
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            // 复制smv字符串到指定文件夹
            osw.write(smvStr);
            // 给文件追加验证属性
            if(addProperties) {
                for (String property : properties)
                    osw.write("\r\n" + PROPERTY_TYPE + property);
            }
            osw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return copiedSmvFilePath;
    }

    /**
     * 获取操作系统名称
     * @return
     */
    private static String getOS(){
        return System.getProperty("os.name");
    }

}
