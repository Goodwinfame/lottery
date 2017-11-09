package com.goodwin.controller;

/**
 * Created by Superwen on 2017/1/5.
 */
import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.goodwin.model.Member;
import com.goodwin.singleton.MemberSingleton;
import com.goodwin.singleton.OnlineUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.TextMessage;
import com.goodwin.model.Message;
import com.goodwin.model.User;
import com.goodwin.socket.MyWebSocketHandler;

import com.google.gson.GsonBuilder;

@Controller
@RequestMapping("")
public class MainController {
    @Resource
    MyWebSocketHandler handler;
    public static MemberSingleton memberSingleton = MemberSingleton.getInstance();


    //登录
    @RequestMapping(value="",method=RequestMethod.GET)
    public ModelAndView index(){
//        request.getSession().setAttribute("uid", user.getId());
//        request.getSession().setAttribute("name", users.get(user.getId()).getName());
        return new ModelAndView("index");
    }

    //设置
    @RequestMapping(value="settings",method=RequestMethod.GET)
    public ModelAndView settings(){
//        request.getSession().setAttribute("uid", user.getId());
//        request.getSession().setAttribute("name", users.get(user.getId()).getName());
        return new ModelAndView("settings/settings");
    }

    @RequestMapping(value="appredirect",method=RequestMethod.GET)
    public String appredirect(){
//        request.getSession().setAttribute("uid", user.getId());
//        request.getSession().setAttribute("name", users.get(user.getId()).getName());
        return "redirect:http://www.baidu.com?apip=192.168.80.33&hw=3000&_t=545678987654564&min_port=100&max_port=9999&apmac=60cda90088eb";
    }


    //展示
    @RequestMapping(value="display",method=RequestMethod.GET)
    public ModelAndView display(Member member, HttpServletRequest request){
        return new ModelAndView("display");
    }

    //摇一摇
    @RequestMapping(value="rolling",method=RequestMethod.GET)
    public ModelAndView rolling(Member member, HttpServletRequest request){
        if (request.getSession().getAttribute("userinfo") == null){
            return new ModelAndView("redirect:");
        }
//        request.getSession().setAttribute("userinfo", member);
        return new ModelAndView("rolling");
    }

    //环节控制
    @RequestMapping(value="controll",method=RequestMethod.GET)
    public ModelAndView controll(Member member, HttpServletRequest request){

        return new ModelAndView("settings/controll");
    }




    //跳转到发布广播页面
    @RequestMapping(value="broadcast",method=RequestMethod.GET)
    public ModelAndView broadcast(){
        return new ModelAndView("broadcast");
    }

    //发布系统广播（群发）
    @ResponseBody
    @RequestMapping(value="broadcast",method=RequestMethod.POST)
    public void broadcast(String text) throws IOException{
        Message msg=new Message();
        msg.setDate(new Date());
        msg.setFrom(-1);
        msg.setFromName("系统广播");
        msg.setTo(0);
        msg.setText(text);
        handler.broadcast(new TextMessage(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)));
    }


    //上传页面
//    @RequestMapping(value="uploadpage",method=RequestMethod.GET)
//    public ModelAndView controll(){
//
//        return new ModelAndView("upload");
//    }
}
