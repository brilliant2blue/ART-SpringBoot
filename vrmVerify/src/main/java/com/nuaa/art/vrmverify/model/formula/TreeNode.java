package com.nuaa.art.vrmverify.model.formula;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * CTL 公式树节点
 * @author djl
 * @date 2024-03-28
 */
public abstract class TreeNode {

    private String nodeName;
    private boolean isBooleanVal;
    private List<Object> values = new ArrayList<>();
    private List<TreeNode> childList = new ArrayList<>();
    private boolean isVariable;
    private boolean isProposition;
    private boolean isLeaf;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @JsonProperty(value = "isBooleanVal")
    public boolean isBooleanVal() {
        return isBooleanVal;
    }

    public void setBooleanVal(boolean booleanVal) {
        isBooleanVal = booleanVal;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public List<TreeNode> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeNode> childList) {
        this.childList = childList;
    }

    @JsonProperty(value = "isVariable")
    public boolean isVariable() {
        return isVariable;
    }

    public void setVariable(boolean variable) {
        isVariable = variable;
    }

    @JsonProperty(value = "isProposition")
    public boolean isProposition() {
        return isProposition;
    }

    public void setProposition(boolean proposition) {
        isProposition = proposition;
    }

    @JsonProperty(value = "isLeaf")
    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }
}
