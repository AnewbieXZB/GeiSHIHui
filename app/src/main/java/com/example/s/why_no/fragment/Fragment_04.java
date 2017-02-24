package com.example.s.why_no.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s.why_no.R;
import com.example.s.why_no.activity.DetailsActivity;
import com.example.s.why_no.bean.CancelCollect;
import com.example.s.why_no.bean.Collect;
import com.example.s.why_no.bean.Goods;
import com.example.s.why_no.servlet.CancelCollectServlet;
import com.example.s.why_no.servlet.CollectionGoodsListServlet;
import com.example.s.why_no.utils_login.ShareLoginData;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by S on 2016/10/30.
 */

public class Fragment_04 extends Fragment implements View.OnClickListener {

    private View mView;
    private Context context ;

    public int isCreat = 0;
    private boolean first  = true;

    private TextView tv_fragment_04_delete;
    private RecyclerView rv_fragment_04_collect_list;

    public  List<Goods> allList = new ArrayList<Goods>();
    public  List<Goods> tempList = new ArrayList<Goods>();
    public  CollectDataAdapter cAdapter = null;

    private LinearLayoutManager linearLayoutManager;
    private final int COLLECTION_DATA = 89;
    private final int REFRESH_DATA = 33;
    private RelativeLayout rl_fragment_04_select_delete;
    private Button rb_fragment_04_sure_delete;
    private ImageView cb_fragment_04_all_delete;
    private int isCheck = 1;//1全选 0没全选
    public boolean flag = false;//false 处于非删除状态  true 处于删除状态
    private boolean all_flag = true;//false 全选框处于空白状态  true全选框处于勾选状态
    private boolean user_change = false;//监听用户在勾选全选状态下 是否又取消了某个item
    private final int CANCEL_COLLECT = 55;
    private String dele_uid = "-1";
    private String phone;
    private String list;
    private String dete_str = "";

    private final int ERROR_REQUEST = 62;

    private static Fragment instance;

    public Fragment_04() {

    }
    public static Fragment_04 getInstance() {
        Fragment_04 newNewsFragment = new Fragment_04();
        return newNewsFragment;
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case COLLECTION_DATA:
                    Collect collect = (Collect) msg.obj;
                    if (collect.error == 1){
                        buildList(collect);
                    }else{

                    }
                    break;
                case REFRESH_DATA:

                    allList.clear();
                    cAdapter.notifyDataSetChanged();
                    Collect collect_refresh = (Collect) msg.obj;
                    if (collect_refresh.error == 1){
                        allList.addAll(collect_refresh.collection);
                    }
                    readyDelete(flag);
                    cAdapter.notifyDataSetChanged();
                    break;
                case CANCEL_COLLECT:

                    CancelCollect cancelCollect = (CancelCollect) msg.obj;
                    if (cancelCollect.error == 0){
                        Toast.makeText(context,"取消收藏失败",Toast.LENGTH_SHORT).show();

                        tempList.clear();
                    }else{
                        Toast.makeText(context,"取消收藏成功",Toast.LENGTH_SHORT).show();
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
                        SharedPreferences sharedPreferences = context.getSharedPreferences("lws",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone", phone);
                        editor.putString("list",list);
                        editor.commit();

//                        Log.e("adapter","" + cAdapter.getItemCount());
//
//                        Log.e("adapter","" + cAdapter.getItemCount());
//                        for (int i = 0; i < tempList.size(); i++) {
//                            if (tempList.get(i).isCheck){
//                                allList.remove(i);
//                                cAdapter.notifyItemRangeChanged(i,cAdapter.getItemCount()-1-i);
//                            }
//                        }
//                        Log.e("adapter","" + cAdapter.getItemCount());
//                        cAdapter.notifyDataSetChanged();
                    }
                    dele_uid = "-1";
                    refreshCollectionList();
                    flag = !flag;
                    readyDelete(flag);
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

        mView = inflater.inflate(R.layout.fragment_04, null);
        context = getActivity().getApplicationContext();
        isCreat = 1;
        init();
        initView();
        initEvent();

        return mView;
    }


    /**
     * 标记删除选中
     */
    private void deleteItem(){

        dele_uid = "-1";
        tempList.clear();
        tempList.addAll(allList);

        for (int i = 0; i < tempList.size(); i++) {
            tempList.get(i).setVis(0);
            if (tempList.get(i).isCheck){
                if (dele_uid.equals("-1")){
                    dele_uid = tempList.get(i).uid.toString();
                }else{
                    dele_uid = dele_uid + "," + tempList.get(i).uid.toString();
                }
            }
        }
        System.out.println("____phone:  " + phone);
        System.out.println("____dele_uid:  " + dele_uid);
        if(dele_uid.equals("-1")){
            tempList.clear();
            flag = !flag;
            readyDelete(flag);
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CancelCollectServlet cancelCollectServlet = new CancelCollectServlet();
                    String json = cancelCollectServlet.getJson(phone,dele_uid);

                    Message msg = new Message();
                    if (json.equals("error")){
                        msg.what = ERROR_REQUEST;
                    }else{
                        Gson gson = new Gson();
                        System.out.println("收藏夹页面 取消收藏json" + json);
                        CancelCollect cancelCollect = gson.fromJson(json, CancelCollect.class);

                        msg.obj = cancelCollect;
                        msg.what = CANCEL_COLLECT;

                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }


    }

    /**
     * 显示删除按钮
     */
    public void readyDelete(final boolean f){


        if (f){//让item处于可选择编辑状态
            tv_fragment_04_delete.setText("完成");
            rl_fragment_04_select_delete.setVisibility(View.VISIBLE);
            isCheck = 0;
            cb_fragment_04_all_delete.setImageResource(R.drawable.check_box_bg_default6);
//            cb_fragment_04_all_delete.setChecked(false);

            for (int i = 0; i < allList.size(); i++) {
                allList.get(i).setVis(1);
                allList.get(i).setCheck(false);
            }
            cb_fragment_04_all_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (cb_fragment_04_all_delete.isChecked()){//点击后变成全选状态
//                        for (int i = 0; i < allList.size(); i++) {
//                            allList.get(i).setCheck(true);
//                        }
//                    }else{
//                        for (int i = 0; i < allList.size(); i++) {
//                            allList.get(i).setCheck(false);
//                        }
//                    }

                    if (isCheck == 0){
                        for (int i = 0; i < allList.size(); i++) {
                            allList.get(i).setCheck(true);
                        }
                        isCheck = 1;
                        cb_fragment_04_all_delete.setImageResource(R.drawable.check_box_bg_checked6);
                    }else{
                        for (int i = 0; i < allList.size(); i++) {
                            allList.get(i).setCheck(false);
                        }
                        isCheck = 0;
                        cb_fragment_04_all_delete.setImageResource(R.drawable.check_box_bg_default6);
                    }
                    cAdapter.notifyDataSetChanged();
                }
            });
//            cAdapter.notifyDataSetChanged();

        }else{//让item处于 不可选择编辑状态
            tv_fragment_04_delete.setText("编辑");
//            cb_fragment_04_all_delete.setChecked(false);
            isCheck = 0;
            cb_fragment_04_all_delete.setImageResource(R.drawable.check_box_bg_default6);

            rl_fragment_04_select_delete.setVisibility(View.GONE);

            for (int i = 0; i < allList.size(); i++) {
                allList.get(i).setVis(0);
                allList.get(i).setCheck(false);
            }
        }
        cAdapter.notifyDataSetChanged();
    }
    /**
     * 构筑列表
     * @param collect
     */
    private void buildList(Collect collect) {

        allList.addAll(collect.collection);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        rv_fragment_04_collect_list.setHasFixedSize(true);
        rv_fragment_04_collect_list.setNestedScrollingEnabled(false);//false r1滑动不卡顿
        rv_fragment_04_collect_list.setLayoutManager(linearLayoutManager);


        rv_fragment_04_collect_list.setAdapter(cAdapter);

    }

    /**
     * 获取列表信息
     */
    private void getCollectionList() {

        ShareLoginData shareLoginData = new ShareLoginData(context);
        phone = shareLoginData.isLogin();
        if (phone.equals("-1")){
            Toast.makeText(context,"尊敬的用户，请先登录",Toast.LENGTH_SHORT).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CollectionGoodsListServlet collectionGoodsListServlet
                            = new CollectionGoodsListServlet();
                    String json = collectionGoodsListServlet.getJson(phone);
                    System.out.println("收藏夹json：" + json);
                    Message msg = new Message();
                    if (json.equals("error")){
                        msg.what = ERROR_REQUEST;
                    }else {
                        Gson gson = new Gson();
                        Collect collect = gson.fromJson(json, Collect.class);
                        msg.obj = collect;
                        msg.what = COLLECTION_DATA;
                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }

    }
    /**
     * 刷新列表信息
     */
    private void refreshCollectionList() {

        ShareLoginData shareLoginData = new ShareLoginData(context);
        final String phone = shareLoginData.isLogin();
        if (phone.equals("-1")){
            Toast.makeText(context,"尊敬的用户，请先登录",Toast.LENGTH_SHORT).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CollectionGoodsListServlet collectionGoodsListServlet
                            = new CollectionGoodsListServlet();
                    String json = collectionGoodsListServlet.getJson(phone);
                    Message msg = new Message();
                    if (json.equals("error")){
                        msg.what = ERROR_REQUEST;
                    }else {

                        Gson gson = new Gson();
                        Collect collect = gson.fromJson(json, Collect.class);
                        msg.obj = collect;
                        msg.what = REFRESH_DATA;

                    }
                    handler.sendMessage(msg);
                }
            }).start();
        }

    }

    private void initEvent() {
        tv_fragment_04_delete.setOnClickListener(this);
        rb_fragment_04_sure_delete.setOnClickListener(this);

    }

    private void initView() {
//        allList = new ArrayList<Goods>();
//        tempList = new ArrayList<Goods>();
        cAdapter = new CollectDataAdapter(allList,context);
        getCollectionList();
    }


    private void init() {
        rv_fragment_04_collect_list = (RecyclerView) mView.findViewById(R.id.rv_fragment_04_collect_list);
        tv_fragment_04_delete = (TextView) mView.findViewById(R.id.tv_fragment_04_delete);
        rl_fragment_04_select_delete = (RelativeLayout) mView.findViewById(R.id.rl_fragment_04_select_delete);
        cb_fragment_04_all_delete = (ImageView) mView.findViewById(R.id.cb_fragment_04_all_delete);
        rb_fragment_04_sure_delete = (Button) mView.findViewById(R.id.rb_fragment_04_sure_delete);
    }



    @Override
    public void onResume() {
        super.onResume();

        if (first){
            first = false;
        }else{
            flag = true;//false 处于非删除状态  true 处于删除状态
            all_flag = true;//false 全选框处于空白状态  true全选框处于勾选状态
            user_change = false;//监听用户在勾选全选状态下 是否又取消了某个item
            if (rl_fragment_04_select_delete.getVisibility() == View.VISIBLE){
                readyDelete(flag);
            }
            getCollectionList();
        }


//        getCollectionList();
//        refreshCollectionList();
    }

    @Override
    public void onStop() {
        super.onStop();

        allList.clear();
////        rl_fragment_04_select_delete.setVisibility(View.GONE);
////        for (int i = 0; i < allList.size(); i++) {
////            allList.get(i).setCheck(false);
////            allList.get(i).setVis(0);
////        }
//        Log.e("allList",allList.size() + "");
//        cAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_fragment_04_delete:
                flag = !flag;
                readyDelete(flag);
                break;
            case R.id.rb_fragment_04_sure_delete:
                deleteItem();//删除选中
                break;
        }
    }




    //---------------------------------------------------------------------------------------
    public class CollectDataAdapter extends  RecyclerView.Adapter<CollectDataAdapter.MyViewHolder> {

        String str = "";

        private DisplayImageOptions options = options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
                .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        private Context context;
        private List<Goods> allList;
        private LayoutInflater mInflater;

        public CollectDataAdapter(List<Goods> allList,Context context) {
            this.allList = allList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public CollectDataAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CollectDataAdapter.MyViewHolder holder =
                    new CollectDataAdapter.MyViewHolder(mInflater.inflate(
                    R.layout.item_collect_goods, parent, false));
            return holder;
        }


        @Override
        public void onBindViewHolder(final CollectDataAdapter.MyViewHolder holder
                , final int position) {

            if (allList.get(position).platform.equals("天猫")){
                holder.imv_collect_platform.setImageResource(R.drawable.tmall);
            }else if(allList.get(position).platform.equals("淘宝")){
                holder.imv_collect_platform.setImageResource(R.drawable.taobao);
            }
            ImageLoader.getInstance().displayImage(allList.get(position).uimg
                    +"_300x300.jpg" ,  holder.imv_item_collect_pic, options);
            holder.tv_item_collect_title.setText(allList.get(position).uname);
            holder.tv_item_collect_roll.setText("¥" + allList.get(position).roll);//券后价
            holder.tv_item_collect_price.setText("¥" +allList.get(position).price );
            holder.tv_item_collect_volume.setText( allList.get(position).volume + "");//已售

            if (allList.get(position).getVis() == 0){
                holder.rl_item_collect_select.setVisibility(View.GONE);
            }else if (allList.get(position).getVis() == 1){
                holder.rl_item_collect_select.setVisibility(View.VISIBLE);
            }

            if (allList.get(position).isCheck){
                holder.cb_item_collect_select.setImageResource(R.drawable.check_box_bg_checked6);
//                holder.cb_item_collect_select.setChecked(true);
            }else{
                holder.cb_item_collect_select.setImageResource(R.drawable.check_box_bg_default6);
//                holder.cb_item_collect_select.setChecked(false);
            }

            holder.cb_item_collect_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (allList.get(position).isCheck){//当已经处于被选中状态时

                        allList.get(position).setCheck(false);
                        holder.cb_item_collect_select.setImageResource(R.drawable.check_box_bg_default6);

//                        holder.cb_item_collect_select.setChecked(false);

//                        cb_fragment_04_all_delete.setChecked(false);
                        isCheck = 0;
                        cb_fragment_04_all_delete.setImageResource(R.drawable.check_box_bg_default6);

                    }else if(!allList.get(position).isCheck){//当没有处于被选中状态时

                        allList.get(position).setCheck(true);
                        holder.cb_item_collect_select.setImageResource(R.drawable.check_box_bg_checked6);
//                        holder.cb_item_collect_select.setChecked(true);

                        if (scanList()){//遍历整个allList 看是否全都被选中  用于判断接全选按钮的选中状态
//                            cb_fragment_04_all_delete.setChecked(true);
                            isCheck = 1;
                            cb_fragment_04_all_delete.setImageResource(R.drawable.check_box_bg_checked6);
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return allList.size();
        }




        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {


            ImageView imv_item_collect_pic;
            ImageView imv_collect_platform;
            RelativeLayout rl_item_collect_select;
            ImageView cb_item_collect_select;
            TextView tv_item_collect_title;
            TextView tv_item_collect_roll;
            TextView tv_item_collect_price;
            TextView tv_item_collect_volume;


            public MyViewHolder(View view)
            {
                super(view);

                view.setOnClickListener(this);
                rl_item_collect_select = (RelativeLayout) view.findViewById(R.id.rl_item_collect_select);
                cb_item_collect_select = (ImageView) view.findViewById(R.id.cb_item_collect_select);
                imv_item_collect_pic = (ImageView) view.findViewById(R.id.imv_item_collect_pic);
                imv_collect_platform = (ImageView) view.findViewById(R.id.imv_collect_platform);
                tv_item_collect_title = (TextView) view.findViewById(R.id.tv_item_collect_title);
                tv_item_collect_roll = (TextView) view.findViewById(R.id.tv_item_collect_roll);
                tv_item_collect_price = (TextView) view.findViewById(R.id.tv_item_collect_price);
                tv_item_collect_volume = (TextView) view.findViewById(R.id.tv_item_collect_volume);

            }

            @Override
            public void onClick(View v) {
                if (allList.get(getPosition()).getVis() == 1){//处于编辑状态

                }else{

                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("id",allList.get(getPosition()).id);
                    intent.putExtra("ification",allList.get(getPosition()).ification);
                    startActivity(intent);
                }


            }
        }

        private boolean scanList(){
            boolean isAllSelect = true;

            for (int i = 0; i < allList.size(); i++) {
                if (!allList.get(i).isCheck){
                    isAllSelect = false;
                    break;
                }
            }

            return isAllSelect;
        }
        public List<Goods> newList(){
            return allList;
        }
    }
}
