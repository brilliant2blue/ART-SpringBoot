package com.nuaa.art.vrmcheck.service.Impl;

import com.nuaa.art.vrmcheck.model.ConcreteState;
import com.nuaa.art.vrmcheck.model.Event;
import com.nuaa.art.vrmcheck.model.EventConsistencyError;
import com.nuaa.art.vrmcheck.model.State;
import com.nuaa.art.vrmcheck.service.EventHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
public class EventHandlerOfXML implements EventHandler {
    // 查找事件一致性错误（多个）
    @Override
    public ArrayList<EventConsistencyError> findConsistencyError(Event e) {
            ArrayList<EventConsistencyError> consistencyErrors = new ArrayList<EventConsistencyError>();
            if (e.continualVariables.size() + e.discreteVariables.size() != 0) {
                for (int i = 0; i < e.states.size(); i++) {
                    for (int j = i + 1; j < e.states.size(); j++) {
                        ArrayList<Long>[] collectionOne = e.states.get(i);
                        ArrayList<Long>[] collectionTwo = e.states.get(j);
                        String assignmentOne = e.behaviorForEachRow.get(i);
                        String assignmentTwo = e.behaviorForEachRow.get(j);
                        EventConsistencyError ece = new EventConsistencyError();
                        ece.assignment[0] = assignmentOne;
                        ece.assignment[1] = assignmentTwo;
                        if (!assignmentOne.equals(assignmentTwo)) {
                            for (int l = 0; l < collectionOne[0].size(); l++) {
                                if (collectionTwo[0].contains(collectionOne[0].get(l))) {
                                    State state = e.coder.decode(collectionOne[0].get(l));
                                    ConcreteState thisConcreteStateOne = new ConcreteState(
                                            e.continualVariables.size() + e.discreteVariables.size());
                                    for (int k = 0; k < e.continualVariables.size() + e.discreteVariables.size(); k++) {
                                        if (k < e.continualVariables.size()) {
                                            int valueOne = state.state[k];
                                            if (valueOne == 1) {
                                                thisConcreteStateOne.concreteState[k] = "("
                                                        + e.continualRanges.get(k).lowLimit + ","
                                                        + e.continualValues.get(k).get((valueOne + 1) / 2 - 1) + ")";
                                            } else if (valueOne == e.continualValues.get(k).size() * 2 + 1) {
                                                thisConcreteStateOne.concreteState[k] = "("
                                                        + e.continualValues.get(k).get((valueOne - 1) / 2 - 1) + ","
                                                        + e.continualRanges.get(k).highLimit + ")";
                                            } else if (valueOne % 2 == 0) {
                                                thisConcreteStateOne.concreteState[k] = e.continualValues.get(k)
                                                        .get(valueOne / 2 - 1);
                                            } else {
                                                thisConcreteStateOne.concreteState[k] = "("
                                                        + e.continualValues.get(k).get((valueOne - 1) / 2 - 1) + ","
                                                        + e.continualValues.get(k).get((valueOne + 1) / 2 - 1) + ")";
                                            }
                                        } else {
                                            int valueOne = state.state[k];
                                            thisConcreteStateOne.concreteState[k] = e.discreteRanges
                                                    .get(k - e.continualVariables.size()).get(valueOne - 1);
                                        }
                                    }
                                    ece.obeyStates[0].add(thisConcreteStateOne);
                                }
                            }
                            for (int l = 0; l < collectionOne[1].size(); l++) {
                                if (collectionTwo[1].contains(collectionOne[1].get(l))) {
                                    State state = e.coder.decode(collectionOne[1].get(l));
                                    ConcreteState thisConcreteStateTwo = new ConcreteState(
                                            e.continualVariables.size() + e.discreteVariables.size());
                                    for (int k = 0; k < e.continualVariables.size() + e.discreteVariables.size(); k++) {
                                        if (k < e.continualVariables.size()) {
                                            int valueTwo = state.state[k];
                                            if (valueTwo == 1) {
                                                thisConcreteStateTwo.concreteState[k] = "("
                                                        + e.continualRanges.get(k).lowLimit + ","
                                                        + e.continualValues.get(k).get((valueTwo + 1) / 2 - 1) + ")";
                                            } else if (valueTwo == e.continualValues.get(k).size() * 2 + 1) {
                                                thisConcreteStateTwo.concreteState[k] = "("
                                                        + e.continualValues.get(k).get((valueTwo - 1) / 2 - 1) + ","
                                                        + e.continualRanges.get(k).highLimit + ")";
                                            } else if (valueTwo % 2 == 0) {
                                                thisConcreteStateTwo.concreteState[k] = e.continualValues.get(k)
                                                        .get(valueTwo / 2 - 1);
                                            } else {
                                                thisConcreteStateTwo.concreteState[k] = "("
                                                        + e.continualValues.get(k).get((valueTwo - 1) / 2 - 1) + ","
                                                        + e.continualValues.get(k).get((valueTwo + 1) / 2 - 1) + ")";
                                            }
                                        } else {
                                            int valueTwo = state.state[k];
                                            thisConcreteStateTwo.concreteState[k] = e.discreteRanges
                                                    .get(k - e.continualVariables.size()).get(valueTwo - 1);
                                        }
                                    }
                                    ece.obeyStates[1].add(thisConcreteStateTwo);
                                }
                            }
                        }
                        if (!ece.obeyStates[0].isEmpty() && !ece.obeyStates[1].isEmpty()) {
                            consistencyErrors.add(ece);
                        }
                    }
                }
            }
            return consistencyErrors;

    }
}
