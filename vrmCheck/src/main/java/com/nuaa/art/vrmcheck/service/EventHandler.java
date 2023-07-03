package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.Event;
import com.nuaa.art.vrmcheck.model.EventConsistencyError;

import java.util.ArrayList;

/**
 * 事件错误查找
 *
 * @author konsin
 * @date 2023/07/02
 */
public interface EventHandler {
    public ArrayList<EventConsistencyError> findConsistencyError(Event e);
}
