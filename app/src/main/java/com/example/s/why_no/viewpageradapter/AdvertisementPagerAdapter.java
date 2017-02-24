package com.example.s.why_no.viewpageradapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.example.s.why_no.activity.DetailsActivity;
import com.example.s.why_no.activity.NetActivity;
import com.example.s.why_no.bean.Thumbnail;

import java.util.List;

public class AdvertisementPagerAdapter extends PagerAdapter {

	private List<View> list;
	private Context context;
	public List<Thumbnail> allList;

	public AdvertisementPagerAdapter(List<View> list, List<Thumbnail> allList,Context context) {
		super();
		this.list = list;
		this.allList = allList;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

//	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
//		container.removeView((View)object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = list.get(position % list.size());
		final Thumbnail aitemList = allList.get(position % allList.size());

		ViewParent parent = view.getParent();
		if (parent != null) {
			ViewGroup group = (ViewGroup) parent;
			group.removeView(view);
		}
		container.addView(view);
		
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = null;

				if (!aitemList.href.equals("")){
					intent= new Intent(context, NetActivity.class);
					intent.putExtra("url",aitemList.href);
					intent.putExtra("title","给实惠");
					Log.e("LINK " ,"   "  + aitemList.href);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}else{
					intent= new Intent(context, DetailsActivity.class);
					intent.putExtra("id",aitemList.a);
					intent.putExtra("ification",aitemList.ification);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);

				}



			}
		});
		return view;
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

}
