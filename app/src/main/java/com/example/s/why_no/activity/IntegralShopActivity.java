package com.example.s.why_no.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.adapter.IntegralGoodsListAdapter;
import com.example.s.why_no.bean.IntegralGoods;
import com.example.s.why_no.bean.IntegralShop;
import com.example.s.why_no.servlet.IntegralShopServlet;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S on 2016/11/7.
 */

public class IntegralShopActivity extends Activity implements View.OnClickListener {

    private ImageView imv_integral_shop_back;
    private RecyclerView rv_integral_shop_list;
    private List<IntegralGoods> allList;
    private IntegralGoodsListAdapter iAdapter;
    private LinearLayoutManager linearLayoutManager;
    private final int GET_SHOP = 45;
    private final int ERROR_REQUEST = 98;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_SHOP:
                    IntegralShop integralShop = (IntegralShop) msg.obj;
                    if (integralShop.error == 0) {

                    } else {
                        buildList(integralShop);
                    }

                    break;
                case ERROR_REQUEST:
                    Toast.makeText(IntegralShopActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_shop);

        init();
        initView();
        initEvent();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * 构筑积分商城列表
     *
     * @param integralShop
     */
    private void buildList(IntegralShop integralShop) {

        allList = new ArrayList<IntegralGoods>();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        rv_integral_shop_list.setHasFixedSize(true);
        rv_integral_shop_list.setNestedScrollingEnabled(false);//false r1滑动不卡顿
        rv_integral_shop_list.setLayoutManager(linearLayoutManager);

        allList.addAll(integralShop.integral);
        iAdapter = new IntegralGoodsListAdapter(allList, this);
        rv_integral_shop_list.setAdapter(iAdapter);

        iAdapter.setOnItemClickLitener(new IntegralGoodsListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(IntegralShopActivity.this,IntegralGoodsDetailsActivity.class);
                Gson gson = new Gson();
                String integralGoods = gson.toJson(allList.get(position));

//                String img = allList.get(position).img;
//                String name = allList.get(position).name;
//                String details = allList.get(position).details;
//                String people = allList.get(position).people;
//                String number = allList.get(position).number;
//                int id = allList.get(position).id;
//                int surplus = allList.get(position).surplus;
//                int money = allList.get(position).money;
//                int need = allList.get(position).need;
//                intent.putExtra("img",img);
//                intent.putExtra("name",name);
//                intent.putExtra("details",details);
//                intent.putExtra("people",people);
//                intent.putExtra("number",number);
//                intent.putExtra("id",id);
//                intent.putExtra("surplus",surplus);
//                intent.putExtra("money",money);
//                intent.putExtra("need",need);
                intent.putExtra("integralGoods",integralGoods);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    /**
     * 获取积分商品列表
     */
    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                IntegralShopServlet integralShopServlet = new IntegralShopServlet();
                String json = integralShopServlet.getJson();
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else {
                    Gson gson = new Gson();

                    IntegralShop integralShop = gson.fromJson(json, IntegralShop.class);


                    msg.obj = integralShop;
                    msg.what = GET_SHOP;

                }
                handler.sendMessage(msg);
            }
        }).start();

    }

    private void initEvent() {
        imv_integral_shop_back.setOnClickListener(this);
    }

    private void initView() {

        getData();

    }

    private void init() {
        imv_integral_shop_back = (ImageView) findViewById(R.id.imv_integral_shop_back);
        rv_integral_shop_list = (RecyclerView) findViewById(R.id.rv_integral_shop_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_integral_shop_back:
                finish();
                break;
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("IntegralShop Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
