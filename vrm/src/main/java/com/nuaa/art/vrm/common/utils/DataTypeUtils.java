package com.nuaa.art.vrm.common.utils;

import com.nuaa.art.vrm.common.BasicDataType;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.Type;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于处理字符串类型的值域，取值与真实值之间的转换功能。
 *
 * @author konsin
 * @date 2023/09/13
 */
@Component
public class DataTypeUtils {
    public Boolean WhetherContinuousRange(Type type){
        String dataType = type.getDataType();
        return dataType.equalsIgnoreCase(BasicDataType.IntegerType.getTypeName()) ||
                dataType.equalsIgnoreCase(BasicDataType.FloatType.getTypeName()) ||
                dataType.equalsIgnoreCase(BasicDataType.DoubleType.getDataType());
    }
    public Type FindVariableType(ConceptLibrary var, List<Type> types){
        Type thisType = null;
        thisType = types.stream().filter(item -> item.getTypeName().equalsIgnoreCase(var.getConceptDatatype())).findFirst().orElse(null);
        return thisType;
    }

    public List GetRangeList(Type type){
        try {
            String baseType = type.getDataType();
            String range = type.getTypeRange();
            if (baseType.equalsIgnoreCase(BasicDataType.StringType.getTypeName())) {
                return new ArrayList<>();
            } else if (baseType.equalsIgnoreCase(BasicDataType.EnumeratedType.getTypeName())) {
                String[] ranges = range.split(",");
                List<String> rangeList = new ArrayList<>();
                for (String singleRange : ranges) {
                    if (singleRange.contains("=")) {
                        singleRange = singleRange.substring(0, singleRange.indexOf("="));
                    }
                    rangeList.add(singleRange);
                }
                return rangeList;
            } else if (range.contains(",")) { // 其他类型的离散型变量 Boolean, Char
                String[] ranges = range.split(",");
                List<String> rangeList = new ArrayList<>();
                for (String singleRange : ranges) {
                    if (singleRange.contains("=")) {
                        singleRange = singleRange.substring(0, singleRange.indexOf("="));
                    }
                    rangeList.add(singleRange);
                }
                return rangeList;
            }
            // interger类型变量
            else if (baseType.equalsIgnoreCase(BasicDataType.IntegerType.getTypeName())) {
                // 分离区间边界
                String left = range.substring(0, range.indexOf("..")).replace(" ","");
                String right = range.substring(range.indexOf("..") + 3).replace(" ","");

                int leftValue = Integer.parseInt(left);
                int rightValue = Integer.parseInt(right);
                return Arrays.asList(leftValue, rightValue);
            } else if (baseType.equalsIgnoreCase(BasicDataType.DoubleType.getTypeName())) {
                String left = range.substring(0, range.indexOf(".."));
                String right = range.substring(range.indexOf("..") + 2);
                double leftValue, rightValue;
                if( left.contains("e") )
                    leftValue = Double.parseDouble(left.substring(0, left.indexOf("e")))
                            * Math.pow(10, Double.parseDouble(left.substring(left.indexOf("e") + 1)));
                else if( left.contains("E") )
                    leftValue = Double.parseDouble(left.substring(0, left.indexOf("E")))
                            * Math.pow(10, Double.parseDouble(left.substring(left.indexOf("E") + 1)));
                else
                    leftValue = Double.parseDouble(left);
                if( right.contains("e") )
                    rightValue= Double.parseDouble(right.substring(0, right.indexOf("e")))
                            * Math.pow(10, Double.parseDouble(right.substring(right.indexOf("e") + 1)));
                else if( left.contains("E") )
                    rightValue = Double.parseDouble(right.substring(0, right.indexOf("E")))
                            * Math.pow(10, Double.parseDouble(right.substring(right.indexOf("E") + 1)));
                else
                    rightValue= Double.parseDouble(right);
                return Arrays.asList(leftValue, rightValue);
            } else if (baseType.equalsIgnoreCase(BasicDataType.FloatType.getTypeName())){
                String left = range.substring(0, range.indexOf(".."));
                String right = range.substring(range.indexOf("..") + 2);
                float leftValue = Float.parseFloat(left);
                float rightValue = Float.parseFloat(right);
                return Arrays.asList(leftValue, rightValue);
            }
            return null;
        } catch (Exception ex) {
            throw new RuntimeException("处理类型值域错误");
        }
    }


    /**
     * 获取变量的值域，变量的值域由其类型确定
     *
     * @param var   变量
     * @param types 类型
     * @return {@link List}
     */
    public List GetRangeList(ConceptLibrary var, List<Type> types){
        Type thisType = FindVariableType(var, types);
        try {
            return GetRangeList(thisType);
        } catch (Exception e){
            throw e;
        }
    }

    /**
     * 获得与值相符的真正的值
     *
     * @param val  变量的值
     * @param type 类型
     * @return {@link Object}
     */
    public   Object  GetRealValue(String val, Type type){
        String baseType = type.getDataType();
        try{
            if (baseType.equalsIgnoreCase(BasicDataType.IntegerType.getTypeName())) {
                return Integer.valueOf(val);
            } else if (baseType.equalsIgnoreCase(BasicDataType.DoubleType.getTypeName())) {
                return Double.parseDouble(val);
            } else if (baseType.equalsIgnoreCase(BasicDataType.FloatType.getTypeName())){
                return Float.parseFloat(val);
            } else {
                return val;
            }
        } catch (Exception e){
            throw new RuntimeException("转换真实值出错");
        }
    }

    /**
     * 获得与值相符的真正的值
     *
     * @param var  变量
     * @param type 类型
     * @return {@link Object}
     */
    public Object GetRealValue(ConceptLibrary var, Type type){
        try {
            return GetRealValue(var.getConceptValue(), type);
        } catch (Exception e){
            throw e;
        }
    }

    public Object GetRealValue(ConceptLibrary var, List<Type> types){
        Type thisType = FindVariableType(var, types);
        try {
            return GetRealValue(var, thisType);
        } catch (Exception e){
            throw e;
        }
    }
}
