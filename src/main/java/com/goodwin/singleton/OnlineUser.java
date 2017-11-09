package com.goodwin.singleton;

import com.goodwin.model.User;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Superwen on 2017/1/7.
 */
public class OnlineUser {
    private static HashMap<Long, User> users = new HashMap<Long, User>();
    private static final OnlineUser instance = new OnlineUser();
    private OnlineUser() {}
    public static OnlineUser getInstance() {
        return instance;
    }
    public static void addUser(User user){
        users.put(user.getId(),user);
    }
    public static HashMap getOnlineUsers(){
        return users;
    }
    public static User getUserByName(String name) {
        Iterator iterator = users.entrySet().iterator();
        User user = new User();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            if (((User)entry.getValue()).getName().equals(name)){
                user = (User)entry.getValue();
            }
        }
        return user;
    }
    public static void removeUser(String name){
        Iterator iterator = users.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            if (((User)entry.getValue()).getName().equals(name)){
                users.remove(((User)entry.getValue()).getId());
            }
        }
    }
    public boolean isUserExist(String name){
        Iterator iterator = users.entrySet().iterator();
        boolean flag = false;
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            if (((User)entry.getValue()).getName().equals(name)){
                flag = true;

            }
        }
        return flag;
    }
    public void userOffline(Long id){
        User user = users.get(id);
        user.setStatus(false);
    }
    public void userOnline(Long id){
        User user = users.get(id);
        user.setStatus(true);
    }
    public static void removeUser(Long id){
        users.remove(id);
    }
}
