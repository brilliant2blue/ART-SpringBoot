package com.nuaa.art.vrmverify.common.exception;

/**
 * 非法CTL异常
 * @author djl
 * @date 2024-07-13
 */
public class InvalidCTLException extends RuntimeException{

    public InvalidCTLException() {
        super();
    }

    public InvalidCTLException(String ctlStr) {
        super("非法CTL公式：" + ctlStr);
    }
}
