package com.example.s.why_no.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.activity.DetailsActivity;
import com.example.s.why_no.activity.MainActivity;
import com.example.s.why_no.adapter.GoodsListAdapter;
import com.example.s.why_no.adapter.TopCategoryAdapter;
import com.example.s.why_no.bean.Advertisement;
import com.example.s.why_no.bean.Goods;
import com.example.s.why_no.bean.Shop;
import com.example.s.why_no.bean.Thumbnail;
import com.example.s.why_no.servlet.MainAdvertisementServlet;
import com.example.s.why_no.servlet.MainGoodsListServlet;
import com.example.s.why_no.utils_window.ManageWindow;
import com.example.s.why_no.view.BanRecylerView;
import com.example.s.why_no.view.ChildViewPager;
import com.example.s.why_no.view.CustomProgress;
import com.example.s.why_no.viewpageradapter.AdvertisementPagerAdapter;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S on 2016/10/30.
 */

public class Fragment_01_main extends Fragment implements View.OnClickListener {

    private DisplayImageOptions options = options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
            .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private View mView;
    private Context context;
    private static Fragment instance;

    private PullToRefreshScrollView sc_fragment_main;
    private ImageView imv_fragmen_01_return_top;

    /**
     * 顶部文字分类导航栏
     */
    private RecyclerView rv_fragment_main_top_categroy;
    private LinearLayoutManager linearLayoutManager;
    private TopCategoryAdapter tAdapter;

    /**
     * 广告viewpager部分
     */
    private ChildViewPager vp_fragment_main_advertisement;
    private List<View> adList;
    private List<Thumbnail> advList;
    private AdvertisementPagerAdapter adAdapter;
    private final int GET_ADVERTISEMENT = 20;
    private final int ADVERTISEMENT_CHANGE_TIME = 4000;
    private final int MSG_ADVERTISEMENT = 156;

    /**
     * 分类标签部分
     */
    private ImageView fragment_main_category_clothes;
    private ImageView fragment_main_category_baby;
    private ImageView fragment_main_category_home;
    private ImageView fragment_main_category_shoes;
    private ImageView fragment_main_category_ornaments;
    private ImageView fragment_main_category_stationery;
    private ImageView fragment_main_category_digital;
    private ImageView fragment_main_category_beauty;
    private ImageView fragment_main_category_food;
    private ImageView fragment_main_category_other;

    /**
     * 商品列表部分
     */
    private int numerical = 0;//传递到服务器的页码

    private String tag = "default";//标签
    private String order = "default";//排序
    private int ci;//记录 已经使用了多少次
    private int shang ;//求商 记录取得消息能使用多少次
    private int yu ; //求余 记录剩下多少条

    private final int ORDER_RESULT = 99;
    private final int FIRST_DATA = 101;//第一次加载
    private final int MORE_DATA = 102;//加载更多
    private BanRecylerView rv_fragment_main_goodslist;
    private GoodsListAdapter gAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<Goods> tempList;
    private List<Goods> allList;
    private List<String> list;
    private boolean isRefreshing = false;//标记正在加载
    private boolean allow = true;//true允许加载更多 false 不允许加载更多

//    private View v_fragment_main_loading;

    private final int ERROR_REQUEST = 54;
    public Fragment_01_main() {

    }
    public static Fragment_01_main getInstance() {
        Fragment_01_main newNewsFragment = new Fragment_01_main();
        return newNewsFragment;
    }



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FIRST_DATA:
                    CustomProgress.disDia();
                    sc_fragment_main.setVisibility(getView().VISIBLE);

                    Shop frist_data = (Shop) msg.obj;
                    if (frist_data.error == 1){
                        buildGoodsList(frist_data);
                    }else{
                        Toast.makeText(context,"没有更多商品了",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case MSG_ADVERTISEMENT:
//                    scrollplay();//广告轮播
                    vp_fragment_main_advertisement.setCurrentItem
                            (vp_fragment_main_advertisement.getCurrentItem() + 1);
                    handler.sendEmptyMessageDelayed(MSG_ADVERTISEMENT, ADVERTISEMENT_CHANGE_TIME);// 递归
                    break;
                case MORE_DATA:
                    Shop more_data = (Shop) msg.obj;
                    if (more_data.error == 1){
//                        allList.addAll(more_data.goods);
//                        gAdapter.notifyDataSetChanged();
                        buildMore( more_data);
                    }else{
                        Toast.makeText(context,"没有更多商品了",Toast.LENGTH_SHORT).show();
                    }
                    allow = true;
                    isRefreshing = false;
                    sc_fragment_main.onRefreshComplete();

                    break;
                case GET_ADVERTISEMENT:
                    Advertisement advertisement = (Advertisement) msg.obj;
                    if (advertisement.error == 1){
                        buildViewpager(advertisement);
                    }
                    break;
                case ORDER_RESULT:

                    Shop orderShop = (Shop) msg.obj;
                    if(orderShop.error == 1){
                        numerical = 0;
                        allList.clear();
                        buildNew(orderShop);
                    }else{
                        Toast.makeText(context,"排序出错",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case ERROR_REQUEST:
                    Toast.makeText(context,"网络错误",Toast.LENGTH_LONG).show();
                    CustomProgress.disDia();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_01_main, null);
        context = getActivity().getApplicationContext();

        CustomProgress.show(getActivity(), "正在加载...", false,
                null);//加载中的动画


        init();

        initView();
        initEvent();


        return mView;
    }


    /**
     * 排序后重新构筑列表
     */
    private void buildNew(Shop shop) {

        allList.clear();
        tempList.clear();
        tempList.addAll(shop.goods);

        ci = 0;
        shang = shop.goods.size()/20; //记录每次取出20条可以使用多少次
        yu = shop.goods.size()%20;//记录剩余多少条

        Log.e("结果条数：" , shop.goods.size() + "");
        if (shang >= 1){
            for (int i = ci*20; i < ci*20 + 20; i++) {
                allList.add(shop.goods.get(i));
            }
            ci++;
        }else {
            if (yu >= 1){
                for (int i = 0; i < yu; i++) {
                    allList.add(shop.goods.get(i));
                }
                yu = 0;
            }
            ci = shang+1;
        }
        gAdapter.notifyDataSetChanged();

        numerical += 100;
//        isReady = 1;
    }

    /**
     * 请求（排序）刷新数据
     */
    private void requestOrder() {

//        if (isReady != 2){//用户只选择了一项，无法构成排序条件
//
//        }else{
        numerical = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainGoodsListServlet mainGoodsListServlet
                        = new MainGoodsListServlet();


                System.out.println("排序刷新：numerical  " + numerical);
                System.out.println("排序刷新：tag  " + tag);
                System.out.println("排序刷新：order  " + order);

                String json = mainGoodsListServlet.getJson(numerical,tag,order);

                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else{
                    Gson gson = new Gson();
                    Shop orderShop = gson.fromJson(json,Shop.class);

                    msg.what = ORDER_RESULT;
                    msg.obj = orderShop;

                }
                handler.sendMessage(msg);
            }
        }).start();
//        }

    }


    /**
     * 构筑商品列表
     */
    private void buildGoodsList(Shop data){


        allList = new ArrayList<Goods>();
        tempList = new ArrayList<Goods>();
        tempList.addAll(data.goods);

        ci = 0;
        shang = data.goods.size()/20; //记录每次取出20条可以使用多少次
        yu = data.goods.size()%20;//记录剩余多少条

        Log.e("结果条数：" , data.goods.size() + "");
        if (shang >= 1){
            for (int i = ci*20; i < ci*20 + 20; i++) {
                allList.add(data.goods.get(i));
            }
            ci++;
        }else {
            if (yu >= 1){
                for (int i = 0; i < yu; i++) {
                    allList.add(data.goods.get(i));
                }
                yu = 0;
            }
            ci = shang+1;
        }

        numerical += 100;

        gridLayoutManager = new GridLayoutManager(context,2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rv_fragment_main_goodslist.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        gridLayoutManager.setAutoMeasureEnabled(true);

        rv_fragment_main_goodslist.setHasFixedSize(true);
        rv_fragment_main_goodslist.setNestedScrollingEnabled(false);//false r1滑动不卡顿
        rv_fragment_main_goodslist.setFocusable(false);//scrollview嵌套gridview一定要加
        //因为当获取完数据并调用notifyDataSetChanged();
        // 后 ScrollView自动滚到了最底部,也就是GridView所在的位置.

        ManageWindow mana = new ManageWindow();
        int windowWidth = mana.getWidth(context);
        int windowHeight = mana.getHeight(context);
        gAdapter = new GoodsListAdapter(allList,context,windowWidth,windowHeight);
        rv_fragment_main_goodslist.setAdapter(gAdapter);



        sc_fragment_main.getLoadingLayoutProxy(false, true)
                .setPullLabel("上拉加载更多");
        sc_fragment_main.getLoadingLayoutProxy(false, true)
                .setRefreshingLabel("正在载入...");
        sc_fragment_main.getLoadingLayoutProxy(false, true)
                .setReleaseLabel("放开以刷新");

        sc_fragment_main.getLoadingLayoutProxy(true, false)
                .setPullLabel("下拉刷新");
        sc_fragment_main.getLoadingLayoutProxy(true, false)
                .setRefreshingLabel("正在刷新...");
        sc_fragment_main.getLoadingLayoutProxy(true, false)
                .setReleaseLabel("放开以刷新");

        isRefreshing = false;

        sc_fragment_main
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        if (isRefreshing)
                            return;
                        isRefreshing = true;
                        if (sc_fragment_main.isHeaderShown()) {

                        }
                        if (sc_fragment_main.isFooterShown()) {

                            Log.e("shang",shang + "");
                            Log.e("ci",ci + "");
                            Log.e("yu",yu + "");

                            if(shang <=  ci){//次数用完了，
                                //判断余数 余数也为空则需要请求服务器重新获得
                                if (yu >= 1){
                                    useYu();//添加余数
                                }else{
                                    getMoreData();
                                }

                            }else{//次数没用完 从获取到的allList继续取出
                                useMore();
                            }
                            isRefreshing = false;
                            sc_fragment_main.onRefreshComplete();
                        }

                    }
                });
//        sc_fragment_main.getView();
//        sc_fragment_main.setOnScrollListener(new LazyScrollView.OnScrollListener() {
//            @Override
//            public void onBottom() {
//
//                if (allow){
//                    getMoreData();
//                }
//
//            }
//
//            @Override
//            public void onTop() {
//
//            }
//
//            @Override
//            public void onScroll() {
//
//            }
//        });


        gAdapter.setOnItemClickLitener(new GoodsListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

//                Intent intent = new Intent(context, NetActivity.class);
//                intent.putExtra("url",allList.get(position).extension);
//                startActivity(intent);
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("id",allList.get(position).id);
                intent.putExtra("ification",allList.get(position).ification);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }
    /**
     * 构筑更多的商品
     * @param tempShop
     */
    private void buildMore(Shop tempShop) {

        Log.e("第二次从服务器获取的" ,tempShop.goods.size() + "");
        tempList.addAll(tempShop.goods);

        ci = 0;
        shang = tempShop.goods.size()/20;
        yu = tempShop.goods.size()%20;

        Log.e("第二次从服务器获取的求出shang" ,shang + "");
        Log.e("第二次从服务器获取的求出yu" ,yu + "");

        if (shang >= 1){
            for (int i = ci*20 ; i < ci*20 + 20; i++) {
                allList.add(tempShop.goods.get(i));
            }
            ci++;
        }else {
            if (yu >= 1){
                for (int i = 0; i < yu; i++) {
                    allList.add(tempShop.goods.get(i));
                }
                yu = 0;
            }
            ci = shang+1;
        }

        numerical += 100;
        gAdapter.notifyDataSetChanged();

    }

    //次数没用完 从获取到的allList继续取出
    private void useMore() {
        if (shang >= 1){
            for (int i = ci*20 ; i < ci*20 + 20; i++) {
                allList.add(tempList.get(i));
            }
            ci++;
        }else {
            if (yu >= 1){
                for (int i = 0; i < yu; i++) {
                    allList.add(tempList.get(i));
                }
                yu = 0;
            }
            ci = shang+1;
        }

        gAdapter.notifyDataSetChanged();

    }
    /**
     * 使用余数 添加到列表
     */
    private void useYu() {
        if (yu >= 1){
            for (int i = tempList.size() - yu; i < tempList.size(); i++) {
                allList.add(tempList.get(i));
            }
            yu = 0;
        }
        ci = shang+1;
        gAdapter.notifyDataSetChanged();
    }
    /**
     * 获取商品列表第一次的数据
     */
    private void getFirstData() {

//        tempList = new ArrayList<Goods>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                MainGoodsListServlet main = new MainGoodsListServlet();
                String json = main.getJson(numerical,tag,order);
                System.out.println("numerical  " + numerical);
                System.out.println("tag  " + tag);
                System.out.println("order  " + order);

                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else {

                    Gson gson = new Gson();
                    Shop data = gson.fromJson(json, Shop.class);


                    msg.what = FIRST_DATA;
                    msg.obj = data;

                }
                handler.sendMessage(msg);
            }
        }).start();



    }
    /**
     * 获取商品列表下一页的数据
     */
    private void getMoreData() {

        tempList.clear();

        allow = false;
        new Thread(new Runnable() {
            @Override
            public void run() {

                MainGoodsListServlet main = new MainGoodsListServlet();
                String json = main.getJson(numerical,tag,order);
                System.out.println("numerical  " + numerical);
                System.out.println("tag  " + tag);
                System.out.println("order  " + order);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else {
                    Gson gson = new Gson();
                    Shop data = gson.fromJson(json, Shop.class);
                    msg.what = MORE_DATA;
                    msg.obj = data;
                }
                handler.sendMessage(msg);
            }
        }).start();


    }
    /**
     * 广告页滚动播放
     */
    private void scrollplay(){
//        int index = vp_fragment_main_advertisement.getCurrentItem();
//        // 如果已经到了最尾项 则返回最初端（-1）
//        if (index == adList.size() - 1) {
//            index = -1;
//        }
//        vp_fragment_main_advertisement.setCurrentItem(index + 1);
//
//        handler.sendEmptyMessageDelayed(MSG_ADVERTISEMENT, ADVERTISEMENT_CHANGE_TIME);// 递归

        vp_fragment_main_advertisement.setCurrentItem
                (vp_fragment_main_advertisement.getCurrentItem() + 1);
        handler.sendEmptyMessageDelayed(MSG_ADVERTISEMENT, ADVERTISEMENT_CHANGE_TIME);// 递归

    }

    /**
     * 获取广告轮播的数据
     */
    public void getAdvertisementData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                MainAdvertisementServlet mainAdvertisementServlet = new MainAdvertisementServlet();
                String json = mainAdvertisementServlet.getJson();
                Message msg = new Message();
//                Log.e("广告：   " ,json);
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else {
                    Gson gson = new Gson();
                    Advertisement advertisement = gson.fromJson(json,Advertisement.class);
                    msg.obj = advertisement;
                    msg.what = GET_ADVERTISEMENT;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void buildViewpager(Advertisement advertisement){



        adList = new ArrayList<View>();
        advList = new ArrayList<Thumbnail>();
        advList.addAll(advertisement.thumbnail);

        ManageWindow mana = new ManageWindow();
        int windowWidth = mana.getWidth(context);
        int windowHeight = (int) (windowWidth/2.4);
        ViewGroup.LayoutParams  para = vp_fragment_main_advertisement.getLayoutParams();
        para.height = windowHeight;
        para.width = windowWidth;
        vp_fragment_main_advertisement.setLayoutParams(para);


        for (int i = 0; i < advList.size(); i++) {
            final int pos = i;
            ImageView iv = new ImageView(context);
            ImageLoader.getInstance().displayImage(advList.get(i).url,  iv, options);


            RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(rp);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);//铺满屏幕
            adList.add(iv);


        }

        adAdapter = new AdvertisementPagerAdapter(adList,advList,context);
        vp_fragment_main_advertisement.setAdapter(adAdapter);
        // 布置完毕,设置一定周期内自动播放广告viewpage广告
        vp_fragment_main_advertisement.setCurrentItem(1000);
        vp_fragment_main_advertisement.setOffscreenPageLimit(12);
//        handler.sendEmptyMessageDelayed(MSG_ADVERTISEMENT, ADVERTISEMENT_CHANGE_TIME);
        handler.sendEmptyMessage(MSG_ADVERTISEMENT);
        vp_fragment_main_advertisement.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageSelected(int position) {

//                Toast.makeText(context,"第" + position + "张广告页" ,Toast.LENGTH_LONG).show();

            }
        });
    }
    private void initEvent() {
        imv_fragmen_01_return_top.setOnClickListener(this);
        fragment_main_category_clothes.setOnClickListener(this);
        fragment_main_category_baby.setOnClickListener(this);
        fragment_main_category_home.setOnClickListener(this);
        fragment_main_category_shoes.setOnClickListener(this);
        fragment_main_category_ornaments.setOnClickListener(this);
        fragment_main_category_stationery.setOnClickListener(this);
        fragment_main_category_digital.setOnClickListener(this);
        fragment_main_category_beauty.setOnClickListener(this);
        fragment_main_category_food.setOnClickListener(this);
        fragment_main_category_other.setOnClickListener(this);


    }

    private void initView() {

        sc_fragment_main.setVisibility(getView().INVISIBLE);

        getFirstData();//获取第一次商品列表的数据
        getAdvertisementData();//获取广告轮播的数据
    }

    private void init() {
        sc_fragment_main = (PullToRefreshScrollView) mView.findViewById(R.id.sc_fragment_main);
        imv_fragmen_01_return_top = (ImageView) mView.findViewById(R.id.imv_fragmen_01_return_top);

        vp_fragment_main_advertisement = (ChildViewPager) mView.findViewById(R.id.vp_fragment_main_advertisement);

        fragment_main_category_clothes = (ImageView) mView.findViewById(R.id.fragment_main_category_clothes);
        fragment_main_category_baby = (ImageView) mView.findViewById(R.id.fragment_main_category_baby);
        fragment_main_category_home = (ImageView) mView.findViewById(R.id.fragment_main_category_home);
        fragment_main_category_shoes = (ImageView) mView.findViewById(R.id.fragment_main_category_shoes);
        fragment_main_category_ornaments = (ImageView) mView.findViewById(R.id.fragment_main_category_ornaments);
        fragment_main_category_stationery = (ImageView) mView.findViewById(R.id.fragment_main_category_stationery);
        fragment_main_category_digital = (ImageView) mView.findViewById(R.id.fragment_main_category_digital);
        fragment_main_category_beauty = (ImageView) mView.findViewById(R.id.fragment_main_category_beauty);
        fragment_main_category_food = (ImageView) mView.findViewById(R.id.fragment_main_category_food);
        fragment_main_category_other = (ImageView) mView.findViewById(R.id.fragment_main_category_other);

        rv_fragment_main_goodslist = (BanRecylerView) mView.findViewById(R.id.rv_fragment_main_goodslist);
//        v_fragment_main_loading = mView.findViewById(R.id.v_fragment_main_loading);
    }

    /**
     * 跳转viewpager
     */
    private void jumpViewpager(int curr){

        MainActivity main = (MainActivity) getActivity();
        Fragment_01 fr_01 = (Fragment_01) main.getSupportFragmentManager().findFragmentById(R.id.fl_main_fragmentlist);
        fr_01.viewpagerJump(curr);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_main_category_clothes:
                jumpViewpager(3);

                break;
            case R.id.fragment_main_category_baby:
                jumpViewpager(4);

                break;
            case R.id.fragment_main_category_home:
                jumpViewpager(6);

                break;
            case R.id.fragment_main_category_shoes:
                jumpViewpager(7);

                break;
            case R.id.fragment_main_category_ornaments:
                jumpViewpager(1);

                break;
            case R.id.fragment_main_category_stationery:
                jumpViewpager(9);

                break;
            case R.id.fragment_main_category_digital:
                jumpViewpager(10);

                break;
            case R.id.fragment_main_category_beauty:
                jumpViewpager(5);

                break;
            case R.id.fragment_main_category_food:
                jumpViewpager(8);

                break;
            case R.id.fragment_main_category_other:
                jumpViewpager(2);
                break;
            case R.id.imv_fragmen_01_return_top:
                sc_fragment_main.getRefreshableView().scrollTo(0,0);
                break;
        }
    }

    /**
     * 让activity调用 用于排序当前显示的列表
     */
    public void orderList(String toTag,String toOrder){
        tag = toTag;
        order = toOrder;
        requestOrder();
    }

}
