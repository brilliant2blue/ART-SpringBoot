package com.nuaa.art.vrmcheck.model;

import java.util.ArrayList;

/**
     * 事件一致性错误 存储类
     *
     * @author konsin
     * @date 2023/06/12
     */
    public class EventConsistencyError {
        public int id;
        public String[] assignment = new String[2];
        public ArrayList<ConcreteState>[] obeyStates = new ArrayList[2];

        public EventConsistencyError() {
            obeyStates[0] = new ArrayList<ConcreteState>();
            obeyStates[1] = new ArrayList<ConcreteState>();
        }
    }