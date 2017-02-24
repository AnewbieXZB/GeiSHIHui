package com.example.s.why_no.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.s.why_no.R;
import com.example.s.why_no.bean.Goods;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by S on 2016/10/20.
 */

public class GoodsListAdapter extends  RecyclerView.Adapter<GoodsListAdapter.MyViewHolder> {


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
    private int windowWidth;
    private int windowHeight;
    private LayoutInflater mInflater;

    public GoodsListAdapter(List<Goods> allList, Context context
            , int windowWidth, int windowHeight) {
        this.allList = allList;
        this.context = context;
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
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
                R.layout.item_fragment01_goods, parent, false));
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

//        ImageSize imageSize = new ImageSize(24, 20, 0);
//        holder.imv_item_pic.setImageBitmap(ImageLoader.getInstance().loadImageSync(allList.get(position).uimg, imageSize, options));


        ViewGroup.LayoutParams  para = holder.imv_item_pic.getLayoutParams();
        para.height = (windowWidth-24)/2;
        para.width = (windowWidth-24)/2;
        holder.imv_item_pic.setLayoutParams(para);

        ImageLoader.getInstance().displayImage(allList.get(position).uimg
               +"_300x300.jpg" ,  holder.imv_item_pic, options);

        holder.tv_item_title.setText(allList.get(position).uname);
        holder.tv_item_price.setText("¥" + allList.get(position).roll);//券后价
        holder.tv_item_volume.setText(allList.get(position).volume + "");
        holder.tv_item_quan.setText( allList.get(position).discount + "元");//优惠券的面值
//

        Log.e("why no :","allList.get(position).heat    "   + position + "   " + allList.get(position).heat);

        if ((allList.get(position).heat) != null){
            if (allList.get(position).heat.equals("1")){
                holder.tv_item_today_new.setVisibility(View.VISIBLE);
            }else{
                holder.tv_item_today_new.setVisibility(View.GONE);
            }
        }else{
            holder.tv_item_today_new.setVisibility(View.GONE);
        }
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
        ImageView imv_item_pic;
        TextView tv_item_quan;
        TextView tv_item_today_new;
        TextView tv_item_title;
        TextView tv_item_price;
        TextView tv_item_volume;


        public MyViewHolder(View view)
        {
            super(view);
            imv_item_pic = (ImageView) view.findViewById(R.id.imv_item_pic);
            tv_item_quan = (TextView) view.findViewById(R.id.tv_item_quan);
            tv_item_today_new = (TextView) view.findViewById(R.id.tv_item_today_new);
            tv_item_title = (TextView) view.findViewById(R.id.tv_item_title);
            tv_item_price = (TextView) view.findViewById(R.id.tv_item_price);
            tv_item_volume = (TextView) view.findViewById(R.id.tv_item_volume);
        }
    }


//    private boolean isTodayNew(String startTime){
//
////        String startTime = time.replaceAll("-","");
//        String systemTime = getSystemTime();//当前系统的年月日
//        System.out.println("服务器传递过来的时间：" + startTime);
//        System.out.println("系统时间：" + systemTime);
//
//        //判断两个时间是否相同  相同则返回 ture
//        if (startTime.equals(systemTime)){
//            return true;
//        }else{
//            return false;
//        }
//
//    }
//
//    // 获取系统当前时间
//    public String getSystemTime() {
//
//        SimpleDateFormat formatter;
//        Date curDate;
//
//        String time = "";
//
//        formatter = new SimpleDateFormat("yyyy-MM-dd");
//        curDate = new Date(System.currentTimeMillis());
//        time = formatter.format(curDate);
//
//        return time;
//    }
}
