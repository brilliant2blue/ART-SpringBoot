package com.nuaa.art.vrmverify.model.formula.expression;

import com.nuaa.art.vrmverify.common.utils.ExpressionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 比较运算符
 * @author djl
 * @date 2024-03-27
 */
public class ComparisonOperator extends BaseExpression{

    private final BaseExpression leftArgument;
    private final BaseExpression rightArgument;

    public ComparisonOperator(String name, BaseExpression leftArgument, BaseExpression rightArgument) {
        super(name);
        this.leftArgument = leftArgument;
        this.rightArgument = rightArgument;
    }

    @Override
    public String toString() {
        return ExpressionUtils.par(leftArgument + " " + name + " " + rightArgument);
    }

    @Override
    public Object calculate(Map<String, List<String>> values, int position) {
        final Object leftValue = intToRational(leftArgument.calculate(values, position));
        final Object rightValue = intToRational(rightArgument.calculate(values, position));

        switch (name) {
            case "=":
                return leftValue.equals(rightValue);
            case "!=":
                return !leftValue.equals(rightValue);
            default:
                if (leftValue instanceof BigRational && rightValue instanceof BigRational) {
                    final int cmp = ((BigRational) leftValue).compareTo((BigRational) rightValue);
                    return switch (name) {
                        case ">" -> cmp > 0;
                        case ">=" -> cmp >= 0;
                        case "<" -> cmp < 0;
                        case "<=" -> cmp <= 0;
                        default -> throw unexpectedOperatorException("比较");
                    };
                } else {
                    throw arithmeticException();
                }
        }
    }

    @Override
    public Set<String> variableSet() {
        return new TreeSet<String>() {{
            addAll(leftArgument.variableSet());
            addAll(rightArgument.variableSet());
        }};
    }
}
