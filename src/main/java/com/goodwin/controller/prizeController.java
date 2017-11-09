package com.goodwin.controller;

/**
 * Created by Superwen on 2017/1/5.
 */

import com.goodwin.model.Member;
import com.goodwin.model.Prize;
import com.goodwin.singleton.MemberSingleton;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("prize")
public class prizeController {
    public static MemberSingleton memberSingleton = MemberSingleton.getInstance();
    @RequestMapping(value="/roll",method=RequestMethod.POST,consumes = "application/json")
    public @ResponseBody Object rollPrize(HttpServletRequest request, @PathVariable("level") String level) {

        return null;
    }


}
