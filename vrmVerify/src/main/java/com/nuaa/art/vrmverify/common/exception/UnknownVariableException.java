package com.nuaa.art.vrmverify.common.exception;

/**
 * @author djl
 * @date 2024-07-27
 */
public class UnknownVariableException extends RuntimeException{

    public UnknownVariableException() {
        super();
    }

    public UnknownVariableException(String property, String variableName) {
        super("属性公式“" + property + "”中存在未知变量：" + variableName);
    }
}

