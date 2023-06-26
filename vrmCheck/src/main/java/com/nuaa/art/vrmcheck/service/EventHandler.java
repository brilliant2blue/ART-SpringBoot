package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.Event;
import com.nuaa.art.vrmcheck.model.EventConsistencyError;

import java.util.ArrayList;

public interface EventHandler {
    public ArrayList<EventConsistencyError> findConsistencyError(Event e);
}
