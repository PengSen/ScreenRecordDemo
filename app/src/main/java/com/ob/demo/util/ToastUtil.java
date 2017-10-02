package com.ob.demo.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.ob.demo.R;

/**
 * Created by ob on 2017/8/20.
 *
 */

public class ToastUtil {
    private static Toast toast;
    private ToastUtil(Context context, String text, int duration){
        TextView textView = new TextView(context);
        textView.setBackgroundResource(R.drawable.toast_bg);
//        textView.setPadding(ScreenUtil.dip2px(context, 20),ScreenUtil.dip2px(context, 10),ScreenUtil.dip2px(context, 20),ScreenUtil.dip2px(context, 10));
        textView.setPadding(20,10,20,10);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        if(toast == null){
            toast = new Toast(context);
            toast.setDuration(duration);
        }
        toast.setView(textView);
        toast.show();
    }

    public static ToastUtil makeText(Context context, String text, int duration) {
        return new ToastUtil(context, text, duration);
    }
    public static ToastUtil makeText(Context context, int resId, int duration) {
        return makeText(context, context.getString(resId), duration);
    }
    public static ToastUtil makeText(Context context, int resId) {
        return makeText(context, context.getString(resId));
    }
    public static ToastUtil makeText(Context context, String text) {//默认是长吐司
        return new ToastUtil(context, text, Toast.LENGTH_LONG);
    }

}
