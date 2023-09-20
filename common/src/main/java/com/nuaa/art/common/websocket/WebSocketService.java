package com.nuaa.art.common.websocket;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.ServletUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * WebSocket服务
 *
 * @author konsin
 * @date 2023/09/05
 */
@Service
public class WebSocketService {
    /**
     * 发送消息
     *
     * @param session
     * @param text
     * @return
     * @throws IOException
     */
    public void sendMsg(WebSocketSession session, String text) throws IOException {
        session.sendMessage(new TextMessage(text));
    }

    public void sendMsg(WebSocketSession session, TextMessage msg) throws IOException {
        session.sendMessage(msg);
    }

    public <T> void sendMsg(T msg) {
        try {
            String user = ServletUtils.getRequest().getParameter("user");
            LogUtils.info("对连接发送消息：" + user);
            System.out.println(ServletUtils.getRequest().getRequestURL());
            //LogUtils.info(user);
            WebSocketSession session = WebSocketSessionManager.get(user);
            if (msg instanceof String) {
                sendMsg(session, (String) msg);
            }
            if (msg instanceof TextMessage) {
                sendMsg(session, (TextMessage) msg);
            }
        } catch (Exception e){
            LogUtils.error("websocket向session发送消息失败。");
        }
    }

    /**
     * 广播消息
     *
     * @param text
     * @return
     * @throws IOException
     */
    public void broadcastMsg(String text) throws IOException {
        for (WebSocketSession session : WebSocketSessionManager.SESSION_POOL.values()) {
            session.sendMessage(new TextMessage(text));
        }
    }
}
