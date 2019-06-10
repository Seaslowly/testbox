package com.silvia.controlbox.utils;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

/**
 * @file FileName
 * Created by Silvia_cooper on 2019/1/15.
 */
public class MyAccessibilityService extends AccessibilityService {
    /**
     *(可选)当系统成功连接到该AccessibilityService时，将调用此方法。主要用与一次性配置或调整的代码。
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

    }

    /**
     *
     * @param accessibilityEvent
     * (必要)当系统监测到相匹配的AccessibilityEvent事件时，将调用此方法，在整个Service的生命周期中，该方法将被多次调用。
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType=accessibilityEvent.getEventType();
        switch (eventType){
            //通知栏变化
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                break;

                default:
                    break;
        }
    }

    /**
     * 中断
     * (必要)系统需要中断AccessibilityService反馈时，将调用此方法。AccessibilityService反馈包括服务发起的震动、音频等行为。
     */
    @Override
    public void onInterrupt() {

    }

    /**
     * (可选)系统要关闭该服务是，将调用此方法。主要用来释放资源。
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



}
