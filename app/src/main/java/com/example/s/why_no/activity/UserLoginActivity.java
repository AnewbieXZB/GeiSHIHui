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
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.LoginStatus;
import com.example.s.why_no.servlet.UserLoginServlet;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by S on 2016/11/7.
 */

public class UserLoginActivity extends Activity implements View.OnClickListener{


    private ImageView imv_login_back;
    private EditText edt_login_user;
    private EditText edt_login_password;
    private Button bt_login_sure;
    private TextView tv_login_find_password;
    private TextView tv_login_to_register;

    private final int SAVE_PERSON_DATA = 44;
    private final int ERROR_REQUEST = 546;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){

                case SAVE_PERSON_DATA:
                    LoginStatus status = (LoginStatus) msg.obj;

                    if (status.error == 0){
                        bt_login_sure.setEnabled(true);
//                        bt_login_sure.setBackgroundResource(R.drawable.btn_accent);
                        Toast.makeText(UserLoginActivity.this,"用户名不存在或者密码错误",Toast.LENGTH_SHORT).show();
                    }else if (status.error == 1){

                        bt_login_sure.setEnabled(true);
//                        bt_login_sure.setBackgroundResource(R.drawable.btn_accent);
                        //保存用户登录信息
                        SharedPreferences sharedPreferences = getSharedPreferences("lws",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone", status.user_name);
                        editor.putString("list","-1");
                        editor.commit();

                        Intent intent = getIntent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    break;

                case ERROR_REQUEST:
                    Toast.makeText(UserLoginActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    bt_login_sure.setEnabled(true);
//                    bt_login_sure.setBackgroundResource(R.drawable.btn_accent);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        init();
        initView();
        initEvent();

    }


    /**
     * 确认登录
     */
    private void sureLogin() throws NoSuchAlgorithmException {

        bt_login_sure.setEnabled(false);
//        bt_login_sure.setBackgroundResource(R.drawable.btn_gray);
        int status = isNull();
        switch (status){

            case 2:
                Toast.makeText(UserLoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                bt_login_sure.setEnabled(true);
//                bt_login_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 1:
                Toast.makeText(UserLoginActivity.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                bt_login_sure.setEnabled(true);
//                bt_login_sure.setBackgroundResource(R.drawable.btn_accent);
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

        final String user_name = edt_login_user.getText().toString();
        final String password = toMD5(edt_login_password.getText().toString());

        Log.e("user ",user_name);
        Log.e("user ",password);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserLoginServlet userLoginServlet
                        = new UserLoginServlet();
                String json = userLoginServlet.getJson(user_name,password);
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
    private String toMD5(String string) throws NoSuchAlgorithmException {

        /**
         * 加密32位
         */
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
         * 2：edt_password为空
         */
        int mess = 0;
        String edt_phone = edt_login_user.getText().toString();
        String edt_password = edt_login_password.getText().toString();

        if (edt_phone.equals("")){
            mess = 1;
        }else if(edt_password.equals("")){
            mess = 2;
        }
        return mess;
    }



    private void initEvent() {
        imv_login_back.setOnClickListener(this);
        bt_login_sure.setOnClickListener(this);
        tv_login_find_password.setOnClickListener(this);
        tv_login_to_register.setOnClickListener(this);

//        /**
//         * 监听输入框是否为空
//         */
//        TextWatcher watcher_phone = new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // TODO Auto-generated method stub
//                String user = edt_login_user.getText().toString();
//                String password = edt_login_password.getText().toString();
//                if (user.equals("")|| password.equals("") ){
//                    bt_login_sure.setEnabled(false);
//                    bt_login_sure.setBackgroundResource(R.drawable.btn_gray);
//                }else{
//                    bt_login_sure.setEnabled(true);
//                    bt_login_sure.setBackgroundResource(R.drawable.btn_accent);
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
//        edt_login_user.addTextChangedListener(watcher_phone);
//        edt_login_password.addTextChangedListener(watcher_phone);
    }

    private void initView() {

    }

    private void init() {
        imv_login_back = (ImageView) findViewById(R.id.imv_login_back);
        bt_login_sure = (Button) findViewById(R.id.bt_login_sure);
        edt_login_user = (EditText) findViewById(R.id.edt_login_user);
        edt_login_password = (EditText) findViewById(R.id.edt_login_password);
        tv_login_find_password = (TextView) findViewById(R.id.tv_login_find_password);
        tv_login_to_register = (TextView) findViewById(R.id.tv_login_to_register);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_login_back:
                finish();
                break;
            case R.id.bt_login_sure:
                try {
                    sureLogin();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_login_find_password:
                startActivity(new Intent(UserLoginActivity.this,CustomerActivity.class));
                break;
            case R.id.tv_login_to_register:
                startActivityForResult((new Intent(UserLoginActivity.this, UserRegisterActivity.class)),404);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 404:
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;

        }

    }

}
