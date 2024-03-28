package com.nuaa.art.vrmverify.model;

import lombok.Data;

import java.util.List;

/**
 * 验证结果
 * @author djl
 * @date 2024-03-25
 */

@Data
public class VerifyResult {

    private boolean hasError; // 验证途中是否报错
    private String errMsg;  // 报错信息
    private String details; // 错误具体信息
    private int propertyCount;    // 验证属性个数
    private List<Counterexample> cxList;    // 反例链表
}
