package com.example.s.why_no.bean;

/**
 * Created by S on 2016/11/8.
 */

public class Code {

        public int error = -1;
        public String phone ;
        public int verification ;

    @Override
    public String toString() {
        return "Code{" +
                "error=" + error +
                ", phone='" + phone + '\'' +
                ", verification=" + verification +
                '}';
    }
}
