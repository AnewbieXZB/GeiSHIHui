package com.example.s.why_no.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.adapter.ExchangeRecordListAdapter;
import com.example.s.why_no.bean.ExchangeRecord;
import com.example.s.why_no.servlet.ExchangeRecordServlet;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.google.gson.Gson;

/**
 * Created by S on 2016/11/7.
 */

public class IntegralExchangeActivity extends Activity implements View.OnClickListener{

    private ImageView imv_integral_exchange_back;
    private RecyclerView rv_integral_exchange_details;
    private TextView tv_integral_exchange_null;
    private LinearLayoutManager linearLayoutManager;
    private ExchangeRecordListAdapter eAdapter;

    private final int ERROR_REQUEST = 45;
    private final int GET_RECORD = 63;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_RECORD:
                    ExchangeRecord exchangeRecord = (ExchangeRecord) msg.obj;
                    if (exchangeRecord.error == 1){
                        buildList(exchangeRecord);
                    }else{
                        tv_integral_exchange_null.setVisibility(View.VISIBLE);
                    }
                    break;
                case ERROR_REQUEST:
                    Toast.makeText(IntegralExchangeActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_exchange);

        init();
        initView();
        initEvent();

    }

    /**
     * 构筑兑换记录
     * @param exchangeRecord
     */
    private void buildList(ExchangeRecord exchangeRecord) {

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        rv_integral_exchange_details.setHasFixedSize(true);
        rv_integral_exchange_details.setNestedScrollingEnabled(false);//false r1滑动不卡顿
        rv_integral_exchange_details.setLayoutManager(linearLayoutManager);

        eAdapter = new ExchangeRecordListAdapter(exchangeRecord.record,this);
        rv_integral_exchange_details.setAdapter(eAdapter);

    }

    /**
     * 获取兑换记录
     */
    private void getRecord() {

        ShareLoginData share = new ShareLoginData(this);
        final String phone = share.isLogin();
        if (phone.equals("-1")){

        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ExchangeRecordServlet exchangeRecordServlet
                            = new ExchangeRecordServlet();
                    String json = exchangeRecordServlet.getJson(phone);
                    System.out.println("兑换记录的json   " + phone);
                    Message msg = new Message();
                    if (json.equals("error")){
                        msg.what = ERROR_REQUEST;
                    }else{
                        Gson gson = new Gson();
                        ExchangeRecord exchangeRecord = gson.fromJson(json,ExchangeRecord.class);

                        msg.what = GET_RECORD;
                        msg.obj = exchangeRecord;

                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }

    private void initEvent() {
        imv_integral_exchange_back.setOnClickListener(this);
    }

    private void initView() {
        getRecord();
    }



    private void init() {
        tv_integral_exchange_null = (TextView) findViewById(R.id.tv_integral_exchange_null);
        imv_integral_exchange_back = (ImageView) findViewById(R.id.imv_integral_exchange_back);
        rv_integral_exchange_details = (RecyclerView) findViewById(R.id.rv_integral_exchange_details);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_integral_exchange_back:
                finish();
                break;
        }
    }
}
