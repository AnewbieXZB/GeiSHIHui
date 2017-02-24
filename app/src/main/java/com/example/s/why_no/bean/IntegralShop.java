package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/11/9.
 */

public class IntegralShop {

    public int error = 0;
    public List<IntegralGoods> integral;

    @Override
    public String toString() {
        return "IntegralShop{" +
                "error=" + error +
                ", integral=" + integral +
                '}';
    }
}
