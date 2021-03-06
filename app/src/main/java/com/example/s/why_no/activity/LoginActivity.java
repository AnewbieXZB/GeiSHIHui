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
import com.example.s.why_no.bean.CancelCollect;
import com.example.s.why_no.bean.Code;
import com.example.s.why_no.servlet.CollectionUidListServlet;
import com.example.s.why_no.servlet.GetCodeFromLoginServlet;
import com.google.gson.Gson;

/**
 * Created by S on 2016/11/7.
 */

public class LoginActivity extends Activity implements View.OnClickListener{

    private Code code;

    private ImageView tv_login_back;
    private EditText edt_login_phone;
    private EditText edt_login_code;
    private TextView tv_login_getcode;
    private Button bt_login_sure;

    private boolean ableCode = true;//true允许获取验证码  false不允许
    private final int GET_CODE = 12;
    private final int GET_ING = 54;
    private boolean ing = false;//true标记获取验证码中

    private final int GET_COLLECT_UID = 89;
    private final int ERROR_REQUEST = 55;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case GET_CODE:

                    if (code.error == 0){
                        Toast.makeText(LoginActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                    }else if(code.error == 1){
                        resetCode();//三分钟后 验证码失效
                    }else if(code.error == 2){
                        Toast.makeText(LoginActivity.this,"该用户不存在",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GET_ING:

                    int i = (Integer) msg.obj;
                    tv_login_getcode.setText(i + "秒后才能再次获取");
                    changCodeText(false);

                    if (i == 0) {
                        tv_login_getcode.setText("获取验证码");
                        if (edt_login_phone.getText().toString().equals("")){
                            changCodeText(false);
                        }else{
                            changCodeText(true);
                        }
                        ing = true;
                    }

                    break;
                case GET_COLLECT_UID:
                    CancelCollect cancelCollect = (CancelCollect) msg.obj;
                    saveListAndPhone(cancelCollect);//保存有关信息到本地
                    break;
                case ERROR_REQUEST:
                    Toast.makeText(LoginActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
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

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                GetCodeFromLoginServlet getCodeServlet = new GetCodeFromLoginServlet();
                String json = getCodeServlet.getJson(phone);

                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else {


                    System.out.println("登录获取验证码json:" + json);
                    Gson gson = new Gson();
                    code = gson.fromJson(json, Code.class);
                    System.out.println("code" + code.toString());


                    msg.what = GET_CODE;

                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 确认登录
     */
    private void sureLogin() {

        bt_login_sure.setEnabled(false);
        int status = isNull();
        switch (status){
            case 6:
                bt_login_sure.setEnabled(true);
                Toast.makeText(LoginActivity.this,"验证码已失效，请重新获取",Toast.LENGTH_SHORT).show();
                break;
            case 5:
                bt_login_sure.setEnabled(true);
                Toast.makeText(LoginActivity.this,"输入的验证码错误",Toast.LENGTH_SHORT).show();
                break;
            case 4:
                bt_login_sure.setEnabled(true);
                Toast.makeText(LoginActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                bt_login_sure.setEnabled(true);
                Toast.makeText(LoginActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                bt_login_sure.setEnabled(true);
                Toast.makeText(LoginActivity.this,"手机号输入框不能为空",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                bt_login_sure.setEnabled(true);
                Toast.makeText(LoginActivity.this,"验证码输入框不能为空",Toast.LENGTH_SHORT).show();
                break;
            case 0:
                loginToGetCollect();
                break;
        }

    }
    /**
     * 保存信息到本地
     */
    private void saveListAndPhone(CancelCollect cancelCollect){
        String list = "-1";
        if (cancelCollect.error == 0){

        }else{
            if (cancelCollect.list.size()>0){
                for (int i = 0; i < cancelCollect.list.size(); i++) {
                    if (i==0){
                        list = cancelCollect.list.get(i).uid;
                    }else{
                        list = list + "_" + cancelCollect.list.get(i).uid;
                    }
                }
            }else{

            }
        }
        //保存用户登录信息
        SharedPreferences sharedPreferences = getSharedPreferences("lws",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", code.phone);
        editor.putString("list", list);
        editor.commit();

        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }
    /**
     * 允许登录 获取用户收藏的uid列表
     */
    private void loginToGetCollect(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                CollectionUidListServlet collectionUidListServlet
                        = new CollectionUidListServlet();
                String json = collectionUidListServlet.getJson(code.phone);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else {
                    Gson gson = new Gson();
                    System.out.println("获取uid：" + json);
                    CancelCollect cancelCollect = gson.fromJson(json, CancelCollect.class);

                    msg.what = GET_COLLECT_UID;
                    msg.obj = cancelCollect;
                }
                handler.sendMessage(msg);
            }
        }).start();
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
        String edt_phone = edt_login_phone.getText().toString();
        String edt_code = edt_login_code.getText().toString();
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

            tv_login_getcode.setTextColor(getResources().getColor(R.color.color_red));
            tv_login_getcode.setEnabled(true);
        }else{

            tv_login_getcode.setTextColor(getResources().getColor(R.color.color_line));
            tv_login_getcode.setEnabled(false);
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
        tv_login_back.setOnClickListener(this);
        bt_login_sure.setOnClickListener(this);
        tv_login_getcode.setOnClickListener(this);


        /**
         * 监听手机号输入框是否为空
         */
        TextWatcher watcher_phone = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (ing == false){
                    if (edt_login_phone.getText().toString().equals("")){
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
                if (edt_login_code.getText().toString().equals("")
                        || edt_login_phone.getText().toString().equals("")){
                    bt_login_sure.setBackgroundResource(R.drawable.btn_gray);
                    bt_login_sure.setEnabled(false);
                }else{
                    bt_login_sure.setBackgroundResource(R.drawable.btn_accent);
                    bt_login_sure.setEnabled(true);
                }
            }
        };

        edt_login_phone.addTextChangedListener(watcher_phone);
        edt_login_code.addTextChangedListener(watcher_code);
    }

    private void initView() {

    }

    private void init() {
        tv_login_back = (ImageView) findViewById(R.id.tv_login_back);
        bt_login_sure = (Button) findViewById(R.id.bt_login_sure);
        tv_login_getcode = (TextView) findViewById(R.id.tv_login_getcode);
        edt_login_code = (EditText) findViewById(R.id.edt_login_code);
        edt_login_phone = (EditText) findViewById(R.id.edt_login_phone);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login_back:
                finish();
                break;
            case R.id.tv_login_getcode:
                code = new Code();//实例化验证码class
                getCode(edt_login_phone.getText().toString());
                changeCodeText();
                break;
            case R.id.bt_login_sure:
                sureLogin();
                break;
        }
    }



}
