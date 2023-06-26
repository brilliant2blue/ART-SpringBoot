package com.nuaa.art.vrmcheck.model;

import java.util.Arrays;

/**
 * 表示场景的类，使用int[]表示法（取值的代号）
 *
 * @author konsin
 * @date 2023/06/12
 */
public class State {
    public int[] state;
    public int variableNumber;

    public boolean containsZero() {
        for (int i = 0; i < variableNumber; i++)
            if (state[i] == 0)
                return true;
        return false;
    }

    public State(int variableNumber) {
        this.variableNumber = variableNumber;
        state = new int[variableNumber];
    }

    public State(State otherState) {
        this.variableNumber = otherState.variableNumber;
        state = new int[variableNumber];
        for (int i = 0; i < this.variableNumber; i++)
            this.state[i] = otherState.state[i];
    }

    public State(int[] state) {
        this.variableNumber = state.length;
        this.state = Arrays.copyOf(state, variableNumber);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof State) {
            State otherState = (State) obj;
            if (this.variableNumber != otherState.variableNumber)
                return false;
            else {
                boolean isEquals = true;
                for (int i = 0; i < this.variableNumber; i++) {
                    if (this.state[i] != otherState.state[i])
                        isEquals = false;
                }
                return isEquals;
            }
        } else
            return false;
    }

    @Override
    public String toString() {
        return "State [state=" + Arrays.toString(state) + "]";
    }

    // 场景的相似（除目标场景中的0外，数组每一位都与本场景相同）
    public boolean almostEquals(Object obj) {
        if (obj instanceof State) {
            State otherState = (State) obj;
            if (this.variableNumber != otherState.variableNumber)
                return false;
            else {
                boolean isEquals = true;
                for (int i = 0; i < this.variableNumber; i++) {
                    if (otherState.state[i] != 0 && this.state[i] != otherState.state[i])
                        isEquals = false;
                }
                return isEquals;
            }

        } else {
            return false;
        }
    }
}
