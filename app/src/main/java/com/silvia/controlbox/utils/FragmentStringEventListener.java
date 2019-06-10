package com.silvia.controlbox.utils;

/**
 * @file FragmentKeyEventListener
 * 写个接口把activity里的方法传给fragment
 * Created by Silvia_cooper on 2019/1/21.
 */
public interface FragmentStringEventListener {
    String onFragmentStringEvent(String text);
    String GetDeviceID(String Dev);
    String GetIMEI(String IMEI);
    String GETNID(String NID);
    String GETBLEMAC(String BLEMAC);
    void getAdcResult(boolean adc);//返回adc的结果
    void getMenciResult(boolean menci);//返回门磁的结果
    void getProductResult(boolean product);//返回测试结果
    void getPrinterResult(boolean printer);//返回打印结果


}
