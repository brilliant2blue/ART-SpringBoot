package com.nuaa.art.vrmverify.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表达式工具类
 * @author djl
 * @date 2024-03-27
 */
public class ExpressionUtils {

    public final static int FALSE_MODE = 0;
    public final static int TRUE_MODE = 1;
    public final static int OTHER_MODE = 2;

    public final static String TRUE_STYLE = "color: blue; font-weight: 700";
    public final static String VARIABLE_STYLE = "background-color: lightgrey;";
    public final static String CAUSE_STYLE = "border: 2px solid red;";

    public static int lengthWithoutTags(String s) {
        return s.replaceAll("</?\\w+[^>]*>","").replaceAll("&(nbsp|lt|gt);", " ").length();
    }

    public static String nStrings(String s, int n) {
        return String.join("", Collections.nCopies(n, s));
    }

    public static String nChars(char c, int n) {
        final char[] arr = new char[n];
        Arrays.fill(arr, c);
        return new String(arr);
    }

    public static String par(Object text) {
        return "(" + text + ")";
    }

    public static List<String> bind(List<?>... lists) {
        final List<String> result = new ArrayList<>();
        for (int i = 0; i < lists[0].size(); i++) {
            final StringBuilder sb = new StringBuilder();
            for (List<?> list : lists) {
                sb.append(list.get(i));
            }
            result.add(sb.toString());
        }
        return result;
    }

    public static String toHtmlString(String s) {
        return s.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    /**
     * flag 0: false、1: true、2：值
     * @param s
     * @param flag
     * @param isVariable
     * @param isCause
     * @param value
     * @return
     */
    public static String genHtmlSpan(String s, int flag, boolean isVariable, boolean isCause, Object value) {
        StringBuilder htmlSpan = new StringBuilder("<span style=''>" + s + "</span>");
        if(flag == TRUE_MODE)
            styleDecorator(htmlSpan, 0, value);
        if(isVariable)
            styleDecorator(htmlSpan, 1, value);
        if(isCause)
            styleDecorator(htmlSpan, 2, value);
        return htmlSpan.toString();
    }

    private static void styleDecorator(StringBuilder sb, int styleMode, Object value){
        int offset = sb.indexOf(";");
        if(offset == -1)
            offset = sb.indexOf("'");
        offset++;

        switch (styleMode){
            case 0:
                sb.insert(offset, TRUE_STYLE);
                break;
            case 1:
                sb.insert(offset, VARIABLE_STYLE);
                offset = sb.indexOf("n") + 2;
                sb.insert(offset, "title='" + value.toString() + "'");
                break;
            case 2:
                sb.insert(offset, CAUSE_STYLE);
                break;
        }
    }
}
