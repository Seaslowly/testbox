package com.silvia.controlbox.utils;

import android.view.KeyEvent;

/**
 * @file FragmentKeyEventListener
 * 写个接口把activity里的方法传给fragment
 * Created by Silvia_cooper on 2019/1/21.
 */
public interface FragmentKeyEventListener {
    String onFragmentKeyEvent(String barcode);

}
