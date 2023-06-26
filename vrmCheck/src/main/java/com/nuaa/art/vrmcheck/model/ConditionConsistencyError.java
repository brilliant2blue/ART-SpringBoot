package com.nuaa.art.vrmcheck.model;

import java.util.HashSet;

/**
 * 条件一致性错误 存储类
 *
 * @author konsin
 * @date 2023/06/12
 */
public class ConditionConsistencyError {
        public HashSet<String> assignment = new HashSet<String>();
        public boolean isTrueTooMuch = false;
        public ConcreteState obeyStates;

}
