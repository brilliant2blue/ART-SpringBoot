package com.nuaa.art.vrmcheck.model;

import java.util.ArrayList;

/**
 * 编码器 用于场景编码和解码的类
 *
 * @author konsin
 * @date 2023/06/12
 */
public class Coder {
    public int variableNumber;

    public int[] variableRanges;

    public long[] weights;

    public long codeLimit;

    public Coder(int variableNumber, int[] variableRanges) {
        this.variableNumber = variableNumber;
        this.variableRanges = new int[variableNumber];
        weights = new long[variableNumber];
        long weight = 1;
        for (int i = variableNumber - 1; i >= 0; i--) {
            this.variableRanges[i] = variableRanges[i] + 1;
            weights[i] = weight;
            weight *= this.variableRanges[i];
        }
        codeLimit = weight;
    }

    /**
     * 编码
     *
     * @param s 场景
     * @return long
     */
    public long encode(Scenario s) {
        long code = 0;
        for (int i = 0; i < variableNumber; i++) {
            code += (weights[i] * (s.scenario[i]));
        }
        return code;
    }

    /**
     * 解码
     *
     * @param code 场景码
     * @return {@link Scenario}
     */
    public Scenario decode(long code) {
        Scenario s = new Scenario(variableNumber);
        for (int i = 0; i < variableNumber; i++) {
            s.scenario[i] = (int) (code / weights[i]);
            code = code % weights[i];
        }
        return s;
    }

    /**
     * 场景抽象 过程， 采用了附加的方式而不是重新生成一个完整的编码器
     *
     * @param scenarioCollection 状态集合
     * @param code            场景代码
     * @param inconsidered    未考虑的变量
     * @return {@link ArrayList}<{@link Long}>
     */
    public ArrayList<Long> appendInconsidered(ArrayList<Long> scenarioCollection, long code,
                                              ArrayList<Integer> inconsidered) {
        int[] inconsideredRanges = new int[inconsidered.size()];
        for (int i = 0; i < inconsidered.size(); i++) {
            inconsideredRanges[i] = variableRanges[inconsidered.get(i).intValue()] - 2;
        }
        Coder c = new Coder(inconsidered.size(), inconsideredRanges);
        for (long l = 0; l < c.codeLimit; l++) {
            Scenario s = c.decode(l);
            long newCode = code;
            for (int i = 0; i < inconsidered.size(); i++) {
                newCode += weights[inconsidered.get(i)] * (s.scenario[i] + 1);
            }
            //System.out.println("newState:" + newCode);
            if (!scenarioCollection.contains(newCode))
                scenarioCollection.add(newCode);
        }
        return scenarioCollection;
    }
}
