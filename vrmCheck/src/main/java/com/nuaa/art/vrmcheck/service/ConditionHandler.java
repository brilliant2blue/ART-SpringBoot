package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.Condition;
import com.nuaa.art.vrmcheck.model.ConditionConsistencyError;
import com.nuaa.art.vrmcheck.model.ConditionIntegrityError;

import java.util.ArrayList;

/**
 * 条件错误查找
 *
 * @author konsin
 * @date 2023/07/02
 */
public interface ConditionHandler {
    public ArrayList<ConditionConsistencyError> findConsistencyError(Condition c);

    public ConditionIntegrityError fineIntegrityError(Condition c);
}
