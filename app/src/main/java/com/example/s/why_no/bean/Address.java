package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/11/9.
 */

public class Address {

    public int error;
    public List<Delivery> delivery;


    @Override
    public String toString() {
        return "Address{" +
                "delivery=" + delivery +
                ", error=" + error +
                '}';
    }
}
