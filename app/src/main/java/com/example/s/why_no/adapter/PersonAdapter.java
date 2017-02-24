package com.example.s.why_no.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.s.why_no.R;

/**
 * Created by S on 2016/10/20.
 */

public class PersonAdapter extends  RecyclerView.Adapter<PersonAdapter.MyViewHolder> {


    private Context context;
    private LayoutInflater mInflater;
    private String[] explains = {"送货地址","我的收藏","积分记录","站内消息"
            ,"关于我们","意见反馈","积分商城","积分兑换"};
    private int[] pictures = {R.drawable.ic_launcher,R.drawable.ic_launcher
            ,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher
            ,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};

    public PersonAdapter( Context context) {
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
                R.layout.item_person, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

//        holder.imv_person_pic.setImageResource(pictures[position]);
        holder.tv_person_explain.setText(explains[position]);

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
        return explains.length;
    }



    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imv_person_pic;
        TextView tv_person_explain;

        public MyViewHolder(View view)
        {
            super(view);
            imv_person_pic = (ImageView) view.findViewById(R.id.imv_item_pic);
            tv_person_explain = (TextView) view.findViewById(R.id.tv_person_explain);

        }
    }


}
