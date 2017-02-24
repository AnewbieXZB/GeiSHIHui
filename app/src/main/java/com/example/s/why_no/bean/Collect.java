package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/11/10.
 */

public class Collect {

    public int error = 0;
    public List<Goods> collection;

    @Override
    public String toString() {
        return "Collect{" +
                "collection=" + collection +
                ", error=" + error +
                '}';
    }
}
