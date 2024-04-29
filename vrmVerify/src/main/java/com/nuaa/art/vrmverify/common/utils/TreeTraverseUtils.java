package com.nuaa.art.vrmverify.common.utils;

import com.nuaa.art.vrmverify.common.CTLSignal;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.TreeNode;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLBinaryOperator;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLUnaryOperator;
import com.nuaa.art.vrmverify.model.formula.ctl.Proposition;
import com.nuaa.art.vrmverify.model.formula.expression.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author djl
 * @date 2024-03-30
 */
public class TreeTraverseUtils {

    public static void traverseToPrint(TreeNode root){
        if(root == null)
            return;
        System.out.println(root.getNodeName() + " " + root.getClass());
        List<TreeNode> childList = root.getChildList();
        for (TreeNode treeNode : childList) {
            if(treeNode instanceof Proposition){
                traverseToPrint(((Proposition) treeNode).getExpression());
            }
            else
                traverseToPrint(treeNode);
        }
    }

    /**
     * 遍历并修正属性公式
     * @param root
     * @param values
     * @return
     */
    public static Object traverseToModifyFormula(TreeNode root, Map<String, List<String>> values){
        if(root == null)
            return null;
        if(root instanceof Variable){
            // 修正虚假变量为枚举常量
            String name = root.getNodeName();
            if(!values.containsKey(name)){
                return new Constant(name, true);
            }
        }
        List<TreeNode> childList = root.getChildList();
        for (int i = 0; i < childList.size(); i++) {
            TreeNode treeNode = childList.get(i);
            if(treeNode instanceof Proposition)
                traverseToModifyFormula(((Proposition) treeNode).getExpression(), values);
            else if(treeNode instanceof BaseExpression){
                Object modifiedNode = traverseToModifyFormula(treeNode, values);
                if(modifiedNode != null){
                    if(root instanceof ComparisonOperator co){
                        if(co.getLeftArgument().getNodeName().equals(treeNode.getNodeName()))
                            co.setLeftArgument((BaseExpression) modifiedNode);
                        else
                            co.setRightArgument((BaseExpression) modifiedNode);
                        childList.remove(i);
                        childList.add(i, (TreeNode) modifiedNode);
                    }
                }
            }
            else
                traverseToModifyFormula(treeNode, values);
        }
        return null;
    }

    /**
     * 遍历属性公式并生成html形式的属性视图
     * @param root
     * @param index
     * @param causeSet
     * @return
     */
    public static String traverseToGenHighlightedProperty(TreeNode root, int index, Set<Cause> causeSet){
        if(root == null || index < 0)
            return "";

        if(root instanceof Proposition){
            List<Object> values = ((Proposition) root).getExpression().getValues();
            if(values == null || index >= values.size())
                return "";

            BaseExpression expression = ((Proposition) root).getExpression();
            String nodeName = expression.getNodeName();
            Object res = expression.getValues().get(index);
            int flag;
            if(expression.isBooleanVal())
                flag = res.equals(true) ? ExpressionUtils.TRUE_MODE : ExpressionUtils.FALSE_MODE;
            else
                flag = ExpressionUtils.OTHER_MODE;

            if(expression instanceof UnaryOperator){
                return ExpressionUtils.genHtmlSpan(nodeName + " ", flag, false, false, null)
                        + "( "
                        + traverseToGenHighlightedProperty(new Proposition(((UnaryOperator) expression).getArgument()), index, causeSet)
                        + " )";
            }
            else if(expression instanceof BinaryOperator || expression instanceof ComparisonOperator){
                return "( "
                        + traverseToGenHighlightedProperty(new Proposition((BaseExpression) expression.getChildList().get(0)), index, causeSet)
                        + ExpressionUtils.genHtmlSpan(" " + nodeName + " ", flag, false, false, null)
                        + traverseToGenHighlightedProperty(new Proposition((BaseExpression) expression.getChildList().get(1)), index, causeSet)
                        + " )";
            }
            else if(expression instanceof CountOperator){
                List<BaseExpression> arguments = ((CountOperator) expression).getArguments();
                StringBuilder htmlStr = new StringBuilder(ExpressionUtils.genHtmlSpan(nodeName + " ", flag, false, false, null) + "( ");
                for (int i = 0; i < arguments.size(); i++) {
                    htmlStr.append(traverseToGenHighlightedProperty(new Proposition(arguments.get(i)), index, causeSet));
                    if(i < arguments.size() - 1)
                        htmlStr.append(" , ");
                }
                htmlStr.append(" )");
                return htmlStr.toString();
            }
            else if(expression instanceof TernaryOperator){
                return "( "
                        + traverseToGenHighlightedProperty(new Proposition(((TernaryOperator) expression).getCondition()), index, causeSet)
                        + ExpressionUtils.genHtmlSpan(" ? ", flag, false, false, null)
                        + traverseToGenHighlightedProperty(new Proposition(((TernaryOperator) expression).getLeftOption()), index, causeSet)
                        + ExpressionUtils.genHtmlSpan(" : ", flag, false, false, null)
                        + traverseToGenHighlightedProperty(new Proposition(((TernaryOperator) expression).getRightOption()), index, causeSet)
                        + " )";
            }
            else if(expression instanceof Constant)
                return ExpressionUtils.genHtmlSpan(nodeName, flag, false, false, null);
            else if(expression instanceof Variable){
                boolean isCause = false;
                if(causeSet != null)
                    for (Cause cause : causeSet) {
                        if(cause.getPosition() == index && cause.getVariable().equals(nodeName)){
                            isCause = true;
                            break;
                        }
                    }
                return ExpressionUtils.genHtmlSpan(nodeName, flag, true, isCause, res);
            }
        }
        else{
            if(root.getValues() == null || index >= root.getValues().size())
                return "";

            String nodeName = root.getNodeName();
            Object res = root.getValues().get(index);
            int flag;
            if(root.isBooleanVal())
                flag = res.equals(true) ? ExpressionUtils.TRUE_MODE : ExpressionUtils.FALSE_MODE;
            else
                flag = ExpressionUtils.OTHER_MODE;

            if(root instanceof CTLUnaryOperator){
                return  ExpressionUtils.genHtmlSpan(nodeName + " ", flag, false, false, null)
                        + "( "
                        + traverseToGenHighlightedProperty(((CTLUnaryOperator) root).getArgument(), index, causeSet)
                        + " )";
            }
            else if(root instanceof CTLBinaryOperator){
                if(nodeName.equals(CTLSignal.AU) || nodeName.equals(CTLSignal.EU)){
                    return ExpressionUtils.genHtmlSpan(nodeName.charAt(0) + " ", flag, false, false, null)
                            + " [ "
                            + traverseToGenHighlightedProperty(((CTLBinaryOperator) root).getLeftArgument(), index, causeSet)
                            + ExpressionUtils.genHtmlSpan(" " + nodeName.charAt(1) + " ", flag, false, false, null)
                            + traverseToGenHighlightedProperty(((CTLBinaryOperator) root).getRightArgument(), index, causeSet)
                            + " ] ";
                }
                else {
                    return "( "
                            + traverseToGenHighlightedProperty(((CTLBinaryOperator) root).getLeftArgument(), index, causeSet)
                            + ExpressionUtils.genHtmlSpan(" " + nodeName + " ", flag, false, false, null)
                            + traverseToGenHighlightedProperty(((CTLBinaryOperator) root).getRightArgument(), index, causeSet)
                            + " )";
                }
            }
        }

        return "";
    }
}
