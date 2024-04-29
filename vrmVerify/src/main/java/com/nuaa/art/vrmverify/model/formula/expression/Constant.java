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

    boolean isEnumValue;

    public Constant(String name) {
        super(name);
        setLeaf(true);
    }

    public Constant(String name, boolean isEnumValue){
        super(name);
        this.isEnumValue = isEnumValue;
        setLeaf(true);
    }

    @Override
    public Object calculate(Map<String, List<String>> values, int position) {
        if (name.equals("TRUE"))
            return true;
        else if (name.equals("FALSE"))
            return false;

        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException ignored) {
            final BigRational result;
            final String strValue = name.toLowerCase();
            if (strValue.contains(".") || strValue.contains("e")) {
                try {
                    final BigDecimal x = new BigDecimal(strValue);
                    BigInteger numerator = x.unscaledValue();
                    BigInteger denominator = BigInteger.ONE;
                    if (x.scale() <= 0) {
                        numerator = numerator.multiply(BigInteger.TEN.pow(-x.scale()));
                    } else {
                        denominator = denominator.multiply(BigInteger.TEN.pow(x.scale()));
                    }
                    result = new BigRational(numerator, denominator);
                }
                catch (NumberFormatException e){
                    isEnumValue = true;
                    return name;
                }
            } else if (strValue.startsWith("f'") || strValue.startsWith("-f'")) {
                try {
                    result = new BigRational(strValue.replace("f'", ""));
                }
                catch (NumberFormatException e){
                    isEnumValue = true;
                    return name;
                }
            } else {
                isEnumValue = true;
                return name;
            }
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
