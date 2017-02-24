package com.example.s.why_no.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.Address;
import com.example.s.why_no.bean.ExchangeStatus;
import com.example.s.why_no.bean.IntegralGoods;
import com.example.s.why_no.servlet.GetAddressServlet;
import com.example.s.why_no.servlet.ToIntegralExchangeServlet;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by S on 2016/11/11.
 */

public class SureExchangeActivity extends Activity implements View.OnClickListener{
    //配置ImageLoader参数
    DisplayImageOptions options = options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
            .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private IntegralGoods goods;

    private ImageView imv_sure_exchange_back;
    private ImageView imv_sure_exchange_pic;
    private TextView tv_sure_exchange_title;
    private TextView tv_sure_exchange_defalut_message;
    private TextView tv_integral_goods_details_need;
    private RadioButton rb_sure_exchange_default_message;
    private RadioButton rb_sure_exchange_new_message;
    private int rb_isCheck = 0;//0 默认信息rb被选  新信息rb被选
    private LinearLayout ll_sure_exchange_new_message;
    private TextView tv_sure_exchange_sure;

    private EditText edt_sure_exchange_name;
    private EditText edt_sure_exchange_tel;
    private EditText edt_sure_exchange_city;
    private EditText edt_sure_exchange_address;
    private String name = "";
    private String tel = "";
    private String city = "";
    private String more = "";


    private String phone = "-1";//判断是否登录
    private Address address ;//对应的默认地址信息

    private final int GET_ADDRESS = 122;
    private final int SURE_EXCHANGE = 56;
    private final int ERROR_REQUEST = 66;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_ADDRESS:
                    buildDefalut(address);
                    break;
                case SURE_EXCHANGE:
                    tv_sure_exchange_sure.setEnabled(true);
                    ExchangeStatus exchangeStatus = (ExchangeStatus) msg.obj;
                    Log.e("兑换json：" ,exchangeStatus.toString());
                    if (exchangeStatus.error == 1){
                        Toast.makeText(SureExchangeActivity.this,"兑换成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else if(exchangeStatus.error == 0){
                        Toast.makeText(SureExchangeActivity.this,"兑换失败,积分不足",Toast.LENGTH_SHORT).show();
                    }else if(exchangeStatus.error == 2){
                        Toast.makeText(SureExchangeActivity.this,"积分扣除失败，请联系客服",Toast.LENGTH_SHORT).show();
                    }else if(exchangeStatus.error == 3){
                        Toast.makeText(SureExchangeActivity.this,"积分记录插入失败，请联系客服",Toast.LENGTH_SHORT).show();
                    }else if(exchangeStatus.error == 4){
                        Toast.makeText(SureExchangeActivity.this,"兑换记录插入失败，请联系客服",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case  ERROR_REQUEST:
                    Toast.makeText(SureExchangeActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    tv_sure_exchange_sure.setEnabled(true);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sure_exchange);

        init();
        initView();
        initEvent();

    }

    /**
     * 发送请求
     */
    private void postExchange(final String str_name, final String str_tel
            , final String str_city, final String str_more){

        new Thread(new Runnable() {
            @Override
            public void run() {
                ToIntegralExchangeServlet toIntegralExchangeServlet
                        = new ToIntegralExchangeServlet();

                System.out.println("phone  :" + phone);
                System.out.println("need  :" + goods.need);
                System.out.println("name  :" + goods.name);
                System.out.println("str_name  :" + str_name);
                System.out.println("str_tel  :" + str_tel);
                System.out.println("str_city  :" + str_city);
                System.out.println("str_more  :" + str_more);
                System.out.println("id  :" + goods.id);
                System.out.println("img  :" + goods.img);

                String json = toIntegralExchangeServlet.getJson(phone,goods.need,goods.name
                        ,str_name,str_tel,str_city,str_more,goods.id,goods.img);


                System.out.println("发送请求json" + json);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else{
                    Gson gson = new Gson();
                    ExchangeStatus exStatus = gson.fromJson(json,ExchangeStatus.class);


                    msg.obj = exStatus;
                    msg.what = SURE_EXCHANGE;

                }
                handler.sendMessage(msg);
            }
        }).start();

    }


    /**
     * 判断能否兑换
     */
    private void exChange() {
        if (phone.equals("-1")){
            //还没登录
            Toast.makeText(SureExchangeActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
            startActivityForResult((new Intent(SureExchangeActivity.this, UserLoginActivity.class)),101);
            finish();
        }else{
            tv_sure_exchange_sure.setEnabled(false);
            if (rb_isCheck == 0){//默认地址
                if (name == null|| tel == null||city == null||more == null){
                    tv_sure_exchange_sure.setEnabled(true);
                    Toast.makeText(SureExchangeActivity.this,"请选择一种收件人信息",Toast.LENGTH_SHORT).show();
                }else if(name.equals("")||tel.equals("")||city.equals("")||more.equals("")){
                    tv_sure_exchange_sure.setEnabled(true);
                    Toast.makeText(SureExchangeActivity.this,"请选择一种收件人信息",Toast.LENGTH_SHORT).show();
                }else{
                    postExchange(name,tel,city,more);
                }


            }else{//新地址
                switch (isNull()){
                    case 4:
                        Toast.makeText(SureExchangeActivity.this,"收货人不能为空",Toast.LENGTH_SHORT).show();
                        tv_sure_exchange_sure.setEnabled(true);
                        break;
                    case 3:
                        Toast.makeText(SureExchangeActivity.this,"手机号码不能为空",Toast.LENGTH_SHORT).show();
                        tv_sure_exchange_sure.setEnabled(true);
                        break;
                    case 2:
                        Toast.makeText(SureExchangeActivity.this,"所属市区不能为空",Toast.LENGTH_SHORT).show();
                        tv_sure_exchange_sure.setEnabled(true);
                        break;
                    case 1:
                        Toast.makeText(SureExchangeActivity.this,"详细地址不能为空",Toast.LENGTH_SHORT).show();
                        tv_sure_exchange_sure.setEnabled(true);
                        break;
                    case 0:
                        String name_edt = edt_sure_exchange_name.getText().toString();
                        String tel_edt = edt_sure_exchange_tel.getText().toString();
                        String city_edt = edt_sure_exchange_city.getText().toString();
                        String address_edt = edt_sure_exchange_address.getText().toString();
                        if (name_edt == null||tel_edt == null||city_edt == null||address_edt == null){

                        }else if (name_edt.equals("")||tel_edt.equals("")||city_edt.equals("")||address_edt.equals("")){

                        }else{
                            postExchange(name_edt,tel_edt,city_edt,address_edt);
                        }

                        break;
                }
            }


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                if (resultCode == RESULT_OK) {


                }
                break;

        }

    }

    /**
     * 判断新地址是否有空
     * 0 正确
     * 1 收件人为空
     * 2 手机号码为空
     * 3 所属市区为空
     * 4 详细地址为空
     * @return
     */
    public int isNull() {
        int mess = 0;

        if (edt_sure_exchange_name.getText().toString().equals("")){
            mess = 1;
        }else if(edt_sure_exchange_tel.getText().toString().equals("")){
            mess = 2;
        }else if(edt_sure_exchange_city.getText().toString().equals("")){
            mess = 3;
        }else if(edt_sure_exchange_address.getText().toString().equals("")){
            mess = 4;
        }

        return mess;
    }

    private void buildDefalut(Address obj) {

        name = obj.delivery.get(0).name;
        tel = obj.delivery.get(0).tel;
        city = obj.delivery.get(0).region;
        more = obj.delivery.get(0).address;

        System.out.println("name:  " + name);
        System.out.println("tel:  " + tel);
        System.out.println("city:  " + city);
        System.out.println("more:  " + more);

        if (name == null|| tel == null||city == null||more == null){

            tv_sure_exchange_defalut_message.setText("请先去个人中心保存收货地址");
            rb_sure_exchange_default_message.setEnabled(false);
            rb_sure_exchange_default_message.setChecked(false);

        }else if(name.equals("")||tel.equals("")||city.equals("")||more.equals("")){
            tv_sure_exchange_defalut_message.setText("请先去个人中心保存收货地址");
            rb_sure_exchange_default_message.setEnabled(false);
            rb_sure_exchange_default_message.setChecked(false);
        }else{
            //        String text = " 所属市区+详细地址 ，收件人 姓名 联系方式 12345678900"
            tv_sure_exchange_defalut_message
                    .setText(city
                            + " " + more
                            + "，收件人" + name
                            + " 联系方式 "+ tel);
        }



    }
    /**
     * 获取地址
     */
    public void getAdress() {
        if (phone.equals("-1")){
            tv_sure_exchange_defalut_message.setText("请先去个人中心保存收货地址");
            rb_sure_exchange_default_message.setEnabled(false);
            rb_sure_exchange_default_message.setChecked(false);
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GetAddressServlet getAddressServlet = new GetAddressServlet();
                    String json = getAddressServlet.getJson(phone);
                    Message msg = new Message();
                    if (json.equals("error")){
                        msg.what = ERROR_REQUEST;
                    }else {

                        Gson gson = new Gson();
                        address = gson.fromJson(json, Address.class);
                        msg.what = GET_ADDRESS;

                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }

    }

    private void initEvent() {
        imv_sure_exchange_back.setOnClickListener(this);
        rb_sure_exchange_default_message.setOnClickListener(this);
        rb_sure_exchange_new_message.setOnClickListener(this);
        tv_sure_exchange_sure.setOnClickListener(this);
    }

    private void initView() {

        getGoods();//获取传递过来的商品信息
        getLogin();//判断是否登录
        getAdress();//获取填写过的默认地址
    }

    /**
     * 构筑传递过来的商品信息
     */
    private void getGoods() {
        Intent intent = getIntent();
        String integralGoods = intent.getStringExtra("integralGoods");
        Gson gson = new Gson();
        goods = gson.fromJson(integralGoods,IntegralGoods.class);

        ImageLoader.getInstance().displayImage(goods.img
                ,  imv_sure_exchange_pic, options);
        tv_sure_exchange_title.setText(goods.name);
        tv_integral_goods_details_need.setText(goods.need + "");
    }

    private void init() {
        imv_sure_exchange_back = (ImageView) findViewById(R.id.imv_sure_exchange_back);
        imv_sure_exchange_pic = (ImageView) findViewById(R.id.imv_sure_exchange_pic);
        tv_sure_exchange_title = (TextView) findViewById(R.id.tv_sure_exchange_title);
        rb_sure_exchange_default_message = (RadioButton) findViewById(R.id.rb_sure_exchange_default_message);
        rb_sure_exchange_new_message = (RadioButton) findViewById(R.id.rb_sure_exchange_new_message);
        ll_sure_exchange_new_message = (LinearLayout) findViewById(R.id.ll_sure_exchange_new_message);
        edt_sure_exchange_name = (EditText) findViewById(R.id.edt_sure_exchange_name);
        edt_sure_exchange_tel = (EditText) findViewById(R.id.edt_sure_exchange_tel);
        edt_sure_exchange_city = (EditText) findViewById(R.id.edt_sure_exchange_city);
        edt_sure_exchange_address = (EditText) findViewById(R.id.edt_sure_exchange_address);
        tv_sure_exchange_defalut_message = (TextView) findViewById(R.id.tv_sure_exchange_defalut_message);
        tv_sure_exchange_sure = (TextView) findViewById(R.id.tv_sure_exchange_sure);
        tv_integral_goods_details_need = (TextView) findViewById(R.id.tv_integral_goods_details_need);
    }

    private void changeClick(int click){
        if (click == 0){

//            name = edt_sure_exchange_name.getText().toString();
//            tel = edt_sure_exchange_name.getText().toString();
//            city = edt_sure_exchange_name.getText().toString();
//            more = edt_sure_exchange_name.getText().toString();

            rb_sure_exchange_default_message.setChecked(false);
            rb_sure_exchange_new_message.setChecked(true);
            rb_isCheck = 1;
            ll_sure_exchange_new_message.setVisibility(View.VISIBLE);
        }else{

//            name = address.delivery.get(0).name;
//            tel = address.delivery.get(0).tel;
//            city = address.delivery.get(0).region;
//            more = address.delivery.get(0).address;

            rb_sure_exchange_default_message.setChecked(true);
            rb_sure_exchange_new_message.setChecked(false);
            rb_isCheck = 0;
            ll_sure_exchange_new_message.setVisibility(View.GONE);
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_sure_exchange_back:
                finish();
                break;
            case R.id.rb_sure_exchange_default_message:
                changeClick(1);
                break;
            case R.id.rb_sure_exchange_new_message:
                changeClick(0);
                break;
            case R.id.tv_sure_exchange_sure:
                exChange();
                break;
        }
    }

    public void getLogin() {
        ShareLoginData share = new ShareLoginData(this);
        phone = share.isLogin();
    }


}
