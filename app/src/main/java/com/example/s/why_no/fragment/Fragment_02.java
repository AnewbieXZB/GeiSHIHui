package com.example.s.why_no.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.activity.DetailsActivity;
import com.example.s.why_no.activity.MainActivity;
import com.example.s.why_no.bean.Goods;
import com.example.s.why_no.bean.Shop;
import com.example.s.why_no.servlet.GoodsNineServlet;
import com.example.s.why_no.utils_window.ManageWindow;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S on 2016/10/30.
 */

public class Fragment_02 extends Fragment implements View.OnClickListener{

    public int new_data = 0;//0数据库没有刷新数据 1数据库刷新了数据

    private int note = 0;//记录用户选择了哪种排序方式
    private int numerical = 0;//传递到服务器的页码
//    String[] str_tag = {"全部","按销量","按券后价"};
//    String[] str_order = {"默认","由高到低","由低到高"};
    private String tag = "default";//标签
    private String order = "default";//排序

//    /**
//     * 因为排序按钮需要tag和order都要非默认值，所以当用户已经点击了其中一个则
//     * 改变isReady的值为1  当点击第二个按钮时判断isReday是否为1
//     * 是则 允许发送排序请求
//     */
//    private int isReady = 0;

    private ImageView imv_fragmen_02_return_top;
    private PullToRefreshGridView gv_fragmen_02_list;
    private GoodsAdapter gAdapter = new GoodsAdapter();
    private List<Goods> allList = new ArrayList<Goods>();
    private List<Goods> tempList = new ArrayList<Goods>();

//    //分类按钮
//    private TextView tv_fragment_02_tag;
//    private TextView tv_fragment_02_order;
//    // 弹出窗口
//    private PopupWindow pop_ll_tag;
    private PopupWindow pop_ll_order;
//    private View layout_tag;
    private View layout_order;

    private ImageView imv_top_left;
//    private ImageView imv_top_right;
    private TextView tv_top_right;

    private Shop shop;
    private int ci;//记录 已经使用了多少次
    private int shang ;//求商 记录取得消息能使用多少次
    private int yu ; //求余 记录剩下多少条
    private boolean isRefreshing = false;//标记是否处于刷新状态

    private final int FIRST_RESULT = 66;
    private final int MORE_RESULT = 33;
    private final int ORDER_RESULT = 22;

    private final int ERROR_REQUEST = 30;

    private View mView;
    private Context context;
    private static Fragment instance;

    public Fragment_02() {

    }
    public static Fragment_02 getInstance() {
        Fragment_02 newNewsFragment = new Fragment_02();
        return newNewsFragment;
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
                        buildMore(tempShop);
                    }else{
                        Toast.makeText(context,"没有更多商品了",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context,"网络错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_02, null);
        context = getActivity().getApplicationContext();

        init();
        initView();
        initEvent();

        return mView;
    }

    /**
     * 展开标签窗口
     */
//    private void showMenuforTag(){
//        if (pop_ll_tag != null && pop_ll_tag.isShowing()) {
//            pop_ll_tag.dismiss();
//        } else {
//            layout_tag = getActivity().getLayoutInflater().inflate(R.layout.pop_menulist, null);
//            ListView menulist_tag = (ListView) layout_tag.findViewById(R.id.lv_menu_list);
//
//
//            List<Map<String,String>> list_tag = new ArrayList<Map<String, String>>();
//            for (int i = 0; i < str_tag.length; i++) {
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("item", str_tag[i]);
//                list_tag.add(map);
//            }
//            SimpleAdapter listAdapter = new SimpleAdapter(context, list_tag, R.layout.pop_menuitem,
//                    new String[] { "item" }, new int[] { R.id.tv_menu_item });
//            menulist_tag.setAdapter(listAdapter);
//
//            // 点击listview中item的处理
//            menulist_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//
//                    switch (arg2){
//                        case 0:
//                            tag = "default";
//                            if(tag.equals(order)){
//                                isReady = 2;
//                            }
//                            break;
//                        case 1:
//                            tag = "volume";
//                            if (order.equals("default")){
//
//                            }else{
//                                isReady = 2;
//                            }
//                            break;
//                        case 2:
//                            tag = "roll";
//                            if (order.equals("default")){
//
//                            }else{
//                                isReady = 2;
//                            }
//                            break;
//                    }
//                    tv_fragment_02_tag.setText(str_tag[arg2]);
//                    // 隐藏弹出窗口
//                    if (pop_ll_tag != null && pop_ll_tag.isShowing()) {
//                        pop_ll_tag.dismiss();
//                    }
//                    requestOrder();
//
//                }
//            });
//
//
//
//            // 创建弹出窗口
//            // 窗口内容为layoutLeft，里面包含一个ListView
//            // 窗口宽度跟tvLeft一样
//            pop_ll_tag = new PopupWindow(layout_tag, tv_fragment_02_tag.getWidth(),
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//
////            pop_ll_tag.setAnimationStyle(R.style.PopupAnimation);
//            pop_ll_tag.update();
//            pop_ll_tag.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//            pop_ll_tag.setTouchable(true); // 设置popupwindow可点击
//            pop_ll_tag.setOutsideTouchable(true); // 设置popupwindow外部可点击
//            pop_ll_tag.setFocusable(true); // 获取焦点
//
//            // 设置popupwindow的位置（相对tvLeft的位置）
//            int topBarHeight = tv_fragment_02_tag.getBottom();
//            pop_ll_tag.showAsDropDown(tv_fragment_02_tag, 0, (topBarHeight - tv_fragment_02_tag.getHeight()) / 2);
//
//
//            pop_ll_tag.setTouchInterceptor(new View.OnTouchListener() {
//
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    // 如果点击了popupwindow的外部，popupwindow也会消失
//                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                        pop_ll_tag.dismiss();
//                        return true;
//                    }
//                    return false;
//                }
//            });
//        }
//
//    }
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

//        if (isReady != 2){//用户只选择了一项，无法构成排序条件
//
//        }else{
            numerical = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GoodsNineServlet goodsNineServlet
                            = new GoodsNineServlet();


                    System.out.println("排序刷新：numerical  " + numerical);
                    System.out.println("排序刷新：tag  " + tag);
                    System.out.println("排序刷新：order  " + order);

                    String json = goodsNineServlet.getJson(numerical,tag,order);
                    System.out.println("fragment02 json" + json);
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
     * 展开排序窗口
     */
    private void showMenuforOrder(){

        if (pop_ll_order != null && pop_ll_order.isShowing()) {
            pop_ll_order.dismiss();
        } else {
            layout_order = getActivity().getLayoutInflater().inflate(R.layout.pop_order, null);
//            ListView menulist_tag = (ListView) layout_order.findViewById(R.id.lv_menu_list);
//
//
//            List<Map<String,String>> list_tag = new ArrayList<Map<String, String>>();
//            for (int i = 0; i < str_order.length; i++) {
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("item", str_order[i]);
//                list_tag.add(map);
//            }
//            SimpleAdapter listAdapter = new SimpleAdapter(context, list_tag, R.layout.pop_menuitem,
//                    new String[] { "item" }, new int[] { R.id.tv_menu_item });
//            menulist_tag.setAdapter(listAdapter);
//
//            // 点击listview中item的处理
//            menulist_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//
//                    switch (arg2){
//                        case 0:
//
//                            break;
//                        case 1:
//
//                            break;
//                        case 2:
//
//                            break;
//                    }
////                    tv_fragment_02_order.setText(str_order[arg2]);
//                    // 隐藏弹出窗口
//                    if (pop_ll_order != null && pop_ll_order.isShowing()) {
//                        pop_ll_order.dismiss();
//                    }
//                    requestOrder();
//
//                }
//            });

            TextView pop_order_default = (TextView) layout_order.findViewById(R.id.pop_order_default);
            TextView pop_order_volum_ace = (TextView) layout_order.findViewById(R.id.pop_order_volum_ace);
            TextView pop_order_volum_des = (TextView) layout_order.findViewById(R.id.pop_order_volum_des);
            TextView pop_order_roll_asc = (TextView) layout_order.findViewById(R.id.pop_order_roll_asc);
            TextView pop_order_roll_desc = (TextView) layout_order.findViewById(R.id.pop_order_roll_desc);

            TextView[] tvs = new TextView[]{pop_order_default,pop_order_roll_asc,pop_order_roll_desc,
                    pop_order_volum_des ,pop_order_volum_ace};
            for (int i = 0; i < tvs.length; i++) {
                if (note == i){
                    tvs[i].setTextColor(getResources().getColor(R.color.color_top));
                }else{
                    tvs[i].setTextColor(getResources().getColor(R.color.color_grey));
                }
            }

            pop_order_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    note = 0;
                    order = "default";
                    tag = "default";
                    requestOrder();
                    //隐藏弹出窗口
                    if (pop_ll_order != null && pop_ll_order.isShowing()) {
                        pop_ll_order.dismiss();
                    }
                }
            });
            pop_order_volum_ace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    note = 4;
                    order = "asc";
                    tag = "volume";
                    requestOrder();
                     //隐藏弹出窗口
                    if (pop_ll_order != null && pop_ll_order.isShowing()) {
                        pop_ll_order.dismiss();
                    }
                }
            });
            pop_order_volum_des.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    note = 3;
                    order = "desc";
                    tag = "volume";
                    requestOrder();
                    //隐藏弹出窗口
                    if (pop_ll_order != null && pop_ll_order.isShowing()) {
                        pop_ll_order.dismiss();
                    }
                }
            });
            pop_order_roll_asc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    note = 1;
                    order = "asc";
                    tag = "roll";
                    requestOrder();
                    //隐藏弹出窗口
                    if (pop_ll_order != null && pop_ll_order.isShowing()) {
                        pop_ll_order.dismiss();
                    }
                }
            });
            pop_order_roll_desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    note = 2;
                    order = "desc";
                    tag = "roll";
                    requestOrder();
                    //隐藏弹出窗口
                    if (pop_ll_order != null && pop_ll_order.isShowing()) {
                        pop_ll_order.dismiss();
                    }
                }
            });




            // 创建弹出窗口
            // 窗口内容为layoutLeft，里面包含一个ListView
            // 窗口宽度跟tvLeft一样
            pop_ll_order = new PopupWindow(layout_order, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);


//            pop_ll_tag.setAnimationStyle(R.style.PopupAnimation);
            pop_ll_order.update();
            pop_ll_order.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            pop_ll_order.setTouchable(true); // 设置popupwindow可点击
            pop_ll_order.setOutsideTouchable(true); // 设置popupwindow外部可点击
            pop_ll_order.setFocusable(true); // 获取焦点
            pop_ll_order.setBackgroundDrawable(new BitmapDrawable());
            // 设置popupwindow的位置（相对tvLeft的位置）
            int topBarHeight = tv_top_right.getBottom();
            pop_ll_order.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    pop_ll_order.dismiss();
                }
            });
            pop_ll_order.showAsDropDown(tv_top_right, 0, (topBarHeight - tv_top_right.getHeight()) / 2);

            pop_ll_order.setTouchInterceptor(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    System.out.println("event:" + event);
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        pop_ll_order.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }

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

        gv_fragmen_02_list.getLoadingLayoutProxy(false, true)
                .setPullLabel("上拉加载更多");
        gv_fragmen_02_list.getLoadingLayoutProxy(false, true)
                .setRefreshingLabel("正在载入...");
        gv_fragmen_02_list.getLoadingLayoutProxy(false, true)
                .setReleaseLabel("放开以刷新");

        gv_fragmen_02_list.getLoadingLayoutProxy(true, false)
                .setPullLabel("下拉刷新");
        gv_fragmen_02_list.getLoadingLayoutProxy(true, false)
                .setRefreshingLabel("正在刷新...");
        gv_fragmen_02_list.getLoadingLayoutProxy(true, false)
                .setReleaseLabel("放开以刷新");

        isRefreshing = false;
        gv_fragmen_02_list.setMode(PullToRefreshBase.Mode.BOTH);
        gv_fragmen_02_list
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<GridView> refreshView) {
                        if (isRefreshing)
                            return;
                        isRefreshing = true;
                        if (gv_fragmen_02_list.isHeaderShown()) {
                            requestOrder();
                            isRefreshing = false;
                            gv_fragmen_02_list.onRefreshComplete();
                        }
                        if (gv_fragmen_02_list.isFooterShown()) {
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
                            gv_fragmen_02_list.onRefreshComplete();
                        }

                    }
                });

        gv_fragmen_02_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id",allList.get(position).id);
                intent.putExtra("ification",allList.get(position).ification);
                startActivityForResult(intent,999);
            }
        });

        allList = new ArrayList<Goods>();
        tempList = new ArrayList<Goods>();
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
        gAdapter = new GoodsAdapter();
        gv_fragmen_02_list.setAdapter(gAdapter);

        numerical += 100;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gv_fragmen_02_list.getRefreshableView().setSelection(0);
            }
        }, 10);

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
    private void getMore() {

        tempList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                GoodsNineServlet goodsNineServlet
                        = new GoodsNineServlet();

                System.out.println("第二次进来：numerical  " + numerical);
                System.out.println("第二次进来：tag  " + tag);
                System.out.println("第二次进来：order  " + order);

                String json = goodsNineServlet.getJson(numerical,tag,order);
                Message msg = new Message();
                System.out.println("使用关键词搜索的json" + json);
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
                GoodsNineServlet goodsNineServlet
                        = new GoodsNineServlet();
                System.out.println("第一次进来：numerical  " + numerical);
                System.out.println("第一次进来：tag  " + tag);
                System.out.println("第一次进来：order  " + order);
                String json = goodsNineServlet.getJson(numerical,tag,order);
                System.out.println("fragment02 json" + json);
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

    private void initEvent() {
        imv_fragmen_02_return_top.setOnClickListener(this);
//        tv_fragment_02_tag.setOnClickListener(this);
//        tv_fragment_02_order.setOnClickListener(this);
        imv_top_left.setOnClickListener(this);
//        imv_top_right.setOnClickListener(this);
        tv_top_right.setOnClickListener(this);
    }

    private void initView() {
//        tv_fragment_02_order.setText(str_order[0]);
//        tv_fragment_02_tag.setText(str_tag[0]);
        getData();
    }

    private void init() {

//        tv_fragment_02_tag = (TextView) mView.findViewById(R.id.tv_fragment_02_tag);
//        tv_fragment_02_order = (TextView) mView.findViewById(R.id.tv_fragment_02_order);
        imv_fragmen_02_return_top = (ImageView) mView.findViewById(R.id.imv_fragmen_02_return_top);
        gv_fragmen_02_list = (PullToRefreshGridView) mView.findViewById(R.id.gv_fragmen_02_list);
        imv_top_left = (ImageView) mView.findViewById(R.id.imv_top_left);
//        imv_top_right = (ImageView) mView.findViewById(R.id.imv_top_right);
        tv_top_right = (TextView) mView.findViewById(R.id.tv_top_right);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_fragmen_02_return_top:
                gv_fragmen_02_list.getRefreshableView().setSelection(0);
                break;
//            case R.id.tv_fragment_02_tag:
//                showMenuforTag();
//                break;
//            case R.id.tv_fragment_02_order:
//                showMenuforOrder();
//                break;
            case R.id.imv_top_left:
                break;
//            case R.id.imv_top_right:
//                showMenuforOrder();
//                break;
            case R.id.tv_top_right:
                showMenuforOrder();
                break;
        }
    }


    /**
     * 弹出窗口让用户选择排序方式
     */
    private void showOrderWindow(){

        View dialogView = View.inflate(context, R.layout.dialog_order_select, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(dialogView);
        Window window = alertDialog.getWindow();

        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 200;
        wl.y = window.getWindowManager().getDefaultDisplay().getHeight();
        // 设置显示位置
        alertDialog.onWindowAttributesChanged(wl);

        final TextView tv_dialog_volum_desc = (TextView) dialogView.findViewById(R.id.tv_dialog_volum_desc);
        final TextView tv_dialog_volum_asc = (TextView) dialogView.findViewById(R.id.tv_dialog_volum_asc);
        final TextView tv_dialog_roll_desc = (TextView) dialogView.findViewById(R.id.tv_dialog_roll_desc);
        final TextView tv_dialog_roll_asc = (TextView) dialogView.findViewById(R.id.tv_dialog_roll_asc);

        tv_dialog_volum_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order = "desc";
                tag = "volume";
                requestOrder();
                alertDialog.dismiss();
            }
        });
        tv_dialog_volum_asc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order = "asc";
                tag = "volume";
                requestOrder();
                alertDialog.dismiss();
            }
        });
        tv_dialog_roll_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order = "desc";
                tag = "roll";
                requestOrder();
                alertDialog.dismiss();
            }
        });
        tv_dialog_roll_asc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order = "asc";
                tag = "roll";
                requestOrder();
                alertDialog.dismiss();
            }
        });


        alertDialog.show();
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
