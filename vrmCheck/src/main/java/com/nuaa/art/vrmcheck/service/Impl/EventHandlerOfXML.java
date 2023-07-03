package com.nuaa.art.vrmcheck.service.impl;

import com.nuaa.art.vrmcheck.model.ConcreteScenario;
import com.nuaa.art.vrmcheck.model.Event;
import com.nuaa.art.vrmcheck.model.EventConsistencyError;
import com.nuaa.art.vrmcheck.model.Scenario;
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
                for (int i = 0; i < e.scenarios.size(); i++) {
                    for (int j = i + 1; j < e.scenarios.size(); j++) {
                        ArrayList<Long>[] collectionOne = e.scenarios.get(i);
                        ArrayList<Long>[] collectionTwo = e.scenarios.get(j);
                        String assignmentOne = e.behaviorForEachRow.get(i);
                        String assignmentTwo = e.behaviorForEachRow.get(j);
                        EventConsistencyError ece = new EventConsistencyError();
                        ece.assignment[0] = assignmentOne;
                        ece.assignment[1] = assignmentTwo;
                        if (!assignmentOne.equals(assignmentTwo)) {
                            for (int l = 0; l < collectionOne[0].size(); l++) {
                                if (collectionTwo[0].contains(collectionOne[0].get(l))) {
                                    Scenario scenario = e.coder.decode(collectionOne[0].get(l));
                                    ConcreteScenario thisConcreteScenarioOne = new ConcreteScenario(
                                            e.continualVariables.size() + e.discreteVariables.size());
                                    for (int k = 0; k < e.continualVariables.size() + e.discreteVariables.size(); k++) {
                                        if (k < e.continualVariables.size()) {
                                            int valueOne = scenario.scenario[k];
                                            if (valueOne == 1) {
                                                thisConcreteScenarioOne.concreteScenario[k] = "("
                                                        + e.continualRanges.get(k).lowLimit + ","
                                                        + e.continualValues.get(k).get((valueOne + 1) / 2 - 1) + ")";
                                            } else if (valueOne == e.continualValues.get(k).size() * 2 + 1) {
                                                thisConcreteScenarioOne.concreteScenario[k] = "("
                                                        + e.continualValues.get(k).get((valueOne - 1) / 2 - 1) + ","
                                                        + e.continualRanges.get(k).highLimit + ")";
                                            } else if (valueOne % 2 == 0) {
                                                thisConcreteScenarioOne.concreteScenario[k] = e.continualValues.get(k)
                                                        .get(valueOne / 2 - 1);
                                            } else {
                                                thisConcreteScenarioOne.concreteScenario[k] = "("
                                                        + e.continualValues.get(k).get((valueOne - 1) / 2 - 1) + ","
                                                        + e.continualValues.get(k).get((valueOne + 1) / 2 - 1) + ")";
                                            }
                                        } else {
                                            int valueOne = scenario.scenario[k];
                                            thisConcreteScenarioOne.concreteScenario[k] = e.discreteRanges
                                                    .get(k - e.continualVariables.size()).get(valueOne - 1);
                                        }
                                    }
                                    ece.obeyScenarios[0].add(thisConcreteScenarioOne);
                                }
                            }
                            for (int l = 0; l < collectionOne[1].size(); l++) {
                                if (collectionTwo[1].contains(collectionOne[1].get(l))) {
                                    Scenario scenario = e.coder.decode(collectionOne[1].get(l));
                                    ConcreteScenario thisConcreteScenarioTwo = new ConcreteScenario(
                                            e.continualVariables.size() + e.discreteVariables.size());
                                    for (int k = 0; k < e.continualVariables.size() + e.discreteVariables.size(); k++) {
                                        if (k < e.continualVariables.size()) {
                                            int valueTwo = scenario.scenario[k];
                                            if (valueTwo == 1) {
                                                thisConcreteScenarioTwo.concreteScenario[k] = "("
                                                        + e.continualRanges.get(k).lowLimit + ","
                                                        + e.continualValues.get(k).get((valueTwo + 1) / 2 - 1) + ")";
                                            } else if (valueTwo == e.continualValues.get(k).size() * 2 + 1) {
                                                thisConcreteScenarioTwo.concreteScenario[k] = "("
                                                        + e.continualValues.get(k).get((valueTwo - 1) / 2 - 1) + ","
                                                        + e.continualRanges.get(k).highLimit + ")";
                                            } else if (valueTwo % 2 == 0) {
                                                thisConcreteScenarioTwo.concreteScenario[k] = e.continualValues.get(k)
                                                        .get(valueTwo / 2 - 1);
                                            } else {
                                                thisConcreteScenarioTwo.concreteScenario[k] = "("
                                                        + e.continualValues.get(k).get((valueTwo - 1) / 2 - 1) + ","
                                                        + e.continualValues.get(k).get((valueTwo + 1) / 2 - 1) + ")";
                                            }
                                        } else {
                                            int valueTwo = scenario.scenario[k];
                                            thisConcreteScenarioTwo.concreteScenario[k] = e.discreteRanges
                                                    .get(k - e.continualVariables.size()).get(valueTwo - 1);
                                        }
                                    }
                                    ece.obeyScenarios[1].add(thisConcreteScenarioTwo);
                                }
                            }
                        }
                        if (!ece.obeyScenarios[0].isEmpty() && !ece.obeyScenarios[1].isEmpty()) {
                            consistencyErrors.add(ece);
                        }
                    }
                }
            }
            return consistencyErrors;

    }
}
