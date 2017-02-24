package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/11/11.
 */

public class ExchangeStatus {

    public int error = 0;
    public List<Integral> integrals;

    @Override
    public String toString() {
        return "ExchangeStatus{" +
                "error=" + error +
                ", integrals=" + integrals +
                '}';
    }
}
