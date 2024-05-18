package com.nuaa.art.vrmverify.model.send;

import com.nuaa.art.vrmverify.model.VerifyResult;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 封装返回的验证结果
 * @author djl
 * @date 2024-04-16
 */
@Data
@AllArgsConstructor
public class ReturnVerifyResult {

    public static final int SMV_FILE = 0;
    public static final int VRM_MODEL = 1;
    public static final int XML_FILE = 2;

    private Integer type;
    private String name;
    private String filePath;
    private String verifyResultStr;
    private VerifyResult verifyResult;
}
