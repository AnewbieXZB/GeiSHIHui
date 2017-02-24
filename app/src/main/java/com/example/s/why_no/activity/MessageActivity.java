package com.example.s.why_no.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.adapter.MessageRecordListAdapter;
import com.example.s.why_no.bean.MessageRecord;
import com.example.s.why_no.servlet.MessageServlet;
import com.google.gson.Gson;

/**
 * Created by S on 2016/11/7.
 */

public class MessageActivity extends Activity implements View.OnClickListener{

    private ImageView imv_message_back;
    private RecyclerView rv_message_list;
    private LinearLayoutManager linearLayoutManager;
    private MessageRecordListAdapter mAdapter;
    private final int GET_MESSAGE_LIST = 23;
    private final int ERROR_REQUEST = 87;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_MESSAGE_LIST:
                    MessageRecord messageRecord = (MessageRecord) msg.obj;
                    if (messageRecord.error == 1){
                        buildList(messageRecord);
                    }
                    break;
                case ERROR_REQUEST:
                    Toast.makeText(MessageActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        init();
        initView();
        initEvent();

    }

    /**
     * 构筑消息列表
     * @param messageRecord
     */
    private void buildList(final MessageRecord messageRecord) {

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        rv_message_list.setHasFixedSize(true);
        rv_message_list.setNestedScrollingEnabled(false);//false r1滑动不卡顿
        rv_message_list.setLayoutManager(linearLayoutManager);

        mAdapter = new MessageRecordListAdapter(messageRecord.information,this);
        rv_message_list.setAdapter(mAdapter);

        mAdapter.setOnItemClickLitener(new MessageRecordListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                showTextDialog(messageRecord,position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }
    /**
     * 获取消息列表
     */
    private void getData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                MessageServlet messageServlet = new MessageServlet();
                String json = messageServlet.getJson();

                System.out.println("message json" + json);
                Message msg = new Message();
                if (json.equals("error")){
                    msg.what = ERROR_REQUEST;
                }else{
                    Gson gson = new Gson();
                    MessageRecord messageRecord = gson.fromJson(json,MessageRecord.class);

                    msg.what = GET_MESSAGE_LIST;
                    msg.obj = messageRecord;

                }
                handler.sendMessage(msg);
            }
        }).start();


    }

    /**
     * 弹出允许复制的框
     */
    private void showTextDialog(MessageRecord messageRecord,int position) {

        View dialogView = View.inflate(MessageActivity.this, R.layout.dialog_message_text, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setView(dialogView);

        TextView tv_dialog_message_title = (TextView) dialogView.findViewById(R.id.tv_dialog_message_title);
        TextView tv_dialog_message_time = (TextView) dialogView.findViewById(R.id.tv_dialog_message_time);
        TextView tv_dialog_message_details = (TextView) dialogView.findViewById(R.id.tv_dialog_message_details);

        tv_dialog_message_title.setText(messageRecord.information.get(position).name);
        tv_dialog_message_time.setText(messageRecord.information.get(position).time);
        tv_dialog_message_details.setText(messageRecord.information.get(position).text);

        alertDialog.show();

    }

    private void initEvent() {
        imv_message_back.setOnClickListener(this);
    }

    private void initView() {
        getData();
    }



    private void init() {
        imv_message_back = (ImageView) findViewById(R.id.imv_message_back);
        rv_message_list = (RecyclerView) findViewById(R.id.rv_message_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_message_back:
                finish();
                break;
        }
    }
}
