package com.silvia.controlbox.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silvia.controlbox.R;


public class MyDialog extends Dialog implements OnClickListener{
    private Context mContext;
    private TextView mTextVoltage;//电压
    private TextView mTextTiltSensor;//倾斜传感器
    private TextView mTextTemper;//温度
    private TextView mTextHumidity;//湿度
    private TextView mTextWateBan;//水禁
    private TextView mButtonCancel;//取消
    private TextView mButtonNext;//下一步
    private TextView mTextTitle;//title
    private LinearLayout mLinearChoose;//选择
    private String voltageStr;
    private String tiltsensorStr;
    private String temperStr;
    private String humidityStr;
    private String waterStr;
    private String titleStr;
    private String buttonStr;
    private boolean addParam=false;
    private boolean showChoose=false;
    boolean param1;
    boolean param2;
    boolean param3;
    boolean param4;
    boolean param5;
    private DialogListenner dialogListenner;
    public MyDialog(@NonNull Context context, DialogListenner listenner) {
        super(context);
        this.mContext=context;
        this.dialogListenner=listenner;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_main);
        setHeightWidth();
        //按空白处不能取消动画
        //setCanceledOnTouchOutside(false);
        //初始化界面控件
        initView();
        //初始化数据
        initData();
    }

    public void setHeightWidth(){
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = this.getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.height = (int) (d.heightPixels * 0.4); // 改变的是dialog框在屏幕中的位置而不是大小
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(lp);
    }


    /**
     * 初始化界面控件
     */
    private void initView() {
        mTextVoltage=findViewById(R.id.t_voltage);
        mTextTiltSensor=findViewById(R.id.t_tiltSensor);
        mTextTemper=findViewById(R.id.t_temper);
        mTextHumidity=findViewById(R.id.t_humidity);
        mTextWateBan=findViewById(R.id.t_waterban);
        mButtonCancel=findViewById(R.id.button_cancel);
        mButtonNext=findViewById(R.id.button_next);
        mLinearChoose=findViewById(R.id.l_choose);
        mTextTitle=findViewById(R.id.t_title);
        mButtonCancel.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
    }
    public void testDialog(boolean param1value,String param1String,boolean param2value,String param2String,boolean param3value,String param3String,boolean param4value,String param4String){
        showChoose=true;
        buttonStr="完成";
        titleStr="测试传感器";
        param1=param1value;
        voltageStr="电压："+param1String+"V";
        param2=param2value;
        tiltsensorStr="倾斜传感器:"+param2String;
        param3=param3value;
        temperStr="温度："+param3String+"℃";
        param4=param4value;
        humidityStr="湿度:"+param4String+"%";
        show();
    }

    /**
     * 门磁dialog
     * @param param1value
     * @param param1String
     * @param param2value
     * @param param2String
     * @param param3value
     * @param param3String
     * @param param4value
     * @param param4String
     * @param param5value
     * @param param5String
     */
    public void MenciDialog(boolean isShow,String btnStr,boolean param1value,String param1String,boolean param2value,String param2String,boolean param3value,String param3String,boolean param4value,String param4String,boolean param5value,String param5String){
        buttonStr=btnStr;
        titleStr="测试开关量";
        showChoose=isShow;//是否显示底部选择框
        addParam=true;//显示第五个参数
        param1=param1value;
        voltageStr=param1String;
        param2=param2value;
        tiltsensorStr=param2String;
        param3=param3value;
        temperStr=param3String;
        param4=param4value;
        humidityStr=param4String;
        param5=param5value;
        waterStr=param5String;
        show();
    }


    /**
     * 初始化定义数据
     */
    public void initData(){
        if (buttonStr!=null){
            mButtonNext.setText(buttonStr);
        }
        if (titleStr!=null){
            mTextTitle.setText(titleStr);
        }
        if (showChoose==false){
            mLinearChoose.setVisibility(View.GONE);
        }else {
            mLinearChoose.setVisibility(View.VISIBLE);
        }
        if (addParam==false){
            mTextWateBan.setVisibility(View.GONE);
        }else {
            mTextWateBan.setVisibility(View.VISIBLE);
        }
        if (voltageStr != null) {
            mTextVoltage.setText(voltageStr);
        }

        if (temperStr!=null){
            mTextTemper.setText(temperStr);
        }
        if (tiltsensorStr!=null){
            mTextTiltSensor.setText(tiltsensorStr);
        }
        if (humidityStr!=null){
            mTextHumidity.setText(humidityStr);
        }

        if (waterStr!=null){
            mTextWateBan.setText(waterStr);
        }
        Drawable drawableTrueLeft = this.getContext().getResources().getDrawable(
                R.mipmap.right);
        Drawable drawableFalseLeft = this.getContext().getResources().getDrawable(
                R.mipmap.error);
        if (param1==true){
            mTextVoltage.setCompoundDrawablesWithIntrinsicBounds(drawableTrueLeft,
                    null, null, null);
        }

        if (param2==true){
            mTextTiltSensor.setCompoundDrawablesWithIntrinsicBounds(drawableTrueLeft,
                    null, null, null);
        }
        if (param3==true){
            mTextTemper.setCompoundDrawablesWithIntrinsicBounds(drawableTrueLeft,
                    null, null, null);
        }
        if (param4==true){
            mTextHumidity.setCompoundDrawablesWithIntrinsicBounds(drawableTrueLeft,
                    null, null, null);
        }

        if (param5==true){
            mTextWateBan.setCompoundDrawablesWithIntrinsicBounds(drawableTrueLeft,
                    null, null, null);
        }
    }
    public void DissmissDialog(Context context){
       dismiss();
    }

    public void cancelDialog(){
        dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v) {
        dialogListenner.onClick(v);
        switch (v.getId()){
            case R.id.button_next:
                dismiss();
                break;
        }
    }
}

