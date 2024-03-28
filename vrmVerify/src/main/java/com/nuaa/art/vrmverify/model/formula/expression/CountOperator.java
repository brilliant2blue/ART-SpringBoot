package com.nuaa.art.vrmverify.model.formula.expression;

import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.utils.ExpressionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * count 运算符
 * @author djl
 * @date 2024-03-27
 */
public class CountOperator extends BaseExpression{

    private final List<BaseExpression> arguments;

    public CountOperator(List<BaseExpression> arguments) {
        super("count");
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return name + ExpressionUtils.par(arguments.toString()
                .replace("[", "")
                .replace("]", ""));
    }

    @Override
    public Object calculate(Map<String, List<String>> values, int position) {
        int count = 0;
        for (BaseExpression arg : arguments) {
            final Object argValue = arg.calculate(values, position);
            if (argValue instanceof Boolean) {
                count += ((Boolean) argValue) ? 1 : 0;
            } else {
                throw new RuntimeException(Msg.WRONG_ARITHMETIC_TYPE + "count 运算符仅支持 boolean 表达式");
            }
        }
        return count;
    }

    @Override
    public Set<String> variableSet() {
        final Set<String> vars = new TreeSet<>();
        arguments.forEach(arg -> vars.addAll(arg.variableSet()));
        return vars;
    }

}
