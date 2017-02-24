package com.example.s.why_no.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.LoginStatus;
import com.example.s.why_no.servlet.UserRegisterServlet;
import com.example.s.why_no.utils_phone.Judge;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by S on 2016/11/7.
 */

public class UserRegisterActivity extends Activity implements View.OnClickListener{


    private ImageView tv_register_back;
    private EditText edt_register_user;
    private EditText edt_register_password1;
    private EditText edt_register_password2;
    private Button bt_register_sure;


    private final int SAVE_PERSON_DATA = 44;
    private final int ERROR_REQUEST = 546;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){

                case SAVE_PERSON_DATA:
                    LoginStatus status = (LoginStatus) msg.obj;

                    if (status.error == 0){
                        bt_register_sure.setEnabled(true);
//                        bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                        Toast.makeText(UserRegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                    }else if (status.error == 1){

                        bt_register_sure.setEnabled(true);
//                        bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                        //保存用户登录信息
                        SharedPreferences sharedPreferences = getSharedPreferences("lws",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone", status.user_name);
                        editor.putString("list","-1");
                        editor.commit();

                        Intent intent = getIntent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }else if(status.error == 2){
                        bt_register_sure.setEnabled(true);
//                        bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                        Toast.makeText(UserRegisterActivity.this,"该用户已注册",Toast.LENGTH_SHORT).show();
                    }
                    break;

                case ERROR_REQUEST:
                    Toast.makeText(UserRegisterActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    bt_register_sure.setEnabled(true);
                    bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        init();
        initView();
        initEvent();

    }


    /**
     * 确认注册
     */
    private void sureRegister() throws NoSuchAlgorithmException {

        bt_register_sure.setEnabled(false);
//        bt_register_sure.setBackgroundResource(R.drawable.btn_gray);
        int status = isNull();
        switch (status){

            case 5:
                Toast.makeText(UserRegisterActivity.this,"手机格式不正确，请重新输入",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
//                bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 4:
                Toast.makeText(UserRegisterActivity.this,"两次输入的密码不相同",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
//                bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 3:
                Toast.makeText(UserRegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
//                bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 2:
                Toast.makeText(UserRegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
//                bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 1:
                Toast.makeText(UserRegisterActivity.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
//                bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 0:
                sendUser();//输入合法 允许发送到服务器
                break;
        }

    }

    /**
     * 输入合法 允许发送到服务器
     */
    private void sendUser() throws NoSuchAlgorithmException {

        final String user_name = edt_register_user.getText().toString();
        final String password = toMD5(edt_register_password1.getText().toString());

        Log.e("MD5",password);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserRegisterServlet userRegisterServlet
                        = new UserRegisterServlet();
                String json = userRegisterServlet.getJson(user_name,password);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else{
                    Gson gson = new Gson();
                    LoginStatus login = gson.fromJson(json,LoginStatus.class);

                    msg.obj = login;
                    msg.what = SAVE_PERSON_DATA;
                }
                handler.sendMessage(msg);
            }
        }).start();

    }

    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
    }

    /**
     * MD5加密
     * @param
     * @return
     */
    private String toMD5(String string) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();



//        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        md5.update(string.getBytes());
//        byte[] m = md5.digest();//加密
//        return getString(m);

//        byte[] hash;
//        try {
//            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Huh, MD5 should be supported?", e);
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
//        }
//
//        StringBuilder hex = new StringBuilder(hash.length * 2);
//        for (byte b : hash) {
//            if ((b & 0xFF) < 0x10) hex.append("0");
//            hex.append(Integer.toHexString(b & 0xFF));
//        }
//        return hex.toString();

    }

    /**
     * 做一些基本判断  判断输入框是否为空
     */
    private int isNull(){
        /**
         * 0: 所有情况排除 允许发送注册信息到服务器
         * 1：edt_phone为空
         * 2：edt_password1为空
         * 3: edt_password1为空
         * 4: 两次输入的密码不一样
         * 5：不是手机格式
         */
        int mess = 0;
        String edt_phone = edt_register_user.getText().toString();
        String edt_password1 = edt_register_password1.getText().toString();
        String edt_password2 = edt_register_password2.getText().toString();
        if (edt_phone.equals("")){
            mess = 1;
        }else if(edt_password1.equals("")){
            mess = 2;
        }else if(edt_password2.equals("")){
            mess = 3;
        }else if(!edt_password1.equals(edt_password2)){
            mess = 4;
        }else if(!(new Judge().isPhoneNumberValid(edt_phone))){
            mess = 5;
        }
        return mess;
    }



    private void initEvent() {
        tv_register_back.setOnClickListener(this);
        bt_register_sure.setOnClickListener(this);


//        /**
//         * 监听输入框是否为空
//         */
//        TextWatcher watcher_phone = new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // TODO Auto-generated method stub
//                String user = edt_register_user.getText().toString();
//                String password1 = edt_register_password1.getText().toString();
//                String password2 = edt_register_password2.getText().toString();
//                if (user.equals("")|| password1.equals("") || password2.equals("")){
//                    bt_register_sure.setEnabled(false);
//                    bt_register_sure.setBackgroundResource(R.drawable.btn_gray);
//                }else{
//                    bt_register_sure.setEnabled(true);
//                    bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
//                }
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//                // TODO Auto-generated method stub
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // TODO Auto-generated method stub
//
//            }
//        };
//
//
//
//        edt_register_user.addTextChangedListener(watcher_phone);
//        edt_register_password1.addTextChangedListener(watcher_phone);
//        edt_register_password2.addTextChangedListener(watcher_phone);
    }

    private void initView() {

    }

    private void init() {
        tv_register_back = (ImageView) findViewById(R.id.tv_register_back);
        bt_register_sure = (Button) findViewById(R.id.bt_register_sure);
        edt_register_user = (EditText) findViewById(R.id.edt_register_user);
        edt_register_password1 = (EditText) findViewById(R.id.edt_register_password1);
        edt_register_password2 = (EditText) findViewById(R.id.edt_register_password2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_register_back:
                finish();
                break;
            case R.id.bt_register_sure:
                try {
                    sureRegister();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
        }
    }



}
