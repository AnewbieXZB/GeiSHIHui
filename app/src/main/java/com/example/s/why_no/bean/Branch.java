package com.example.s.why_no.bean;

/**
 * Created by S on 2016/11/8.
 */

public class Branch {

    public int id;
    public String phone;
    public String event;
    public String arithmetic;
    public String time;

    @Override
    public String toString() {
        return "Branch{" +
                "arithmetic='" + arithmetic + '\'' +
                ", id=" + id +
                ", phone='" + phone + '\'' +
                ", Event='" + event + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
