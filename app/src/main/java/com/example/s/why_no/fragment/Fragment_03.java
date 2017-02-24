package com.example.s.why_no.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.DataBase.SearchHistorysDao;
import com.example.s.why_no.R;
import com.example.s.why_no.activity.SearchActivity;
import com.example.s.why_no.bean.SearchHistorysBean;
import com.example.s.why_no.bean.SearchHot;
import com.example.s.why_no.servlet.SearchHotServlet;
import com.example.s.why_no.utils_window.ManageWindow;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by S on 2016/10/30.
 */

public class Fragment_03 extends Fragment implements View.OnClickListener{

    private boolean first = false;

    private LinearLayout ll_fragment_03_history;
    private LinearLayout ll_fragment_03_hot;
    private ImageView imv_fragment_03_search;
    private EditText edt_fragment_03_search;
    private final int GET_HOT_WORD = 141;
    private TextView tv_fragment_03_hop;
    private TextView tv_fragment_03_history;

    private View mView;
    private Context context;
    private static Fragment instance;

    private final int ERROR_REQUEST = 94;
    public Fragment_03() {

    }
    public static Fragment_03 getInstance() {
        Fragment_03 newNewsFragment = new Fragment_03();
        return newNewsFragment;
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_HOT_WORD:

                    SearchHot searchHot = (SearchHot) msg.obj;
                    if (searchHot.error == 0){
                        tv_fragment_03_hop.setVisibility(View.GONE);
                        ll_fragment_03_hot.setVisibility(View.GONE);
                    }else{
                        buildHotList(searchHot);
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

        mView = inflater.inflate(R.layout.fragment_03, null);
        context = getActivity().getApplicationContext();
//        CustomProgress.show(getActivity(), "正在加载...", false,
//                null);//加载中的动画


        init();
        initView();
        initEvent();


        return mView;
    }

    /**
     * 保存搜索记录到数据库并跳转
     */
    private void saveAndGo() {

        String word = edt_fragment_03_search.getText().toString();
        if (word.equals("")) {

        }
        else{
            SearchHistorysDao dao = new SearchHistorysDao(context);
            dao.addOrUpdate(word);

            Intent intent = new Intent(context, SearchActivity.class);
            intent.putExtra("word",word);
            startActivity(intent);
        }

    }


    private void initEvent() {
        imv_fragment_03_search.setOnClickListener(this);
        /**
         * 重写监听器 使输入框回车键直接执行搜索代码
         */
        edt_fragment_03_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    saveAndGo();
                    return true;
                }

                return false;
            }
        });
        /**
         * 重写监听器 使输入框回车键不换行
         */
        edt_fragment_03_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
    }

    private void initView() {
        getHistoryWord();
        getHotWord();
    }

    /**
     * 获取保存到数据库的搜索记录词
     */
    private void getHistoryWord() {
        SearchHistorysDao dao = new SearchHistorysDao(context);
        ArrayList<SearchHistorysBean> list = dao.listMore();
        if (list.size() == 0){
            tv_fragment_03_history.setVisibility(View.GONE);
            ll_fragment_03_history.setVisibility(View.GONE);
        }else{
             buildHistoryList(list);
        }
    }

    /**
     * 构筑搜索热词的列表
     * @param searchHot
     */
    private void buildHotList(SearchHot searchHot) {

        LinearLayout linearLayout = new LinearLayout(context);

        ManageWindow manage = new ManageWindow();
        int widthWindow = manage.getWidth(context);
        widthWindow = widthWindow - 50;//减去24是因为垂直linearLayout对屏幕左右margin各12
        //多减去26是避免下面计算不准去 因为改窄窗口宽
        System.out.println("窗口宽度" + widthWindow);
        int widthView = 0;
        for (int i = 0; i < searchHot.list.size(); i++) {

            final TextView tv = new TextView(context);
            tv.setText(searchHot.list.get(i).text);
            tv.setTextColor(getResources().getColor(R.color.color_sousuojilu));
            tv.setPadding(10,0,10,0);
            tv.setBackgroundColor(getResources().getColor(R.color.color_white));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 0);//marginRight = 10
            tv.setLayoutParams(lp);

            int measureText= (int)tv.getPaint().measureText(searchHot.list.get(i).text);//测量字符串占屏幕的宽度

            if (widthView == 0){
                linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                ViewGroup.LayoutParams  para = linearLayout.getLayoutParams();
//                para.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                para.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                linearLayout.setLayoutParams(para);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.setMargins(0,20,0,0);//margintop = 20
                linearLayout.setLayoutParams(lp2);
                linearLayout.addView(tv);
                widthView =  widthView + measureText +40;//20是控件多加的padding  marginRight = 10
                                                        //多10是避免布局难看
                if (i == searchHot.list.size() - 1){
                    ll_fragment_03_hot.addView(linearLayout);
                }

            }else{
                if (widthView + measureText + 40 < widthWindow)//加起来的宽度比屏幕小
                {
                    linearLayout.addView(tv);
                    widthView = widthView + measureText + 40;
                    if (i == searchHot.list.size() - 1){
                        ll_fragment_03_hot.addView(linearLayout);
                    }
                }else{
                    ll_fragment_03_hot.addView(linearLayout);
                    widthView = 0;
                    i--;//循环回退一次
                }
            }

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_fragment_03_search.setText(tv.getText().toString());
//                    Toast.makeText(context,tv.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    /**
     * 获取搜索热词
     */
    private void getHotWord() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchHotServlet searchHotServlet = new SearchHotServlet();
                String json = searchHotServlet.getJson();
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;

                }else{
                    Gson gson = new Gson();
                    SearchHot search = gson.fromJson(json,SearchHot.class);


                    msg.what = GET_HOT_WORD;
                    msg.obj = search;

                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 构筑历史记录表
     */
    private void buildHistoryList(ArrayList<SearchHistorysBean> list) {

        LinearLayout linearLayout = new LinearLayout(context);

        ManageWindow manage = new ManageWindow();
        int widthWindow = manage.getWidth(context);
        widthWindow = widthWindow - 50;//减去24是因为垂直linearLayout对屏幕左右margin各12
                                        //多减去26是避免下面计算不准去 因为改窄窗口宽
        System.out.println("窗口宽度" + widthWindow);
        int widthView = 0;
        for (int i = 0; i < list.size(); i++) {

            final TextView tv = new TextView(context);
            tv.setText(list.get(i).historyword);
            tv.setPadding(10,0,10,0);
            tv.setTextColor(getResources().getColor(R.color.color_sousuojilu));
            tv.setBackgroundColor(getResources().getColor(R.color.color_white));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 0);//marginRight = 10
            tv.setLayoutParams(lp);



            int measureText= (int)tv.getPaint().measureText(list.get(i).historyword);//测量字符串占屏幕的宽度
            System.out.println("history:[" + i + "]  " + measureText);

            if (widthView == 0){
                linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                ViewGroup.LayoutParams  para = linearLayout.getLayoutParams();
//                para.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                para.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                linearLayout.setLayoutParams(para);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.setMargins(0,20,0,0);//margintop = 20
                linearLayout.setLayoutParams(lp2);
                linearLayout.addView(tv);
                widthView =  widthView + measureText +40;//20是控件多加的padding  marginRight = 10
                                                    //多10是避免布局难看
                if (i == list.size() - 1){
                    ll_fragment_03_history.addView(linearLayout);
                }

            }else{
                if (widthView + measureText + 40 < widthWindow)//加起来的宽度比屏幕小
                {
                    linearLayout.addView(tv);
                    widthView = widthView + measureText + 40;
                    if (i == list.size() - 1){
                        ll_fragment_03_history.addView(linearLayout);
                    }
                }else{
                    ll_fragment_03_history.addView(linearLayout);
                    widthView = 0;
                    i--;//循环回退一次
                }
            }

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edt_fragment_03_search.setText(tv.getText().toString());
//                    Toast.makeText(context,tv.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void init() {
        edt_fragment_03_search = (EditText) mView.findViewById(R.id.edt_fragment_03_search);
        imv_fragment_03_search = (ImageView) mView.findViewById(R.id.imv_fragment_03_search);
        ll_fragment_03_history = (LinearLayout) mView.findViewById(R.id.ll_fragment_03_history);
        tv_fragment_03_history = (TextView) mView.findViewById(R.id.tv_fragment_03_history);
        tv_fragment_03_hop = (TextView) mView.findViewById(R.id.tv_fragment_03_hop);
        ll_fragment_03_hot = (LinearLayout) mView.findViewById(R.id.ll_fragment_03_hot);
    }

    @Override
    public void onPause() {
        super.onPause();
        first = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (first){
            ll_fragment_03_history.removeAllViews();
            ll_fragment_03_hot.removeAllViews();
            initView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_fragment_03_search:
                //保存到搜索记录并跳转
                saveAndGo();
//                SearchHistorysDao dao = new SearchHistorysDao(context);
//                dao.deleteAll();
                break;
        }
    }


}
