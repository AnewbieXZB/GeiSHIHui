package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/10/31.
 */

public class Shop {

    public int error  = 0;
    public List<Goods> goods;

    @Override
    public String toString() {
        return "Shop{" +
                "error=" + error +
                ", goods=" + goods +
                '}';
    }
}
