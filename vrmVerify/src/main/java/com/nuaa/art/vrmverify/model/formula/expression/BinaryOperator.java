package com.nuaa.art.vrmverify.model.formula.expression;

import com.nuaa.art.vrmverify.common.utils.ExpressionUtils;
import com.nuaa.art.vrmverify.model.formula.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 二元运算符
 * @author djl
 * @date 2024-03-27
 */
public class BinaryOperator extends BaseExpression {
    private final BaseExpression leftArgument;
    private final BaseExpression rightArgument;

    public BinaryOperator(String name, BaseExpression leftArgument, BaseExpression rightArgument) {
        super(name);
        this.leftArgument = leftArgument;
        this.rightArgument = rightArgument;
        List<TreeNode> childList = getChildList();
        childList.add(leftArgument);
        childList.add(rightArgument);
    }

    public BaseExpression getLeftArgument(){
        return leftArgument;
    }

    public BaseExpression getRightArgument(){
        return rightArgument;
    }

    @Override
    public String toString() {
        return ExpressionUtils.par(leftArgument + " " + name + " " + rightArgument);
    }

    @Override
    public Object calculate(Map<String, List<String>> values, int position) {
        final Object leftValue = leftArgument.calculate(values, position);
        final Object rightValue = rightArgument.calculate(values, position);

        if (leftValue instanceof Boolean && rightValue instanceof Boolean) {
            final boolean l = (boolean) leftValue;
            final boolean r = (boolean) rightValue;
            return switch (name) {
                case "&" -> l & r;
                case "|" -> l || r;
                case "->" -> !l || r;
                case "xor" -> l != r;
                case "<->", "xnor" -> l == r;
                default -> throw unexpectedOperatorException("二元");
            };
        } else if (leftValue instanceof Integer && rightValue instanceof Integer) {
            final int l = (int) leftValue;
            final int r = (int) rightValue;
            return switch (name) {
                case "+" -> l + r;
                case "-" -> l - r;
                case "*" -> l * r;
                case "/" -> l / r;
                case "mod" -> l % r;
                default -> throw unexpectedOperatorException("二元");
            };
        } else if (leftValue instanceof BigRational || rightValue instanceof BigRational) {
            if (leftValue instanceof Boolean || rightValue instanceof Boolean) {
                throw arithmeticException();
            }
            // ensure that both are rationals
            final BigRational l = (BigRational) intToRational(leftValue);
            final BigRational r = (BigRational) intToRational(rightValue);
            return switch (name) {
                case "+" -> l.plus(r);
                case "-" -> l.minus(r);
                case "*" -> l.multiply(r);
                case "/" -> l.divides(r);
                case "mod" -> throw new RuntimeException("nuXmv 不支持对实数进行 mod 操作");
                default -> throw unexpectedOperatorException("二元");
            };
        } else {
            throw arithmeticException();
        }
    }

    @Override
    public Set<String> variableSet() {
        final Set<String> vars = new TreeSet<>();
        vars.addAll(leftArgument.variableSet());
        vars.addAll(rightArgument.variableSet());
        return vars;
    }
}
