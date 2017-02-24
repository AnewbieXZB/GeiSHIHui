package com.example.s.why_no.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.IntegralGoods;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by S on 2016/10/20.
 */

public class IntegralGoodsListAdapter extends  RecyclerView.Adapter<IntegralGoodsListAdapter.MyViewHolder> {


    private DisplayImageOptions options = options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
            .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private Context context;
    private List<IntegralGoods> allList;
    private LayoutInflater mInflater;

    public IntegralGoodsListAdapter(List<IntegralGoods> allList, Context context) {
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
                R.layout.item_integral_goods, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

//        ImageSize imageSize = new ImageSize(24, 20, 0);
//        holder.imv_item_pic.setImageBitmap(ImageLoader.getInstance().loadImageSync(allList.get(position).uimg, imageSize, options));



        ImageLoader.getInstance().displayImage(allList.get(position).img
                ,  holder.imv_item_collect_pic, options);

        holder.tv_item_intergral_goods_title.setText(allList.get(position).name);
        holder.tv_item_intergral_goods_intergral.setText(allList.get(position).need + "");
        holder.tv_intergral_goods_surplus.setText(allList.get(position).surplus + "");
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



    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imv_item_collect_pic;
        TextView tv_item_intergral_goods_title;
        TextView tv_item_intergral_goods_intergral;
        TextView tv_intergral_goods_surplus;


        public MyViewHolder(View view)
        {
            super(view);
            imv_item_collect_pic = (ImageView) view.findViewById(R.id.imv_item_collect_pic);
            tv_item_intergral_goods_title = (TextView) view.findViewById(R.id.tv_item_intergral_goods_title);
            tv_item_intergral_goods_intergral = (TextView) view.findViewById(R.id.tv_item_intergral_goods_intergral);
            tv_intergral_goods_surplus = (TextView) view.findViewById(R.id.tv_intergral_goods_surplus);
        }
    }


}
