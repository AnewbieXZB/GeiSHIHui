package com.example.s.why_no.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.MessageRecord;

import java.util.List;

/**
 * Created by S on 2016/10/20.
 */

public class MessageRecordListAdapter extends  RecyclerView.Adapter<MessageRecordListAdapter.MyViewHolder> {


    private Context context;
    private List<MessageRecord.Information> allList;
    private LayoutInflater mInflater;

    public MessageRecordListAdapter(List<MessageRecord.Information> allList, Context context) {
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
                R.layout.item_message, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tv_item_message_titile.setText(allList.get(position).name);
        holder.tv_item_message_details.setText(allList.get(position).text);
        holder.tv_item_message_time.setText(allList.get(position).time);
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
        TextView tv_item_message_titile;
        TextView tv_item_message_details;
        TextView tv_item_message_time;

        public MyViewHolder(View view)
        {
            super(view);
            tv_item_message_titile = (TextView) view.findViewById(R.id.tv_item_message_titile);
            tv_item_message_details = (TextView) view.findViewById(R.id.tv_item_message_details);
            tv_item_message_time = (TextView) view.findViewById(R.id.tv_item_message_time);
        }
    }


}
