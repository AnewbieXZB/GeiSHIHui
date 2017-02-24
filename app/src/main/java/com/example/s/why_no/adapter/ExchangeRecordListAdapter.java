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
import com.example.s.why_no.bean.ExchangeRecord;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by S on 2016/10/20.
 */

public class ExchangeRecordListAdapter extends  RecyclerView.Adapter<ExchangeRecordListAdapter.MyViewHolder> {

    private DisplayImageOptions options = options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.pictures_err)//刚开始显示图片
            .showImageOnFail(R.drawable.pictures_err)//加载产生错误显示
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private Context context;
    private List<ExchangeRecord.Record> allList;
    private LayoutInflater mInflater;

    public ExchangeRecordListAdapter(List<ExchangeRecord.Record> allList, Context context) {
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
                R.layout.item_exchange_record, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        ImageLoader.getInstance().displayImage(allList.get(position).img
                ,  holder.imv_item_exchange_img, options);

        holder.tv_item_exchange_name.setText(allList.get(position).name );
        holder.tv_item_exchange_phone.setText( allList.get(position).tel);
        holder.tv_item_exchange_address.setText("地址：" + allList.get(position).region + " "
                + allList.get(position).address);
        holder.tv_item_exchange_time.setText(allList.get(position).time);
        holder.tv_item_exchange_arithmetic.setText(allList.get(position).arithmetic + " 积分");

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
        ImageView imv_item_exchange_img;
        TextView tv_item_exchange_name;
        TextView tv_item_exchange_phone;
        TextView tv_item_exchange_address;
        TextView tv_item_exchange_time;
        TextView tv_item_exchange_arithmetic;


        public MyViewHolder(View view)
        {
            super(view);
            imv_item_exchange_img = (ImageView) view.findViewById(R.id.imv_item_exchange_img);
            tv_item_exchange_name = (TextView) view.findViewById(R.id.tv_item_exchange_name);
            tv_item_exchange_phone = (TextView) view.findViewById(R.id.tv_item_exchange_phone);
            tv_item_exchange_address = (TextView) view.findViewById(R.id.tv_item_exchange_address);
            tv_item_exchange_time = (TextView) view.findViewById(R.id.tv_item_exchange_time);
            tv_item_exchange_arithmetic = (TextView) view.findViewById(R.id.tv_item_exchange_arithmetic);

        }
    }


}
