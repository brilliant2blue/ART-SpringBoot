package com.nuaa.art.vrmverify.model.formula.expression;

import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.model.formula.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基本表达式
 * @author djl
 * @date 2024-03-27
 */
public abstract class BaseExpression extends TreeNode {

    final String name;

    BaseExpression(String name) {
        this.name = name;
    }

    public abstract Object calculate(Map<String, List<String>> values, int position);

    public abstract Set<String> variableSet();

    public Object intToRational(Object x) {
        if (x instanceof Integer) {
            return new BigRational((int) x, 1);
        }
        return x;
    }

    public RuntimeException arithmeticException() {
        return new RuntimeException(Msg.WRONG_ARITHMETIC_TYPE + toString());
    }

    public RuntimeException unexpectedOperatorException(String description) {
        return new RuntimeException("在表达式中：" + toString() + "，存在未知或非法"
                + description + "运算符" + name);
    }
}
