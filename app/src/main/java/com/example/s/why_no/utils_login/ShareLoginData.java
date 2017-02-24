package com.example.s.why_no.utils_login;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by S on 2016/11/8.
 * 判断用户是否有登录
 */

public class ShareLoginData {

    private Context context;

    public ShareLoginData(Context context) {
        this.context = context;
    }

    public String isLogin(){

        SharedPreferences share = context.getSharedPreferences("lws", context.MODE_PRIVATE);
        String id = share.getString("phone","-1");

        return id;
    }


    public String collectionId(){

        SharedPreferences share = context.getSharedPreferences("lws", context.MODE_PRIVATE);
        String list = share.getString("list","-1");

        return list;
    }
}
