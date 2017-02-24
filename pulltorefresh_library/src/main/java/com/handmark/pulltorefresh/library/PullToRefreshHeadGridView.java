package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;

import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;

public class PullToRefreshHeadGridView extends PullToRefreshAdapterViewBase<HeaderGridView>{

	public PullToRefreshHeadGridView(Context context) {
		super(context);
	}

	public PullToRefreshHeadGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshHeadGridView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshHeadGridView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected final HeaderGridView createRefreshableView(Context context, AttributeSet attrs) {
		final HeaderGridView gv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			gv = new InternalGridViewSDK9(context, attrs);
		} else {
			gv = new InternalGridView(context, attrs);
		}

		// Use Generated ID (from res/values/ids.xml)
		gv.setId(R.id.gridview);
		return gv;
	}



	class InternalGridView extends HeaderGridView implements EmptyViewMethodAccessor {

		public InternalGridView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshHeadGridView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}
	}

	@TargetApi(9)
	final class InternalGridViewSDK9 extends InternalGridView {

		public InternalGridViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshHeadGridView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}
}
