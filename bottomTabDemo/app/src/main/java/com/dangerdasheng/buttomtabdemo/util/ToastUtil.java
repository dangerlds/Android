package com.dangerdasheng.buttomtabdemo.util;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.dangerdasheng.buttomtabdemo.base.MyApplication;

import me.drakeet.support.toast.ToastCompat;

/**
 * =============================
 * Author:   liudasheng
 * Version:  1.0
 * DateTime: 2020/08/14
 * Function: 单例Toast
 * =============================
 */
public class ToastUtil {
    private static ToastCompat mToast;
    @SuppressLint("ShowToast")
    public static void showToast(String text){
        if (mToast == null){
            mToast = ToastCompat.makeText(MyApplication.getInstance(),text,
                    Toast.LENGTH_SHORT);
        }else {
            mToast.cancel();
            mToast = ToastCompat.makeText(MyApplication.getInstance(),text,
                    Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(text);
        mToast.show();
    }

    @SuppressLint("ShowToast")
    public static void showToastLong(String text){
        if (mToast == null){
            mToast = ToastCompat.makeText(MyApplication.getInstance(),text,
                    Toast.LENGTH_LONG);
        }else {
            mToast.cancel();
            mToast = ToastCompat.makeText(MyApplication.getInstance(),text,
                    Toast.LENGTH_LONG);
        }
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setText(text);
        mToast.show();
    }
}
