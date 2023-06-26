package com.nuaa.art.vrm.model;

import lombok.Data;

@Data
public class ReqContent {
    private String assignment;
    private String state;
    private Integer relateReqId;
    private String content;
}
