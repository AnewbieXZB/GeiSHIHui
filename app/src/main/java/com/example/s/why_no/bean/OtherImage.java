package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/11/11.
 */

public class OtherImage {

    public int error = 0;
    public List<IntegralImage> integralimg;

    public class IntegralImage{

        public int id;
        public String img;
        public String number;
    }

}
