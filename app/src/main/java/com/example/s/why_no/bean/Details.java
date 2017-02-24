package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/11/5.
 */

public class Details {

    public int error = 0;
    public List<Goods> goods;
    public List<Goods> more;

    @Override
    public String toString() {
        return "Details{" +
                "goods=" + goods +
                ", more=" + more +
                '}';
    }
}
