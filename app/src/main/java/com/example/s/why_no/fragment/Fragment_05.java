package com.example.s.why_no.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.activity.AboutActivity;
import com.example.s.why_no.activity.AddressActivity;
import com.example.s.why_no.activity.IntegralExchangeActivity;
import com.example.s.why_no.activity.IntegralRecordActivity;
import com.example.s.why_no.activity.IntegralShopActivity;
import com.example.s.why_no.activity.MainActivity;
import com.example.s.why_no.activity.MessageActivity;
import com.example.s.why_no.activity.OpinionActivity;
import com.example.s.why_no.activity.UserLoginActivity;
import com.example.s.why_no.activity.UserRegisterActivity;
import com.example.s.why_no.bean.IntegralRecord;
import com.example.s.why_no.servlet.GetIntegralRecordServlet;
import com.example.s.why_no.utils_clean.DataCleanManager;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by S on 2016/10/30.
 */

public class Fragment_05 extends Fragment implements View.OnClickListener{


    public int isCreat = 0;
    private View mView;
    private Context context;
    private View popView;
    private PopupWindow pop_ll_cancel;
    private static Fragment instance;

    private TextView tv_fragment_05_gerenzhongxin;
    private LinearLayout ll_fragment_05_dengluzhuce;
    private TextView tv_fragment_05_yonghuxinxi;
    private TextView tv_fragment_05_id;
    private TextView tv_fragment_05_cancel;

    private LinearLayout ll_fragment_05_zongjifen;
    private TextView tv_fragment_05_all_integral;

    private TextView tv_fragment_05_login;
    private TextView tv_fragment_05_register;

    private ImageView imv_fragment_05_to_message;
    private ImageView imv_fragment_05_to_address;
    private ImageView imv_fragment_05_to_collect;
    private ImageView imv_fragment_05_to_integral_record;
    private ImageView imv_fragment_05_to_integral_shop;
    private ImageView imv_fragment_05_to_integral_exchange;
    private ImageView imv_fragment_05_to_about;
    private ImageView imv_fragment_05_to_integral_feedback;
    private ImageView imv_fragment_05_clean;


    public Fragment_05() {

    }
    public static Fragment_05 getInstance() {
        Fragment_05 newNewsFragment = new Fragment_05();
        return newNewsFragment;
    }

    private final int GET_RECORD = 55;
    private final int ERROR_REQIEST = 66;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_RECORD:
                    IntegralRecord integralRecord = (IntegralRecord) msg.obj;
                    if (integralRecord.error == 1){
                        buildList(integralRecord);
                    }else if(integralRecord.error == 0){
                        Toast.makeText(context,"获取积分失败，请联系客服",Toast.LENGTH_SHORT).show();
                    }else if(integralRecord.error == 2){
                        tv_fragment_05_all_integral.setText("0");
                    }
                    break;
                case ERROR_REQIEST:
                    Toast.makeText(context,"网络错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_05, null);
        context = getActivity().getApplicationContext();

//        CustomProgress.show(getActivity(), "正在加载...", false,
//                null);//加载中的动画
        isCreat = 1;
        init();
        initView();
        initEvent();
        return mView;
    }

    private void initEvent() {
        tv_fragment_05_login.setOnClickListener(this);
        tv_fragment_05_register.setOnClickListener(this);
        tv_fragment_05_cancel.setOnClickListener(this);

        imv_fragment_05_to_message.setOnClickListener(this);
        imv_fragment_05_to_address.setOnClickListener(this);
        imv_fragment_05_to_collect.setOnClickListener(this);
        imv_fragment_05_to_integral_record.setOnClickListener(this);
        imv_fragment_05_to_integral_shop.setOnClickListener(this);
        imv_fragment_05_to_integral_exchange.setOnClickListener(this);
        imv_fragment_05_to_about.setOnClickListener(this);
        imv_fragment_05_to_integral_feedback.setOnClickListener(this);
        imv_fragment_05_clean.setOnClickListener(this);

    }

    private void initView() {
        hasUserDataToChangeUI();//判断是否有用户信息保存在app，有则改变布局
    }


    private void init() {

        tv_fragment_05_yonghuxinxi = (TextView) mView.findViewById(R.id.tv_fragment_05_yonghuxinxi);
        tv_fragment_05_id = (TextView) mView.findViewById(R.id.tv_fragment_05_id);
        tv_fragment_05_gerenzhongxin = (TextView) mView.findViewById(R.id.tv_fragment_05_gerenzhongxin);
        ll_fragment_05_dengluzhuce = (LinearLayout) mView.findViewById(R.id.ll_fragment_05_dengluzhuce);
        tv_fragment_05_login = (TextView) mView.findViewById(R.id.tv_fragment_05_login);
        tv_fragment_05_register = (TextView) mView.findViewById(R.id.tv_fragment_05_register);
        tv_fragment_05_cancel = (TextView) mView.findViewById(R.id.tv_fragment_05_cancel);

        tv_fragment_05_all_integral = (TextView) mView.findViewById(R.id.tv_fragment_05_all_integral);
        ll_fragment_05_zongjifen = (LinearLayout) mView.findViewById(R.id.ll_fragment_05_zongjifen);

        imv_fragment_05_to_message = (ImageView) mView.findViewById(R.id.imv_fragment_05_to_message);
        imv_fragment_05_to_address = (ImageView) mView.findViewById(R.id.imv_fragment_05_to_address);
        imv_fragment_05_to_collect = (ImageView) mView.findViewById(R.id.imv_fragment_05_to_collect);
        imv_fragment_05_to_integral_record = (ImageView) mView.findViewById(R.id.imv_fragment_05_to_integral_record);
        imv_fragment_05_to_integral_shop = (ImageView) mView.findViewById(R.id.imv_fragment_05_to_integral_shop);
        imv_fragment_05_to_integral_exchange = (ImageView) mView.findViewById(R.id.imv_fragment_05_to_integral_exchange);
        imv_fragment_05_to_about = (ImageView) mView.findViewById(R.id.imv_fragment_05_to_about);
        imv_fragment_05_to_integral_feedback = (ImageView) mView.findViewById(R.id.imv_fragment_05_to_integral_feedback);
        imv_fragment_05_clean = (ImageView) mView.findViewById(R.id.imv_fragment_05_clean);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_fragment_05_login:
                startActivityForResult((new Intent(context, UserLoginActivity.class)),666);
                break;
            case R.id.tv_fragment_05_register:
                startActivityForResult((new Intent(context, UserRegisterActivity.class)),888);
                break;
            case R.id.tv_fragment_05_cancel:
                showPopfromUser();
//                //注销用户信息
//                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("lws",MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("phone", "-1");
//                editor.commit();
//                hasUserDataToChangeUI();
                break;
            case R.id.imv_fragment_05_to_message:
                startActivity(new Intent(context, MessageActivity.class));
                break;
            case R.id.imv_fragment_05_to_address:
                startActivity(new Intent(context, AddressActivity.class));
                break;
            case R.id.imv_fragment_05_to_collect:
//                startActivity(new Intent(context, IntegralGoodsActivity.class));
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.change(3);
                break;
            case R.id.imv_fragment_05_to_integral_record:
                if (hasUserDataToVisit()){
                    startActivity(new Intent(context, IntegralRecordActivity.class));
                }else{
                    Toast.makeText(context,"尊敬的用户，请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imv_fragment_05_to_integral_shop:
                startActivity(new Intent(context, IntegralShopActivity.class));
                break;
            case R.id.imv_fragment_05_to_integral_exchange:
                if (hasUserDataToVisit()){
                    startActivity(new Intent(context, IntegralExchangeActivity.class));
                }else{
                    Toast.makeText(context,"尊敬的用户，请先登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imv_fragment_05_to_about:
                startActivity(new Intent(context, AboutActivity.class));
                break;
            case R.id.imv_fragment_05_to_integral_feedback:
                startActivity(new Intent(context, OpinionActivity.class));
                break;
            case R.id.imv_fragment_05_clean:
                showWindowClean();//展开清空缓存的窗口
                break;
        }
    }

    /**
     * 展开清空缓存的窗口
     */
    private void showWindowClean() {

        View dialogView = View.inflate(context, R.layout.dialog_clean, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setView(dialogView);

        TextView tv_sure = (TextView) dialogView.findViewById(R.id.tv_sure);
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCleanManager manager = new DataCleanManager();
                manager.clearAllCache(context);
                hasUserDataToChangeUI();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();


    }

    /**
     * 判断是否有用户信息保存在app而更改访问下面功能的权限
     */
    private boolean hasUserDataToVisit(){
        ShareLoginData shareLoginData = new ShareLoginData(context);
        String phone = shareLoginData.isLogin();
        if (phone.equals("-1")){
            return false;
        }else{
            return true;
        }
    }
    /**
     * 判断是否有用户信息保存在app而更改顶部ui
     */
    public void hasUserDataToChangeUI(){

        MainActivity main = (MainActivity) getActivity();
        if (1 == main.fragment_04.isCreat){
            if (main.fragment_04.flag == true){
                main.fragment_04.flag = !main.fragment_04.flag;
                main.fragment_04.readyDelete(main.fragment_04.flag);
            }
        }

        ShareLoginData shareLoginData = new ShareLoginData(context);
        String phone = shareLoginData.isLogin();
        if (phone.equals("-1")){
            tv_fragment_05_gerenzhongxin.setVisibility(getView().VISIBLE);
            ll_fragment_05_dengluzhuce.setVisibility(getView().VISIBLE);
            tv_fragment_05_id.setVisibility(getView().INVISIBLE);
            tv_fragment_05_yonghuxinxi.setVisibility(getView().INVISIBLE);
            tv_fragment_05_cancel.setVisibility(getView().INVISIBLE);
            tv_fragment_05_login.setEnabled(true);
            tv_fragment_05_register.setEnabled(true);
            tv_fragment_05_cancel.setEnabled(false);
            ll_fragment_05_zongjifen.setVisibility(getView().INVISIBLE);

            if (1 == main.fragment_04.isCreat){
                main.fragment_04.allList.clear();
                main.fragment_04.tempList.clear();
                if (main.fragment_04.cAdapter != null){
                    main.fragment_04.cAdapter.notifyDataSetChanged();
                }
            }
        }else{
            tv_fragment_05_login.setEnabled(false);
            tv_fragment_05_register.setEnabled(false);
            tv_fragment_05_cancel.setEnabled(true);
            tv_fragment_05_cancel.setVisibility(getView().VISIBLE);
            tv_fragment_05_gerenzhongxin.setVisibility(getView().INVISIBLE);
            ll_fragment_05_dengluzhuce.setVisibility(getView().INVISIBLE);
            tv_fragment_05_id.setText(phone);
            tv_fragment_05_id.setVisibility(getView().VISIBLE);
            tv_fragment_05_yonghuxinxi.setVisibility(getView().VISIBLE);
            ll_fragment_05_zongjifen.setVisibility(getView().VISIBLE);
            getIntegral(phone);//获取登录用户的总积分
        }

    }

    /**
     * 用户注销弹出提示
     */
    private void showPopfromUser() {

        View parent = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);

        if (pop_ll_cancel != null && pop_ll_cancel.isShowing()) {
            pop_ll_cancel.dismiss();
        } else {
            popView = getActivity().getLayoutInflater().inflate(R.layout.pop_user_cancel, null);

            Button bt_pop_sure = (Button) popView.findViewById(R.id.bt_pop_sure);
            Button bt_pop_cancel = (Button) popView.findViewById(R.id.bt_pop_cancel);
            LinearLayout pop_layout = (LinearLayout) popView.findViewById(R.id.pop_layout);

            bt_pop_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("lws",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phone", "-1");
                editor.commit();
                hasUserDataToChangeUI();
                    //隐藏弹出窗口
                    if (pop_ll_cancel != null && pop_ll_cancel.isShowing()) {
                        pop_ll_cancel.dismiss();
                    }
                }
            });
            bt_pop_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //隐藏弹出窗口
                    if (pop_ll_cancel != null && pop_ll_cancel.isShowing()) {
                        pop_ll_cancel.dismiss();
                    }
                }
            });


            // 创建弹出窗口
            pop_ll_cancel = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);


//            pop_ll_tag.setAnimationStyle(R.style.PopupAnimation);
            pop_ll_cancel.update();
            pop_ll_cancel.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            pop_ll_cancel.setTouchable(true); // 设置popupwindow可点击
            pop_ll_cancel.setOutsideTouchable(true); // 设置popupwindow外部可点击
            pop_ll_cancel.setFocusable(true); // 获取焦点
            pop_ll_cancel.setBackgroundDrawable(new ColorDrawable(0x30000000));
            pop_ll_cancel.setAnimationStyle(R.style.AnimBottom);
            pop_ll_cancel.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    pop_ll_cancel.dismiss();
                }
            });

            pop_ll_cancel.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            pop_ll_cancel.setTouchInterceptor(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    System.out.println("event:" + event);
                    // 如果点击了popupwindow的外部，popupwindow也会消失
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        pop_ll_cancel.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            popView.setOnTouchListener(new View.OnTouchListener() {//设置背景区域外为点击消失popwindow
                public boolean onTouch(View v, MotionEvent event) {

                    int height = popView.findViewById(R.id.pop_layout).getTop();
                    int y=(int) event.getY();
                    if(event.getAction()==MotionEvent.ACTION_UP){
                        if(y<height){
                            pop_ll_cancel.dismiss();
                        }
                    }
                    return true;
                }
            });
        }
    }
    /**
     * 获取登录用户的总积分
     */
    private void getIntegral(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetIntegralRecordServlet getIntegralRecordServlet = new GetIntegralRecordServlet();
                String json = getIntegralRecordServlet.getJson(phone);

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
    /**
     * 构筑积分记录的文本框
     */
    private void buildList(IntegralRecord integralRecord){

        int all = integralRecord.inte.get(0).integral;
        tv_fragment_05_all_integral.setText(all + "");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 555:
                if (resultCode == getActivity().RESULT_OK) {

//                    Intent intent = new Intent(context,MainActivity.class);
//                    intent.putExtra("curr", 3);
//                    startActivity(intent);
//                    getActivity().finish();
                }
                break;
            case 666:
                if (resultCode == getActivity().RESULT_OK) {
                    hasUserDataToChangeUI();//判断是否有用户信息保存在app，有则改变布局
                }
                break;
            case 888:
                if (resultCode == getActivity().RESULT_OK) {
                    hasUserDataToChangeUI();//判断是否有用户信息保存在app，有则改变布局
                }
                break;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        hasUserDataToChangeUI();//判断是否有用户信息保存在app，有则改变布局
    }

}
