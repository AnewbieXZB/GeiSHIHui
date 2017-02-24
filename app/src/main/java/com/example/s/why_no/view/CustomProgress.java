package com.example.s.why_no.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.s.why_no.R;


/**
 * 一个转动菊花的等待动画
 */
public class CustomProgress extends Dialog {
	private static CustomProgress dialog;

	public CustomProgress(Context context) {
		super(context);
	}

	public CustomProgress(Context context, int theme) {
		super(context, theme);
	}

	
	public void onWindowFocusChanged(boolean hasFocus) {
		ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
		
		AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
		
		spinner.start();
	}

	
	public void setMessage(CharSequence message) {
		if (message != null && message.length() > 0) {
			findViewById(R.id.message).setVisibility(View.VISIBLE);
			TextView txt = (TextView) findViewById(R.id.message);
			txt.setText(message);
			txt.invalidate();
		}
	}

	
	public static CustomProgress show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
		dialog = new CustomProgress(context, R.style.Custom_Progress);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_custom);
		if (message == null || message.length() == 0) {
			dialog.findViewById(R.id.message).setVisibility(View.GONE);
		} else {
			TextView txt = (TextView) dialog.findViewById(R.id.message);
			txt.setText(message);
		}
		
		dialog.setCancelable(cancelable);
		
		dialog.setOnCancelListener(cancelListener);
		
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		
		lp.dimAmount = 0.2f;
		dialog.getWindow().setAttributes(lp);
		// dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		return dialog;
	}
	
	public static void disDia(){

//		dialog.dismiss();
//		dialog=null;

		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
				dialog=null;
			}
		}
	}
}
