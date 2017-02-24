package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/11/7.
 */

public class Advertisement {

    public int error;
    public int number;
    public List<Thumbnail> thumbnail;

    @Override
    public String toString() {
        return "Advertisement{" +
                "error=" + error +
                ", number=" + number +
                ", thumbnail=" + thumbnail +
                '}';
    }
}
