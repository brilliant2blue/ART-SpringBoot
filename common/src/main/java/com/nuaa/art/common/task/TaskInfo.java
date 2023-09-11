package com.nuaa.art.common.task;

import lombok.Data;

import java.util.Date;

/**
 * 任务信息
 */
@Data
public class TaskInfo {
    private String taskId;
    private String status;
    private Date startTime;
    private Date endTime;
    private String totalTime;


    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime() {
        this.totalTime = (this.endTime.getTime() - this.startTime.getTime()) + "ms";
    }
}