package com.example.s.why_no.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.s.why_no.R;

/**
 * Created by S on 2016/11/7.
 */

public class CustomerActivity extends Activity implements View.OnClickListener{

    private ImageView imv_customer_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);

        init();
        initView();
        initEvent();

    }

    private void initEvent() {
        imv_customer_back.setOnClickListener(this);
    }

    private void initView() {

    }

    private void init() {
        imv_customer_back = (ImageView) findViewById(R.id.imv_customer_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_customer_back:
                finish();
                break;
        }
    }
}
