package com.example.s.why_no.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * Created by S on 2016/10/31.
 */

public class LazyScrollView extends ScrollView {

    private static final String tag="LazyScrollView";
    private Handler handler;
    private View view;

    private View inner; // 孩子View
    private static final int DEFAULT_POSITION = -1;
    private float y = DEFAULT_POSITION;// 点击时y的坐标
    private Rect normal = new Rect();

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;


    public LazyScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public LazyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public LazyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }


//--------------------------------------------------------------------------------------------
    /**
     * 反弹效果
     */
    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            inner = getChildAt(0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (inner == null) {
            return super.onTouchEvent(ev);
        } else {
            commOnTouchEvent(ev);
        }

        return super.onTouchEvent(ev);
    }

    public void commOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                y = ev.getY();
                break;
            case MotionEvent.ACTION_UP:

                if (isNeedAnimation()) {
                    animation();
                }

                y = DEFAULT_POSITION;
                break;

            /**
             * 排除第一次移动计算，因为第一次无法得知y左边，在MotionEvent.ACTION_DOWN中获取不到，
             * 因为此时是MyScrollView的Tocuh时间传递到了ListView的孩子item上面。所以从第二次开始计算
             * 然而我们也要进行初始化，就是第一次移动的时候让滑动距离归零，之后记录准确了就正常执行
             */
            case MotionEvent.ACTION_MOVE:
                float preY = y;
                float nowY = ev.getY();
                if (isDefaultPosition(y)) {
                    preY = nowY;
                }
                int deltaY = (int) (preY - nowY);
                scrollBy(0, deltaY);
                y = nowY;
                // 当滚动到最上或者最下时就不会再滚动，这时移动布局
                if (isNeedMove()) {
                    if (normal.isEmpty()) {
                        // 保存正常的布局位置
                        normal.set(inner.getLeft(), inner.getTop(),
                                inner.getRight(), inner.getBottom());

                    }
                    // 移动布局
                    inner.layout(inner.getLeft(), inner.getTop() - deltaY,
                            inner.getRight(), inner.getBottom() - deltaY);
                }
                break;

            default:
                break;
        }
    }

    // 开启动画移动

    public void animation() {
        // 开启移动动画
        TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),
                normal.top);
        ta.setDuration(200);
        inner.startAnimation(ta);
        // 设置回到正常的布局位置
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);

        normal.setEmpty();

    }

    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    // 是否需要移动布局
    public boolean isNeedMove() {

        int offset = inner.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();

        if ( scrollY == offset) {//若想顶部反弹加上   || scrollY == 0
            return true;
        }
        return false;
    }

    // 检查是否处于默认位置
    private boolean isDefaultPosition(float position) {
        return position == DEFAULT_POSITION;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }


        //这个获得总的高度
    public int computeVerticalScrollRange(){
        return super.computeHorizontalScrollRange();
    }
    public int computeVerticalScrollOffset(){
        return super.computeVerticalScrollOffset();
    }


    //--------------------------------------------------------------------------------------------
    private void init(){

        this.setOnTouchListener(onTouchListener);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // process incoming messages here
                super.handleMessage(msg);
                switch(msg.what){
                    case 1:
                        if(view.getMeasuredHeight() <= getScrollY() + getHeight()) {
                            if(onScrollListener!=null){
                                onScrollListener.onBottom();
                            }

                        }else if(getScrollY()==0){
                            if(onScrollListener!=null){
                                onScrollListener.onTop();
                            }
                        }
                        else{
                            if(onScrollListener!=null){
                                onScrollListener.onScroll();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };

    }

    OnTouchListener onTouchListener=new OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    if(view!=null&&onScrollListener!=null){
                        handler.sendMessageDelayed(handler.obtainMessage(1), 200);
                    }
                    requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    requestDisallowInterceptTouchEvent(false);
                    break;
                default:
                    break;
            }
            return false;
        }

    };

    /**
     * 获得参考的View，主要是为了获得它的MeasuredHeight，然后和滚动条的ScrollY+getHeight作比较。
     */
    public void getView(){
        this.view=getChildAt(0);
        if(view!=null){
            init();
        }
    }

    /**
     * 定义接口
     * @author admin
     *
     */
    public interface OnScrollListener{
        void onBottom();
        void onTop();
        void onScroll();
    }
    private OnScrollListener onScrollListener;
    public void setOnScrollListener(OnScrollListener onScrollListener){
        this.onScrollListener=onScrollListener;
    }
}
