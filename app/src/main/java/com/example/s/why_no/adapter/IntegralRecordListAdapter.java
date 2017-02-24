package com.example.s.why_no.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.Branch;

import java.util.List;

/**
 * Created by S on 2016/10/20.
 */

public class IntegralRecordListAdapter extends  RecyclerView.Adapter<IntegralRecordListAdapter.MyViewHolder> {


    private Context context;
    private List<Branch> allList;
    private LayoutInflater mInflater;

    public IntegralRecordListAdapter(List<Branch> allList, Context context) {
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
                R.layout.item_integral_record, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tv_item_record_nature.setText(allList.get(position).event);
        holder.tv_item_record_time.setText(allList.get(position).time);
        holder.tv_item_record_change.setText(allList.get(position).arithmetic);
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
        TextView tv_item_record_nature;
        TextView tv_item_record_time;
        TextView tv_item_record_change;


        public MyViewHolder(View view)
        {
            super(view);
            tv_item_record_nature = (TextView) view.findViewById(R.id.tv_item_record_nature);
            tv_item_record_time = (TextView) view.findViewById(R.id.tv_item_record_time);
            tv_item_record_change = (TextView) view.findViewById(R.id.tv_item_record_change);

        }
    }


}
