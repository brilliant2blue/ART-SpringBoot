package com.nuaa.art.vrmverify.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类
 * @author djl
 * @date 2024-07-27
 */
public class StringUtils {

    /**
     * KMP模式匹配算法
     *
     * @param str
     * @param target
     * @return
     */
    public static List<Integer> KMP(String str, String target) {
        char[] t = str.toCharArray(), p = target.toCharArray();
        int i = 0, j = 0;
        int[] next = getNext(target);
        List<Integer> res = new ArrayList<>();
        int n = 0;
        while(i < t.length){
            while (i < t.length && j < p.length) {
                if (j == -1 || t[i] == p[j]) {
                    i++;
                    j++;
                } else
                    j = next[j];
            }
            if(j == p.length){
                res.add(i - j + p.length + n * 7);
                j = 0;
                n++;
            }
        }
        return res;
    }

    private static int[] getNext(String target) {
        char[] p = target.toCharArray();
        int[] next = new int[p.length];
        next[0] = -1;
        int j = 0, k = -1;
        while (j < p.length - 1) {
            if (k == -1 || p[j] == p[k]) {
                if (p[++j] == p[++k])
                    next[j] = next[k];
                else
                    next[j] = k;

            } else
                k = next[k];
        }
        return next;
    }
}
