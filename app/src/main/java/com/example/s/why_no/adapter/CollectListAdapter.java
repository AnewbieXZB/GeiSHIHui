package com.example.s.why_no.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.Goods;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by S on 2016/10/20.
 */

public class CollectListAdapter extends  RecyclerView.Adapter<CollectListAdapter.MyViewHolder> {

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

    public CollectListAdapter(List<Goods> allList, Context context) {
        this.allList = allList;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);

    }
    private OnItemClickLitener mOnItemClickLitener;
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(
                R.layout.item_collect_goods, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

//        ImageSize imageSize = new ImageSize(24, 20, 0);
//        holder.imv_item_pic.setImageBitmap(ImageLoader.getInstance().loadImageSync(allList.get(position).uimg, imageSize, options));


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
            holder.cb_item_collect_select.setChecked(true);
        }else{
            holder.cb_item_collect_select.setChecked(false);
        }

        holder.cb_item_collect_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    System.out.println("选中" + position);
                    allList.get(position).setCheck(true);
                }else{
                    System.out.println("取消选中" + position);
                    allList.get(position).setCheck(false);
                    Intent intent = new Intent("CHANGE_ACTION");
                    context.sendBroadcast(intent);
                }
            }
        });

//
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);

                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return allList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imv_item_collect_pic;
        ImageView imv_collect_platform;
        RelativeLayout rl_item_collect_select;
        CheckBox cb_item_collect_select;
        TextView tv_item_collect_title;
        TextView tv_item_collect_roll;
        TextView tv_item_collect_price;
        TextView tv_item_collect_volume;


        public MyViewHolder(View view)
        {
            super(view);
            rl_item_collect_select = (RelativeLayout) view.findViewById(R.id.rl_item_collect_select);
//            cb_item_collect_select = (CheckBox) view.findViewById(R.id.cb_item_collect_select);
            imv_item_collect_pic = (ImageView) view.findViewById(R.id.imv_item_collect_pic);
            imv_collect_platform = (ImageView) view.findViewById(R.id.imv_collect_platform);
            tv_item_collect_title = (TextView) view.findViewById(R.id.tv_item_collect_title);
            tv_item_collect_roll = (TextView) view.findViewById(R.id.tv_item_collect_roll);
            tv_item_collect_price = (TextView) view.findViewById(R.id.tv_item_collect_price);
            tv_item_collect_volume = (TextView) view.findViewById(R.id.tv_item_collect_volume);

        }
    }

    public List<Goods> newList(){
        return allList;
    }
}
