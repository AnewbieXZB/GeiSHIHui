package com.example.s.why_no.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.activity.DetailsActivity;
import com.example.s.why_no.adapter.GoodsListAdapter;
import com.example.s.why_no.bean.Goods;
import com.example.s.why_no.bean.Shop;
import com.example.s.why_no.servlet.CategoryGoodsListServlet;
import com.example.s.why_no.utils_window.ManageWindow;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S on 2016/11/4.
 */

public class Fragment_01_digital extends Fragment {

    private String category = "digital";//数码

    private View mView;
    private Context context;

    private PullToRefreshScrollView sc_fragment_category;
    private RecyclerView rv_fragment_category;
//    private SwipeRefreshLayout sf_frgament_category;
    private GridLayoutManager gridLayoutManager;
    private GoodsListAdapter gAdapter;
    private List<Goods> tempList;
    private List<Goods> allList;
    private final int FIRST_DATA = 205;
    private final int MORE_DATA = 456;
    private boolean isRefreshing = false;//标记正在加载
    private boolean allow = true;//true允许加载更多 false 不允许加载更多
    private int page = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FIRST_DATA:
                    Shop frist_data = (Shop) msg.obj;
                    if (frist_data.error == 1){
                        page+=20;
                        buildGoodsList(frist_data);
                    }else{
                        Toast.makeText(context,"没有更多商品了",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MORE_DATA:
                    Shop more_data = (Shop) msg.obj;
                    if (more_data.error == 1){
                        page+=20;
                        allList.addAll(more_data.goods);
                        gAdapter.notifyDataSetChanged();
//                    v_fragment_main_loading.setVisibility(getView().GONE);

                    }else{
                        Toast.makeText(context,"没有更多商品了",Toast.LENGTH_SHORT).show();
                    }
                    allow = true;
                    isRefreshing = false;
                    sc_fragment_category.onRefreshComplete();
                    break;
            }
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_category, null);
        context = getActivity().getApplicationContext();


        init();
        initView();
        return mView;
    }

    /**
     * 构筑商品列表
     */
    private void buildGoodsList(Shop frist_data) {

        allList = new ArrayList<Goods>();

        gridLayoutManager = new GridLayoutManager(context,2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rv_fragment_category.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        gridLayoutManager.setAutoMeasureEnabled(true);
        rv_fragment_category.setHasFixedSize(true);
        rv_fragment_category.setNestedScrollingEnabled(false);//false r1滑动不卡顿
        allList.addAll(frist_data.goods);

        ManageWindow mana = new ManageWindow();
        int windowWidth = mana.getWidth(context);
        int windowHeight = mana.getHeight(context);
        gAdapter = new GoodsListAdapter(allList,context,windowWidth,windowHeight);
        rv_fragment_category.setAdapter(gAdapter);


        sc_fragment_category.getLoadingLayoutProxy(false, true)
                .setPullLabel("上拉加载更多");
        sc_fragment_category.getLoadingLayoutProxy(false, true)
                .setRefreshingLabel("正在载入...");
        sc_fragment_category.getLoadingLayoutProxy(false, true)
                .setReleaseLabel("放开以刷新");

        sc_fragment_category.getLoadingLayoutProxy(true, false)
                .setPullLabel("下拉刷新");
        sc_fragment_category.getLoadingLayoutProxy(true, false)
                .setRefreshingLabel("正在刷新...");
        sc_fragment_category.getLoadingLayoutProxy(true, false)
                .setReleaseLabel("放开以刷新");

        isRefreshing = false;

        sc_fragment_category
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        if (isRefreshing)
                            return;
                        isRefreshing = true;
                        if (sc_fragment_category.isHeaderShown()) {

                        }
                        if (sc_fragment_category.isFooterShown()) {
                            getMoreData();
                        }

                    }
                });

        gAdapter.setOnItemClickLitener(new GoodsListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("id",allList.get(position).id);
                intent.putExtra("ification",allList.get(position).ification);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        rv_fragment_category.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * 获取下一页数据
     */
    public void getMoreData() {
//        tempList.clear();

        allow = false;
        new Thread(new Runnable() {
            @Override
            public void run() {

                CategoryGoodsListServlet main = new CategoryGoodsListServlet();
                String json = main.getJson(category,page);
                if (json.equals("error")){
                    Toast.makeText(context,"网络错误",Toast.LENGTH_SHORT).show();
                }else {
                    Gson gson = new Gson();
                    System.out.println("加载更多的json:" + json);
                    Shop data = gson.fromJson(json, Shop.class);

                    Message msg = new Message();
                    msg.what = MORE_DATA;
                    msg.obj = data;
                    handler.sendMessage(msg);
                }

            }
        }).start();

    }
    /**
     * 获取第一次的数据
     */
    private void getFirstData(){

//        tempList = new ArrayList<Goods>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                CategoryGoodsListServlet categoryGoodsListServlet = new CategoryGoodsListServlet();
                String json = categoryGoodsListServlet.getJson(category,page);
                Gson gson = new Gson();
                Shop shop = gson.fromJson(json,Shop.class);

                Message msg = new Message();
                msg.obj = shop;
                msg.what = FIRST_DATA;
                handler.sendMessage(msg);
            }
        }).start();
    }
    private void initView() {
        getFirstData();
    }

    private void init() {
        sc_fragment_category = (PullToRefreshScrollView) mView.findViewById(R.id.sc_fragment_category);
        rv_fragment_category = (RecyclerView) mView.findViewById(R.id.rv_fragment_category);


    }


}
