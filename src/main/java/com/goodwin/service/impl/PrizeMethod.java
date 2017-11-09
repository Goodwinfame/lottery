package com.goodwin.service.impl;

import com.goodwin.model.Iset;
import com.goodwin.model.Prize;
import com.goodwin.service.Imethods;
import com.goodwin.singleton.PrizeSingleton;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * Created by Superwen on 2017/1/24.
 */
@Service("prizeMethod")
public class PrizeMethod extends Imethods {
    private static int maxId;
    public static PrizeSingleton prizeSingleton = PrizeSingleton.getInstance();
    @Override
    public List<?> doQuery() {
        return prizeSingleton.getAllPrize();
    }

    @Override
    public Iset doPost(Iset set) {
        List<Prize> prizeList = prizeSingleton.getAllPrize();
        for (Prize prize: prizeList) {
            if (prize.getId() > maxId) {
                maxId = prize.getId();
            }
        }
        set.setId(++maxId);
        prizeSingleton.addPrize((Prize) set);
        return set;
    }

    @Override
    public Iset doPut(Iset set) {
        prizeSingleton.changePrize((Prize) set);
        return set;
    }

    @Override
    public Iset doDelete(Iset set) {
        prizeSingleton.deletePrize((Prize) set);
        return set;
    }
}
