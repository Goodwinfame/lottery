package com.goodwin.service.impl;

import com.goodwin.model.*;
import com.goodwin.service.Imethods;
import com.goodwin.singleton.MemberSingleton;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Superwen on 2017/1/24.
 */
@Service("memberMethod")
public class MemberMethod extends Imethods {
    private static int maxId;
    public static MemberSingleton memberSingleton = MemberSingleton.getInstance();

    @Override
    public List<?> doQuery() {
        return memberSingleton.getAllMember();
    }
    @Override
    public List<?> doQuery(Integer unPrized) {
        if (unPrized == 1){
            return memberSingleton.getUnprizedMember();

        } else {
            return memberSingleton.getPrizedMember();

        }
    }

    @Override
    public Iset doPost(Iset set) {
        List<Member> memberList = memberSingleton.getAllMember();
        for (Member member: memberList) {
            if (member.getId() > maxId) {
                maxId = member.getId();
            }
        }
        set.setId(++maxId);
        memberSingleton.addMember((Member) set);
        return set;
    }

    @Override
    public Iset doPut(Iset set) {
        memberSingleton.changeMember((Member) set);
        return set;
    }

    @Override
    public Iset doDelete(Iset set) {
        memberSingleton.deleteMember((Member) set);
        return set;
    }
}
