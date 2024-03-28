package com.nuaa.art.vrmverify.model.formula.expression;

import com.nuaa.art.vrmverify.common.utils.ExpressionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 单元运算符
 * @author djl
 * @date 2024-03-27
 */
public class UnaryOperator extends BaseExpression{

    private final BaseExpression argument;

    public UnaryOperator(String name, BaseExpression argument) {
        super(name);
        this.argument = argument;
    }

    @Override
    public String toString() {
        return name + ExpressionUtils.par(argument.toString());
    }

    @Override
    public Object calculate(Map<String, List<String>> values, int position) {
        final Object argValue = argument.calculate(values, position);
        if ("+".equals(name) && (argValue instanceof Integer || argValue instanceof BigRational)) {
            return argValue;
        }
        if (argValue instanceof Integer) {
            if ("-".equals(name)) {
                return -((int) argValue);
            }
            throw unexpectedOperatorException("单元（integer）");
        } else if (argValue instanceof Boolean) {
            if ("!".equals(name)) {
                return !((boolean) argValue);
            }
            throw unexpectedOperatorException("单元（boolean）");
        } else if (argValue instanceof BigRational) {
            if ("-".equals(name)) {
                return ((BigRational) argValue).negate();
            }
            throw unexpectedOperatorException("单元（real）");
        } else {
            throw arithmeticException();
        }
    }

    @Override
    public Set<String> variableSet() {
        return argument.variableSet();
    }

}
