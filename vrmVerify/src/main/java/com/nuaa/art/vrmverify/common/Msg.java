package com.nuaa.art.vrmverify.common;

/**
 * 返回给用户的信息
 * @author djl
 * @date 2024-03-25
 */
public class Msg {

    public static final String FILE_NOT_FOUND = "找不到指定文件！";
    public static final String FILE_TYPE_ERROR = "文件类型错误！";
    public static final String INDEX_ERROR = "索引错误！";
    public static final String PARSE_ERROR = "模型解析失败，存在语法错误！";
    public static final String TYPE_ERROR = "模型检查失败，存在变量类型错误！";
    public static final String OUT_OF_MEMORY = "验证超出内存限制！";
    public static final String INFINITE_DOMAIN = "检查器不支持含有无限域变量的模型！";
    public static final String UNKNOWN_ERROR = "未知错误！";

    public static final String INVALID_INPUT_ARGUMENTS = "输入参数非法！";
    public static final String DIVIDE_BY_ZERO = "除零异常！";
    public static final String WRONG_ARITHMETIC_TYPE = "运算类型错误：";
    public static final String UNKNOWN_UNARY_OPERATOR = "未知一元操作符：";
    public static final String UNKNOWN_BINARY_OPERATOR = "未知二元操作符：";
    public static final String NOT_CTL_FORMULA = "含有非CTL属性公式，无法解析！";
    public static final String UNKNOWN_VARIABLE_TYPE = "未知变量类型";


}
