package com.example.s.why_no.activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.CancelCollect;
import com.example.s.why_no.fragment.Fragment_01;
import com.example.s.why_no.fragment.Fragment_02;
import com.example.s.why_no.fragment.Fragment_03;
import com.example.s.why_no.fragment.Fragment_04;
import com.example.s.why_no.fragment.Fragment_05;
import com.example.s.why_no.servlet.CollectionUidListServlet;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private ViewPager vp_main_fragmentlist;
    private ArrayList<Fragment> fragmentList;

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Fragment current_fragment ;//用作标记当前哪个fragment被add
    public Fragment_01 fragment_01;
//    private Fragment_01_main fragment_01_main;
    public Fragment_02 fragment_02;
    public Fragment_03 fragment_03;
    public Fragment_04 fragment_04;
    public Fragment_05 fragment_05;

    private Fragment[] fragments ;

//    private ImageView imv_top_left;
//    private ImageView imv_top_right;

    /**
     * 底部按钮的有关
     */
    private LinearLayout ll_tab_01;
    private LinearLayout ll_tab_02;
    private LinearLayout ll_tab_03;
    private LinearLayout ll_tab_04;
    private LinearLayout ll_tab_05;
    private int[] id_tabs = {R.id.imv_tab_01,R.id.imv_tab_02,R.id.imv_tab_03,R.id.imv_tab_04,R.id.imv_tab_05};
    private ImageView[] imv_tabs = new ImageView[id_tabs.length];
    private  int[] draw_off_tabs = {R.drawable.tab01_off,R.drawable.tab02_off,R.drawable.tab03_off,
            R.drawable.tab04_off,R.drawable.tab05_off,};
    private int[] draw_on_tabs = {R.drawable.tab01_on,R.drawable.tab02_on,R.drawable.tab03_on,
            R.drawable.tab04_on,R.drawable.tab05_on};
    private int[] id_tvs = {R.id.tv_tab_01,R.id.tv_tab_02,R.id.tv_tab_03,R.id.tv_tab_04,R.id.tv_tab_05};
    private TextView[] tv_tvs = new TextView[id_tvs.length];


    private final int FIRST_GET_COLLECT_ID = 66;
    private final int ERROR_REQUEST = 46;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case FIRST_GET_COLLECT_ID:
                    CancelCollect cancelCollect = (CancelCollect) msg.obj;
                    saveListAndPhone(cancelCollect);//保存有关信息到本地
                    break;
                case ERROR_REQUEST:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType. E_UM_NORMAL);
        initEntrance();//程序的入口  注册一些控件的 参数
        init();
        initView();
        initEvent();

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);
    }

    /**
     * 保存信息到本地
     */
    private void saveListAndPhone(CancelCollect cancelCollect){
        String list = "-1";
        if (cancelCollect.error == 0){

        }else{
            if (cancelCollect.list.size()>0){
                for (int i = 0; i < cancelCollect.list.size(); i++) {
                    if (i==0){
                        list = cancelCollect.list.get(i).uid;
                    }else{
                        list = list + "_" + cancelCollect.list.get(i).uid;
                    }
                }
            }else{

            }
        }
        //保存用户登录信息
        SharedPreferences sharedPreferences = getSharedPreferences("lws",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("list", list);
        editor.commit();

    }
    /**
     * 设置默认加载的fragment
     */
    private void defalutFragment(){

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fl_main_fragmentlist, fragment_01).commit();

        current_fragment = fragment_01;
    }

    public void change(int curr){
        changeFragmentView(fragments[curr]);
        changeBottomView(curr);
    }
    /**
     * 更改底部按钮的样式
     */
    private void changeBottomView(int tab){

        for (int i = 0; i < imv_tabs.length; i++) {
            if (tab == i){
                imv_tabs[i].setImageResource(draw_on_tabs[i]);
                tv_tvs[i].setTextColor(getResources().getColor(R.color.color_bottom_on));
            }else{
                imv_tabs[i].setImageResource(draw_off_tabs[i]);
                tv_tvs[i].setTextColor(getResources().getColor(R.color.color_bottom_off));
            }
        }
    }
    /**
     * 更改中部fragment呈现的列表
     */
    private void changeFragmentView(Fragment fr){

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (current_fragment != fr) {

            if (!fr.isAdded()) { // 先判断是否被add过
                transaction.hide(current_fragment).add(R.id.fl_main_fragmentlist, fr); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(current_fragment).show(fr); // 隐藏当前的fragment，显示下一个
            }
            transaction.commit();
            current_fragment = fr;
        }
    }



    private void initEvent() {
//        imv_top_left.setOnClickListener(this);
//        imv_top_right.setOnClickListener(this);
        ll_tab_01.setOnClickListener(this);
        ll_tab_02.setOnClickListener(this);
        ll_tab_03.setOnClickListener(this);
        ll_tab_04.setOnClickListener(this);
        ll_tab_05.setOnClickListener(this);


//        for (int i = 0; i < imv_tabs.length; i++) {
//            imv_tabs[i].setOnClickListener(this);
//        }
    }

    private void initView() {

        fragment_01 = new Fragment_01();
//        fragment_01_main = new Fragment_01_main();
        fragment_02 = new Fragment_02();
        fragment_03 = new Fragment_03();
        fragment_04 = new Fragment_04();
        fragment_05 = new Fragment_05();

        fragments = new Fragment[]{fragment_01, fragment_02, fragment_03, fragment_04, fragment_05};
        defalutFragment();
//        buildViewpager();


        getCollectIdList();
    }



    private void init() {
//        vp_main_fragmentlist = (ViewPager) findViewById(R.id.vp_main_fragmentlist);
//        imv_top_left = (ImageView) findViewById(R.id.imv_top_left);
//        imv_top_right = (ImageView) findViewById(R.id.imv_top_right);
        ll_tab_01 = (LinearLayout) findViewById(R.id.ll_tab_01);
        ll_tab_02 = (LinearLayout) findViewById(R.id.ll_tab_02);
        ll_tab_03 = (LinearLayout) findViewById(R.id.ll_tab_03);
        ll_tab_04 = (LinearLayout) findViewById(R.id.ll_tab_04);
        ll_tab_05 = (LinearLayout) findViewById(R.id.ll_tab_05);

        for (int i = 0; i < imv_tabs.length; i++) {
            imv_tabs[i] = (ImageView) findViewById(id_tabs[i]);
            tv_tvs[i] = (TextView) findViewById(id_tvs[i]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.ll_tab_01:
                change(0);
                break;
            case R.id.ll_tab_02:
                change(1);
                fragment_02.refreshDataFromServer();
                break;
            case R.id.ll_tab_03:
                change(2);
                break;
            case R.id.ll_tab_04:
                change(3);
                break;
            case R.id.ll_tab_05:
                change(4);
                if (fragment_05.isCreat == 1){
                    fragment_05.hasUserDataToChangeUI();
                }
                break;

        }
    }

    /**
     * app的入口  注册一些控件的 参数
     */
    private void initEntrance() {

        DisplayImageOptions options = options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
                .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

//        //创建默认的ImageLoader配置参数
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
//                .createDefault(this);
//        //imageloader初始化配置。
////        ImageLoader.getInstance().init(configuration);

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

    }

    /**
     * 获取用户收藏列表的id
     */
    public void getCollectIdList() {
        ShareLoginData share = new ShareLoginData(this);
        final String phone = share.isLogin();
        if (phone.equals("-1")){

        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CollectionUidListServlet collectionUidListServlet
                            = new CollectionUidListServlet();
                    String json = collectionUidListServlet.getJson(phone);
                    Message msg = new Message();
                    if (json.equals("error")){
                        msg.what = ERROR_REQUEST;
                    }else {
                        Gson gson = new Gson();
                        CancelCollect cancelCollect = gson.fromJson(json, CancelCollect.class);

                        msg.what = FIRST_GET_COLLECT_ID;
                        msg.obj = cancelCollect;
                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }
    }

    private long firstTime;
    private long secondTime;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            secondTime = System.currentTimeMillis();
            if (secondTime - firstTime <= 2000) {
                MobclickAgent.onKillProcess(this);//杀死友盟统计
                android.os.Process.killProcess(android.os.Process.myPid());

            } else {
                Toast.makeText(MainActivity.this, "请再按一次退出",Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
            }
            return true;

        }
        return false;
    }


}
