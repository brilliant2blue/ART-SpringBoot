package com.nuaa.art.vrmcheck.model.table;

import java.util.ArrayList;

public class CriticalVariables {
    public ArrayList<String> continualVariables;// 数值型变量
    public ArrayList<String> discreteVariables;// 枚举型变量
    public ArrayList<ArrayList<String>> continualRanges; // 每个子list大小为2，表示值域的左右边界
    public ArrayList<ArrayList<String>> continualValues;// 每个数值型变量在各条件中比较的值
    public ArrayList<ArrayList<String>> discreteRanges;// 每个枚举型变量的值域

    public CriticalVariables(){
        continualVariables = new ArrayList<>();
        discreteVariables = new ArrayList<>();
        continualRanges = new ArrayList<>();
        continualValues = new ArrayList<>();
        discreteRanges = new ArrayList<>();
    }

    public CriticalVariables(ArrayList<String> continualVariables, ArrayList<String> discreteVariables, ArrayList<ArrayList<String>> continualRanges, ArrayList<ArrayList<String>> continualValues, ArrayList<ArrayList<String>> discreteRanges) {
        this.continualVariables = continualVariables;
        this.discreteVariables = discreteVariables;
        this.continualRanges = continualRanges;
        this.continualValues = continualValues;
        this.discreteRanges = discreteRanges;
    }

    public int size(){
        return continualVariables.size()+discreteVariables.size();
    }

    public void sortContinualValues() {
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
     * @return {@link int[]}
     */
    public int[] rangeDiscretization(){
        int variableNumber = this.size();
        int[] variableRanges = new int[variableNumber];
        for (int i = 0; i < variableNumber; i++) {
            if (i < continualVariables.size()) {
                variableRanges[i] = 2 * continualValues.get(i).size() + 1;
            } else {
                variableRanges[i] = discreteRanges.get(i - continualVariables.size()).size();
            }
        }
        return variableRanges;
    }

    public CriticalVariables merge(CriticalVariables Source){
        for (int i = 0; i < Source.continualVariables.size(); i++) {
            if (!this.continualVariables.contains(Source.continualVariables.get(i))) {
                this.continualVariables.add(Source.continualVariables.get(i));
                this.continualValues.add(Source.continualValues.get(i));
                this.continualRanges.add(Source.continualRanges.get(i));
            } else {
                int variableIndex = this.continualVariables.indexOf(Source.continualVariables.get(i));
                ArrayList<String> thisContinualValues = Source.continualValues.get(i);
                ArrayList<String> continualValuesOfVariable = this.continualValues.get(variableIndex);
                for (String continualValue : thisContinualValues) {
                    if (!continualValuesOfVariable.contains(continualValue))
                        continualValuesOfVariable.add(continualValue);
                }
                this.continualValues.set(variableIndex, continualValuesOfVariable);
            }
        }
        for (int i = 0; i < Source.discreteVariables.size(); i++) {
            if (!this.discreteVariables.contains(Source.discreteVariables.get(i))) {
                this.discreteVariables.add(Source.discreteVariables.get(i));
                this.discreteRanges.add(Source.discreteRanges.get(i));
            } else {
                int variableIndex = this.discreteVariables.indexOf(Source.discreteVariables.get(i));
                ArrayList<String> thisDiscreteRanges = Source.discreteRanges.get(i);
                ArrayList<String> discreteValuesOfVariable = this.discreteRanges.get(variableIndex);
                for (String discreteRange : thisDiscreteRanges) {
                    if (!discreteValuesOfVariable.contains(discreteRange))
                        discreteValuesOfVariable.add(discreteRange);
                }
                this.discreteRanges.set(variableIndex, discreteValuesOfVariable);
            }
        }
        return this;
    }

}
