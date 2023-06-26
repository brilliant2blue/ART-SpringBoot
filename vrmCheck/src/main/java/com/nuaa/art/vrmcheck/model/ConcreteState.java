package com.nuaa.art.vrmcheck.model;

/**
 * 表示场景的类，使用String[]表示法（具体的取值）
 *
 * @author konsin
 * @date 2023/06/12
 */
public class ConcreteState {
    public String[] concreteState;
    public int variableNumber;

    public ConcreteState(int variableNumber) {
        this.variableNumber = variableNumber;
        concreteState = new String[variableNumber];
    }

    public ConcreteState(ConcreteState otherState) {
        this.variableNumber = otherState.variableNumber;
        concreteState = new String[variableNumber];
        for (int i = 0; i < this.variableNumber; i++)
            this.concreteState[i] = new String(otherState.concreteState[i]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConcreteState) {
            ConcreteState otherState = (ConcreteState) obj;
            if (this.variableNumber != otherState.variableNumber)
                return false;
            else {
                boolean isEquals = true;
                for (int i = 0; i < this.variableNumber; i++) {
                    if (!this.concreteState[i].equals(otherState.concreteState[i]))
                        isEquals = false;
                }
                return isEquals;
            }
        } else
            return false;
    }
}
