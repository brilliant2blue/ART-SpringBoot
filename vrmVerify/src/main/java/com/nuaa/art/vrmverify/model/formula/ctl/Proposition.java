package com.nuaa.art.vrmverify.model.formula.ctl;

import com.nuaa.art.vrmverify.common.utils.ExpressionUtils;
import com.nuaa.art.vrmverify.model.formula.expression.BaseExpression;
import com.nuaa.art.vrmverify.model.formula.expression.Constant;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author djl
 * @date 2024-03-27
 */
public class Proposition extends CTLFormula{

    private final BaseExpression expression;
    private final Proposition originalVersion;
    private final boolean negated;

    private Proposition(BaseExpression expression, Proposition originalVersion, boolean negated) {
        this.expression = expression;
        this.originalVersion = originalVersion;
        this.negated = negated;
        this.setProposition(true);
    }

    public Proposition(BaseExpression expression) {
        this(expression, null, false);
        registerFormula(this);
    }

    public static Proposition trueFormula() {
        return new Proposition(new Constant("TRUE"));
    }

    public static Proposition falseFormula() {
        return new Proposition(new Constant("FALSE"));
    }

    public Proposition originalVersion() {
        return originalVersion == null ? this : originalVersion;
    }

    public BaseExpression getExpression(){
        return expression;
    }

    public Proposition not() {
        return new Proposition(expression, originalVersion == null ? this : originalVersion, !negated);
    }

    @Override
    public String toString() {
        return negated ? ("!" + ExpressionUtils.par(expression)) : expression.toString();
    }

    private String htmlToString() {
        return ExpressionUtils.toHtmlString(toString());
    }

    @Override
    public Set<String> variableSet() {
        return new TreeSet<>(expression.variableSet());
    }

//    @Override
//    public CTLFormula removeF() {
//        return this;
//    }
//
//    @Override
//    public CTLFormula removeG() {
//        return this;
//    }
//
    @Override
    public CTLFormula removeImplication() {
        return this;
    }

    @Override
    public CTLFormula removeEquivalence() {
        return this;
    }

    @Override
    public CTLFormula removeXor() {
        return this;
    }

    @Override
    public CTLFormula toNNF() {
        return this;
    }

}
