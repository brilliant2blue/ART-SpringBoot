package com.nuaa.art.vrmverify.model.formula;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * CTL 公式树节点
 * @author djl
 * @date 2024-03-28
 */
@Data
public abstract class TreeNode {

    private String name;
    private List<TreeNode> childList = new ArrayList<>();
    private boolean isLeaf;

}
