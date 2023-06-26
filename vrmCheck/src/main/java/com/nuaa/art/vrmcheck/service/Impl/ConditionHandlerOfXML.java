package com.nuaa.art.vrmcheck.service.Impl;

import com.nuaa.art.vrmcheck.model.*;
import com.nuaa.art.vrmcheck.service.ConditionHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

@Service
public class ConditionHandlerOfXML implements ConditionHandler {
    public ArrayList<ConditionConsistencyError> findConsistencyError(Condition c) {
        ArrayList<ConditionConsistencyError> consistencyErrors = new ArrayList<ConditionConsistencyError>();
        if (c.continualVariables.size() + c.discreteVariables.size() != 0) {
            for (int i = 0; i < c.coder.codeLimit; i++) {
                State s = c.coder.decode(i);
                if (!s.containsZero() && c.outputForEachState.get(i).size() > 1) {
                    ConditionConsistencyError cce = new ConditionConsistencyError();
                    ConcreteState thisConcreteState = new ConcreteState(
                            c.continualVariables.size() + c.discreteVariables.size());
                    for (int k = 0; k < s.variableNumber; k++) {
                        if (k < c.continualVariables.size()) {
                            int value = s.state[k];
                            if (value == 1) {
                                thisConcreteState.concreteState[k] = "(" + c.continualRanges.get(k).lowLimit + ","
                                        + c.continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                            } else if (value == c.continualValues.get(k).size() * 2 + 1) {
                                thisConcreteState.concreteState[k] = "("
                                        + c.continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                        + c.continualRanges.get(k).highLimit + ")";
                            } else if (value % 2 == 0) {
                                thisConcreteState.concreteState[k] = c.continualValues.get(k).get(value / 2 - 1);
                            } else {
                                thisConcreteState.concreteState[k] = "("
                                        + c.continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                        + c.continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                            }
                        } else {
                            int value = s.state[k];
                            thisConcreteState.concreteState[k] = c.discreteRanges.get(k - c.continualVariables.size())
                                    .get(value - 1);
                        }
                    }
                    cce.obeyStates = thisConcreteState;
                    HashSet<String> assignments = new HashSet<String>();
                    for (Integer line : c.outputForEachState.get(i))
                        assignments.add(c.outputRanges.get(line));
                    cce.assignment = assignments;
                    consistencyErrors.add(cce);
                }
            }
        } else {
            int trueCount = 0;
            for (int i = 0; i < c.nuclearTreeForEachRow.size(); i++) {
                if (c.nuclearTreeForEachRow.get(i).get(0).get(0).isTrue)
                    trueCount++;
            }
            if (trueCount > 1) {
                ConditionConsistencyError cce = new ConditionConsistencyError();
                cce.isTrueTooMuch = true;
                consistencyErrors.add(cce);
            }
        }
        return consistencyErrors;
    }

    // 查找条件完整性错误（一个）
    public ConditionIntegrityError fineIntegrityError(Condition c) {
        ConditionIntegrityError cie = new ConditionIntegrityError();
        if (c.continualVariables.size() + c.discreteVariables.size() != 0) {
            for (int i = 0; i < c.coder.codeLimit; i++) {
                State s = c.coder.decode(i);
                if (!s.containsZero() && c.outputForEachState.get(i).size() == 0) {
                    ConcreteState thisConcreteState = new ConcreteState(
                            c.continualVariables.size() + c.discreteVariables.size());
                    for (int k = 0; k < s.variableNumber; k++) {
                        if (k < c.continualVariables.size()) {
                            int value = s.state[k];
                            if (value == 1) {
                                thisConcreteState.concreteState[k] = "(" + c.continualRanges.get(k).lowLimit + ","
                                        + c.continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                            } else if (value == c.continualValues.get(k).size() * 2 + 1) {
                                thisConcreteState.concreteState[k] = "("
                                        + c.continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                        + c.continualRanges.get(k).highLimit + ")";
                            } else if (value % 2 == 0) {
                                thisConcreteState.concreteState[k] = c.continualValues.get(k).get(value / 2 - 1);
                            } else {
                                thisConcreteState.concreteState[k] = "("
                                        + c.continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                        + c.continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                            }
                        } else {
                            int value = s.state[k];
                            thisConcreteState.concreteState[k] = c.discreteRanges.get(k - c.continualVariables.size())
                                    .get(value - 1);
                        }
                    }
                    cie.lostStates.add(thisConcreteState);
                }
            }
        } else {
            int trueCount = 0;
            for (int i = 0; i < c.nuclearTreeForEachRow.size(); i++) {
                if (c.nuclearTreeForEachRow.get(i).get(0).get(0).isTrue)
                    trueCount++;
            }
            if (trueCount == 0) {
                cie.isTrueNotExist = true;
            }
        }
        return cie;
    }
}
