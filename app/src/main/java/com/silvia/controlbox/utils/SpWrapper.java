package com.silvia.controlbox.utils;

import android.content.Context;


public class SpWrapper {
    /**
     * 设置服务ip
     * @param context
     * @param value
     */
    public static void setHost(Context context, String value) {
        SpUtil.saveString(context, Common.HOST, value);
    }

    /**
     * 获取服务ip
     * @param context
     * @param defaultValue
     * @return
     */
    public static String getHost(Context context, String defaultValue) {
        return SpUtil.getString(context, Common.HOST, defaultValue);
    }
    /**
     * 保存ip端口号
     * @param context
     * @param value
     */
    public static void setHostPort(Context context,int value){
        SpUtil.saveInt(context, Common.HOST_PORT, value);
    }

    /**
     * 获取ip端口号
     * @param context
     * @param defaultValue
     * @return
     */
    public static int getHostPort(Context context, int defaultValue) {
        return SpUtil.getInt(context, Common.HOST_PORT, defaultValue);
    }

    /**
     * 保存设备工作时间
     * @param context
     * @param value
     */
    public static void setDeviceWorkTime(Context context,int value){
        SpUtil.saveInt(context, Common.DEVICE_WORK_TIME, value);
    }

    /**
     * 获取设备工作时间
     * @param context
     * @param defaultValue
     * @return
     */
    public static int getDeviceWorkTime(Context context, int defaultValue) {
        return SpUtil.getInt(context, Common.DEVICE_WORK_TIME, defaultValue);
    }
    /**
     * 保存设备定时采集时间
     * @param context
     * @param value
     */
    public static void setDeviceTimingAcquisition(Context context,int value){
        SpUtil.saveInt(context, Common.DEVICE_TIMING_ACQUISITION, value);
    }

    /**
     * 获取设备定时采集时间
     * @param context
     * @param defaultValue
     * @return
     */
    public static int getDeviceTimingAcquisition(Context context, int defaultValue) {
        return SpUtil.getInt(context, Common.DEVICE_TIMING_ACQUISITION, defaultValue);
    }

    /**
     * 保存设备设备定时上报
     * @param context
     * @param value
     */
    public static void setDeviceTimingReport(Context context,int value){
        SpUtil.saveInt(context, Common.DEVICE_TIMEING_REPORT, value);
    }

    /**
     * 获取设备定时上报
     * @param context
     * @param defaultValue
     * @return
     */
    public static int getDeviceTimingReport(Context context, int defaultValue) {
        return SpUtil.getInt(context, Common.DEVICE_TIMEING_REPORT, defaultValue);
    }

    /**
     * 保存设备设备定时上报
     * @param context
     * @param value
     */
    public static void setDeviceInstallMethod(Context context,int value){
        SpUtil.saveInt(context, Common.DEVICE_INSTALL_METHOD, value);
    }

    /**
     * 获取设备定时上报
     * @param context
     * @param defaultValue
     * @return
     */
    public static int getDeviceInstallMethod(Context context, int defaultValue) {
        return SpUtil.getInt(context, Common.DEVICE_INSTALL_METHOD, defaultValue);
    }


    /**
     * 设置设备号
     * @param context
     * @param value
     */
    public static void setDev(Context context, String value) {
        SpUtil.saveString(context, Common.DEV, value);
    }

    /**
     * 获取设备号
     * @param context
     * @param defaultValue
     * @return
     */
    public static String getDev(Context context, String defaultValue) {
        return SpUtil.getString(context, Common.DEV, defaultValue);
    }

    /**
     * 设置IMEI
     * @param context
     * @param value
     */
    public static void setIMEI(Context context, String value) {
        SpUtil.saveString(context, Common.IMEI, value);
    }

    /**
     * 获取IMEI
     * @param context
     * @param defaultValue
     * @return
     */
    public static String getIMEI(Context context, String defaultValue) {
        return SpUtil.getString(context, Common.IMEI, defaultValue);
    }


    /**
     * 设置BLEMAC
     * @param context
     * @param value
     */
    public static void setBleMac(Context context, String value) {
        SpUtil.saveString(context, Common.BLEMAC, value);
    }

    /**
     * 获取BLEMAC
     * @param context
     * @param defaultValue
     * @return
     */
    public static String getBleMac(Context context, String defaultValue) {
        return SpUtil.getString(context, Common.BLEMAC, defaultValue);
    }

    /**
     * 设置NID
     * @param context
     * @param value
     */
    public static void setNid(Context context, String value) {
        SpUtil.saveString(context, Common.NID, value);
    }

    /**
     * 获取BLEMAC
     * @param context
     * @param defaultValue
     * @return
     */
    public static String getNid(Context context, String defaultValue) {
        return SpUtil.getString(context, Common.NID, defaultValue);
    }

}
