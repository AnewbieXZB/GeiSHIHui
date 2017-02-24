package com.example.s.why_no.utils_window;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by S on 2016/11/4.
 */

public class ManageWindow {

    public int getWidth(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }
    public int getHeight(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }
}
