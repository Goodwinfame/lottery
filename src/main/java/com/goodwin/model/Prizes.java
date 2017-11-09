package com.goodwin.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Superwen on 2017/1/23.
 */
@XmlRootElement(name="prizes")
@XmlAccessorType(XmlAccessType.FIELD)
public class Prizes {
    @XmlElement(name="prize")
    private List<Prize> prizes = new ArrayList<>();

    public List<Prize> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<Prize> prizes) {
        this.prizes = prizes;
    }

    public void addPrize(Prize prize) { this.prizes.add(prize);}
}
