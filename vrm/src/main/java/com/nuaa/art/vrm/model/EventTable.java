package com.nuaa.art.vrm.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class EventTable {
    public Integer andNum = 0;
    public Integer orNum = 0;
    public ArrayList<EventItem> events = new ArrayList<>();
    public ArrayList<ArrayList<String>> orList = new ArrayList<>();

    public void addAndNum(){
        andNum++;
    }
}
