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

    public long encode(State s) {
        long code = 0;
        for (int i = 0; i < variableNumber; i++) {
            code += (weights[i] * (s.state[i]));
        }
        return code;
    }

    public State decode(long code) {
        State s = new State(variableNumber);
        for (int i = 0; i < variableNumber; i++) {
            s.state[i] = (int) (code / weights[i]);
            code = code % weights[i];
        }
        return s;
    }

    public ArrayList<Long> appendInconsidered(ArrayList<Long> stateCollection, long code,
                                              ArrayList<Integer> inconsidered) {
        int[] inconsideredRanges = new int[inconsidered.size()];
        for (int i = 0; i < inconsidered.size(); i++) {
            inconsideredRanges[i] = variableRanges[inconsidered.get(i).intValue()] - 2;
        }
        Coder c = new Coder(inconsidered.size(), inconsideredRanges);
        for (long l = 0; l < c.codeLimit; l++) {
            State s = c.decode(l);
            long newCode = code;
            for (int i = 0; i < inconsidered.size(); i++) {
                newCode += weights[inconsidered.get(i)] * (s.state[i] + 1);
            }
            System.out.println("newState:" + newCode);
            if (!stateCollection.contains(newCode))
                stateCollection.add(newCode);
        }
        return stateCollection;
    }
}
