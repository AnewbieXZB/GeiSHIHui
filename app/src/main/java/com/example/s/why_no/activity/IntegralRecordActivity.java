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
import com.example.s.why_no.adapter.IntegralRecordListAdapter;
import com.example.s.why_no.bean.IntegralRecord;
import com.example.s.why_no.servlet.GetIntegralRecordServlet;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.google.gson.Gson;

/**
 * Created by S on 2016/11/7.
 */

public class IntegralRecordActivity extends Activity implements View.OnClickListener {

    private ImageView imv_integral_record_back;
    private TextView tv_integral_record_all;
    private RecyclerView rv_integral_record_details;
    private LinearLayoutManager linearLayoutManager;
    private IntegralRecordListAdapter iAdapter;
    private int lastVisibleItem ;
    private int firstVisibeItem ;

    private final int GET_RECORD = 5;
    private final int ERROR_REQIEST = 99;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_RECORD:
                    IntegralRecord integralRecord = (IntegralRecord) msg.obj;
                    if (integralRecord.error == 1){
                        buildList(integralRecord);
                    }else if(integralRecord.error == 0){
                        Toast.makeText(IntegralRecordActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
                    }else if(integralRecord.error == 2){

                    }
                    break;
                case ERROR_REQIEST:
                    Toast.makeText(IntegralRecordActivity.this,"网络错误",Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_record);

        init();
        initView();
        initEvent();

    }

    /**
     * 构筑积分记录的列表
     */
    private void buildList(IntegralRecord integralRecord){



        int all = integralRecord.inte.get(0).integral;
        tv_integral_record_all.setText(all + "");

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        rv_integral_record_details.setHasFixedSize(true);
        rv_integral_record_details.setNestedScrollingEnabled(false);//false r1滑动不卡顿
        rv_integral_record_details.setLayoutManager(linearLayoutManager);

        iAdapter = new IntegralRecordListAdapter(integralRecord.branch,this);
        rv_integral_record_details.setAdapter(iAdapter);




    }

    public void getRecord(final String phone) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                GetIntegralRecordServlet getIntegralRecordServlet = new GetIntegralRecordServlet();
                String json = getIntegralRecordServlet.getJson(phone);
                System.out.println("积分记录的json:" + json);

                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQIEST;
                }else {
                    Gson gson = new Gson();
                    IntegralRecord integralRecord = gson.fromJson(json, IntegralRecord.class);

                    msg.what = GET_RECORD;
                    msg.obj = integralRecord;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void initEvent() {
        imv_integral_record_back.setOnClickListener(this);

    }

    private void initView() {
        ShareLoginData shareLoginData = new ShareLoginData(this);
        String phone = shareLoginData.isLogin();
        if (phone.equals("-1")){

        }else{
            getRecord(phone);
        }
    }

    private void init() {
        imv_integral_record_back = (ImageView) findViewById(R.id.imv_integral_record_back);
        tv_integral_record_all = (TextView) findViewById(R.id.tv_integral_record_all);
        rv_integral_record_details = (RecyclerView) findViewById(R.id.rv_integral_record_details);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_integral_record_back:
                finish();
                break;
        }
    }

}
