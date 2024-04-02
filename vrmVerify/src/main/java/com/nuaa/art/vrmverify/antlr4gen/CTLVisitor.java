// Generated from E:/idea_projects/ART-SpringBoot/vrmVerify/src/main/resources/CTL.g4 by ANTLR 4.13.1
package com.nuaa.art.vrmverify.antlr4gen;

    import com.nuaa.art.vrmverify.model.formula.expression.*;
    import com.nuaa.art.vrmverify.model.formula.ctl.*;
    import com.nuaa.art.vrmverify.model.formula.TreeNode;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CTLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CTLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CTLParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(CTLParser.ConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#composite_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComposite_id(CTLParser.Composite_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#path_quantifier_sign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPath_quantifier_sign(CTLParser.Path_quantifier_signContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#unary_operator_sign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_operator_sign(CTLParser.Unary_operator_signContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator_sign5}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_sign5(CTLParser.Binary_operator_sign5Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator_sign4}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_sign4(CTLParser.Binary_operator_sign4Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator_sign3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_sign3(CTLParser.Binary_operator_sign3Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator_sign2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_sign2(CTLParser.Binary_operator_sign2Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator_sign1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_sign1(CTLParser.Binary_operator_sign1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#comparison_operator_sign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_operator_sign(CTLParser.Comparison_operator_signContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#arithmetic_atomic_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmetic_atomic_value(CTLParser.Arithmetic_atomic_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#arithmetic_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmetic_atom(CTLParser.Arithmetic_atomContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#arithmetic_expression3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmetic_expression3(CTLParser.Arithmetic_expression3Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#arithmetic_expression2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmetic_expression2(CTLParser.Arithmetic_expression2Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#arithmetic_expression1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmetic_expression1(CTLParser.Arithmetic_expression1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#comparison_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison_expression(CTLParser.Comparison_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#and_arithmetic_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_arithmetic_expression(CTLParser.And_arithmetic_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#or_arithmetic_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr_arithmetic_expression(CTLParser.Or_arithmetic_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#ternary_arithmetic_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTernary_arithmetic_expression(CTLParser.Ternary_arithmetic_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#eq_arithmetic_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEq_arithmetic_expression(CTLParser.Eq_arithmetic_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#implies_arithmetic_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImplies_arithmetic_expression(CTLParser.Implies_arithmetic_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#proposition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProposition(CTLParser.PropositionContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(CTLParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#unary_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_operator(CTLParser.Unary_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator5}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator5(CTLParser.Binary_operator5Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator4}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator4(CTLParser.Binary_operator4Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator3(CTLParser.Binary_operator3Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator2(CTLParser.Binary_operator2Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#binary_operator1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator1(CTLParser.Binary_operator1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#formula}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormula(CTLParser.FormulaContext ctx);
	/**
	 * Visit a parse tree produced by {@link CTLParser#formula_eof}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormula_eof(CTLParser.Formula_eofContext ctx);
}