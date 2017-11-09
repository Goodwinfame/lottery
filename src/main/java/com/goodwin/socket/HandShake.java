package com.goodwin.socket;

/**
 * Created by Superwen on 2017/1/5.
 */


import java.util.Map;

import javax.servlet.http.HttpSession;

import com.goodwin.model.Member;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


/**
 * Socket建立连接（握手）和断开
 *
 * @author Goofy
 * @Date 2015年6月11日 下午2:23:09
 */
public class HandShake implements HandshakeInterceptor {

    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("Websocket:用户[ID:" + ((ServletServerHttpRequest) request).getServletRequest().getSession(false).getAttribute("id") + "]已经建立连接");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            // 标记用户
            Member user = (Member) session.getAttribute("userinfo");
            if(user!=null){
                attributes.put("userinfo", user);
            }else{
                Member displayUser = new Member();
                displayUser.setId(Integer.parseInt((servletRequest.getServletRequest().getParameterValues("id"))[0]));
                attributes.put("userinfo", displayUser);
            }
        }
        return true;
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }


}