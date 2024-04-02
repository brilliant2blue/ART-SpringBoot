package com.nuaa.art.vrmverify.model.formula.expression;

import com.nuaa.art.vrmverify.common.utils.ExpressionUtils;
import com.nuaa.art.vrmverify.model.formula.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 三元运算符
 * @author djl
 * @date 2024-03-27
 */
public class TernaryOperator extends BaseExpression{

    private final BaseExpression condition;
    private final BaseExpression leftOption;
    private final BaseExpression rightOption;

    public TernaryOperator(BaseExpression condition, BaseExpression leftOption, BaseExpression rightOption) {
        super("?:");
        this.condition = condition;
        this.leftOption = leftOption;
        this.rightOption = rightOption;
        List<TreeNode> childList = getChildList();
        childList.add(condition);
        childList.add(leftOption);
        childList.add(rightOption);
    }

    public BaseExpression getCondition() {
        return condition;
    }

    public BaseExpression getLeftOption() {
        return leftOption;
    }

    public BaseExpression getRightOption() {
        return rightOption;
    }

    @Override
    public String toString() {
        return ExpressionUtils.par(condition + " ? " + leftOption + " : " + rightOption);
    }

    @Override
    public Object calculate(Map<String, List<String>> values, int position) {
        final Object conditionValue = condition.calculate(values, position);
        if (conditionValue instanceof Boolean) {
            return ((boolean) conditionValue ? leftOption : rightOption).calculate(values, position);
        } else {
            throw arithmeticException();
        }
    }

    @Override
    public Set<String> variableSet() {
        return new TreeSet<String>() {{
            addAll(condition.variableSet());
            addAll(leftOption.variableSet());
            addAll(rightOption.variableSet());
        }};
    }
}
