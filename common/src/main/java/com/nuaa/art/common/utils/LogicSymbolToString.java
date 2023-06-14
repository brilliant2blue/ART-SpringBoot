package com.nuaa.art.common.utils;

import org.springframework.context.annotation.Bean;

/**
 * 把条件或事件中的逻辑符号转英文
 *
 * @author konsin
 * @date 2023/06/12
 */
public class LogicSymbolToString {
    public static String processString(String string) {
        String res = "";
        res = string.replaceAll("&", " AND ");
        res = res.replaceAll("\\|\\|", " OR ");
        res = res.replaceAll("\\}\\{", " AND ");
        res = res.replaceAll("\\{", "");
        res = res.replaceAll("\\}", "");
        res = res.replaceAll("!", " NOT ");
        //System.out.println(res);
        return res;
    }
}
