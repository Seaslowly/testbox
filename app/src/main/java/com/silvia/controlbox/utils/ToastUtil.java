package com.silvia.controlbox.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 统一全局的toast
 */

public class ToastUtil {

    private static Context mContext;

    public ToastUtil(Context context) {
        mContext = context;
    }

    private static Toast mToast;

    public static void showShortToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void showLongToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


    public static void showShortToast(int text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, mContext.getString(text), Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void showLongToast(int text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, mContext.getString(text), Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
