package com.nuaa.art.vrmverify.handler;

import com.nuaa.art.vrmverify.common.CTLSignal;
import com.nuaa.art.vrmverify.model.Counterexample;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.explanation.Trace;
import com.nuaa.art.vrmverify.model.formula.TreeNode;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLBinaryOperator;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLUnaryOperator;
import com.nuaa.art.vrmverify.model.formula.ctl.Proposition;
import com.nuaa.art.vrmverify.model.formula.expression.*;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 处理反例解释
 *
 * @author djl
 * @date 2024-03-30
 */
public class CxExplanationHandler {

    /**
     * 计算反例路径
     *
     * @param cx
     * @return
     */
    public static Trace computeTrace(Counterexample cx) {
        if (cx.isPassed())
            return null;
        int traceLength = cx.getTraceLength();
        boolean existLoop = cx.isExistLoop();
        int loopStartsState = cx.getLoopStartsState();
        List<Map<String, String>> assignments = cx.getAssignments();
        Set<String> variableSet = new HashSet<>();
        for (Map.Entry<String, String> assignment : assignments.get(0).entrySet()) {
            variableSet.add(assignment.getKey());
        }
        for (int i = 1; i < assignments.size(); i++) {
            Map<String, String> curAssignment = assignments.get(i);
            for (String v : variableSet) {
                if (!curAssignment.containsKey(v))
                    curAssignment.put(v, assignments.get(i - 1).get(v));
            }
        }
        return new Trace(traceLength, existLoop, loopStartsState, assignments);
    }

    /**
     * 计算原因集
     *
     * @param vt
     * @param index
     * @param f
     * @return
     */
    public static Set<Cause> computeCauseSet(VariableTable vt, int index, CTLFormula f, boolean flag) {
        if (vt == null || index < 0 || index >= vt.getTraceLength() || f == null)
            return null;

        Set<Cause> causeSet = new HashSet<>();
        Map<String, List<String>> variableValues = vt.getVariableValues();

        if (f instanceof Proposition) {
            BaseExpression exp = ((Proposition) f).getExpression();
            String name = exp.getNodeName();
            Object res = exp.calculate(variableValues, index);

            if (!res.equals(flag))
                return null;

            if (name.equals("TRUE") || name.equals("FALSE"))
                return null;

            if (exp instanceof UnaryOperator) {
                if (name.equals("!")) {
                    BaseExpression argument = ((UnaryOperator) exp).getArgument();
                    CTLFormula f1 = new Proposition(argument);
                    return computeCauseSet(vt, index, f1, !flag);
                } else
                    return null;
            } else if (exp instanceof BinaryOperator) {
                BaseExpression leftArgument = ((BinaryOperator) exp).getLeftArgument();
                BaseExpression rightArgument = ((BinaryOperator) exp).getRightArgument();
                Object leftRes = leftArgument.calculate(variableValues, index);
                Object rightRes = rightArgument.calculate(variableValues, index);
                CTLFormula f1 = new Proposition(leftArgument);
                CTLFormula f2 = new Proposition(rightArgument);

                switch (name) {
                    case "&": {
                        if (leftRes.equals(false)) {
                            Set<Cause> cs1 = computeCauseSet(vt, index, f1, (boolean) leftRes);
                            if (cs1 != null && !cs1.isEmpty())
                                causeSet.addAll(cs1);
                        }
                        if (rightRes.equals(false)) {
                            Set<Cause> cs2 = computeCauseSet(vt, index, f2, (boolean) leftRes);
                            if (cs2 != null && !cs2.isEmpty())
                                causeSet.addAll(cs2);
                        }
                        break;
                    }
                    case "|", "->", "<->", "xor", "xnor": {
                        Set<Cause> cs1 = computeCauseSet(vt, index, f1, (boolean) leftRes);
                        if (cs1 != null && !cs1.isEmpty())
                            causeSet.addAll(computeCauseSet(vt, index, f1, (boolean) leftRes));
                        Set<Cause> cs2 = computeCauseSet(vt, index, f2, (boolean) leftRes);
                        if (cs2 != null && !cs2.isEmpty())
                            causeSet.addAll(computeCauseSet(vt, index, f2, (boolean) rightRes));
                        break;
                    }
                    default:
                        return null;
                }
            } else if (exp instanceof ComparisonOperator) {
                BaseExpression leftArgument = ((ComparisonOperator) exp).getLeftArgument();
                BaseExpression rightArgument = ((ComparisonOperator) exp).getRightArgument();

                for (String v : leftArgument.variableSet())
                    causeSet.add(new Cause(index, v));
                for (String v : rightArgument.variableSet())
                    causeSet.add(new Cause(index, v));
            } else if (exp instanceof TernaryOperator) {
                BaseExpression condition = ((TernaryOperator) exp).getCondition();
                Object condRes = condition.calculate(variableValues, index);
                Set<Cause> cs1 = computeCauseSet(vt, index, new Proposition(condition), (boolean) condRes);
                if (cs1 != null && !cs1.isEmpty())
                    causeSet.addAll(cs1);
                else
                    return null;

                Set<Cause> cs2 = null;
                if (condRes.equals(true)) {
                    BaseExpression leftOption = ((TernaryOperator) exp).getLeftOption();
                    Object leftRes = leftOption.calculate(variableValues, index);
                    cs2 = computeCauseSet(vt, index, new Proposition(leftOption), (boolean) leftRes);
                } else {
                    BaseExpression rightOption = ((TernaryOperator) exp).getRightOption();
                    Object rightRes = rightOption.calculate(variableValues, index);
                    cs2 = computeCauseSet(vt, index, new Proposition(rightOption), (boolean) rightRes);
                }
                if (cs2 != null && !cs2.isEmpty())
                    causeSet.addAll(cs2);
            } else if (exp instanceof CountOperator) {
                List<BaseExpression> arguments = ((CountOperator) exp).getArguments();
                for (BaseExpression argument : arguments) {
                    Set<Cause> cs = computeCauseSet(vt, index, new Proposition(argument), (boolean) argument.calculate(variableValues, index));
                    if (cs != null && !cs.isEmpty())
                        causeSet.addAll(cs);
                }
            } else if (exp instanceof Variable) {
                causeSet.add(new Cause(index, name));
            } else
                return null;
        } else if (f instanceof CTLUnaryOperator) {
            String name = f.getNodeName();
            CTLFormula argument = ((CTLUnaryOperator) f).getArgument();

            switch (name) {
                case "!" -> {
                    return computeCauseSet(vt, index, argument, !flag);
                }
                case CTLSignal.AX, CTLSignal.EX -> {
                    return computeCauseSet(vt, index + 1, argument, flag);
                }
                case CTLSignal.AF -> {
                    if (flag)
                        return null;
                    for (int i = index; i < vt.getTraceLength(); i++) {
                        Set<Cause> cs = computeCauseSet(vt, i, argument, false);
                        if (cs == null || cs.isEmpty())
                            return null;
                        causeSet.addAll(cs);
                    }
                }
                case CTLSignal.EF -> {
                    if (!flag)
                        return null;
                    for (int i = index; i < vt.getTraceLength(); i++) {
                        Set<Cause> cs = computeCauseSet(vt, i, argument, true);
                        if (cs != null && !cs.isEmpty())
                            return cs;
                    }
                    return null;
                }
                case CTLSignal.AG -> {
                    if (flag)
                        return null;
                    Set<Cause> cs = computeCauseSet(vt, index, argument, false);
                    if (cs != null && !cs.isEmpty())
                        return cs;
                    else
                        return computeCauseSet(vt, index + 1, f, false);
                }
                case CTLSignal.EG -> {
                    if (!flag)
                        return null;
                    for (int i = index; i < vt.getTraceLength(); i++) {
                        Set<Cause> cs = computeCauseSet(vt, i, argument, true);
                        if (cs == null || cs.isEmpty())
                            return null;
                        causeSet.addAll(cs);
                    }
                }
                default -> {
                    return null;
                }
            }
        } else if (f instanceof CTLBinaryOperator) {
            String name = f.getNodeName();
            CTLFormula leftArgument = ((CTLBinaryOperator) f).getLeftArgument();
            CTLFormula rightArgument = ((CTLBinaryOperator) f).getRightArgument();
            Set<Cause> cs1 = computeCauseSet(vt, index, leftArgument, flag);
            Set<Cause> cs2 = computeCauseSet(vt, index, rightArgument, flag);
            boolean flag1 = cs1 != null && !cs1.isEmpty();
            boolean flag2 = cs2 != null && !cs2.isEmpty();

            switch (name) {
                case "&" -> {
                    if (flag) {
                        if (flag1 && flag2) {
                            causeSet.addAll(cs1);
                            causeSet.addAll(cs2);
                        } else
                            return null;
                    } else {
                        if (flag1)
                            causeSet.addAll(cs1);
                        if (flag2)
                            causeSet.addAll(cs2);
                    }
                }
                case "|" -> {
                    if (flag) {
                        if (flag1)
                            causeSet.addAll(cs1);
                        if (flag2)
                            causeSet.addAll(cs2);
                    } else {
                        if (flag1 && flag2) {
                            causeSet.addAll(cs1);
                            causeSet.addAll(cs2);
                        } else
                            return null;
                    }
                }
                case "->" -> {
                    cs1 = computeCauseSet(vt, index, leftArgument, true);
                    cs2 = computeCauseSet(vt, index, rightArgument, false);
                    flag1 = cs1 != null && !cs1.isEmpty();
                    flag2 = cs2 != null && !cs2.isEmpty();
                    if (flag) {
                        if (flag1 && flag2)
                            return null;
                        else {
                            Set<Cause> cs3 = computeCauseSet(vt, index, leftArgument, false);
                            Set<Cause> cs4 = computeCauseSet(vt, index, rightArgument, true);
                            if (flag1)
                                causeSet.addAll(cs1);
                            else
                                causeSet.addAll(cs3);
                            if (flag2)
                                causeSet.addAll(cs2);
                            else
                                causeSet.addAll(cs4);
                        }
                    } else {
                        if (flag1 && flag2) {
                            causeSet.addAll(cs1);
                            causeSet.addAll(cs2);
                        } else
                            return null;
                    }
                }
                case "xor" -> {
                    if (flag1 == flag2) {
                        if (!flag1) {
                            cs1 = computeCauseSet(vt, index, leftArgument, !flag);
                            cs2 = computeCauseSet(vt, index, rightArgument, !flag);
                        }
                        causeSet.addAll(cs1);
                        causeSet.addAll(cs2);
                    } else
                        return null;
                }
                case "<->", "xnor" -> {
                    if (flag1 != flag2) {
                        if (flag1)
                            cs2 = computeCauseSet(vt, index, rightArgument, !flag);
                        else
                            cs1 = computeCauseSet(vt, index, leftArgument, !flag);
                        causeSet.addAll(cs1);
                        causeSet.addAll(cs2);
                    } else
                        return null;
                }
                case CTLSignal.AU -> {
                    if (flag)
                        return null;
                    if (flag1 && flag2) {
                        causeSet.addAll(cs1);
                        causeSet.addAll(cs2);
                    } else if (!flag1 && flag2) {
                        if (index == vt.getTraceLength() - 1)
                            causeSet.addAll(cs2);
                        else {
                            Set<Cause> cs = computeCauseSet(vt, index + 1, f, false);
                            if (cs != null && !cs.isEmpty())
                                causeSet.addAll(cs);
                        }
                    } else
                        return null;
                }
                case CTLSignal.EU -> {
                    if (!flag)
                        return null;
                    for (int i = index; i < vt.getTraceLength(); i++) {
                        cs1 = computeCauseSet(vt, i, leftArgument, true);
                        cs2 = computeCauseSet(vt, i, rightArgument, false);
                        flag1 = cs1 != null && !cs1.isEmpty();
                        flag2 = cs2 != null && !cs2.isEmpty();
                        if (!flag1 && flag2)
                            return null;
                        else {
                            Set<Cause> cs3 = computeCauseSet(vt, i, leftArgument, false);
                            Set<Cause> cs4 = computeCauseSet(vt, i, rightArgument, true);
                            if (flag1)
                                causeSet.addAll(cs1);
                            else
                                causeSet.addAll(cs3);
                            if (flag2)
                                causeSet.addAll(cs2);
                            else
                                causeSet.addAll(cs4);
                        }
                    }
                }
                default -> {
                    return null;
                }
            }
        }

        return causeSet;
    }


    /**
     * 计算公式中各个元素在每一反例步上的值
     * @param f
     * @param vt
     */
    public static void computeFormulaValues(CTLFormula f, VariableTable vt) {
        for (int i = 0; i < vt.getTraceLength(); i++) {
            computeOneStepFormulaValues(f, vt, i, true);
        }
    }

    /**
     * 计算公式中各个元素在单次反例步上的值
     * @param f
     * @param vt
     * @param index
     * @param enable
     * @return
     */
    private static Object computeOneStepFormulaValues(CTLFormula f, VariableTable vt, int index, boolean enable) {
        if (f == null || vt == null || index < 0 || index >= vt.getTraceLength())
            return null;

        Map<String, List<String>> variableValues = vt.getVariableValues();
        List<Object> values = f.getValues();
        Object res = null;

        if (f instanceof Proposition) {
            BaseExpression exp = ((Proposition) f).getExpression();
            res = exp.calculate(variableValues, index);
            List<Object> expValues = exp.getValues();

            if(enable)
                expValues.add(res);

            if (res.equals(true) || res.equals(false))
                exp.setBooleanVal(true);


            if (exp instanceof UnaryOperator) {
                BaseExpression argument = ((UnaryOperator) exp).getArgument();
                CTLFormula f1 = new Proposition(argument);
                computeOneStepFormulaValues(f1, vt, index, enable);
            } else if (exp instanceof BinaryOperator) {
                BaseExpression leftArgument = ((BinaryOperator) exp).getLeftArgument();
                BaseExpression rightArgument = ((BinaryOperator) exp).getRightArgument();
                CTLFormula f1 = new Proposition(leftArgument);
                CTLFormula f2 = new Proposition(rightArgument);
                computeOneStepFormulaValues(f1, vt, index, enable);
                computeOneStepFormulaValues(f2, vt, index, enable);
            }
            else if(exp instanceof ComparisonOperator){
                BaseExpression leftArgument = ((ComparisonOperator) exp).getLeftArgument();
                BaseExpression rightArgument = ((ComparisonOperator) exp).getRightArgument();
                CTLFormula f1 = new Proposition(leftArgument);
                CTLFormula f2 = new Proposition(rightArgument);
                computeOneStepFormulaValues(f1, vt, index, enable);
                computeOneStepFormulaValues(f2, vt, index, enable);
            }else if (exp instanceof TernaryOperator) {
                BaseExpression condition = ((TernaryOperator) exp).getCondition();
                BaseExpression leftOption = ((TernaryOperator) exp).getLeftOption();
                BaseExpression rightOption = ((TernaryOperator) exp).getRightOption();
                CTLFormula f1 = new Proposition(condition);
                CTLFormula f2 = new Proposition(leftOption);
                CTLFormula f3 = new Proposition(rightOption);
                computeOneStepFormulaValues(f1, vt, index, enable);
                computeOneStepFormulaValues(f2, vt, index, enable);
                computeOneStepFormulaValues(f3, vt, index, enable);
            } else if (exp instanceof CountOperator) {
                List<BaseExpression> arguments = ((CountOperator) exp).getArguments();
                for (BaseExpression argument : arguments) {
                    CTLFormula f1 = new Proposition(argument);
                    computeOneStepFormulaValues(f1, vt, index, enable);
                }
            }
        } else if (f instanceof CTLUnaryOperator) {
            String name = f.getNodeName();
            CTLFormula argument = ((CTLUnaryOperator) f).getArgument();
            boolean subRes = (boolean)computeOneStepFormulaValues(argument, vt, index, enable);
            f.setBooleanVal(true);

            switch (name) {
                case "!" ->
                    res = !subRes;
                case CTLSignal.AX, CTLSignal.EX ->
                    res = computeOneStepFormulaValues(argument, vt, index + 1, false);
                case CTLSignal.AF, CTLSignal.EF  -> {
                    res = false;
                    for (int i = index; i < vt.getTraceLength(); i++) {
                        if (computeOneStepFormulaValues(argument, vt, i, false).equals(true)){
                            res = true;
                            break;
                        }
                    }
                }
                case CTLSignal.AG, CTLSignal.EG  -> {
                    res = true;
                    for (int i = index; i < vt.getTraceLength(); i++) {
                        if (computeOneStepFormulaValues(argument, vt, i, false).equals(false)) {
                            res = false;
                            break;
                        }
                    }
                }
            }
        } else if (f instanceof CTLBinaryOperator) {
            String name = f.getNodeName();
            CTLFormula leftArgument = ((CTLBinaryOperator) f).getLeftArgument();
            CTLFormula rightArgument = ((CTLBinaryOperator) f).getRightArgument();
            boolean leftRes = (boolean)computeOneStepFormulaValues(leftArgument, vt, index, enable);
            boolean rightRes = (boolean)computeOneStepFormulaValues(rightArgument, vt, index, enable);
            f.setBooleanVal(true);
            res = false;

            switch (name) {
                case "&" -> {
                    if(leftRes && rightRes)
                        res = true;
                }
                case "|" -> {
                    if(leftRes || rightRes)
                        res = true;
                }
                case "->" -> {
                    if(!(leftRes && !rightRes))
                        res = true;
                }
                case "xor" -> {
                    if(leftRes != rightRes)
                        res = true;
                }
                case "<->", "xnor" -> {
                    if(leftRes == rightRes)
                        res = true;
                }
                case CTLSignal.AU, CTLSignal.EU -> {
                    res = true;
                    for (int i = index; i < vt.getTraceLength(); i++) {
                        boolean res1 = (boolean)computeOneStepFormulaValues(leftArgument, vt, i, false);
                        boolean res2 = (boolean)computeOneStepFormulaValues(rightArgument, vt, i, false);
                        if (!res1 && !res2){
                            res = false;
                            break;
                        }
                    }
                }
            }
        }

        if(enable)
            values.add(res);
        return res;
    }

}
