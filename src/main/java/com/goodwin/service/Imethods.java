package com.goodwin.service;

import com.goodwin.model.Iset;

import java.io.File;
import java.util.List;

/**
 * Created by Superwen on 2017/1/24.
 */
public abstract class Imethods {
    public abstract List<?> doQuery();
    public List<?> doQuery(Integer param){
        return null;
    }
    public abstract Iset doPost(Iset set);
    public abstract Iset doPut(Iset set);
    public abstract Iset doDelete(Iset set);
}
