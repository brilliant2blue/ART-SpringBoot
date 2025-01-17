package com.nuaa.art.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuaa.art.common.EventLevelEnum;
import com.nuaa.art.common.utils.LogUtils;
import lombok.Data;
import org.springframework.web.socket.TextMessage;
@Data
@JsonIgnoreProperties(ignoreUnknown = true) //忽略该目标对象不存在的属性
public class SocketMessage<T> {
    String messageType;
    String dataType;
    Integer level;
    T data;

    public SocketMessage() {

    }

    public SocketMessage(String messageType, String dataType, T data){
        this.messageType = messageType;
        this.dataType = dataType;
        this.level = EventLevelEnum.INFO.ordinal();
        this.data = data;
    }

    public SocketMessage(String messageType, String dataType, EventLevelEnum level, T data){
        this.messageType = messageType;
        this.dataType = dataType;
        this.level = level.ordinal();
        this.data = data;
    }

    public SocketMessage(String json) throws JsonProcessingException {
        SocketMessage obj = new ObjectMapper().readValue(json, SocketMessage.class);
        this.messageType = obj.messageType;
        this.dataType = obj.messageType;
        this.data = (T) obj.data;
    }

    public static TextMessage asText(String data){
        SocketMessage<String> msg = new SocketMessage<>("msg", "string", data);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return new TextMessage(mapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            LogUtils.error(e.getMessage());
        }
        return null;
    }
    public static TextMessage asText(String type, String data){
        SocketMessage<String> msg = new SocketMessage<>("msg", type, data);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return new TextMessage(mapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            LogUtils.error(e.getMessage());
        }
        return null;
    }

    public static TextMessage asText(String type, EventLevelEnum level , String data){
        SocketMessage<String> msg = new SocketMessage<>("msg", type, level, data);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return new TextMessage(mapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            LogUtils.error(e.getMessage());
        }
        return null;
    }

    public static TextMessage asText(String messageType, String dataType, EventLevelEnum level , String data){
        SocketMessage<String> msg = new SocketMessage<>(messageType, dataType, level, data);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return new TextMessage(mapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            LogUtils.error(e.getMessage());
        }
        return null;
    }

    public static <T> TextMessage asObject(String type, T data){
        SocketMessage<T> msg = new SocketMessage<>("obj", type, data);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return new TextMessage(mapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            LogUtils.error(e.getMessage());
        }
        return null;
    }

}
