package com.nuaa.art.vrmcheck.model;

import java.util.Arrays;

/**
 * 表示场景的类，使用int[]表示法（取值的代号）
 *
 * @author konsin
 * @date 2023/06/12
 */
// todo bug是如果变量的取值就是0，那么会认为这个值是空的，从而不会生成对应场景。
public class Scenario {
    public int[] scenario;
    public int variableNumber;

    public boolean containsZero() {
        for (int i = 0; i < variableNumber; i++)
            if (scenario[i] == 0)
                return true;
        return false;
    }

    public Scenario(int variableNumber) {
        this.variableNumber = variableNumber;
        scenario = new int[variableNumber];
    }

    public Scenario(Scenario otherScenario) {
        this.variableNumber = otherScenario.variableNumber;
        scenario = new int[variableNumber];
        for (int i = 0; i < this.variableNumber; i++)
            this.scenario[i] = otherScenario.scenario[i];
    }

    public Scenario(int[] scenario) {
        this.variableNumber = scenario.length;
        this.scenario = Arrays.copyOf(scenario, variableNumber);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scenario) {
            Scenario otherScenario = (Scenario) obj;
            if (this.variableNumber != otherScenario.variableNumber)
                return false;
            else {
                boolean isEquals = true;
                for (int i = 0; i < this.variableNumber; i++) {
                    if (this.scenario[i] != otherScenario.scenario[i])
                        isEquals = false;
                }
                return isEquals;
            }
        } else
            return false;
    }

    @Override
    public String toString() {
        return "State [scenario=" + Arrays.toString(scenario) + "]";
    }

    // 场景的相似（除目标场景中的0外，数组每一位都与本场景相同）
    public boolean almostEquals(Object obj) {
        if (obj instanceof Scenario) {
            Scenario otherScenario = (Scenario) obj;
            if (this.variableNumber != otherScenario.variableNumber)
                return false;
            else {
                boolean isEquals = true;
                for (int i = 0; i < this.variableNumber; i++) {
                    if (otherScenario.scenario[i] != 0 && this.scenario[i] != otherScenario.scenario[i])
                        isEquals = false;
                }
                return isEquals;
            }

        } else {
            return false;
        }
    }
}
