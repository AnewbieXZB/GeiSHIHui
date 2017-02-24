package com.example.s.why_no.bean;

import java.util.List;

/**
 * Created by S on 2016/11/12.
 */

public class MessageRecord {

    public int error = 0;
    public List<Information> information;

    public class Information{
        public int id;
        public String name ;
        public String text;
        public String time;
    }
}
