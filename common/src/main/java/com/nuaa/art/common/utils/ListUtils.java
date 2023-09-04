package com.nuaa.art.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ListUtils {
    public static List<Integer> StringToNumArray(String str) {
        List<Integer> nums = new ArrayList<>();
        for (String s : str.split(", ")){
            //System.out.println("s: "+s);
            if(!s.isBlank()){
                nums.add(Integer.valueOf(s));
            }
        }
        return nums;
    }

    public static String NumArrayToString(List<Integer> nums) {
        String str = nums.toString();
        return str.substring(1, str.length()-1);
    }
}
