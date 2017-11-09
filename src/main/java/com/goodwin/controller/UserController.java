package com.goodwin.controller;

/**
 * Created by Superwen on 2017/1/5.
 */

import com.goodwin.model.Member;
import com.goodwin.model.Message;
import com.goodwin.singleton.MemberSingleton;
import com.goodwin.socket.MyWebSocketHandler;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.TextMessage;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("user")
public class UserController {
    public static MemberSingleton memberSingleton = MemberSingleton.getInstance();


    //获取
    @RequestMapping(value="/self",method=RequestMethod.GET,consumes = "application/json")
    public @ResponseBody
    Object userQuery(HttpServletRequest request) {
        return request.getSession().getAttribute("userinfo");
    }

    //用户登录
    @RequestMapping(value="/login",method=RequestMethod.POST)
    public String doLogin(Member member, HttpServletRequest request){
        member = memberSingleton.getMember(member.getPhone());
        try {
            if (memberSingleton.getMember(member.getId()) == null){
                return "redirect:/";
            }
            if (!memberSingleton.isOnline(member.getId())){
                member = memberSingleton.memberOnline(member.getId());
            }
            request.getSession().setAttribute("userinfo", member);
            return "redirect:/rolling";
        } catch (Exception e){
            return "redirect:/";
        }

    }

}
