package com.goodwin.model;

import javax.xml.bind.annotation.*;

/**
 * Created by Superwen on 2017/1/23.
 */
@XmlRootElement(name = "prize")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"id", "name", "count", "level","drawed"})
public class Prize implements Iset {
    private int id;
    private String name;
    private int count;
    private int level;
    private int drawed = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDrawed() {
        return drawed;
    }

    public void setDrawed(int drawed) {
        this.drawed = drawed;
    }
}
