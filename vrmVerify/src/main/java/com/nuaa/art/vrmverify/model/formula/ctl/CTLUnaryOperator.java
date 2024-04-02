package com.nuaa.art.vrmverify.model.formula.ctl;

import com.nuaa.art.vrmverify.common.CTLSignal;
import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.utils.ExpressionUtils;

import java.util.Set;
import java.util.function.Function;

/**
 * @author djl
 * @date 2024-03-27
 */
public class CTLUnaryOperator extends CTLFormula{

    public final String name;
    public final CTLFormula argument;

    public CTLUnaryOperator(String name, CTLFormula argument) {
        name = name.trim();
        this.name = name;
        this.argument = argument;
        setName(name);
        getChildList().add(argument);
        registerFormula(this);
    }

    @Override
    public String getName() {
        return name;
    }

    public CTLFormula getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return name + ExpressionUtils.par(argument.toString());
    }

    @Override
    public Set<String> variableSet() {
        return argument.variableSet();
    }

    private CTLFormula recursion(Function<CTLFormula, CTLFormula> baseFunction,
                                 Function<CTLUnaryOperator, CTLFormula> transformation, String specialName) {
        final CTLFormula processedArgument = baseFunction.apply(argument);
        final CTLUnaryOperator processed = new CTLUnaryOperator(name, processedArgument);
        return name.equals(specialName) ? transformation.apply(processed) : processed;
    }

//    @Override
//    public CTLFormula removeF() {
//        return recursion(CTLFormula::removeF, f -> until(Proposition.trueFormula(), f.argument), "F");
//    }
//
//    @Override
//    public CTLFormula removeG() {
//        return recursion(CTLFormula::removeG, f -> release(Proposition.falseFormula(), f.argument), "G");
//    }
//
    @Override
    public CTLFormula removeImplication() {
        return recursion(CTLFormula::removeImplication, null, null);
    }

    @Override
    public CTLFormula removeEquivalence() {
        return recursion(CTLFormula::removeEquivalence, null, null);
    }

    @Override
    public CTLFormula removeXor() {
        return recursion(CTLFormula::removeXor, null, null);
    }

    @Override
    public CTLFormula toNNF() {
        if (!name.equals("!")) {
            return new CTLUnaryOperator(name, argument.toNNF());
        } if (argument instanceof Proposition) {
            return ((Proposition) argument).not();
        } else if (argument instanceof CTLUnaryOperator) {
            final CTLUnaryOperator o = (CTLUnaryOperator) argument;
            return switch (o.name) {
                case "!" -> o.argument.toNNF();
                case CTLSignal.EG -> forallGlobally(not(o.argument).toNNF());
                case CTLSignal.EF -> forallFinally(not(o.argument).toNNF());
                case CTLSignal.EX -> forallNextState(not(o.argument).toNNF());
                case CTLSignal.AG -> existsGlobally(not(o.argument).toNNF());
                case CTLSignal.AF -> existsFinally(not(o.argument).toNNF());
                case CTLSignal.AX -> existsNextState(not(o.argument).toNNF());
                default -> throw new RuntimeException(Msg.UNKNOWN_UNARY_OPERATOR + o.name);
            };
        } else if (argument instanceof CTLBinaryOperator) {
            final CTLBinaryOperator o = (CTLBinaryOperator) argument;
            return switch (o.name) {
                case "&" -> or(not(o.leftArgument).toNNF(), not(o.rightArgument).toNNF());
                case "|" -> and(not(o.leftArgument).toNNF(), not(o.rightArgument).toNNF());
                case "xor" -> xnor(not(o.leftArgument).toNNF(), not(o.rightArgument).toNNF());
                case "xnor" -> xor(not(o.leftArgument).toNNF(), not(o.rightArgument).toNNF());
                case "->" -> and(o.leftArgument.toNNF(), not(o.rightArgument).toNNF());
                case "<->" -> equivalence(o.leftArgument.toNNF(), not(o.rightArgument).toNNF());
                case CTLSignal.EU -> forallUntil(not(o.leftArgument).toNNF(), not(o.rightArgument).toNNF());
                case CTLSignal.AU -> existsUntil(not(o.leftArgument).toNNF(), not(o.rightArgument).toNNF());
                default -> throw new RuntimeException(Msg.UNKNOWN_UNARY_OPERATOR + o.name);
            };
        } else {
            throw new AssertionError();
        }
    }

}
