package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/11/8.
 */

public class IntegralRecord {

    public int error;
    public List<Integral> inte;
    public List<Branch> branch;

    @Override
    public String toString() {
        return "IntegralRecord{" +
                "branch=" + branch +
                ", error=" + error +
                ", inte=" + inte +
                '}';
    }
}
