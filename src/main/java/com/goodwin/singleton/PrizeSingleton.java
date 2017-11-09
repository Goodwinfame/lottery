package com.goodwin.singleton;

import com.goodwin.model.Member;
import com.goodwin.model.Members;
import com.goodwin.model.Prize;
import com.goodwin.model.Prizes;
import com.goodwin.utils.PrizeComparator;
import com.goodwin.utils.XmlUtil;

import java.io.File;
import java.util.*;

/**
 * Created by Superwen on 2017/1/7.
 */
public class PrizeSingleton {
    private String path = new File(this.getClass().getResource("/").getPath()).toString().replace("classes", "") + "prizes.xml";

    private static HashMap<Integer, Prize> prizeMap = new HashMap<>();
    private static final PrizeSingleton instance = new PrizeSingleton();

    private PrizeSingleton() {
        List<Prize> prizeList = ((Prizes) XmlUtil.xml2Bean(path, Prizes.class)).getPrizes();
        for (Prize prize: prizeList){
            prizeMap.put(prize.getId(), prize);
        }
    }

    public static PrizeSingleton getInstance() {
        return instance;
    }

    public Prize prizeDraw(Integer id) {
        Iterator<Map.Entry<Integer, Prize>> iterator = prizeMap.entrySet().iterator();
        Prize prize = null;
        while (iterator.hasNext()){
            Map.Entry<Integer, Prize> prizeObj = iterator.next();
            prize = prizeObj.getValue();
            if (prizeObj.getKey().equals(id)){
                prize.setDrawed(prize.getDrawed() + 1);
                break;
            }

        }
        return prize;

    }


    public List<Prize> getAllPrize(){
        return new ArrayList<>(prizeMap.values());
    }


    public Prize getPrize(Integer id) {
        return prizeMap.get(id);
    }
    public List<Prize> getPrizeByLevel(Integer level) {
        Iterator<Map.Entry<Integer, Prize>> iterator = prizeMap.entrySet().iterator();
        List<Prize> prizeList = new ArrayList<>();
        while (iterator.hasNext()){
            Map.Entry<Integer, Prize> prizeObj = iterator.next();
            Prize prize = prizeObj.getValue();
            if (prize.getLevel() == level){
                prizeList.add(prize);
            }

        }
        return prizeList;
    }

    public boolean isDrawOut(Integer id) {
        Prize prize = prizeMap.get(id);
        return (prize.getCount() - prize.getDrawed()) == 0;
    }


    public void addPrize(Prize prize) {
        prizeMap.put(prize.getId(), prize);
        List<Prize> prizeList = getAllPrize();
        Prizes prizes = new Prizes();
        prizes.setPrizes(prizeList);
        XmlUtil.object2Xml(prizes, path);
    }

    public void changePrize(Prize prize) {
        prizeMap.replace(prize.getId(),prize);
        Prizes prizes = new Prizes();
        prizes.setPrizes(getAllPrize());
        XmlUtil.object2Xml(prizes, path);
    }
    public void deletePrize(Prize prize) {
        prizeMap.remove(prize.getId());
        Prizes prizes = new Prizes();
        prizes.setPrizes(getAllPrize());
        XmlUtil.object2Xml(prizes, path);
    }

    public List<Prize> getLevelsDesc(){
        List<Prize> prizeList = getAllPrize();
        Collections.sort(prizeList, new PrizeComparator());
        return prizeList;
    }

}
