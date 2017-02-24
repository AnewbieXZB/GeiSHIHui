package com.example.s.why_no.bean;

/**
 * Created by S on 2016/11/9.
 */

public class Thumbnail {

    public int id;
    public String url;
    public int a;
    public String ification;
    public String href;

    @Override
    public String toString() {
        return "Thumbnail{" +
                "a=" + a +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", ification='" + ification + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
