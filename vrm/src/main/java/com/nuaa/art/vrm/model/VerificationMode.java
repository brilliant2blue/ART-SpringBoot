package com.nuaa.art.vrm.model;

/**
 * 验证模式
 *
 * @author konsin
 * @date 2023/06/10
 */
public class VerificationMode {
    private String source;
    private String event;
    private String destinate;
    private String eventString;

    public String getEventString() {
        return eventString;
    }
    public void setEventString(String eventString) {
        this.eventString = eventString;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
    public String getDestinate() {
        return destinate;
    }
    public void setDestinate(String destinate) {
        this.destinate = destinate;
    }
    public VerificationMode() {

    }
    public VerificationMode(String source, String event, String destinate) {
        this.source = source;
        this.event = event;
        this.destinate = destinate;
    }
}
