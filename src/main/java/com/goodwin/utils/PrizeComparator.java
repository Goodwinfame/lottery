package com.goodwin.utils;

import com.goodwin.model.Prize;

import java.util.Comparator;

/**
 * Created by Superwen on 2017/1/24.
 */
public class PrizeComparator implements Comparator {

    public int compare(Object a, Object b) {
        if(null!=a&&null!=b)
        {
            int one = ((Prize) a).getLevel();
            int two = ((Prize) b).getLevel();
            return two - one;
        }
        return 0;
    }

}
