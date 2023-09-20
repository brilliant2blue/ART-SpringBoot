package com.nuaa.art.vrmcheck.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VariableUtils {
    // 将数值型变量的比较值进行排序，方便将值域分段
    public static void sortContinualValues( ArrayList<ArrayList<String>> continualValues) {
        for (int i = 0; i < continualValues.size(); i++) {
            ArrayList<String> thisContinualValues = continualValues.get(i);
            thisContinualValues.sort((o1, o2) -> {
                Double fo1 = Double.parseDouble(o1);
                Double fo2 = Double.parseDouble(o2);
                if (fo1 > fo2)
                    return 1;
                else if (fo1.equals(fo2))
                    return 0;
                else
                    return -1;
            });
            continualValues.set(i, thisContinualValues);
        }
    }

    /**
     * 值域离散化，为所有关键变量构建新值域
     *
     * @param discreteNumber         离散型变量个数
     * @param continualNumber        连续型变量个数
     * @param continualValues        连续型变量关键值
     * @param discreteRanges         离散变量值域
     * @return {@link int[]}
     */
    public static int[] rangeDiscretization(int continualNumber, int discreteNumber, ArrayList<ArrayList<String>> continualValues, ArrayList<ArrayList<String>> discreteRanges){
        int variableNumber = continualNumber + discreteNumber;
        int[] variableRanges = new int[variableNumber];
        for (int i = 0; i < variableNumber; i++) {
            if (i < continualNumber) {
                variableRanges[i] = 2 * continualValues.get(i).size() + 1;
            } else {
                variableRanges[i] = discreteRanges.get(i - continualNumber).size();
            }
        }
        return variableRanges;
    }

}
