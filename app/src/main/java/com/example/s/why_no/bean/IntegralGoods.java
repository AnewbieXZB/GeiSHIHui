package com.example.s.why_no.bean;

/**
 * Created by S on 2016/11/9.
 */

public class IntegralGoods {

    public int id;
    public String name;
    public int surplus;
    public int need;
    public String img;
    public String number;
    public int money;
    public String people;
    public String details;

    @Override
    public String toString() {
        return "IntegralGoods{" +
                "details='" + details + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", surplus=" + surplus +
                ", need=" + need +
                ", img='" + img + '\'' +
                ", number='" + number + '\'' +
                ", money=" + money +
                ", people='" + people + '\'' +
                '}';
    }
}
