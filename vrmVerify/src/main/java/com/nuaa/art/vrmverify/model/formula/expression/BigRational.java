package com.nuaa.art.vrmverify.model.formula.expression;

import com.nuaa.art.vrmverify.common.Msg;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 大有理数
 *
 * @author djl
 * @date 2024-03-27
 */
public class BigRational {

    private BigInteger numerator;   // 分子
    private BigInteger denominator; // 分母

    public BigRational(int numerator, int denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public BigRational(int numerator) {
        this(numerator, 1);
    }

    /**
     * 通过字符串形式初始化，例："1/3"
     *
     * @param s
     */
    public BigRational(String s) {
        String[] tokens = s.split("/");
        if (tokens.length == 2)
            init(new BigInteger(tokens[0]), new BigInteger(tokens[1]));
        else if (tokens.length == 1)
            init(new BigInteger(tokens[0]), BigInteger.ONE);
        else
            throw new IllegalArgumentException(Msg.INVALID_INPUT_ARGUMENTS + "\"" + s + "\"");
    }

    public BigRational(BigInteger numerator, BigInteger denominator) {
        init(numerator, denominator);
    }

    private void init(BigInteger numerator, BigInteger denominator) {
        // 除零异常
        if (denominator.equals(BigInteger.ZERO)) {
            throw new ArithmeticException(Msg.DIVIDE_BY_ZERO);
        }

        // 若分子为 0，保证分母为 0
        BigInteger g = numerator.gcd(denominator);
        this.numerator = numerator.divide(g);
        this.denominator = denominator.divide(g);

        // 保证分母为正数
        if (denominator.compareTo(BigInteger.ZERO) < 0) {
            this.denominator = denominator.negate();
            this.numerator = numerator.negate();
        }
    }

    public String toString() {
        if (denominator.equals(BigInteger.ONE))
            return numerator + "";
        else
            return numerator + "/" + denominator;
    }

    public int compareTo(BigRational b) {
        BigRational a = this;
        return a.numerator.multiply(b.denominator).compareTo(a.denominator.multiply(b.numerator));
    }

    public boolean isZero() {
        return numerator.signum() == 0;
    }

    public boolean isPositive() {
        return numerator.signum() > 0;
    }

    public boolean isNegative() {
        return numerator.signum() < 0;
    }

    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        BigRational b = (BigRational) y;
        return compareTo(b) == 0;
    }

    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }


    /**
     * 乘法
     *
     * @param b
     * @return
     */
    public BigRational multiply(BigRational b) {
        BigRational a = this;
        return new BigRational(a.numerator.multiply(b.numerator), a.denominator.multiply(b.denominator));
    }

    /**
     * 加法
     *
     * @param b
     * @return
     */
    public BigRational plus(BigRational b) {
        BigRational a = this;
        BigInteger numerator = a.numerator.multiply(b.denominator).add(b.numerator.multiply(a.denominator));
        BigInteger denominator = a.denominator.multiply(b.denominator);
        return new BigRational(numerator, denominator);
    }

    /**
     * 取反
     *
     * @return
     */
    public BigRational negate() {
        return new BigRational(numerator.negate(), denominator);
    }

    /**
     * 绝对值
     *
     * @return
     */
    public BigRational abs() {
        if (isNegative())
            return negate();
        else
            return this;
    }

    /**
     * 减法
     *
     * @param b
     * @return
     */
    public BigRational minus(BigRational b) {
        BigRational a = this;
        return a.plus(b.negate());
    }

    /**
     * 倒数
     *
     * @return
     */
    public BigRational reciprocal() {
        return new BigRational(denominator, numerator);
    }

    /**
     * 除法
     *
     * @param b
     * @return
     */
    public BigRational divides(BigRational b) {
        BigRational a = this;
        return a.multiply(b.reciprocal());
    }

    /**
     * 返回 double 值
     * @return
     */
    public double doubleValue() {
        int SCALE = 32;
        BigDecimal num = new BigDecimal(numerator);
        BigDecimal den = new BigDecimal(denominator);
        BigDecimal quotient = num.divide(den, SCALE, RoundingMode.HALF_EVEN);
        return quotient.doubleValue();
    }

}