package com.nuaa.art.vrm.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class EventTable {
    Integer andNum;
    ArrayList<EventItem> events = new ArrayList<>();
}
