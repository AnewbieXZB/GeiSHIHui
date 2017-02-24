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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.activity.DetailsActivity;
import com.example.s.why_no.activity.MainActivity;
import com.example.s.why_no.bean.Advertisement;
import com.example.s.why_no.bean.Goods;
import com.example.s.why_no.bean.Shop;
import com.example.s.why_no.bean.Thumbnail;
import com.example.s.why_no.servlet.MainAdvertisementServlet;
import com.example.s.why_no.servlet.MainGoodsListServlet;
import com.example.s.why_no.utils_window.ManageWindow;
import com.example.s.why_no.view.BanScrollViewPager;
import com.example.s.why_no.viewpageradapter.AdvertisementPagerAdapter;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.HeaderGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshHeadGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S on 2016/10/30.
 */

public class Fragment_01_main_new extends Fragment implements View.OnClickListener{

    public int new_data = 0;//0数据库没有刷新数据 1数据库刷新了数据


    private DisplayImageOptions options = options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
            .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public int numerical = 0;//传递到服务器的页码
    private String tag = "default";//标签
    private String order = "default";//排序


    private ImageView imv_fragmen_01_category_return_top;
    private PullToRefreshHeadGridView gv_fragmen_01_category_list;
    private GoodsAdapter gAdapter = new GoodsAdapter();
    private List<Goods> allList = new ArrayList<Goods>();
    private List<Goods> tempList = new ArrayList<Goods>();

    private int ci;//记录 已经使用了多少次
    private int shang ;//求商 记录取得消息能使用多少次
    private int yu ; //求余 记录剩下多少条
    private boolean isRefreshing = false;//标记是否处于刷新状态

    private final int FIRST_RESULT = 66;
    private final int MORE_RESULT = 33;
    private final int ORDER_RESULT = 22;

    private final int ERROR_REQUEST = 77;


    private View header;
    private HeaderGridView lv;
    private Shop first_shop;
    /**
     * 广告viewpager部分
     */
    private BanScrollViewPager vp_fragment_main_advertisement;
    private LinearLayout ll_fragment_main_dot;
    private List<View> adList;
    private List<Thumbnail> advList;
    private AdvertisementPagerAdapter adAdapter;
    private final int GET_ADVERTISEMENT = 20;
    private final int ADVERTISEMENT_CHANGE_TIME = 4000;
    private final int MSG_ADVERTISEMENT = 156;
    private final int GET_ADVERTISEMENT_NEW = 44;
    private  int setDotAndRoll = 0;//设置dot和轮播线程只需要一次
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

    private View mView;
    private Context context;
    private static Fragment instance;

    public Fragment_01_main_new() {

    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FIRST_RESULT:
                    Shop firstShop = (Shop) msg.obj;
                    if (firstShop.error == 1){
                        buildList(firstShop);
                    }else{
                        Toast.makeText(context,"无此类商品的收录",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MORE_RESULT:
                    Shop tempShop = (Shop) msg.obj;
                    if(tempShop.error == 1){
                        if (tempShop.goods.size() == 0){
                            Toast.makeText(context,"没有更多商品了",Toast.LENGTH_SHORT).show();
                        }else{
                            buildMore(tempShop);
                        }

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
                case GET_ADVERTISEMENT:
                    Advertisement advertisement = (Advertisement) msg.obj;
                    if (advertisement.error == 1){
                        buildViewpager(advertisement);
                    }
                    break;
                case GET_ADVERTISEMENT_NEW:
                    Advertisement advertisement_new = (Advertisement) msg.obj;
                    if (advertisement_new.error == 1){
                        buildViewpagerFromNew(advertisement_new);
                    }
                    break;
                case ORDER_RESULT:

                    Shop orderShop = (Shop) msg.obj;
                    if(orderShop.error == 1){
                        numerical = 0;
//                        allList.clear();
                        buildNew(orderShop);
                    }else{
//                        Toast.makeText(context,"排序出错",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case ERROR_REQUEST:
                    Toast.makeText(context,"网络错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_01_category_new, null);
        context = getActivity().getApplicationContext();

        init();
        initView();
        initEvent();

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 999:
                if (resultCode == getActivity().RESULT_OK) {
                    numerical = 0;
                    new_data = 1;
                    allList.clear();
                    tempList.clear();
                    getMore();
                    MainActivity main = (MainActivity) getActivity();
                    main.fragment_01.setNew();
                }
                break;
        }
    }

    /**
     * 服务器刷了数据 这边也要刷新数据
     */
    public void refreshDataFromServer(){
        if (new_data == 1){
            numerical = 0;
            allList.clear();
            tempList.clear();
            new_data = 0;
            getMore();
        }
    }

    /**
     * 请求（排序）刷新数据
     */
    private void requestOrder() {

        numerical = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainGoodsListServlet mainGoodsListServlet
                        = new MainGoodsListServlet();

                System.out.println("排序刷新 f1：numerical  " + numerical);
                System.out.println("排序刷新 f1：tag  " + tag);
                System.out.println("排序刷新 f1：order  " + order);

                String json = mainGoodsListServlet.getJson(numerical,tag,order);
                System.out.println("fragment01 category json" + json);
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
     * 构筑更多的商品
     * @param tempShop
     */
    private void buildMore(Shop tempShop) {

        Log.e("第二次从服务器获取的" ,tempShop.goods.size() + "");
        tempList.addAll(tempShop.goods);

        ci = 0;
        shang = tempShop.goods.size()/20;
        yu = tempShop.goods.size()%20;

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
    /**
     * 获取的100条信息全保存到数据库
     * 先显示20条 刷新再从数据库拿20条
     * 100条全使用完后再从服务器拿100条覆盖到以前的100条
     * 意在减轻服务器负担
     * @param shop
     */
    private void buildList(Shop shop) {
    /**
         * 设置头部
         */
//        gv_fragmen_01_category_list.setMode(PullToRefreshBase.Mode.BOTH);
//        initHeat();
        initBody(shop);//头部加载完再设置主体
    }

    /**
     * 设置主体
     */
    private void initBody(Shop shop) {

        gv_fragmen_01_category_list.getLoadingLayoutProxy(false, true)
                .setPullLabel("上拉加载更多");
        gv_fragmen_01_category_list.getLoadingLayoutProxy(false, true)
                .setRefreshingLabel("正在载入...");
        gv_fragmen_01_category_list.getLoadingLayoutProxy(false, true)
                .setReleaseLabel("放开以刷新");

        gv_fragmen_01_category_list.getLoadingLayoutProxy(true, false)
                .setPullLabel("下拉刷新");
        gv_fragmen_01_category_list.getLoadingLayoutProxy(true, false)
                .setRefreshingLabel("正在刷新...");
        gv_fragmen_01_category_list.getLoadingLayoutProxy(true, false)
                .setReleaseLabel("放开以刷新");

        isRefreshing = false;
        gv_fragmen_01_category_list.setMode(PullToRefreshBase.Mode.BOTH);
        gv_fragmen_01_category_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HeaderGridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                adList.clear();
                advList.clear();
                allList.clear();
                tempList.clear();
                numerical = 0;
//                lv.removeHeaderView(header);
//                CustomProgress.show(getActivity(), "正在加载...", false,
//                        null);//加载中的动画
//                gAdapter.notifyDataSetChanged();
//                adAdapter.notifyDataSetChanged();
//                initHeat();
//                getAdvertisementData();
                ll_fragment_main_dot.removeAllViews();
                getAdvertisementDataFromNew();
//                requestOrder();
                isRefreshing = false;
                gv_fragmen_01_category_list.onRefreshComplete();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                Log.e("shang",shang + "");
                Log.e("ci",ci + "");
                Log.e("yu",yu + "");

                if(shang <=  ci){//次数用完了，
                    //判断余数 余数也为空则需要请求服务器重新获得
                    if (yu >= 1){
                        useYu();//添加余数
                    }else{
                        getMore();
                    }

                }else{//次数没用完 从获取到的allList继续取出
                    useMore();
                }
                isRefreshing = false;
                gv_fragmen_01_category_list.onRefreshComplete();
            }
        });
//        gv_fragmen_01_category_list
//                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
//
//                    @Override
//                    public void onRefresh(
//                            PullToRefreshBase<GridView> refreshView) {
//                        if (isRefreshing)
//                            return;
//                        isRefreshing = true;
//                        if (gv_fragmen_01_category_list.isHeaderShown()) {
//
//                        }
//                        if (gv_fragmen_01_category_list.isFooterShown()) {
//                            Log.e("shang",shang + "");
//                            Log.e("ci",ci + "");
//                            Log.e("yu",yu + "");
//
//                            if(shang <=  ci){//次数用完了，
//                                //判断余数 余数也为空则需要请求服务器重新获得
//                                if (yu >= 1){
//                                    useYu();//添加余数
//                                }else{
//                                    getMore();
//                                }
//
//                            }else{//次数没用完 从获取到的allList继续取出
//                                useMore();
//                            }
//                            isRefreshing = false;
//                            gv_fragmen_01_category_list.onRefreshComplete();
//                        }
//
//                    }
//                });

        gv_fragmen_01_category_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id",allList.get(position-2).id);
                intent.putExtra("ification",allList.get(position-2).ification);
                startActivityForResult(intent,999);
            }
        });

//        allList = new ArrayList<Goods>();
//        tempList = new ArrayList<Goods>();
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
//        gAdapter = new GoodsAdapter();
        lv.addHeaderView(header);//加入头部

        gv_fragmen_01_category_list.setAdapter(gAdapter);

        numerical += 100;

    }

    /**
     * 给列表设置头部
     */
    private void initHeat() {


        header = View.inflate(context, R.layout.fragment_01_main_heat, null);

        lv = gv_fragmen_01_category_list.getRefreshableView();
        vp_fragment_main_advertisement = (BanScrollViewPager) header.findViewById(R.id.vp_fragment_main_advertisement);
        ll_fragment_main_dot = (LinearLayout) header.findViewById(R.id.ll_fragment_main_dot);

        fragment_main_category_clothes = (ImageView) header.findViewById(R.id.fragment_main_category_clothes);
        fragment_main_category_baby = (ImageView) header.findViewById(R.id.fragment_main_category_baby);
        fragment_main_category_home = (ImageView) header.findViewById(R.id.fragment_main_category_home);
        fragment_main_category_shoes = (ImageView) header.findViewById(R.id.fragment_main_category_shoes);
        fragment_main_category_ornaments = (ImageView) header.findViewById(R.id.fragment_main_category_ornaments);
        fragment_main_category_stationery = (ImageView) header.findViewById(R.id.fragment_main_category_stationery);
        fragment_main_category_digital = (ImageView) header.findViewById(R.id.fragment_main_category_digital);
        fragment_main_category_beauty = (ImageView) header.findViewById(R.id.fragment_main_category_beauty);
        fragment_main_category_food = (ImageView) header.findViewById(R.id.fragment_main_category_food);
        fragment_main_category_other = (ImageView) header.findViewById(R.id.fragment_main_category_other);
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

//        getAdvertisementData();
    }
    /**
     * 跳转viewpager
     */
    private void jumpViewpager(int curr){

//        Fragment_01 fr_01 = (Fragment_01) getParentFragment();
////        ViewPager vp = fr_01.vp_fragment_01_category;
//        fr_01.viewpagerJump(curr);
        MainActivity main = (MainActivity) getActivity();
        main.fragment_01.viewpagerJump(curr);
//        Fragment_01 fr_01 = (Fragment_01) main.getSupportFragmentManager().findFragmentById(R.id.fl_main_fragmentlist);
//        fr_01.viewpagerJump(curr);

    }

    /**
     * 下拉刷新重新获取广告轮播的数据
     */
    public void getAdvertisementDataFromNew() {

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
                    msg.what = GET_ADVERTISEMENT_NEW;
                }
                handler.sendMessage(msg);
            }
        }).start();
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

    /**
     * 下拉刷新后重新构筑广告栏
     * @param advertisement
     */
    private void buildViewpagerFromNew(Advertisement advertisement){
        /**
         * viewpager弊端
         * 页码只有1：禁止滑动
         * 页码只有2：将页码翻倍 即变为4 轮播点还是2
         * 页码大于等于3 正常处理
         */

        if (advertisement.thumbnail.size() == 1){
            setPagerOne(advertisement);
        }else if(advertisement.thumbnail.size() == 2){
            setPagerTwo(advertisement);
        }else if(advertisement.thumbnail.size() >= 3){
            setPagerThree(advertisement);
        }

        System.out.println("重新构筑了广告");

        requestOrder();//构筑完viewpager再构筑主体
    }

    private void buildViewpager(Advertisement advertisement){
        /**
         * viewpager弊端
         * 页码只有1：禁止滑动
         * 页码只有2：将页码翻倍 即变为4 轮播点还是2
         * 页码大于等于3 正常处理
         */
//        adList = new ArrayList<View>();
//        advList = new ArrayList<Thumbnail>();

        if (advertisement.thumbnail.size() == 1){
            setPagerOne(advertisement);
        }else if(advertisement.thumbnail.size() == 2){
            setPagerTwo(advertisement);
        }else if(advertisement.thumbnail.size() >= 3){
            setPagerThree(advertisement);
        }
        setDotAndRoll = 1;
        getData();//构筑完viewpager再构筑主体
    }

    //页码大于等于3 正常处理
    private void setPagerThree(Advertisement advertisement) {
        advList.addAll(advertisement.thumbnail);
        ManageWindow mana = new ManageWindow();
        int windowWidth = mana.getWidth(context);
        int windowHeight = (int) ((windowWidth-44)/2.4);
        ViewGroup.LayoutParams  para = vp_fragment_main_advertisement.getLayoutParams();
        para.height = windowHeight;
        para.width = windowWidth-44;
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


//        adAdapter = new AdvertisementPagerAdapter(adList,advList,context);
        vp_fragment_main_advertisement.setAdapter(adAdapter);
        // 布置完毕,设置一定周期内自动播放广告viewpage广告
        vp_fragment_main_advertisement.setCurrentItem(1000);
//        handler.sendEmptyMessageDelayed(MSG_ADVERTISEMENT, ADVERTISEMENT_CHANGE_TIME);
        if (setDotAndRoll == 0){
            handler.sendEmptyMessage(MSG_ADVERTISEMENT);
        }
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
                updateIntroAndDot();
//                Toast.makeText(context,"第" + position + "张广告页" ,Toast.LENGTH_LONG).show();

            }
        });
        initDots(advList.size());
    }

    //页码只有2：将页码翻倍 即变为4 轮播点还是2
    private void setPagerTwo(Advertisement advertisement) {

        advList.addAll(advertisement.thumbnail);
        advList.addAll(advList);//页码只有2：将页码翻倍 即变为4 轮播点还是2


        ManageWindow mana = new ManageWindow();
        int windowWidth = mana.getWidth(context);
        int windowHeight = (int) ((windowWidth-44)/2.4);
        ViewGroup.LayoutParams  para = vp_fragment_main_advertisement.getLayoutParams();
        para.height = windowHeight;
        para.width = windowWidth-44;
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
        vp_fragment_main_advertisement.setCurrentItem(0);
//        handler.sendEmptyMessageDelayed(MSG_ADVERTISEMENT, ADVERTISEMENT_CHANGE_TIME);
        if (setDotAndRoll == 0){
            handler.sendEmptyMessage(MSG_ADVERTISEMENT);
        }
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
                updateIntroAndDot();
//                Toast.makeText(context,"第" + position + "张广告页" ,Toast.LENGTH_LONG).show();

            }
        });
        initDots(2);
    }

    //页码只有1：禁止滑动
    private void setPagerOne(Advertisement advertisement) {

        advList.addAll(advertisement.thumbnail);
        ManageWindow mana = new ManageWindow();
        int windowWidth = mana.getWidth(context);
        int windowHeight = (int) ((windowWidth-44)/2.4);
        ViewGroup.LayoutParams  para = vp_fragment_main_advertisement.getLayoutParams();
        para.height = windowHeight;
        para.width = windowWidth-44;
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


//        adAdapter = new AdvertisementPagerAdapter(adList,advList,context);
        vp_fragment_main_advertisement.setAdapter(adAdapter);
        // 布置完毕,设置一定周期内自动播放广告viewpage广告
        vp_fragment_main_advertisement.setCurrentItem(0);

        vp_fragment_main_advertisement.setScrollble(false);
    }

    private void updateIntroAndDot() {
        int currentPage = vp_fragment_main_advertisement.getCurrentItem() % adAdapter.allList.size();
        for (int i = 0; i < ll_fragment_main_dot.getChildCount(); i++) {
            ll_fragment_main_dot.getChildAt(i).setEnabled(i == currentPage);// 设置setEnabled为true的话
        }
    }
    private void initDots(int adSize) {
        for (int i = 0; i < adSize; i++) {
            if (getActivity() == null) {
                return;
            }
            View view = new View(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
            if (i != 0) {// 第一个点不需要左边距
                params.leftMargin = 8;
            }
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.selector_dot);
            ll_fragment_main_dot.addView(view);
        }
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gv_fragmen_01_category_list.getRefreshableView().setSelection(0);
            }
        }, 10);

        System.out.println("重新构筑了商品列表");
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

    //次数用完了，则需要请求服务器重新获得
    public void getMore() {

        tempList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainGoodsListServlet mainGoodsListServlet
                        = new MainGoodsListServlet();

                System.out.println("第二次进来：numerical  " + numerical);
                System.out.println("第二次进来：tag  " + tag);
                System.out.println("第二次进来：order  " + order);

                String json = mainGoodsListServlet.getJson(numerical,tag,order);
                System.out.println("使用关键词搜索的json" + json);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else{
                    Gson gson = new Gson();
                    Shop tempShop = gson.fromJson(json,Shop.class);

                    msg.what = MORE_RESULT;
                    msg.obj = tempShop;

                }
                handler.sendMessage(msg);
            }
        }).start();
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
     * 获取第一次信息
     */
    private void getData() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                MainGoodsListServlet mainGoodsListServlet
                        = new MainGoodsListServlet();
                System.out.println("第一次进来：numerical  " + numerical);
                System.out.println("第一次进来：tag  " + tag);
                System.out.println("第一次进来：order  " + order);
                String json = mainGoodsListServlet.getJson(numerical,tag,order);
                System.out.println("fragment01 category json" + json);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else{
                    Gson gson = new Gson();
                    Shop tempShop = gson.fromJson(json,Shop.class);


                    msg.what = FIRST_RESULT;
                    msg.obj = tempShop;

                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 让activity调用 用于排序当前显示的列表
     */
    public void orderList(String toTag,String toOrder){
        tag = toTag;
        order = toOrder;
        requestOrder();
    }



    private void initEvent() {
        imv_fragmen_01_category_return_top.setOnClickListener(this);
    }

    private void initView() {
//        tv_fragment_02_order.setText(str_order[0]);
//        tv_fragment_02_tag.setText(str_tag[0]);
        adList = new ArrayList<View>();
        advList = new ArrayList<Thumbnail>();
        adAdapter = new AdvertisementPagerAdapter(adList,advList,context);
        allList = new ArrayList<Goods>();
        tempList = new ArrayList<Goods>();
        gAdapter = new GoodsAdapter();
        initHeat();
        getAdvertisementData();
//        getData();
    }

    private void init() {

        imv_fragmen_01_category_return_top = (ImageView) mView.findViewById(R.id.imv_fragmen_01_category_return_top);
        gv_fragmen_01_category_list = (PullToRefreshHeadGridView) mView.findViewById(R.id.gv_fragmen_01_category_list);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_fragmen_01_category_return_top:
                gv_fragmen_01_category_list.getRefreshableView().setSelection(0);
                break;
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
        }
    }

    /**
     * 搜索结果适配器
     */
    class GoodsAdapter extends BaseAdapter {


        private DisplayImageOptions options = options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
                .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        @Override
        public int getCount() {
            return allList.size();
        }

        @Override
        public Object getItem(int position) {
            return allList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final GoodsAdapter.ViewHolder vh;
            final int pos = position;

            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_goods, parent, false);

                vh = new GoodsAdapter.ViewHolder();

                vh.imv_item_pic = (ImageView) convertView.findViewById(R.id.imv_item_pic);
                vh.tv_item_quan = (TextView) convertView.findViewById(R.id.tv_item_quan);
                vh.tv_item_today_new = (TextView) convertView.findViewById(R.id.tv_item_today_new);
                vh.tv_item_title = (TextView) convertView.findViewById(R.id.tv_item_title);
                vh.tv_item_price = (TextView) convertView.findViewById(R.id.tv_item_price);
                vh.tv_item_volume = (TextView) convertView.findViewById(R.id.tv_item_volume);

                convertView.setTag(vh);
            }else {
                vh = (GoodsAdapter.ViewHolder) convertView.getTag();
            }



            ManageWindow mana = new ManageWindow();
            int windowWidth = mana.getWidth(context);
            ViewGroup.LayoutParams  para = vh.imv_item_pic.getLayoutParams();
            para.height = (windowWidth-24)/2;
            para.width = (windowWidth-24)/2;
            vh.imv_item_pic.setLayoutParams(para);

            ImageLoader.getInstance().displayImage(allList.get(position).uimg
                    +"_300x300.jpg" ,  vh.imv_item_pic, options);
            vh.tv_item_title.setText(allList.get(position).uname);
            vh.tv_item_price.setText("¥" + allList.get(position).roll);//券后价
            vh.tv_item_volume.setText(allList.get(position).volume + "");
            vh.tv_item_quan.setText( allList.get(position).discount + "元");//优惠券的面值

            if ((allList.get(position).heat) != null){
                if (allList.get(position).heat.equals("1")){
                    vh.tv_item_today_new.setVisibility(View.VISIBLE);
                }else{
                    vh.tv_item_today_new.setVisibility(View.GONE);
                }
            }else{
                vh.tv_item_today_new.setVisibility(View.GONE);
            }

            return convertView;
        }


        class ViewHolder {


            ImageView imv_item_pic;
            TextView tv_item_quan;
            TextView tv_item_today_new;
            TextView tv_item_title;
            TextView tv_item_price;
            TextView tv_item_volume;

            int vhType;

        }
    }
}
