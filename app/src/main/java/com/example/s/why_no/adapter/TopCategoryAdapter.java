package com.example.s.why_no.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.s.why_no.R;

import java.util.List;

/**
 * Created by S on 2016/10/20.
 */

public class TopCategoryAdapter extends RecyclerView.Adapter<TopCategoryAdapter.MyViewHolder> {


    private Context context;
    private List<String> list;
    private LayoutInflater mInflater;

    public TopCategoryAdapter(Context context, List<String> list) {
        mInflater = LayoutInflater.from(context);
        this.list = list;

    }

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }
    private OnItemClickLitener mOnItemClickLitener;
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(
                R.layout.item_fragment01_top_categroy, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        holder.tv_item_top_category.setText(list.get(position));

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
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv_item_top_category;

        public MyViewHolder(View view)
        {
            super(view);
            tv_item_top_category = (TextView) view.findViewById(R.id.tv_item_top_category);
        }
    }
}
