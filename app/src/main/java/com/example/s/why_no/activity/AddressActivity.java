package com.example.s.why_no.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.Address;
import com.example.s.why_no.bean.Status;
import com.example.s.why_no.servlet.GetAddressServlet;
import com.example.s.why_no.servlet.SaveAddressServlet;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.google.gson.Gson;

/**
 * Created by S on 2016/11/7.
 */

public class AddressActivity extends Activity implements View.OnClickListener{

    private ImageView imv_adress_back;
//    private ImageView imv_adress_edit;
//    private TextView tv_adress_edit;
    private Button bt_save_address_sure;
    private final int GET_ADDRESS = 45;
    private final int SAVE_ADDRESS = 65;
    private final int ERROR_REQUEST = 30;

    private EditText edt_address_name;
    private EditText edt_address_tel;
    private EditText edt_address_city;
    private EditText edt_address_more;


    private String name = "";
    private String tel = "";
    private String city = "";
    private String more = "";

    private boolean able = true;//标记是否处于允许编辑的状态

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_ADDRESS:
                    buildList((Address) msg.obj);
                    break;
                case SAVE_ADDRESS:
                    bt_save_address_sure.setEnabled(true);
                    Status status = (Status) msg.obj;
                    if (status.error == 0){
                        showWindow();
//                        Toast.makeText(AddressActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                    }else if(status.error == 1){
//                        Toast.makeText(AddressActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                        showWindow();
                    }else {

                    }
//                    changeEnable(able);
//                    bt_save_address_sure.setEnabled(true);
//                    bt_save_address_sure.setBackgroundResource(R.drawable.btn_accent);
                    break;
                case ERROR_REQUEST:
                    bt_save_address_sure.setEnabled(true);
                    Toast.makeText(AddressActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        init();
        initView();
        initEvent();

    }

    /**
     * 修改成功 提示框
     */
    private void showWindow() {
        bt_save_address_sure.setEnabled(true);

        View dialogView = View.inflate(AddressActivity.this, R.layout.dialog_prompt, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        TextView tv_sure = (TextView) dialogView.findViewById(R.id.tv_sure);

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });

        alertDialog.show();
    }

    /**
     * 上传修改后的地址到服务器
     */
    private void saveAddress(){
        bt_save_address_sure.setEnabled(false);
//        bt_save_address_sure.setBackgroundResource(R.drawable.btn_gray);
        switch (isNull()){
            case 5:
//                showWindow();
//                changeEnable(able);
//                Toast.makeText(AddressActivity.this,"",Toast.LENGTH_SHORT).show();
                updateAddressFromEdt();
                break;
            case 4:
                Toast.makeText(AddressActivity.this,"详细地址不能为空",Toast.LENGTH_SHORT).show();
                bt_save_address_sure.setEnabled(true);
//                bt_save_address_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 3:
                Toast.makeText(AddressActivity.this,"所在省市区不能为空",Toast.LENGTH_SHORT).show();
                bt_save_address_sure.setEnabled(true);
//                bt_save_address_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 2:
                Toast.makeText(AddressActivity.this,"收件人手机号不能为空",Toast.LENGTH_SHORT).show();
                bt_save_address_sure.setEnabled(true);
//                bt_save_address_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 1:
                Toast.makeText(AddressActivity.this,"收货人姓名不能为空",Toast.LENGTH_SHORT).show();
                bt_save_address_sure.setEnabled(true);
//                bt_save_address_sure.setBackgroundResource(R.drawable.btn_accent);
                break;
            case 0:
                updateAddress();
//                showWindow();
                break;
        }
    }

    private void updateAddressFromEdt() {
        ShareLoginData shareLoginData = new ShareLoginData(this);
        final String phone = shareLoginData.isLogin();
        if (phone.equals("-1")){
            bt_save_address_sure.setEnabled(true);
            Toast.makeText(AddressActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
            startActivityForResult((new Intent(AddressActivity.this, UserLoginActivity.class)),666);

//            bt_save_address_sure.setBackgroundResource(R.drawable.btn_accent);
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SaveAddressServlet saveAddressServlet = new SaveAddressServlet();
                    String json = saveAddressServlet.getJson(phone
                            ,edt_address_name.getText().toString()
                            ,edt_address_tel.getText().toString()
                            ,edt_address_city.getText().toString()
                            ,edt_address_more.getText().toString());

                    Message msg = new Message();
                    if (json.equals("error")){
                        msg.what = ERROR_REQUEST;
                    }else{
                        Gson gson = new Gson();
                        Status status = gson.fromJson(json,Status.class);
                        msg.obj = status;
                        msg.what = SAVE_ADDRESS;
                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }

    }

    /**
     * 上传更新地址
     */
    private void updateAddress() {

        ShareLoginData shareLoginData = new ShareLoginData(this);
        final String phone = shareLoginData.isLogin();
        if (phone.equals("-1")){
            bt_save_address_sure.setEnabled(true);
            Toast.makeText(AddressActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
            startActivityForResult((new Intent(AddressActivity.this, UserLoginActivity.class)),666);

//            bt_save_address_sure.setBackgroundResource(R.drawable.btn_accent);
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SaveAddressServlet saveAddressServlet = new SaveAddressServlet();
                    String json = saveAddressServlet.getJson(phone,name,tel,city,more);

                    System.out.println("phone:" + phone);
                    System.out.println("name:" + name);
                    System.out.println("tel:" + tel);
                    System.out.println("city:" + city);
                    System.out.println("more:" + more);
                    Message msg = new Message();

                    Log.e("ADDRESS",json);
                    if (json.equals("error")){
                        msg.what = ERROR_REQUEST;
                    }else{
                        Gson gson = new Gson();
                        Status status = gson.fromJson(json,Status.class);
                        msg.obj = status;
                        msg.what = SAVE_ADDRESS;

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
            case 666:
                if (resultCode == RESULT_OK) {
                    bt_save_address_sure.setEnabled(true);
                    initView();//判断是否有用户信息保存在app，有则改变布局
                }
                break;


        }

    }
    /**
     * 判断输入框内容有没有做改变
     * @return
     */
    private boolean isChange(){

        if(!name.equals(edt_address_name.getText().toString())){
            return true;
        }else if(!tel.equals(edt_address_tel.getText().toString())){
            return true;
        }else if(!city.equals(edt_address_city.getText().toString())){
            return true;
        }else if(!more.equals(edt_address_more.getText().toString())){
            return true;
        }

        return false;
    }
    /**
     * 判断是否有空没填
     * 1 收货人姓名为空
     * 2 手机号码为空
     * 3 所在省市区为空
     * 4 详细地址为空
     * @return
     */
    private int isNull(){
        int mess = 0;

        name = edt_address_name.getText().toString();
        tel = edt_address_tel.getText().toString();
        city = edt_address_city.getText().toString();
        more = edt_address_more.getText().toString();
        if (name.equals("")){
            mess = 1;
        }else if (tel.equals("")){
            mess = 2;
        }else if (city.equals("")){
            mess = 3;
        }else if (more.equals("")){
            mess = 4;
        }
        return  mess;
    }
    /**
     * 获取地址
     * 不过要先判断是否登录
     */
    public void getAddress(final String phone) {

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
                    Address address = gson.fromJson(json, Address.class);


                    msg.obj = address;
                    msg.what = GET_ADDRESS;

                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void  buildList(Address address){
        name = address.delivery.get(0).name;
        tel = address.delivery.get(0).tel;
        city = address.delivery.get(0).region;
        more = address.delivery.get(0).address;

        edt_address_name.setText(name);
        edt_address_tel.setText(tel);
        edt_address_city.setText(city);
        edt_address_more.setText(more);
    }

    private void initEvent() {
        imv_adress_back.setOnClickListener(this);
//        tv_adress_edit.setOnClickListener(this);
        bt_save_address_sure.setOnClickListener(this);
    }

    private void initView() {

        ShareLoginData shareLoginData = new ShareLoginData(this);
        String phone = shareLoginData.isLogin();
        if (phone.equals("-1")){

        }else{//已经登录
            getAddress(phone);
        }
    }

    private void init() {
        imv_adress_back = (ImageView) findViewById(R.id.imv_adress_back);
//        tv_adress_edit = (TextView) findViewById(R.id.tv_adress_edit);
        bt_save_address_sure = (Button) findViewById(R.id.bt_save_address_sure);

        edt_address_name = (EditText) findViewById(R.id.edt_address_name);
        edt_address_tel = (EditText) findViewById(R.id.edt_address_tel);
        edt_address_city = (EditText) findViewById(R.id.edt_address_city);
        edt_address_more = (EditText) findViewById(R.id.edt_address_more);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_adress_back:
                finish();
                break;
//            case R.id.tv_adress_edit:
//                changeEnable(able);
//                break;
            case R.id.bt_save_address_sure:
                saveAddress();
                break;
        }
    }

//    /**
//     * 更改edittext的可编辑状态
//     */
//    private void changeEnable(boolean able_save) {
//
//        edt_address_name.setEnabled(able_save);
//        edt_address_tel.setEnabled(able_save);
//        edt_address_city.setEnabled(able_save);
//        edt_address_more.setEnabled(able_save);
//        bt_save_address_sure.setEnabled(able_save);
////        if (able_save){
////            tv_adress_edit.setText("取消");
////            bt_save_address_sure.setBackgroundResource(R.drawable.btn_accent);
////        }else{
////            tv_adress_edit.setText("编辑");
////            bt_save_address_sure.setBackgroundResource(R.drawable.btn_gray);
////        }
//        able = !able_save;
//    }


}
