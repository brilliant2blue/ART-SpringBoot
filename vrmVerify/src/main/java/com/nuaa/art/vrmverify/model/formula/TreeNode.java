package com.nuaa.art.vrmverify.model.formula;

import lombok.Data;

/**
 * CTL 公式树节点
 * @author djl
 * @date 2024-03-28
 */
@Data
public abstract class TreeNode {

    private TreeNode firstChild;
    private TreeNode secondChild;
    private TreeNode thirdChild;
    private boolean isLeaf;

}
