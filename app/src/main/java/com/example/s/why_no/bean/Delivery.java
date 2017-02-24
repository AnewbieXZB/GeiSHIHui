package com.example.s.why_no.bean;

/**
 * Created by S on 2016/11/9.
 */

public class Delivery {

    public int id;
    public String phone;
    public String starttime;
    public String name;
    public String tel;
    public String region;
    public String address;
    public int integral;

    @Override
    public String toString() {
        return "Delivery{" +
                "address='" + address + '\'' +
                ", id=" + id +
                ", phone='" + phone + '\'' +
                ", starttime='" + starttime + '\'' +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", region='" + region + '\'' +
                ", integral=" + integral +
                '}';
    }
}
