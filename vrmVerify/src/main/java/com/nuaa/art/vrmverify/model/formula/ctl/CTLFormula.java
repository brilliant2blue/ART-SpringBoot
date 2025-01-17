package com.nuaa.art.vrmverify.model.formula.ctl;

import com.nuaa.art.vrmverify.common.CTLSignal;
import com.nuaa.art.vrmverify.model.formula.TreeNode;

import java.util.*;
import java.util.function.Function;

/**
 * CTL 公式抽象类
 *
 * @author djl
 * @date 2024-03-27
 */
public abstract class CTLFormula extends TreeNode {

    public static final List<CTLFormula> SUB_FORMULAS = new ArrayList<>();
    public static final Map<CTLFormula, Integer> SUB_FORMULAS_2_INDEX = new HashMap<>();

    public static void reset() {
        SUB_FORMULAS.clear();
        SUB_FORMULAS_2_INDEX.clear();
    }

    public void registerFormula(CTLFormula f) {
        SUB_FORMULAS_2_INDEX.put(f, SUB_FORMULAS_2_INDEX.size());
        SUB_FORMULAS.add(f);
    }

    public static CTLFormula getSubFormula(int index) {
        return SUB_FORMULAS.get(index);
    }

    public abstract Set<String> variableSet();

//    public abstract CTLFormula removeF();
//
//    public abstract CTLFormula removeG();
//
    public abstract CTLFormula removeImplication();

    public abstract CTLFormula removeEquivalence();

    public abstract CTLFormula removeXor();

    public abstract CTLFormula toNNF();

    public static CTLFormula not(CTLFormula other) {
        return new CTLUnaryOperator("!", other);
    }

    public static CTLFormula existsGlobally(CTLFormula other) {
        return new CTLUnaryOperator(CTLSignal.EG, other);
    }

    public static CTLFormula existsFinally(CTLFormula other) {
        return new CTLUnaryOperator(CTLSignal.EF, other);
    }

    public static CTLFormula existsNextState(CTLFormula other) {
        return new CTLUnaryOperator(CTLSignal.EX, other);
    }

    public static CTLFormula forallGlobally(CTLFormula other) {
        return new CTLUnaryOperator(CTLSignal.AG, other);
    }

    public static CTLFormula forallNextState(CTLFormula other) {
        return new CTLUnaryOperator(CTLSignal.AX, other);
    }

    public static CTLFormula forallFinally(CTLFormula other) {
        return new CTLUnaryOperator(CTLSignal.AF, other);
    }

    public static CTLFormula and(CTLFormula left, CTLFormula right) {
        return new CTLBinaryOperator("&", left, right);
    }

    public static CTLFormula or(CTLFormula left, CTLFormula right) {
        return new CTLBinaryOperator("|", left, right);
    }

    public static CTLFormula xor(CTLFormula left, CTLFormula right) {
        return new CTLBinaryOperator("xor", left, right);
    }

    public static CTLFormula xnor(CTLFormula left, CTLFormula right) {
        return new CTLBinaryOperator("xnor", left, right);
    }

    public static CTLFormula implication(CTLFormula left, CTLFormula right) {
        return new CTLBinaryOperator("->", left, right);
    }

    public static CTLFormula equivalence(CTLFormula left, CTLFormula right) {
        return new CTLBinaryOperator("<->", left, right);
    }


    public static CTLFormula existsUntil(CTLFormula left, CTLFormula right) {
        return new CTLBinaryOperator(CTLSignal.EU, left, right);
    }

    public static CTLFormula forallUntil(CTLFormula left, CTLFormula right) {
        return new CTLBinaryOperator(CTLSignal.AU, left, right);
    }

    public String url(int position) {
        return SUB_FORMULAS_2_INDEX.get(this) + ":" + position;
    }
}
