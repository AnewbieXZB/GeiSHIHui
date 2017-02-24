package com.example.s.why_no.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by S on 2016/10/31.
 */

public class BanRecylerView extends RecyclerView {

    private ArrayList<View> mHeaderViews = new ArrayList<>() ;

    private ArrayList<View> mFootViews = new ArrayList<>() ;

    private Adapter mAdapter ;


    public BanRecylerView(Context context) {
        super(context);
    }

    public BanRecylerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    public BanRecylerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /***
     *
     * 此方法用于重新测量尺寸
     * 就可以解决ScrollView、ListView嵌套问题
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

//    public void addHeaderView(View view){
//        mHeaderViews.clear();
//        mHeaderViews.add(view);
//        if (mAdapter != null){
//            if (!(mAdapter instanceof RecyclerWrapAdapter)){
//                mAdapter = new RecyclerWrapAdapter(mHeaderViews,mFootViews,mAdapter) ;
////                mAdapter.notifyDataSetChanged();
//            }
//        }
//    }
//
//    public void addFootView(View view){
//        mFootViews.clear();
//        mFootViews.add(view);
//        if (mAdapter != null){
//            if (!(mAdapter instanceof RecyclerWrapAdapter)){
//                mAdapter = new RecyclerWrapAdapter(mHeaderViews,mFootViews,mAdapter) ;
////                mAdapter.notifyDataSetChanged();
//            }
//        }
//    }
//
//    @Override
//    public void setAdapter(Adapter adapter) {
//
//        if (mHeaderViews.isEmpty()&&mFootViews.isEmpty()){
//            super.setAdapter(adapter);
//        }else {
//            adapter = new RecyclerWrapAdapter(mHeaderViews,mFootViews,adapter) ;
//            super.setAdapter(adapter);
//        }
//        mAdapter = adapter ;
//    }
}
