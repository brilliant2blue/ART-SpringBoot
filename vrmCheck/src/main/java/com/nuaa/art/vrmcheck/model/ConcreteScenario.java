package com.nuaa.art.vrmcheck.model;

/**
 * 表示场景的类，使用String[]表示法（具体的取值）
 *
 * @author konsin
 * @date 2023/06/12
 */
public class ConcreteScenario {
    public String[] concreteScenario;
    public int variableNumber;

    public ConcreteScenario(int variableNumber) {
        this.variableNumber = variableNumber;
        concreteScenario = new String[variableNumber];
    }

    public ConcreteScenario(ConcreteScenario otherState) {
        this.variableNumber = otherState.variableNumber;
        concreteScenario = new String[variableNumber];
        for (int i = 0; i < this.variableNumber; i++)
            this.concreteScenario[i] = new String(otherState.concreteScenario[i]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConcreteScenario) {
            ConcreteScenario otherState = (ConcreteScenario) obj;
            if (this.variableNumber != otherState.variableNumber)
                return false;
            else {
                boolean isEquals = true;
                for (int i = 0; i < this.variableNumber; i++) {
                    if (!this.concreteScenario[i].equals(otherState.concreteScenario[i]))
                        isEquals = false;
                }
                return isEquals;
            }
        } else
            return false;
    }
}
