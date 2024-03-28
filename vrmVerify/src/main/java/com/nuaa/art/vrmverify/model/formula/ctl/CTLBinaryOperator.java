package com.nuaa.art.vrmverify.model.formula.ctl;

import com.nuaa.art.vrmverify.common.utils.ExpressionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;

/**
 * @author djl
 * @date 2024-03-27
 */
public class CTLBinaryOperator extends CTLFormula{

    public final String name;
    public final CTLFormula leftArgument;
    public final CTLFormula rightArgument;

    public CTLBinaryOperator(String name, CTLFormula leftArgument, CTLFormula rightArgument) {
        this.name = name;
        this.leftArgument = leftArgument;
        this.rightArgument = rightArgument;
        registerFormula(this);
    }

    @Override
    public String toString() {
        if(name.equals("EU") || name.equals("AU"))
            return ExpressionUtils.par(name.charAt(0) + " [" + leftArgument + " " + name.charAt(1) + " " + rightArgument);
        else
            return ExpressionUtils.par(leftArgument + " " + name + " " + rightArgument);
    }

    @Override
    public Set<String> variableSet() {
        final Set<String> result = leftArgument.variableSet();
        result.addAll(rightArgument.variableSet());
        return result;
    }

    private CTLFormula recursion(Function<CTLFormula, CTLFormula> baseFunction,
                                 Function<CTLBinaryOperator, CTLFormula> transformation,
                                 String... specialNames) {
        final CTLFormula processedLeft = baseFunction.apply(leftArgument);
        final CTLFormula processedRight = baseFunction.apply(rightArgument);
        final CTLBinaryOperator processed = new CTLBinaryOperator(name, processedLeft, processedRight);
        return Arrays.asList(specialNames).contains(name) ? transformation.apply(processed) : processed;
    }

//    @Override
//    public CTLFormula removeF() {
//        return recursion(CTLFormula::removeF, null);
//    }
//
//    @Override
//    public CTLFormula removeImplication() {
//        return recursion(CTLFormula::removeImplication, f -> or(not(f.leftArgument), f.rightArgument), "->");
//    }
//
//    @Override
//    public CTLFormula removeEquivalence() {
//        return recursion(CTLFormula::removeEquivalence, f -> or(and(f.leftArgument, f.rightArgument),
//                and(not(f.leftArgument), not(f.rightArgument))), "<->", "xnor");
//    }
//
//    @Override
//    public CTLFormula removeXor() {
//        return recursion(CTLFormula::removeXor, f -> not(or(and(f.leftArgument, f.rightArgument),
//                and(not(f.leftArgument), not(f.rightArgument)))), "xor");
//    }
//
//    @Override
//    public CTLFormula removeG() {
//        return recursion(CTLFormula::removeG, null);
//    }

    @Override
    public CTLFormula toNNF() {
        return recursion(CTLFormula::toNNF, null);
    }

}
