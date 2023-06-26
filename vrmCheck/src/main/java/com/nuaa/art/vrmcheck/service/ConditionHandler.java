package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.Condition;
import com.nuaa.art.vrmcheck.model.ConditionConsistencyError;
import com.nuaa.art.vrmcheck.model.ConditionIntegrityError;

import java.util.ArrayList;

public interface ConditionHandler {
    public ArrayList<ConditionConsistencyError> findConsistencyError(Condition c);

    public ConditionIntegrityError fineIntegrityError(Condition c);
}
