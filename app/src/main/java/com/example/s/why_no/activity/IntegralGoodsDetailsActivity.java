package com.example.s.why_no.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.IntegralGoods;
import com.example.s.why_no.bean.OtherImage;
import com.example.s.why_no.servlet.OtherPictureServlet;
import com.example.s.why_no.utils_window.ManageWindow;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by S on 2016/11/11.
 */

public class IntegralGoodsDetailsActivity extends Activity implements View.OnClickListener{

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
    private ImageView imv_integral_goods_details_back;
    private ImageView imv_integral_goods_details_picture;
    private TextView tv_integral_goods_details_need;
    private TextView tv_integral_goods_details_title;
    private TextView tv_integral_goods_details_details;
    private TextView tv_integral_goods_details_surplus;
    private TextView tv_goods_details_money;
    private TextView tv_goods_details_people;
    private LinearLayout ll_integral_goods_details_other_picture;
    private TextView tv_integral_goods_details_toexchange;

    private final int OTHER_PICTURE = 22;
    private final int ERROR_REQUEST = 45;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case OTHER_PICTURE:
                    OtherImage other = (OtherImage) msg.obj;
                    if (other.error == 1){
                        buildOtherPicture(other);
                    }else{

                    }
                    break;
                case ERROR_REQUEST:
                    Toast.makeText(IntegralGoodsDetailsActivity.this,"网络错误",Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_goods_details);

        init();
        initView();
        initEvent();

    }


    /**
     * 获取传递过来的数据构筑页面
     */
    private void buildView() {

        Intent intent = getIntent();
        String integralGoods = intent.getStringExtra("integralGoods");
        System.out.println( "获取传递过来的数据" + integralGoods );
        Gson gson = new Gson();
        goods  = gson.fromJson(integralGoods,IntegralGoods.class);


        ManageWindow mana = new ManageWindow();
        int windowWidth = mana.getWidth(this);
        int windowHeight = mana.getHeight(this);
        ViewGroup.LayoutParams  para = imv_integral_goods_details_picture.getLayoutParams();
        para.height = windowWidth;
        para.width = windowWidth;
        imv_integral_goods_details_picture.setLayoutParams(para);

        ImageLoader.getInstance().displayImage(goods.img
                ,  imv_integral_goods_details_picture, options);
        tv_integral_goods_details_title.setText(goods.name);
        tv_integral_goods_details_details.setText(goods.details);
        tv_integral_goods_details_need.setText(goods.need + "");
        tv_integral_goods_details_surplus.setText(goods.surplus + "");
        tv_goods_details_money.setText(goods.money + "元");
        tv_goods_details_people.setText(goods.people);

        getOtherPic();
    }

    /**
     * 构筑其他商品图片
     */
    private void buildOtherPicture(OtherImage other ) {

        for (int i = 0; i < other.integralimg.size(); i++) {

            ImageView imv = new ImageView(getApplicationContext());

            ImageLoader.getInstance().displayImage(other.integralimg.get(i).img
                    ,  imv, options);

            ll_integral_goods_details_other_picture.addView(imv);

            ManageWindow mana = new ManageWindow();
            int windowWidth = mana.getWidth(this);
            int windowHeight = mana.getHeight(this);
            ViewGroup.LayoutParams  para = imv.getLayoutParams();
            para.height = windowWidth;
            para.width = windowWidth;
            imv.setLayoutParams(para);
        }

    }
    /**
     * 获取其他图片
     */
    private void getOtherPic() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OtherPictureServlet otherPictureServlet = new OtherPictureServlet();
                String json = otherPictureServlet.getJson(goods.number);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else{
                    Gson gson = new Gson();
                    OtherImage other = gson.fromJson(json,OtherImage.class);


                    msg.obj = other;
                    msg.what = OTHER_PICTURE;

                }
                handler.sendMessage(msg);
            }
        }).start();

    }

    private void initEvent() {
        imv_integral_goods_details_back.setOnClickListener(this);
        tv_integral_goods_details_toexchange.setOnClickListener(this);
    }

    private void initView() {
        buildView();
    }



    private void init() {
        imv_integral_goods_details_back = (ImageView) findViewById(R.id.imv_integral_goods_details_back);
        imv_integral_goods_details_picture = (ImageView) findViewById(R.id.imv_integral_goods_details_picture);
        tv_integral_goods_details_title = (TextView) findViewById(R.id.tv_integral_goods_details_title);
        tv_integral_goods_details_details = (TextView) findViewById(R.id.tv_integral_goods_details_details);
        tv_integral_goods_details_need = (TextView) findViewById(R.id.tv_integral_goods_details_need);
        tv_integral_goods_details_surplus = (TextView) findViewById(R.id.tv_integral_goods_details_surplus);
        tv_goods_details_money = (TextView) findViewById(R.id.tv_goods_details_money);
        tv_goods_details_people = (TextView) findViewById(R.id.tv_goods_details_people);
        ll_integral_goods_details_other_picture = (LinearLayout) findViewById(R.id.ll_integral_goods_details_other_picture);
        tv_integral_goods_details_toexchange = (TextView) findViewById(R.id.tv_integral_goods_details_toexchange);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_integral_goods_details_back:
                finish();
                break;
            case R.id.tv_integral_goods_details_toexchange:
                Intent intent = new Intent(IntegralGoodsDetailsActivity.this,SureExchangeActivity.class);
                Gson gson = new Gson();
                String integralGoods = gson.toJson(goods);
                intent.putExtra("integralGoods",integralGoods);
                startActivity(intent);
                break;
        }
    }
}
