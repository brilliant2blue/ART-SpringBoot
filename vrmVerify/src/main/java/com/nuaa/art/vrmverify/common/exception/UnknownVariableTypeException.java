package com.nuaa.art.vrmverify.common.exception;

import com.nuaa.art.vrmverify.common.Msg;

/**
 * 未知变量类型异常
 * @author djl
 * @date 2024-07-26
 */
public class UnknownVariableTypeException extends RuntimeException {

    public UnknownVariableTypeException() {
        super(Msg.UNKNOWN_VARIABLE_TYPE);
    }

}
