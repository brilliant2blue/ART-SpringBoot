package com.nuaa.art.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nuaa.art.common.HttpCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统一响应结构
 *
 * @author konsin
 * @date 2023/06/29
 */
@Data
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResult<T> {
    private int code;
    private  String enMessage;
    private  String zhMessage;
    private T data;

    public HttpResult(HttpCodeEnum httpCodeEnum){
        this.code = httpCodeEnum.getCode();
        this.enMessage = httpCodeEnum.getEnMessage();
        this.zhMessage = httpCodeEnum.getZhMessage();
    }

    public HttpResult(HttpCodeEnum httpCodeEnum, T data){
        this.code = httpCodeEnum.getCode();
        this.enMessage = httpCodeEnum.getEnMessage();
        this.zhMessage = httpCodeEnum.getZhMessage();
        this.data = data;
    }

    public HttpResult(HttpCodeEnum httpCodeEnum, String message, T data){
        this.code = httpCodeEnum.getCode();
        this.enMessage = httpCodeEnum.getEnMessage();
        this.zhMessage = message;
        this.data = data;
    }

}
