package com.example.s.why_no.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.activity.IntegralShopActivity;
import com.example.s.why_no.activity.MainActivity;
import com.example.s.why_no.activity.UserLoginActivity;
import com.example.s.why_no.adapter.ViewPagerAdapter;
import com.example.s.why_no.bean.SignStatus;
import com.example.s.why_no.servlet.SignServlet;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.example.s.why_no.view.PagerSlidingTabStrip;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S on 2016/11/4.
 */

public class Fragment_01 extends Fragment implements View.OnClickListener{
    private View mView;
    private Context context;

    private PagerSlidingTabStrip sc_fragment_01_category_title;
//    private NavitationScrollLayout sc_fragment_01_category;
    public ViewPager vp_fragment_01_category;
    private String[] titles = new String[]{"推 荐 ","9.9包邮 ", "20封顶", "服 装", "母 婴", "化妆品"
            , "居家日用", "鞋包配饰",  "美 食",  "文体车品", "数码家电"};
    private ViewPagerAdapter viewPagerAdapter;
    private List<Fragment> fragments;

    private int view_pos = 0;//标记viewpage的页码
    private int numerical = 0;//传递到服务器的页码

    private int note = 0;//记录用户选择了哪种排序方式
    private String tag = "default";//标签
    private String order = "default";//排序
    private PopupWindow pop_ll_order;
    //    private View layout_tag;
    private View layout_order;

    private ImageView imv_top_left;
    private TextView tv_top_right;

    public Fragment_01_main_new frag_main;
//    private Fragment_01_main frag_main;
    public Fragment_01_category frag_clothing;
    public Fragment_01_category frag_baby;
    public Fragment_01_category frag_home;
    public Fragment_01_category frag_shoes;
    public Fragment_01_category frag_accessories;
    public Fragment_01_category frag_literary;
    public Fragment_01_category frag_digital;
    public Fragment_01_category frag_beauty;
    public Fragment_01_category frag_food;
    public Fragment_01_category frag_other;
    public Fragment_01_category[]  arrFragment;

    private final int SIGN_TO = 45;
    private final int ERROR_REQUEST = 89;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SIGN_TO:
                    SignStatus sign = (SignStatus) msg.obj;
                    if (sign.error == 0){
                        showSignWindowFalse();
                    }else if(sign.error == 1){
                        showSignWindowSuccess(sign);
                    }else if(sign.error == 2){
                        showSignWindowFail();
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
        mView = inflater.inflate(R.layout.fragment_01, null);
        context = getActivity().getApplicationContext();

        init();
        initView();
        initEvent();
        return mView;
    }

    /**
     * 签到
     */
    private void toSign() {

        ShareLoginData share = new ShareLoginData(context);
        final String phone = share.isLogin();

        System.out.println("签到   " + phone);

        if (phone.equals("-1")){
            Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
            startActivityForResult((new Intent(context, UserLoginActivity.class)),202);//跳到登录页面
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SignServlet signServlet = new SignServlet();
                    String json = signServlet.getJson(phone);

                    System.out.println("json:" + json);
                    Message msg = new Message();
                     if (json.equals("error")){
                         msg.what = ERROR_REQUEST;
                     }else{
                         Gson gson = new Gson();
                         SignStatus sign = gson.fromJson(json,SignStatus.class);


                         msg.obj = sign;
                         msg.what = SIGN_TO;

                     }
                    handler.sendMessage(msg);
                }
            }).start();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 202:
                if (resultCode == getActivity().RESULT_OK) {
                    MainActivity main = (MainActivity) getActivity();
                    main.fragment_05.hasUserDataToChangeUI();//判断fragment05是否有用户信息保存在app，有则改变布局
                }
                break;
        }
    }
    /**
     * 展示成功签到窗口
     */
    private void showSignWindowSuccess(SignStatus sign) {
        View dialogView = View.inflate(context, R.layout.dialog_sign_success, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(dialogView);

        TextView sign_success_plus = (TextView) dialogView.findViewById(R.id.sign_success_plus);
        TextView sign_success_to_exchange = (TextView) dialogView.findViewById(R.id.sign_success_to_exchange);

        sign_success_plus.setText( " +"+ sign.sign);

        sign_success_to_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, IntegralShopActivity.class));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
    /**
     * 展示失败签到窗口
     */
    private void showSignWindowFalse() {
        View dialogView = View.inflate(context, R.layout.dialog_sign_false, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(dialogView);
        alertDialog.show();
    }
    /**
     * 展示已经签到窗口
     */
    private void showSignWindowFail() {
        View dialogView = View.inflate(context, R.layout.dialog_sign_fail, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(dialogView);

        TextView sign_fail_to_exchange = (TextView) dialogView.findViewById(R.id.sign_fail_to_exchange);
        sign_fail_to_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, IntegralShopActivity.class));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void initEvent() {
        imv_top_left.setOnClickListener(this);
        tv_top_right.setOnClickListener(this);
    }

    private void initView() {

//        frag_main = new Fragment_01_main();
        frag_main = new Fragment_01_main_new();
        frag_clothing = new Fragment_01_category("clothing");
        frag_baby = new Fragment_01_category("baby");
        frag_home = new Fragment_01_category("home");
        frag_shoes = new Fragment_01_category("shoes");
        frag_accessories = new Fragment_01_category("nine");
        frag_literary = new Fragment_01_category("literary");
        frag_digital = new Fragment_01_category("digital");
        frag_beauty = new Fragment_01_category("cosmetics");
        frag_food = new Fragment_01_category("food");
        frag_other = new Fragment_01_category("twenty");

        arrFragment = new Fragment_01_category[]{frag_accessories,frag_other,frag_clothing
                ,frag_baby,frag_beauty,frag_home,frag_shoes
                ,frag_food ,frag_literary,frag_digital};

//        {"全 部", "衣 服", "母 婴", "居 家", "鞋 包"
//                , "配 饰", "文 体", "数 码", "美 妆", "美 食", "其 他"};

        fragments =  new ArrayList<>();
//        fragments.add(frag_main);
        fragments.add(frag_main);
        fragments.add(frag_accessories);
        fragments.add(frag_other);
        fragments.add(frag_clothing);
        fragments.add(frag_baby);
        fragments.add(frag_beauty);
        fragments.add(frag_home);
        fragments.add(frag_shoes);
        fragments.add(frag_food);
        fragments.add(frag_literary);
        fragments.add(frag_digital);



        viewPagerAdapter = new ViewPagerAdapter(
                getActivity().getSupportFragmentManager(), fragments,titles);
        vp_fragment_01_category.setAdapter(viewPagerAdapter);
        sc_fragment_01_category_title.setViewPager(vp_fragment_01_category);


        /**
         * 设置导航标题条样式
         */
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();// 获取当前屏幕的密度
        // 设置Tab是自动填充满屏幕的
        sc_fragment_01_category_title.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        sc_fragment_01_category_title.setDividerColor(getResources().getColor(R.color.transparent));
        // 设置Tab底部线的高度
        sc_fragment_01_category_title
                .setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics));
        // 设置Tab Indicator的高度
        sc_fragment_01_category_title
                .setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, displayMetrics));


        // 设置Tab标题文字的大小
        sc_fragment_01_category_title.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, displayMetrics));
        // 设置Tab Indicator的颜色
        sc_fragment_01_category_title.setIndicatorColor(getResources().getColor(R.color.color_top));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        sc_fragment_01_category_title.setSelectedTextColor(getResources().getColor(R.color.color_top));
        // 取消点击Tab时的背景色
        sc_fragment_01_category_title.setTabBackground(0);


        sc_fragment_01_category_title.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < fragments.size(); i++) {
                    if (position == i){
                        if (i == 0){
                            frag_main.refreshDataFromServer();
                        }else{
                            arrFragment[i-1].refreshDataFromServer();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




//
//
//
//        sc_fragment_01_category.setViewPager(context, titles, vp_fragment_01_category
//                , R.color.color_grey, R.color.colorLogo
//                , 12, 12, 8, true
//                , R.color.color_grey, 0f, 15f, 15f, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        sc_fragment_01_category.setBgLine(context, 1, R.color.transparent);
//        sc_fragment_01_category.setNavLine(getActivity(), 3, R.color.color_red);
//
//        sc_fragment_01_category.setOnTitleClickListener(new NavitationScrollLayout.OnTitleClickListener() {
//            @Override
//            public void onTitleClick(View v) {
//
//            }
//        });
//
//        sc_fragment_01_category.setOnNaPageChangeListener(new NavitationScrollLayout.OnNaPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                view_pos = position;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }


    private void init() {
        sc_fragment_01_category_title = (PagerSlidingTabStrip) mView.findViewById(R.id.sc_fragment_01_category_title);
        vp_fragment_01_category = (ViewPager) mView.findViewById(R.id.vp_fragment_01_category);
//        sc_fragment_01_category = (NavitationScrollLayout) mView.findViewById(R.id.sc_fragment_01_category);
        imv_top_left = (ImageView) mView.findViewById(R.id.imv_top_left);
        tv_top_right = (TextView) mView.findViewById(R.id.tv_top_right);
    }

    public void viewpagerJump(int curr){
        vp_fragment_01_category.setCurrentItem(curr);
    }


    /**
     * 展开排序窗口
     */
    private void showMenuforOrder(){

        if (pop_ll_order != null && pop_ll_order.isShowing()) {
            pop_ll_order.dismiss();
        } else {
            layout_order = getActivity().getLayoutInflater().inflate(R.layout.pop_order, null);

            TextView pop_order_default = (TextView) layout_order.findViewById(R.id.pop_order_default);
            TextView pop_order_volum_ace = (TextView) layout_order.findViewById(R.id.pop_order_volum_ace);
            TextView pop_order_volum_des = (TextView) layout_order.findViewById(R.id.pop_order_volum_des);
            TextView pop_order_roll_asc = (TextView) layout_order.findViewById(R.id.pop_order_roll_asc);
            TextView pop_order_roll_desc = (TextView) layout_order.findViewById(R.id.pop_order_roll_desc);

            TextView[] tvs = new TextView[]{pop_order_default,pop_order_roll_asc,pop_order_roll_desc,
                    pop_order_volum_des,pop_order_volum_ace };
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
                    sendOrderToFragment();
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
                    sendOrderToFragment();
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
                    sendOrderToFragment();
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
                    sendOrderToFragment();
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
                    sendOrderToFragment();
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
     * 告诉fragment
     */
    private void sendOrderToFragment(){
        view_pos = vp_fragment_01_category.getCurrentItem();

        Log.i("Tag",fragments.size() + "");
        for (int i = 0; i < fragments.size(); i++) {
            if (view_pos == i){
                if (i == 0){
                    frag_main.orderList(tag,order);
                }else{
                    arrFragment[i-1].orderList(tag,order);
                }
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_top_left:
                toSign();//签到
                break;
            case R.id.tv_top_right:
                showMenuforOrder();
                break;
        }
    }

    //告诉fragment 数据库有新数据
    public void setNew(){
        for (int i = 0; i < fragments.size(); i++) {

                if (i == 0){
                    frag_main.new_data = 1;
                }else{
                    arrFragment[i-1].new_data = 1;
                }

        }

        MainActivity main = (MainActivity) getActivity();
        main.fragment_02.new_data = 1;
    }
}
