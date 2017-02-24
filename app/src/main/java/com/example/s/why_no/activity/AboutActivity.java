package com.example.s.why_no.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.s.why_no.R;
import com.example.s.why_no.utils_window.ManageWindow;

import java.io.InputStream;

/**
 * Created by S on 2016/11/7.
 */

public class AboutActivity extends Activity implements View.OnClickListener{

    private ImageView imv_about_back;
    private ImageView imv_about_top;
    private TextView tv_about_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();
        initView();
        initEvent();

    }

    private void initEvent() {
        imv_about_back.setOnClickListener(this);
    }

    private void initView() {

        buildTopImage();
        loadTxt();//读取txt文本

    }

    /**
     * 构筑顶部图片
     */
    private void buildTopImage() {

        ManageWindow mana = new ManageWindow();
        int width = mana.getWidth(this);
        int height = (int) (width/2.4);
        ViewGroup.LayoutParams  para = imv_about_top.getLayoutParams();
        para.width = width;
        para.height = height;
        imv_about_top.setLayoutParams(para);
        imv_about_top.setImageResource(R.drawable.about_background);

    }

    /**
     * 二、从resource中的raw文件夹中获取文件并读取数据（资源文件只能读不能写）
     *
     * @param fileInRaw
     * @return
     */
    public String readFromRaw(int fileInRaw) {
        String res = "";
        try {
            InputStream in = getResources().openRawResource(fileInRaw);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);

            res = new String(buffer,"UTF-8");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    private void loadTxt() {
        String str = readFromRaw(R.raw.our);
        tv_about_txt.setText(str);
    }

    private void init() {
        imv_about_back = (ImageView) findViewById(R.id.imv_about_back);
        imv_about_top = (ImageView) findViewById(R.id.imv_about_top);
        tv_about_txt = (TextView) findViewById(R.id.tv_about_txt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_about_back:
                finish();
                break;
        }
    }
}
