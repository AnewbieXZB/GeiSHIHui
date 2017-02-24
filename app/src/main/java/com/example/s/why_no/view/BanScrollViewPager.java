package com.example.s.why_no.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BanScrollViewPager extends ViewPager {

    private boolean scrollble = true;

    public BanScrollViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public BanScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (!scrollble) {
            return true;
        }
        return super.onTouchEvent(arg0);
    }


    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }

}
