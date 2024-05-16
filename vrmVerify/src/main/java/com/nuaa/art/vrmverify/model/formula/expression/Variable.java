package com.nuaa.art.vrmverify.model.formula.expression;

import com.nuaa.art.vrmverify.common.Msg;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 变量
 * @author djl
 * @date 2024-03-27
 */
public class Variable extends BaseExpression{

    public Variable(String name) {
        super(name);
        setLeaf(true);
    }

    @Override
    public Object calculate(Map<String, List<String>> values, int position) {
        if (!values.containsKey(name)) {
//            throw new RuntimeException("缺少变量名称: " + name
//                    + "。一个可能的原因是该变量在CTL公式中出现"
//                    + "，但在对应的反例中没有出现。所有出现的变量如下："
//                    + values.keySet());
            throw new RuntimeException(Msg.NOT_CTL_FORMULA);
        }
        return new Constant(values.get(name).get(position)).calculate(values, position);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Set<String> variableSet() {
        return Collections.singleton(name);
    }
}
