package com.goodwin.utils;

import com.goodwin.model.Member;
import com.goodwin.model.Prize;
import com.goodwin.singleton.MemberSingleton;
import com.goodwin.singleton.PrizeSingleton;

import java.util.List;
import java.util.Random;

/**
 * Created by Superwen on 2017/2/6.
 */
public class Lottery {
    public static PrizeSingleton prizeSingleton = PrizeSingleton.getInstance();
    public static MemberSingleton memberSingleton = MemberSingleton.getInstance();

    public static Member roll(Prize prize) {
        Random random = new Random();
        int randNum = 0;
        List<Member> rollingMember = memberSingleton.getUnprizedMember();
        if (rollingMember.size() == 0) {
            return null;
        }
        if (rollingMember.size()>1){
            randNum = random.nextInt(rollingMember.size() - 1);
        }
        Member winner = rollingMember.get(randNum);
        winner.setPrize(prize);
        return winner;
    }

}
