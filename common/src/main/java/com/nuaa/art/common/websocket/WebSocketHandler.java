package com.nuaa.art.common.websocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nuaa.art.common.model.SocketMessage;
import com.nuaa.art.common.utils.LogUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * WebSocket消息处理程序
 *
 * @author konsin
 * @date 2023/09/05
 */
@Component
public class WebSocketHandler extends AbstractWebSocketHandler{
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //socket连接成功后触发
        String user  = (String)session.getAttributes().get("user");
        if(!user.isBlank()) {
            LogUtils.info(String.format("用户%s建立websocket连接", user));
            WebSocketSessionManager.add(user, session);
            System.out.println(WebSocketSessionManager.SESSION_POOL.get(user).getId());
        } else {
            session.sendMessage(new TextMessage("用户未登录， 连接关闭！"));
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 客户端发送普通文件信息时触发
        LogUtils.info("发送文本消息");
        // 获得客户端传来的消息
        String payload = message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(payload);
        SocketMessage msg = mapper.readValue(payload, SocketMessage.class);
        if(msg.getMessageType().equals("heart")) msg.setData("心跳信息");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
        LogUtils.info("服务端接收到消息 " + payload);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        //客户端发送二进信息是触发
        LogUtils.info("发送二进制消息");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        //异常时触发
        LogUtils.error("异常处理");
        WebSocketSessionManager.removeAndClose(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // socket连接关闭后触发
        LogUtils.info("关闭websocket连接");
        WebSocketSessionManager.removeAndClose(session.getId());
    }

}
