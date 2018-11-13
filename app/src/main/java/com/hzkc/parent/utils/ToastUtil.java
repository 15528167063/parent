package com.hzkc.parent.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;


/**
 * 类名：ToastUtil
 * 作者：Yong.Wang
 * 功能：
 * 创建日期：2016-11-20 9:30
 */

public class ToastUtil extends Toast {

    private TextView textView;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public ToastUtil(Context context) {
        super(context);
        View rootView = View.inflate(MyApplication.getInstance(), R.layout.toast_view_layout, null);
        textView = (TextView) rootView.findViewById(R.id.toast_text_view);
        setView(rootView);
    }

    @Override
    public void setText(int resId) {
        textView.setText(resId);
    }

    @Override
    public void setText(CharSequence s) {
        textView.setText(s);
    }

    /**
     * Show Toast in short time by custom
     *
     * @param context The context to use.
     * @param msg     The text to show.  Can be formatted text.
     */
    public static void showToast(Context context, CharSequence msg) {
        showToast(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT);
    }

    public static void showToast( CharSequence msg) {
        showToast(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT);
    }
    public static void showToast( int resid) {
        showToast(MyApplication.getInstance(), MyApplication.getInstance().getResources().getString(resid), Toast.LENGTH_SHORT);
    }
    /**
     * Show Toast in short time by custom
     *
     * @param context The context to use.
     * @param msg     The text to show.  Can be formatted text.
     */
    public static void showToastLong(Context context, CharSequence msg) {
        showToast(MyApplication.getInstance(), msg, Toast.LENGTH_LONG);
    }

    /**
     * Show Toast by custom
     *
     * @param context  The context to use.
     * @param msg      The text to show.  Can be formatted text.
     * @param duration How long to display the message.
     */
    public static void showToast(Context context, CharSequence msg, int duration) {
        makeToast(MyApplication.getInstance(), msg, duration, Gravity.BOTTOM).show();
    }

    /**
     * Show Toast by custom
     *
     * @param context  The context to use.
     * @param msg      The text to show.  Can be formatted text.
     * @param duration How long to display the message.
     * @param gravity  Set the location of the text display.
     */
    public static void showToast(Context context, CharSequence msg, int duration, int gravity) {
        makeToast(MyApplication.getInstance(), msg, duration, gravity).show();
    }

    /**
     * Make a standard toast that just contains a text view.
     *
     * @param context  The context to use.  Usually your {@link Application}
     *                 or {@link Activity} object.
     * @param msg      The text to show.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or
     *                 {@link #LENGTH_LONG}
     * @param gravity  Set the location of the text display.
     */
    static ToastUtil toast;
    private static ToastUtil makeToast(Context context, CharSequence msg, int duration, int gravity) {
        if (toast == null){
            toast = new ToastUtil(MyApplication.getInstance());
        }
        toast.setText(msg);
        toast.setDuration(duration);
        toast.setGravity(gravity, 0,50);
        return toast;
    }

}
