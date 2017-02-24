package com.example.s.why_no.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.adapter.GoodsListAdapter;
import com.example.s.why_no.bean.CancelCollect;
import com.example.s.why_no.bean.Details;
import com.example.s.why_no.bean.Goods;
import com.example.s.why_no.bean.Status;
import com.example.s.why_no.servlet.AddCollectServlet;
import com.example.s.why_no.servlet.CancelCollectServlet;
import com.example.s.why_no.servlet.GoodsDetailsServlet;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.example.s.why_no.utils_window.ManageWindow;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by S on 2016/11/5.
 */

public class DetailsActivity extends Activity implements View.OnClickListener{


    private String uid;
    private String phone;
    private String list;
    private boolean isFlag;//标记是否被收藏了
    private final int ADD_COLLECT = 89;
    private final int CANCEL_COLLECT = 36;


    private Details details;
    private ImageView imv_goods_details_share;
    private ImageView imv_goods_details_back;
    private ImageView imv_goods_details_platform;//平台
    private ImageView imv_goods_details_picture;//图片
    private TextView tv_goods_details_title;//标题
    private TextView tv_goods_details_quan;//券后价
//    private TextView tv_goods_details_price;//原价
    private RelativeLayout tv_goods_details_toQuan;//去领券
    private TextView tv_goods_details_toTaobao;//去购买
    private TextView tv_goods_details_shopname;//商家名字
    private TextView tv_goods_details_category;//分类
    private TextView tv_goods_details_surplus;//库存量
    private ImageView tv_goods_details_collect;//收藏的星号
    private TextView tv_goods_details_collect_text;//收藏的文本
    private TextView tv_goods_details_roll;//优惠券的面值
    private RecyclerView rv_goods_details_more;
    private GoodsListAdapter gAdapter;
    private GridLayoutManager gridLayoutManager;

    private final int DATA = 54;
    private List<Goods> goodsList;
    private List<Goods> moreList;

    private final int ERROR_REQUEST = 66;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DATA:
                    details = (Details) msg.obj;
                    if (details.error == 1){
                        buildDetails(details);
                    }else{
                        Toast.makeText(DetailsActivity.this,"该优惠券信息已下架",Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }

                    break;
                case ADD_COLLECT:
                    Status status = (Status) msg.obj;
                    if (status.error == 0){
                        Toast.makeText(DetailsActivity.this,"收藏失败",Toast.LENGTH_SHORT).show();
                    }else{
                        //保存用户信息
                        SharedPreferences sharedPreferences = getSharedPreferences("lws",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone", phone);
                        editor.putString("list",list);
                        editor.commit();

                        tv_goods_details_collect.setImageResource(R.drawable.collect_on);
                        tv_goods_details_collect_text.setTextColor(getResources().getColor(R.color.color_collect_on));
                        isFlag = true;
                    }
                    tv_goods_details_collect.setEnabled(true);
                    break;
                case CANCEL_COLLECT:
                    CancelCollect cancelCollect = (CancelCollect) msg.obj;
                    if (cancelCollect.error == 0){
                        Toast.makeText(DetailsActivity.this,"取消收藏失败",Toast.LENGTH_SHORT).show();
                    }else{

                        tv_goods_details_collect.setImageResource(R.drawable.collect_off);
                        tv_goods_details_collect_text.setTextColor(getResources().getColor(R.color.color_collect_off));
                        isFlag = false;

                        if(cancelCollect.list.size() == 0){
                            list = "-1";
                        }
                        for (int i = 0; i < cancelCollect.list.size(); i++) {
                            if (i == 0){
                                list = cancelCollect.list.get(0).uid;
                            }else{
                                list = list + "_" + cancelCollect.list.get(i).uid;
                            }
                        }
                        //保存用户信息
                        SharedPreferences sharedPreferences = getSharedPreferences("lws",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone", phone);
                        editor.putString("list",list);
                        editor.commit();
                    }
                    tv_goods_details_collect.setEnabled(true);
                    break;
                case ERROR_REQUEST:
                    Toast.makeText(DetailsActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    tv_goods_details_collect.setEnabled(true);
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_details);


        init();
        initView();
        initEvent();

    }

    /**
     * 点击取消收藏
     */
    private void clickCollectToCancel() {
        ShareLoginData share = new ShareLoginData(this);
        phone = share.isLogin();
        list = share.collectionId();
        if (phone.equals("-1")){
            tv_goods_details_collect.setEnabled(true);
            Toast.makeText(DetailsActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
            startActivityForResult((new Intent(DetailsActivity.this, UserLoginActivity.class)),301);
        }else{
            cancelCollect();
        }
    }

    /**
     * 确认取消收藏
     */
    private void cancelCollect() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                CancelCollectServlet cancelCollectServlet = new CancelCollectServlet();
                String json = cancelCollectServlet.getJson(phone,uid);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else {
                    Gson gson = new Gson();
                    System.out.println("取消收藏json" + json);
                    CancelCollect cancelCollect = gson.fromJson(json, CancelCollect.class);

                    msg.obj = cancelCollect;
                    msg.what = CANCEL_COLLECT;

                }
                handler.sendMessage(msg);
            }
        }).start();

    }

    /**
     * 判断该商品是否被收藏
     */
    private boolean judgeCollect(){
        boolean flag = false;
        ShareLoginData share = new ShareLoginData(this);
        String phone= share.isLogin();
        String list = share.collectionId();
        if(phone.equals("-1")){
            flag = false;
        }else if (list.equals("-1")){
            flag = false;
        }else{
            String[] uids = list.split("_");
            for (int i = 0; i < uids.length; i++) {
                if (uids[i].equals(uid)){
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
    /**
     * 添加收藏
     */
    private void addCollect(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                AddCollectServlet addCollectServlet = new AddCollectServlet();
                String json = addCollectServlet.getJson(phone,uid);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else {

                    Gson gson = new Gson();
                    Status status = gson.fromJson(json, Status.class);
                    msg.obj = status;
                    msg.what = ADD_COLLECT;

                }
                handler.sendMessage(msg);
            }
        }).start();
    }
    /**
     * 点击收藏
     */
    private void clickCollectToAdd(){

        ShareLoginData share = new ShareLoginData(this);
        phone = share.isLogin();
        list = share.collectionId();
        if (phone.equals("-1")){
            tv_goods_details_collect.setEnabled(true);
            Toast.makeText(DetailsActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
            startActivityForResult((new Intent(DetailsActivity.this, UserLoginActivity.class)),302);
            return;
        }else if(list.equals("-1")){
            list = uid;
            addCollect();
        }else{
            list = list + "_" + uid;
            addCollect();
        }
    }
    /**
     * 构筑详情页面的列表
     */
    private void buildDetails(Details details) {

        goodsList = new ArrayList<Goods>();
        moreList = new ArrayList<Goods>();
        goodsList.addAll(details.goods);
        moreList.addAll(details.more);

        //配置ImageLoader参数
        DisplayImageOptions options = options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
                .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        /**
         * 构筑商品详情
         */

        ManageWindow mana = new ManageWindow();
        int windowWidth = mana.getWidth(this);
        int windowHeight = mana.getHeight(this);
        ViewGroup.LayoutParams  para = imv_goods_details_picture.getLayoutParams();
        para.height = windowWidth;
        para.width = windowWidth;
        imv_goods_details_picture.setLayoutParams(para);
        ImageLoader.getInstance().displayImage(goodsList.get(0).uimg
                 ,  imv_goods_details_picture, options);
        tv_goods_details_title.setText(goodsList.get(0).uname);
//        tv_goods_details_price.setText("¥" +goodsList.get(0).price);
        tv_goods_details_quan.setText("¥" + goodsList.get(0).roll);
        tv_goods_details_shopname.setText(goodsList.get(0).discount + "元");//本来是店铺名 后来改成优惠券价格
        tv_goods_details_category.setText( goodsList.get(0).price + "元");//本来是分类标签 后来改成原价
        tv_goods_details_surplus.setText("" + goodsList.get(0).volume);//本来是库存 后来改成销量
        tv_goods_details_roll.setText(goodsList.get(0).discount + "元");

        if (goodsList.get(0).platform.equals("天猫")){
            imv_goods_details_platform.setImageResource(R.drawable.tmall);
        }else if(goodsList.get(0).platform.equals("淘宝")){
            imv_goods_details_platform.setImageResource(R.drawable.taobao);
        }

        uid = goodsList.get(0).uid;
        if (judgeCollect()){//被收藏了
            tv_goods_details_collect.setImageResource(R.drawable.collect_on);
            tv_goods_details_collect_text.setTextColor(getResources().getColor(R.color.color_collect_on));
            isFlag = true;
        }else{//没被收藏
            tv_goods_details_collect.setImageResource(R.drawable.collect_off);
            tv_goods_details_collect_text.setTextColor(getResources().getColor(R.color.color_collect_off));

            isFlag = false;
        }

        /**
         * 构筑更多商品列表
         */
        gridLayoutManager = new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rv_goods_details_more.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        gridLayoutManager.setAutoMeasureEnabled(true);
        rv_goods_details_more.setHasFixedSize(true);
        rv_goods_details_more.setNestedScrollingEnabled(false);//false r1滑动不卡顿


        gAdapter = new GoodsListAdapter(moreList,this,windowWidth,windowHeight);
        rv_goods_details_more.setAdapter(gAdapter);
        gAdapter.setOnItemClickLitener(new GoodsListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(DetailsActivity.this, DetailsActivity.class);
                intent.putExtra("id",moreList.get(position).id);
                intent.putExtra("ification",moreList.get(position).ification);
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    public void getDetails() {
        Intent intent = getIntent();
        final int id = intent.getIntExtra("id",0);
        final String ification = intent.getStringExtra("ification");

        Log.e("TAG详情id    ","  " + id);
        Log.e("TAG详情ification    ","  " + ification);

        new Thread(new Runnable() {
            @Override
            public void run() {
                GoodsDetailsServlet goodsDetailsServlet = new GoodsDetailsServlet();
                String json = goodsDetailsServlet.getJson(id,ification);
                System.out.println("商品详情id :" + id);
                System.out.println("商品详情ification :" + ification);
                System.out.println("商品详情json:" + json);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ADD_COLLECT;
                }else {

                    Gson gson = new Gson();
                    Details detail = gson.fromJson(json, Details.class);
                    msg.obj = detail;
                    msg.what = DATA;
                }
                handler.sendMessage(msg);
            }
        }).start();

    }

    /**
     * 去淘宝/天猫的网站
     */
    private void toNet(String url,int tit){

        Intent intent = new Intent(DetailsActivity.this,NetActivity.class);
        intent.putExtra("url",url);
        if (tit == 1){ //淘宝
            intent.putExtra("title","淘宝领券购买页面");
        }else if(tit == 2){//领券
            intent.putExtra("title","淘宝领券购买页面");
        }
        startActivity(intent);
    }


    /**
     * 弹出分享的窗口
     */
    private void showShareWindow() {
        ShareSDK.initSDK(this);//初始化sdk
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
//        oks.setTitle("标题");
//        // titleUrl是标题的网络链接，QQ和QQ空间等使用
//        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
//
//        // 启动分享GUI
//        oks.show(this);


//        oks.setNotification(R.drawable.ic_launcher, menu.getContext().getString(R.string.app_name));
//        oks.setAddress("12345678901");
        oks.setTitle(details.goods.get(0).uname);
        oks.setTitleUrl("http://app.geishihui.com/wap/view/" + details.goods.get(0).uid);
        oks.setText(details.goods.get(0).text + "http://app.geishihui.com/wap/view/" + details.goods.get(0).uid);
//        oks.setImagePath(MainActivity.TEST_IMAGE);//本地图片
        oks.setImageUrl(details.goods.get(0).uimg);
        oks.setUrl("http://app.geishihui.com/wap/view/" + details.goods.get(0).uid);
//        oks.setFilePath(MainActivity.TEST_IMAGE);
//        oks.setComment("不知道是什么 先打上点东西");
        oks.setSite(getString(R.string.app_name));
        oks.setSiteUrl("http://app.geishihui.com/wap/view/" + details.goods.get(0).uid);
        oks.setVenueName("ShareSDK");
//        oks.setVenueDescription("This is a beautiful place!");
//        oks.setLatitude(23.056081f);
//        oks.setLongitude(113.385708f);
        oks.show(this);
////        oks.setSilent(silent);
////        if (platform != null) {
////            oks.setPlatform(platform);
////        }
//
//        // 去除注释，可令编辑页面显示为Dialog模式
////		oks.setDialogMode();
//
//        // 去除注释，在自动授权时可以禁用SSO方式
////		oks.disableSSOWhenAuthorize();
//
//        // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
////		oks.setCallback(new OneKeyShareCallback());
////        oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
//
//        // 去除注释，演示在九宫格设置自定义的图标
////		Bitmap logo = BitmapFactory.decodeResource(menu.getResources(), R.drawable.ic_launcher);
////		String label = menu.getResources().getString(R.string.app_name);
////		OnClickListener listener = new OnClickListener() {
////			public void onClick(View v) {
////				String text = "Customer Logo -- ShareSDK " + ShareSDK.getSDKVersionName();
////				Toast.makeText(menu.getContext(), text, Toast.LENGTH_SHORT).show();
////				oks.finish();
////			}
////		};
////		oks.setCustomerLogo(logo, label, listener);
//
////        oks.show(menu.getContext());
    }


    private void initEvent() {
        imv_goods_details_back.setOnClickListener(this);
        tv_goods_details_toQuan.setOnClickListener(this);
        tv_goods_details_toTaobao.setOnClickListener(this);
        tv_goods_details_collect.setOnClickListener(this);
        imv_goods_details_share.setOnClickListener(this);
    }

    private void initView() {
        getDetails();
    }

    private void init() {
        imv_goods_details_picture = (ImageView) findViewById(R.id.imv_goods_details_picture);
        imv_goods_details_back = (ImageView) findViewById(R.id.imv_goods_details_back);
        imv_goods_details_platform = (ImageView) findViewById(R.id.imv_goods_details_platform);
        tv_goods_details_title = (TextView) findViewById(R.id.tv_goods_details_title);
        tv_goods_details_quan = (TextView) findViewById(R.id.tv_goods_details_quan);
//        tv_goods_details_price = (TextView) findViewById(R.id.tv_goods_details_price);
        tv_goods_details_toQuan = (RelativeLayout) findViewById(R.id.tv_goods_details_toQuan);
        tv_goods_details_toTaobao = (TextView) findViewById(R.id.tv_goods_details_toTaobao);
        rv_goods_details_more = (RecyclerView) findViewById(R.id.rv_goods_details_more);
        tv_goods_details_shopname = (TextView) findViewById(R.id.tv_goods_details_shopname);
        tv_goods_details_category = (TextView) findViewById(R.id.tv_goods_details_category);
        tv_goods_details_surplus = (TextView) findViewById(R.id.tv_goods_details_surplus);
        tv_goods_details_collect = (ImageView) findViewById(R.id.tv_goods_details_collect);
        tv_goods_details_collect_text = (TextView) findViewById(R.id.tv_goods_details_collect_text);
        imv_goods_details_share = (ImageView) findViewById(R.id.imv_goods_details_share);
        tv_goods_details_roll = (TextView) findViewById(R.id.tv_goods_details_roll);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_goods_details_back:
                finish();
                break;
            case R.id.tv_goods_details_toTaobao:
                toNet(goodsList.get(0).extension,1);
                break;
            case R.id.tv_goods_details_toQuan:
                toNet(goodsList.get(0).yhurl,2);
                break;
            case R.id.tv_goods_details_collect:
                tv_goods_details_collect.setEnabled(false);
                if (isFlag){
                    clickCollectToCancel();
                }else{
                    clickCollectToAdd();
                }

                break;
            case R.id.imv_goods_details_share:
                showShareWindow();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                if (resultCode == RESULT_OK) {
                    getDetails();//重构页面数据
                }
                break;
        }

    }


}
