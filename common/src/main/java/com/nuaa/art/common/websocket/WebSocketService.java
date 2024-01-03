package com.nuaa.art.common.websocket;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.common.utils.ServletUtils;
import com.nuaa.art.common.utils.ThreadLocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
            String user, url;
            var par = ThreadLocalUtils.getRequest();
            if(par != null) {
                user = (String) par.get("user");
                //url = String.valueOf(par.get("url"));
                System.out.println(user);
            } else {
                var request = ServletUtils.getRequest();
                user = request.getHeader("user");
                //url = String.valueOf(request.getRequestURL());
            }
            LogUtils.info("对连接发送消息：" + user);
            //System.out.println(url);
            //LogUtils.info(user);
            WebSocketSession session = WebSocketSessionManager.get(user);
            if (msg instanceof String) {
                sendMsg(session, (String) msg);
            }
            if (msg instanceof TextMessage) {
                sendMsg(session, (TextMessage) msg);
            }
        } catch (Exception e){
            //e.printStackTrace();
            LogUtils.error("websocket向session发送消息失败。\n"+e.getMessage());
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
