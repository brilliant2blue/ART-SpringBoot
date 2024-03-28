package com.nuaa.art.vrmverify.model.formula.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 常量
 * @author djl
 * @date 2024-03-27
 */
public class Constant extends BaseExpression{

    public Constant(String name) {
        super(name);
    }

    @Override
    public Object calculate(Map<String, List<String>> values, int position) {
        if (name.equals("TRUE")) {
            return true;
        } else if (name.equals("FALSE")) {
            return false;
        }

        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException ignored) {
            final BigRational result;
            // parse a rational number
            final String strValue = name.toLowerCase();
            // decimal format?
            if (strValue.contains(".") || strValue.contains("e")) {
                final BigDecimal x = new BigDecimal(strValue);
                BigInteger numerator = x.unscaledValue();
                BigInteger denominator = BigInteger.ONE;
                if (x.scale() <= 0) {
                    numerator = numerator.multiply(BigInteger.TEN.pow(-x.scale()));
                } else {
                    denominator = denominator.multiply(BigInteger.TEN.pow(x.scale()));
                }
                result = new BigRational(numerator, denominator);
            } else if (strValue.startsWith("f'") || strValue.startsWith("-f'")) {
                result = new BigRational(strValue.replace("f'", ""));
            } else {
                throw new RuntimeException("未知的常量类型：" + name);
            }
            // System.out.println(name + " -> " + result);
            return result;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Set<String> variableSet() {
        return Collections.emptySet();
    }

}
