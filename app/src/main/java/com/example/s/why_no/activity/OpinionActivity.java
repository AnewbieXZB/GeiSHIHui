package com.example.s.why_no.activity;

import android.app.Activity;
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
import com.example.s.why_no.bean.Status;
import com.example.s.why_no.servlet.OpinionServlet;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.google.gson.Gson;

/**
 * Created by S on 2016/11/7.
 */

public class OpinionActivity extends Activity implements View.OnClickListener{

    private ImageView imv_opinion_back;
    private Button bt_opinion_sure;
    private EditText edt_opinion_test;
    private int[] opinion_ids = {R.id.tv_opinion_type01,R.id.tv_opinion_type02,
            R.id.tv_opinion_type03,R.id.tv_opinion_type04};
    private TextView[] opinion_tvs = new TextView[opinion_ids.length];
    private String type = "";//问题类型
    private String test = "";//问题详细说明
    private final int STATUS = 656;
    private final int ERROR_REQUEST = 78;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case STATUS:

                    Status status = (Status) msg.obj;
                    if (status.error == 1){
                        bt_opinion_sure.setEnabled(true);
                        Toast.makeText(OpinionActivity.this,"提交成功，感谢您的宝贵意见",Toast.LENGTH_SHORT).show();
                        finish();
                    }else if(status.error == 0){
                        bt_opinion_sure.setEnabled(true);
                        Toast.makeText(OpinionActivity.this,"提交失败，状态码： 0 ",Toast.LENGTH_SHORT).show();
                    }else{
                        bt_opinion_sure.setEnabled(true);
                        Toast.makeText(OpinionActivity.this,"提交失败，",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ERROR_REQUEST:
                    Toast.makeText(OpinionActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    bt_opinion_sure.setEnabled(true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);

        init();
        initView();
        initEvent();

    }

    /**
     * 判断是否已经选择问题类型
     * 判断详情是否已填
     * 0 允许提交
     * 1 请选择问题类型
     * 2 详情不能为空
     * 3 还没登录
     */
    private int isNull(){

        int mess = 0;
        if (type.equals("")){
            mess = 1;
        }else if(test.equals("")){
            mess = 2;
        }
        return mess;
    }
    /**
     * 提交反馈信息
     */
    private void feedback(){

        bt_opinion_sure.setEnabled(false);
        switch (isNull()){
            case 2:
                Toast.makeText(OpinionActivity.this,"问题内容不能为空",Toast.LENGTH_SHORT).show();
                bt_opinion_sure.setEnabled(true);
                break;
            case 1:
                Toast.makeText(OpinionActivity.this,"请选择问题类型",Toast.LENGTH_SHORT).show();
                bt_opinion_sure.setEnabled(true);
                break;
            case 0:
                sureFeedback();
                break;
        }

    }


    private void sureFeedback(){

        final ShareLoginData share = new ShareLoginData(this);
        final String phone = share.isLogin();

        if (phone.equals("-1")){
            Toast.makeText(OpinionActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {

                    OpinionServlet opinionServlet = new OpinionServlet();
                    String json = opinionServlet.getJson(phone,type,test);
                    System.out.println("反馈json:" + json);
                    Message msg = new Message();
                    if (json.equals("error")){
                        msg.what = ERROR_REQUEST;
                    }else {
                        Gson gson = new Gson();
                        Status status = gson.fromJson(json, Status.class);

                        msg.what = STATUS;
                        msg.obj = status;

                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }


    }


    /**
     * 根据用户点了问题类型而更改被点击的textview颜色
     * @param index
     */
    private void selectIndex(int index) {

        for (int i = 0; i < opinion_ids.length; i++) {
            if (i == index) {
                opinion_tvs[i].setTextColor(getResources().getColor(R.color.tomato));
                opinion_tvs[i].setEnabled(false);
                type = opinion_tvs[i].getText().toString();
            }else {
                opinion_tvs[i].setTextColor(getResources().getColor(R.color.color_grey));
                opinion_tvs[i].setEnabled(true);
            }
        }
    }

    private void initEvent() {
        imv_opinion_back.setOnClickListener(this);
        bt_opinion_sure.setOnClickListener(this);
        for (int i = 0; i < opinion_tvs.length; i++) {
            opinion_tvs[i].setOnClickListener(this);
        }


        /**
         * 监听详情是否为空
         */
        TextWatcher watcher_test = new TextWatcher() {

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
                test = edt_opinion_test.getText().toString();

            }
        };
        edt_opinion_test.addTextChangedListener(watcher_test);
    }

    private void initView() {


    }

    private void init() {
        imv_opinion_back = (ImageView) findViewById(R.id.imv_opinion_back);
        bt_opinion_sure = (Button) findViewById(R.id.bt_opinion_sure);
        edt_opinion_test = (EditText) findViewById(R.id.edt_opinion_test);

        for (int i = 0; i < opinion_tvs.length; i++) {
            opinion_tvs[i] = (TextView) findViewById(opinion_ids[i]);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_opinion_back:
                finish();
                break;
            case R.id.bt_opinion_sure:
                feedback();
                break;
            case R.id.tv_opinion_type01:
                selectIndex(0);
                break;
            case R.id.tv_opinion_type02:
                selectIndex(1);
                break;
            case R.id.tv_opinion_type03:
                selectIndex(2);
                break;
            case R.id.tv_opinion_type04:
                selectIndex(3);
                break;
        }
    }
}

