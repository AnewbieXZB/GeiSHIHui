package com.example.s.why_no.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.Code;
import com.example.s.why_no.bean.Status;
import com.example.s.why_no.servlet.GetCodeFromRegisterServlet;
import com.example.s.why_no.servlet.SavePersonServlet;
import com.google.gson.Gson;

/**
 * Created by S on 2016/11/7.
 */

public class RegisterActivity extends Activity implements View.OnClickListener{

    private Code code;

    private ImageView tv_register_back;
    private EditText edt_register_phone;
    private EditText edt_register_code;
    private TextView tv_register_getcode;
    private Button bt_register_sure;

    private boolean ableCode = true;//true允许获取验证码  false不允许
    private final int GET_CODE = 12;
    private final int GET_ING = 54;
    private final int SAVE_PERSON_DATA = 44;
    private boolean ing = false;//true标记获取验证码中
    private final int ERROR_REQUEST = 546;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case GET_CODE:
                    resetCode();//三分钟后 验证码失效
                    break;
                case SAVE_PERSON_DATA:
                    Status person = (Status) msg.obj;
                    if (person.error == 0){
                        bt_register_sure.setEnabled(true);
                        Toast.makeText(RegisterActivity.this,"上传用户信息到数据库失败",Toast.LENGTH_SHORT).show();
                    }else if (person.error == 1){

                        bt_register_sure.setEnabled(true);
                        //保存用户登录信息
                        SharedPreferences sharedPreferences = getSharedPreferences("lws",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone", code.phone);
                        editor.putString("list","-1");
                        editor.commit();

                        Intent intent = getIntent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }else if(person.error == 2){
                        bt_register_sure.setEnabled(true);
                        Toast.makeText(RegisterActivity.this,"该用户已注册",Toast.LENGTH_SHORT).show();
                    }


                    break;
                case GET_ING:

                    int i = (Integer) msg.obj;
                    tv_register_getcode.setText(i + "秒后才能再次获取");
                    changCodeText(false);

                    if (i == 0) {
                        tv_register_getcode.setText("获取验证码");
                        if (edt_register_phone.getText().toString().equals("")){
                            changCodeText(false);
                        }else{
                            changCodeText(true);
                        }
                        ing = true;
                    }

                    break;
                case ERROR_REQUEST:
                    Toast.makeText(RegisterActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 获取的验证码只保存3分钟
     * 3分钟后失效
     */
    private void resetCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(180*1000);
                    code = new Code();
                    System.out.println("验证码已失效：" + code.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        initView();
        initEvent();

    }

    /**
     * 获取验证码
     */
    private void getCode(final String phone){
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetCodeFromRegisterServlet getCodeServlet = new GetCodeFromRegisterServlet();
                String json = getCodeServlet.getJson(phone);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else {
                    System.out.println("注册获取验证码json:" + json);
                    Gson gson = new Gson();
                    code = gson.fromJson(json, Code.class);
                    System.out.println("code" + code.toString());

                    msg.what = GET_CODE;
                    handler.sendMessage(msg);
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 确认注册
     */
    private void sureRegister() {

        bt_register_sure.setEnabled(false);
        int status = isNull();
        switch (status){
            case 6:
                Toast.makeText(RegisterActivity.this,"验证码已失效，请重新获取",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
                break;
            case 5:
                Toast.makeText(RegisterActivity.this,"输入的验证码错误",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
                break;
            case 4:
                Toast.makeText(RegisterActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
                break;
            case 3:
                Toast.makeText(RegisterActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
                break;
            case 2:
                Toast.makeText(RegisterActivity.this,"手机号输入框不能为空",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
                break;
            case 1:
                Toast.makeText(RegisterActivity.this,"验证码输入框不能为空",Toast.LENGTH_SHORT).show();
                bt_register_sure.setEnabled(true);
                break;
            case 0:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SavePersonServlet savePersonServlet = new SavePersonServlet();
                        String json = savePersonServlet
                                .getJson(code.phone);
                        Message msg = new Message();
                        if (json.equals("error")){
                            msg.what = ERROR_REQUEST;
                        }else {
                            Gson gson = new Gson();
                            Status person = gson.fromJson(json, Status.class);


                            msg.what = SAVE_PERSON_DATA;
                            msg.obj = person;

                        }
                        handler.sendMessage(msg);
                    }
                }).start();
                break;
        }

    }
    /**
     * 做一些基本判断  判断输入框是否为空
     */
    private int isNull(){
        /**
         * 0: 所有情况排除 允许发送注册信息到服务器
         * 1：edt_phone为空
         * 2：edt_code为空
         * 3: 获取失败（没发送成功）
         * 4: 获取的验证码失败（服务器返回失败结果）
         * 5: 填写的验证码不正确
         * 6: 验证码已失效
         */
        int mess = 0;
        String edt_phone = edt_register_phone.getText().toString();
        String edt_code = edt_register_code.getText().toString();
        if (edt_phone.equals("")){
            mess = 1;
        }else if(code.equals("")){
            mess = 2;
        }else if(code.error == -1 ){
            mess = 3;
        }else if(code.error == 0){
            mess = 4;
        }else if(!edt_code.equals(code.verification + "")){
            mess = 5;
            if (code.verification == -1){
                mess = 6;
            }
        }else{
            mess = 0;
        }
        return mess;
    }
    /**
     * 根据是否允许或许验证码的标志而改变 获取验证码textview的样式
     */
    private void changCodeText(boolean able){
        if (able){

            tv_register_getcode.setTextColor(getResources().getColor(R.color.color_red));
            tv_register_getcode.setEnabled(true);
        }else{

            tv_register_getcode.setTextColor(getResources().getColor(R.color.color_line));
            tv_register_getcode.setEnabled(false);
        }
    }
    /**
     * 修改获取验证码的文本（显示倒数）
     */
    private void changeCodeText(){
        if (ableCode){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ing = true;
                    for (int i = 60; i >= 0; i--) {
                        try {
                            Message msg = new Message();
                            msg.obj = i;
                            msg.what = GET_ING;
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();}
                    }
                }
            }).start();
        }else{

        }
    }

    private void initEvent() {
        tv_register_back.setOnClickListener(this);
        bt_register_sure.setOnClickListener(this);
        tv_register_getcode.setOnClickListener(this);


        /**
         * 监听手机号输入框是否为空
         */
        TextWatcher watcher_phone = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (ing == false){
                    if (edt_register_phone.getText().toString().equals("")){
                        ableCode = false;
                        changCodeText(ableCode);
                    }else{
                        ableCode = true;
                        changCodeText(ableCode);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub


            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub


            }
        };

        /**
         * 监听验证码输入框是否为空
         */
        TextWatcher watcher_code = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub


            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (edt_register_code.getText().toString().equals("")
                        || edt_register_phone.getText().toString().equals("")){
                    bt_register_sure.setBackgroundResource(R.drawable.btn_gray);
                    bt_register_sure.setEnabled(false);
                }else{
                    bt_register_sure.setBackgroundResource(R.drawable.btn_accent);
                    bt_register_sure.setEnabled(true);
                }
            }
        };

        edt_register_phone.addTextChangedListener(watcher_phone);
        edt_register_code.addTextChangedListener(watcher_code);
    }

    private void initView() {

    }

    private void init() {
        tv_register_back = (ImageView) findViewById(R.id.tv_register_back);
        bt_register_sure = (Button) findViewById(R.id.bt_register_sure);
        tv_register_getcode = (TextView) findViewById(R.id.tv_register_getcode);
        edt_register_code = (EditText) findViewById(R.id.edt_register_code);
        edt_register_phone = (EditText) findViewById(R.id.edt_register_phone);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_register_back:
                finish();
                break;
            case R.id.tv_register_getcode:
                code = new Code();//实例化验证码class
                getCode(edt_register_phone.getText().toString());
                changeCodeText();
                break;
            case R.id.bt_register_sure:
                sureRegister();
                break;
        }
    }



}
