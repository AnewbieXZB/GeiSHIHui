package com.example.s.why_no.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.s.why_no.R;

/**
 * Created by S on 2016/11/7.
 */

public class IntegralGoodsActivity extends Activity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_goods_details);

        init();
        initView();
        initEvent();

    }

    private void initEvent() {

    }

    private void initView() {

    }

    private void init() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
