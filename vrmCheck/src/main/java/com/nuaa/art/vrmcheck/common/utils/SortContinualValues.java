package com.nuaa.art.vrmcheck.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortContinualValues {
    // 将数值型变量的比较值进行排序，方便将值域分段
    public static void sortContinualValues( ArrayList<ArrayList<String>> continualValues) {
        for (int i = 0; i < continualValues.size(); i++) {
            ArrayList<String> thisContinualValues = continualValues.get(i);
            Collections.sort(thisContinualValues, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    float fo1 = Float.parseFloat(o1);
                    float fo2 = Float.parseFloat(o2);
                    if (fo1 > fo2)
                        return 1;
                    else if (fo1 == fo2)
                        return 0;
                    else
                        return -1;
                }
            });
            continualValues.set(i, thisContinualValues);
        }
    }
}
