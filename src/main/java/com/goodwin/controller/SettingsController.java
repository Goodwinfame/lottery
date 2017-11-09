package com.goodwin.controller;

/**
 * Created by Superwen on 2017/1/5.
 */

import com.goodwin.model.*;
import com.goodwin.service.Imethods;
import com.goodwin.singleton.MemberSingleton;
import com.goodwin.singleton.OnlineUser;
import com.goodwin.singleton.PrizeSingleton;
import com.goodwin.socket.MyWebSocketHandler;
import com.goodwin.utils.XmlUtil;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("settings")
public class SettingsController {
    @Autowired
    private Imethods prizeMethod;
    @Autowired
    private Imethods memberMethod;

    public static MemberSingleton memberSingleton = MemberSingleton.getInstance();
    public static PrizeSingleton prizeSingleton = PrizeSingleton.getInstance();



    //获取
    @RequestMapping(value="/prize",method=RequestMethod.GET,consumes = "application/json")
    public @ResponseBody List<?> prizeQuery(HttpServletRequest request) {
        return this.prizeMethod.doQuery();
    }

    //新增
    @RequestMapping(value="/prize",method=RequestMethod.POST,consumes = "application/json")
    public @ResponseBody Object prizePost(HttpServletRequest request, @RequestBody Prize newPrize) {
        return this.prizeMethod.doPost(newPrize);
    }

    //修改
    @RequestMapping(value="/prize",method=RequestMethod.PUT,consumes = "application/json")
    public @ResponseBody Object prizePut(HttpServletRequest request, @RequestBody Prize newPrize) {
        return this.prizeMethod.doPut(newPrize);
    }

    //删除
    @RequestMapping(value="/prize/id/{id}",method=RequestMethod.DELETE,consumes = "application/json")
    public @ResponseBody Object prizeDelete(HttpServletRequest request,@PathVariable("id") String id) {
        Prize newPrize = new Prize();
        newPrize.setId(Integer.parseInt(id));
        return this.prizeMethod.doDelete(newPrize);
    }


    /**
     * member接口
     * */

    //获取
    @RequestMapping(value="/member/scale/{scale}",method=RequestMethod.GET,consumes = "application/json")
    public @ResponseBody List<?> memberQuery(HttpServletRequest request,@PathVariable("scale") Integer scale) {

        if (scale != 0){
            return this.memberMethod.doQuery(scale);
        }
        return this.memberMethod.doQuery();
    }

    //新增
    @RequestMapping(value="/member",method=RequestMethod.POST,consumes = "application/json")
    public @ResponseBody Object memberPost(HttpServletRequest request, @RequestBody Member newMember) {
        return this.memberMethod.doPost(newMember);
    }

    //修改
    @RequestMapping(value="/member",method=RequestMethod.PUT,consumes = "application/json")
    public @ResponseBody Object memberPut(HttpServletRequest request, @RequestBody Member newMember) {
        return this.memberMethod.doPut(newMember);
    }

    //删除
    @RequestMapping(value="/member/id/{id}",method=RequestMethod.DELETE,consumes = "application/json")
    public @ResponseBody Object memberDelete(HttpServletRequest request,@PathVariable("id") String id) {
        Member newMember = new Member();
        newMember.setId(Integer.parseInt(id));
        return this.memberMethod.doDelete(newMember);
    }


    //重置奖品
    @RequestMapping(value="/resetPrize",method=RequestMethod.POST,consumes = "application/json")
    public @ResponseBody Object memberPost(HttpServletRequest request,@RequestBody Map<String, Integer> id) {
        ResponesMsg responesMsg = new ResponesMsg();
        responesMsg.setMsg("resetPrize");
        List<Member> memberList = memberSingleton.getPrizedMember();
        List<Prize> prizeList = prizeSingleton.getAllPrize();
        for (Prize prize: prizeList){
            if (prize.getId() == id.get("id")){
                prize.setDrawed(0);
                responesMsg.setData(prize);

            }
        }
        for (Member member: memberList){
            if (member.getPrize().getId() == id.get("id")){
                member.setPrize(null);
            }
        }

        TextMessage socketMessage = new TextMessage(new GsonBuilder().create().toJson(responesMsg));
        try {
            myWebSocketHandler.sendToDisplay(socketMessage);
        } catch (Exception e){
            e.printStackTrace();
        }
        return responesMsg;
    }

    //控制路由调整
    @RequestMapping(value="/routerGo",method=RequestMethod.POST,consumes = "application/json")
    public @ResponseBody Object routerGo(HttpServletRequest request,@RequestBody ResponesMsg message) {
        if (message.getData().equals("prize")){
            List<Prize> prizeList = prizeSingleton.getLevelsDesc();
            for (Prize prize: prizeList){
                if (prize.getDrawed() >= prize.getCount()){
                    continue;
                } else {
                    Map param = new HashMap<>();
                    param.put("level",prize.getLevel());
                    message.setParam(param);
                    break;
                }
            }
        }

        TextMessage socketMessage = new TextMessage(new GsonBuilder().create().toJson(message));
        try {
            myWebSocketHandler.broadcast(socketMessage);
            myWebSocketHandler.sendToDisplay(socketMessage);
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ResponesMsg();
    }


    @Autowired
    MyWebSocketHandler myWebSocketHandler;
}
