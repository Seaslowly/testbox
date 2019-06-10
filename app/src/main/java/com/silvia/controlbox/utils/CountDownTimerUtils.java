package com.silvia.controlbox.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.silvia.controlbox.R;

public class CountDownTimerUtils extends CountDownTimer {
    private TextView mTextView; //显示倒计时的文字
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        mTextView=textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + "秒后可重新发送");  //设置倒计时时间
        mTextView.setBackgroundResource(R.drawable.validate_code_press_bg); //设置按钮为灰色，这时是不能点击的
    }

    @Override
    public void onFinish() {
        mTextView.setText("开始测试");
        mTextView.setClickable(true);//重新获得点击
        mTextView.setBackgroundResource(R.drawable.validate_code_normal_bg);  //还原背景色
    }
}
