package com.example.s.why_no.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.s.why_no.R;
import com.example.s.why_no.cache.Net;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by S on 2016/11/7.
 */

public class NavigationActivity extends Activity{


    private ImageView imv_navigaion_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来消除标题栏（ActionBar），这样欢迎界面才能充满整个屏幕，显得更美观！
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_navigaion);

        init();
        initview();

    }


    private void initview() {
        getPicture();
        setTimer();//计时器 到点就跳转页面
    }

    private void setTimer() {

        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                Intent intent=new Intent(NavigationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        timer.schedule(task,2000);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    Thread.sleep(3*1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }).start();
    }


    private void getPicture() {
        DisplayImageOptions options = options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
                .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getApplicationContext());
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 8 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(options);
//                .threadPriority(3)
//                .writeDebugLogs();//打印日志
        ImageLoader.getInstance().init(config.build());


        ImageLoader.getInstance().displayImage(Net.NAVIGATION
                 ,  imv_navigaion_picture, options);

    }

    private void init() {
       imv_navigaion_picture = (ImageView) findViewById(R.id.imv_navigaion_picture);
   }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

    }
}
