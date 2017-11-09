package com.goodwin.model;

/**
 * Created by Superwen on 2017/2/7.
 */
public class ResponesMsg {
    private String msg = "success";
    private int msgNo = 10000;
    private Object data;
    private Object param;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getMsgNo() {
        return msgNo;
    }

    public void setMsgNo(int msgNo) {
        this.msgNo = msgNo;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }
}
