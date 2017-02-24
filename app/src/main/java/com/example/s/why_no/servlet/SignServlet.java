package com.example.s.why_no.servlet;

import android.util.Log;

import com.example.s.why_no.cache.Net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by S on 2016/10/26.
 */

public class SignServlet {

    private String urlStr = Net.TO_SIGN;
    /**
     * 查询货物列表
     */
    public String getJson(String phone) {

        //网络地址 通过字符串，生成URL对象
        URL url = null;
        // 网络会话链接
        HttpURLConnection conn = null;
        //获取网站返回的输入流
        InputStream in = null;
        //每次读的字节数
        byte[] data = new byte[1024];
        //每次读到的字节数，一般是1024，如果到了最后一行就会少于1024，到了末尾就是 -1
        int len = 0;
        //本地的输出流
        ByteArrayOutputStream os;
        try {
            Log.v("lws", "使用httpurlconnection");

            url = new URL(urlStr);
    //        url = new URL("http://192.168.0.101/shop/public/appindex");//内网
    //        url = new URL("http://api.dataoke.com/index.php?r=goodsLink/android");

            conn = (HttpURLConnection) url.openConnection();
            //默认是get 方式
            conn.setRequestMethod("POST");
            conn.setReadTimeout(30000);
            // 设置是否向connection输出，如果是post请求，参数要放在http正文内，因此需要设为true
            conn.setDoOutput(true);
            // Post 请求不能使用缓存
            conn.setUseCaches(false);

            //设置请求头 一般没特殊要求， 不需要
            //conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，

            // 要注意的是connection.getOutputStream会隐含的进行connect,所以下面这句可以不要
            //conn.connect();
            //要上传的参数

            /**
             * 传参数
             */
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            String content = "phone=" + URLEncoder.encode(phone, "UTF-8");

            pw.print(content);
            pw.flush();
            pw.close();


            int code = conn.getResponseCode();
            System.out.println("状态码：" + code);

            //时候需要获取输入， 废话，当然需要返回，最少要返回状态吧。 所以默认是true
            //conn.setDoInput(true);
            in = conn.getInputStream();
            os = new ByteArrayOutputStream();
            while ((len = in.read(data)) != -1) {
                os.write(data, 0, len);
            }
            in.close();

            return new String(os.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }
}
