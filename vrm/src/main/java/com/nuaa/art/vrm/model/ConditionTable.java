package com.nuaa.art.vrm.model;

import lombok.Data;

import java.util.ArrayList;

/**
 * 条件表的AndOr表数据模型
 *
 * @author konsin
 * @date 2023/07/02
 */
@Data
public class ConditionTable {
        public Integer andNum;
        public Integer orNum;

        public ArrayList<ConditionItem> conditionItems;
        public ArrayList<ArrayList<String>> orList;

        public String logicString;
        public ConditionTable(){
                this.andNum = 0;
                this.orNum = 0;
                this.conditionItems = new ArrayList<>();
                this.orList = new ArrayList<>();
                this.logicString = "";
        };
        public ConditionTable(Integer andNum, ArrayList<ConditionItem> conditionItems, String logicString) {
                this.orNum = 0;
                this.andNum = andNum;
                this.conditionItems = conditionItems;
                this.logicString = logicString;
                this.orList = null;
        }

        public ConditionTable(Integer andNum, Integer orNum, ArrayList<ConditionItem> conditionItems, ArrayList<ArrayList<String>> orList) {
                this.andNum = andNum;
                this.orNum = orNum;
                this.conditionItems = conditionItems;
                this.orList = orList;
                this.logicString = "";
        }

        public Integer getAndNum() {
                return andNum;
        }

        public void setAndNum(Integer andNum) {
                this.andNum = andNum;
        }

        public Integer getOrNum() {
                return orNum;
        }

        public void setOrNum(Integer orNum) {
                this.orNum = orNum;
        }

        public ArrayList<ConditionItem> getConditionItems() {
                return conditionItems;
        }

        public void setConditionItems(ArrayList<ConditionItem> conditionItems) {
                this.conditionItems = conditionItems;
        }

        public ArrayList<ArrayList<String>> getOrList() {
                return orList;
        }

        public void setOrList(ArrayList<ArrayList<String>> orList) {
                this.orList = orList;
        }

        public String getLogicString() {
                return logicString;
        }

        public void setLogicString(String logicString) {
                this.logicString = logicString;
        }

        public void addAndNum(){
                andNum++;
        }

        @Override
        public String toString() {
                String s = "";
                s =  "ConditionTable{" +
                        "andNum=" + andNum +
                        ", orNum=" + orNum +
                        ", conditions={ ";
                for(ConditionItem c: conditionItems){
                        s += c.toString()+" ";
                }
                        s += "}, orList=" + orList +
                        ", logicString='" + logicString + '\'' +
                        '}';
                return s;
        }
}

