package com.silvia.controlbox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;


/**
 * @file FileName
 * Created by Silvia_cooper on 2018/12/11.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarLightMode(this);

    }

}
