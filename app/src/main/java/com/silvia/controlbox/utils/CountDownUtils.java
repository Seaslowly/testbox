package com.silvia.controlbox.utils;

import android.os.CountDownTimer;
import android.widget.Button;

public class CountDownUtils extends CountDownTimer {

    private Button mButton;

    public CountDownUtils(Button button, long millisInFuture, long countDownInterval) {//控件，定时总时间,间隔时间
        super(millisInFuture, countDownInterval);
        this.mButton=button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mButton.setClickable(false);//设置不可点击
        mButton.setText(millisUntilFinished/1000+"");//设置倒计时时间
    }

    @Override
    public void onFinish() {
        mButton.setClickable(true);//重新获得点击
        mButton.setText("开始测试");

    }
}
