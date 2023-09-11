package com.nuaa.art.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuaa.art.common.utils.LogUtils;
import lombok.Data;
import org.springframework.web.socket.TextMessage;
@Data
@JsonIgnoreProperties(ignoreUnknown = true) //忽略该目标对象不存在的属性
public class SocketMessage<T> {
    String messageType;
    String dataType;
    T data;

    public SocketMessage() {

    }

    public SocketMessage(String messageType, String dataType, T data){
        this.messageType = messageType;
        this.dataType = dataType;
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
