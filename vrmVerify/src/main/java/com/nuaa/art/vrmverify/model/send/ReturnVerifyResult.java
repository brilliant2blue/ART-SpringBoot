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
    private String smvFilePath;
    private String verifyResultStr;
    private VerifyResult verifyResult;
}
