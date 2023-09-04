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

    public HttpResult(HttpCodeEnum httpCodeEnum, String msg){
        this.code = httpCodeEnum.getCode();
        this.enMessage = httpCodeEnum.getEnMessage();
        this.zhMessage = msg;
        this.data = null;
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

    //快速封装成功时的响应体
    public static <T> HttpResult<T> success(){
        return new HttpResult<>(HttpCodeEnum.SUCCESS);
    }
    //快速封装成功时的响应体
    public static <T> HttpResult<T> success(T data){
        return new HttpResult<>(HttpCodeEnum.SUCCESS, data);
    }
    //快速封装失败时的响应体
    public static <T> HttpResult<T> fail(){
        return new HttpResult<>(HttpCodeEnum.NOT_EXTENDED);
    }

    //快速封装失败时的响应体
    public static <T> HttpResult<T> fail(String msg){
        return new HttpResult<>(HttpCodeEnum.NOT_EXTENDED, msg);
    }
    //快速封装失败时的响应体
    public static <T> HttpResult<T> fail(String msg, T data){
        return new HttpResult<>(HttpCodeEnum.NOT_EXTENDED, msg, data);
    }

}
