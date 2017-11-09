package com.goodwin.socket;

/**
 * Created by Superwen on 2016/11/1.
 */

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.goodwin.model.Member;
import com.goodwin.model.Prize;
import com.goodwin.model.ResponesMsg;
import com.goodwin.singleton.MemberSingleton;
import com.goodwin.singleton.OnlineUser;
import com.goodwin.singleton.PrizeSingleton;
import com.goodwin.utils.Lottery;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import com.goodwin.model.Message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Socket处理器
 *
 * @author Goofy
 * @Date 2015年6月11日 下午1:19:50
 */
@Component
public class MyWebSocketHandler implements WebSocketHandler {
    public static final Map<Integer, WebSocketSession> userSocketSessionMap;
    public static final Map<Integer, WebSocketSession> displaySessionMap;
    public static MemberSingleton memberSingleton = MemberSingleton.getInstance();
    public static PrizeSingleton prizeSingleton = PrizeSingleton.getInstance();

    static {
        userSocketSessionMap = new HashMap<>();
        displaySessionMap = new HashMap<>();

    }

    /**
     * 建立连接后
     */
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {
        Member user = (Member) session.getAttributes().get("userinfo");
        if (displaySessionMap.get(Integer.parseInt(session.getId())) == null && user.getId() >= 1000){
            displaySessionMap.put(Integer.parseInt(session.getId()), session);

        } else if (userSocketSessionMap.get(user.getId()) == null){
            userSocketSessionMap.put(user.getId(), session);
            user.setOnline(true);

        } else {
            WebSocketSession oldSession = userSocketSessionMap.get(user.getId());
            if (oldSession.isOpen()) {
                oldSession.close();
            }
            userSocketSessionMap.put(user.getId(),session);

        }
        try {
            userStatus(user.getId());

        } catch (Exception e){
            e.printStackTrace();

        }
    }

    /**
     * 消息处理，在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理
     */
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if(message.getPayloadLength()==0)return;
        Message msg=new Gson().fromJson(message.getPayload().toString(),Message.class);
        if(msg.getType().equals("rolling")){
            rollingFeedback(msg.getFrom());
        } else if (msg.getType().equals("draw") && ((Member)session.getAttributes().get("userinfo")).getId() == 8888){
            drawPrize(msg.getText());
        } else {
            msg.setDate(new Date());
            sendMessageToUser(msg.getTo(), new TextMessage(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)), msg.getType());
        }

    }

    /**
     * 消息传输错误处理
     */
    public void handleTransportError(WebSocketSession session,
                                     Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        removeSession(session);

    }

    /**
     * 关闭连接后
     */
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus closeStatus) throws Exception {
        System.out.println("Websocket:" + session.getId() + "已经关闭");
        removeSession(session);
    }

    private void removeSession(WebSocketSession session) throws IOException {
        Iterator<Entry<Integer, WebSocketSession>> it = userSocketSessionMap
                .entrySet().iterator();
        // 移除在线用户
        Map attributes = session.getAttributes();
        Member user = (Member) attributes.get("userinfo");

        if (user.getId() >= 1000){
            displaySessionMap.remove(session.getId());


        } else {
            userSocketSessionMap.remove(user.getId());
            memberSingleton.memberOffline(user.getId());
            try {
                userStatus(user.getId());

            } catch (Exception e){
                e.printStackTrace();

            }
            System.out.println("Socket会话已经移除:用户ID" + user.getId());

        }

    }
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给所有在线用户发送消息
     *
     * @param message
     * @throws IOException
     */
    public void broadcast(final TextMessage message) throws IOException {
        Iterator<Entry<Integer, WebSocketSession>> it = userSocketSessionMap
                .entrySet().iterator();

        // 多线程群发
        while (it.hasNext()) {

            final Entry<Integer, WebSocketSession> entry = it.next();

            if (entry.getValue().isOpen()) {
                // entry.getValue().sendMessage(message);
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            if (entry.getValue().isOpen()) {
                                entry.getValue().sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }

        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param
     * @param message
     * @throws IOException
     */
    public void sendMessageToUser(Integer id, TextMessage message, String type)
            throws IOException {
        WebSocketSession session = userSocketSessionMap.get(id);
        if (session != null && session.isOpen()) {
            session.sendMessage(message);
        }
    }
    /**
     * 用户上线/下线
     * */
    public void userStatus(Integer id)
            throws IOException {
        Member user = memberSingleton.getMember(id);
        TextMessage message = new TextMessage(new GsonBuilder().create().toJson(user));
        try {
            sendToDisplay(message);

        } catch (Exception e){
            e.printStackTrace();

        }
    }
    /**
     * 摇一摇消息反馈
     * */
    public void rollingFeedback(Integer id)
            throws IOException {
        Member user = memberSingleton.getMember(id);
        user.setActivity(user.getActivity() + 1);
        TextMessage message = new TextMessage(new GsonBuilder().create().toJson(user));
        try {
            sendToUser(id, message);
            sendToDisplay(message);

        } catch (Exception e){
            e.printStackTrace();

        }
    }

    /**
     * 发送给观察用户
     *
     * */
    public void sendToDisplay(TextMessage message) throws IOException {
        Iterator<Entry<Integer, WebSocketSession>> it = displaySessionMap
                .entrySet().iterator();

        while (it.hasNext()) {
            Entry<Integer, WebSocketSession> entry = it.next();
            if (entry.getValue().isOpen()) {
                // entry.getValue().sendMessage(message);
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            if (entry.getValue().isOpen()) {
                                entry.getValue().sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
        }
    }

    /**
     * 发送给普通用户
     * */

    public void sendToUser(int id, TextMessage message) throws IOException{
        WebSocketSession session = userSocketSessionMap.get(id);
        if (session != null && session.isOpen()) {
            session.sendMessage(message);
        }
    }

    /**
     * 抽奖
     * */
    public void drawPrize(String prizeId){
        Prize rollingPrize = prizeSingleton.getPrize(Integer.parseInt(prizeId));
        int rollingCount = rollingPrize.getCount() - rollingPrize.getDrawed();
        if (rollingCount == 0) return;
        Member winner = Lottery.roll(rollingPrize);
        if (winner == null){
            return;
        }
        rollingPrize.setDrawed(rollingPrize.getDrawed() + 1);
        ResponesMsg msg = new ResponesMsg();
        msg.setMsg("doDraw");
        msg.setData(winner);
        TextMessage message = new TextMessage(new GsonBuilder().create().toJson(msg));
        try {
            sendToDisplay(message);
            sendToUser(winner.getId(), message);
        } catch (Exception e){
            e.printStackTrace();
        }



    }
}