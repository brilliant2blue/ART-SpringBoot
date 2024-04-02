package com.nuaa.art.vrmverify.common.utils;

import com.nuaa.art.vrmverify.model.formula.TreeNode;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLUnaryOperator;
import com.nuaa.art.vrmverify.model.formula.ctl.Proposition;

import java.util.List;

/**
 * @author djl
 * @date 2024-03-30
 */
public class TreeTraverseUtils {

    public static void treeTraverse(TreeNode root){
        if(root == null)
            return;
        System.out.println(root.getName());
        List<TreeNode> childList = root.getChildList();
        for (TreeNode treeNode : childList) {
            if(treeNode instanceof Proposition){
                treeTraverse(((Proposition) treeNode).getExpression());
            }
            else
                treeTraverse(treeNode);
        }
    }
}
