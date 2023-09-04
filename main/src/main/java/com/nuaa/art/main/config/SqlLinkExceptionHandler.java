package com.nuaa.art.main.config;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.nuaa.art.common.HttpCodeEnum;
import com.nuaa.art.common.model.HttpResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SqlLinkExceptionHandler {
    @ExceptionHandler(value = CommunicationsException.class)
    public HttpResult sqlExcepthandler(CommunicationsException e) {
        return HttpResult.fail("数据库连接失败！");
    }
}
