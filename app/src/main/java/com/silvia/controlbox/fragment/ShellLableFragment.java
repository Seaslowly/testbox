package com.silvia.controlbox.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.silvia.controlbox.MainActivity;
import com.silvia.controlbox.R;
import com.silvia.controlbox.utils.Common;
import com.silvia.controlbox.utils.CountDownUtils;
import com.silvia.controlbox.utils.FragmentDeviceEventListener;
import com.silvia.controlbox.utils.FragmentKeyEventListener;
import com.silvia.controlbox.utils.ToastUtil;
import com.silvia.controlbox.utils.Util;


/**
 * @file ShellLableFragment
 * 外壳标签打印页面
 * Created by Silvia_cooper on 2018/12/11.
 */
public class ShellLableFragment extends Fragment implements View.OnClickListener, FragmentKeyEventListener, View.OnTouchListener, FragmentDeviceEventListener, View.OnKeyListener {
    private View view;
    private RadioGroup mRg_type;
    private RadioButton mDoor_magnet;
    private RadioButton mManhole_cover;
    private EditText mPcba_lable;
    private EditText mDevice_id;
    private EditText mImei_number;
    private EditText mCarrier_identification;
    private EditText mData_now;
    private EditText mBleMac;
    private EditText mSimNum;
    private Button mPrintLable;
    private Context mContext;
    private CountDownUtils mCountDownUtils;

    int Selected = 0;
    String MB = "DX";
    private String ClickEdit=null;
    MainActivity mainActivity;
    private String mBarcode;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_shell_printLable:
                //获取页面上的值,打印 打印之后清空页面上的值
                boolean printerState=getValue();
                if (printerState==true){
                    mBleMac.setText("");
                    mDevice_id.setText("");
                    mImei_number.setText("");
                }
                //防止误触发点击两次 做个倒计时
                mCountDownUtils=new CountDownUtils(mPrintLable,5000,1000);
                break;

        }
    }

    /**
     * 参数1 0单门磁 1 井盖
     * 参数2 设备编号
     * 参数3 IMEI
     * 参数4 DX 电信 YD 移动 LT 联通
     * 参数5 时间 2018-11-22
     * 参数6 二维码的内容
     */
    public boolean getValue() {
        boolean printState=false;
        //Log.e(Common.Log, "按钮选择：" + Selected);//获取点击了门磁还是井盖
        String pcbaString = mPcba_lable.getText().toString();
        Log.e(Common.Log, "pcba标签内容：" + pcbaString);
        String deviceString = mDevice_id.getText().toString();
        Log.e(Common.Log, "设备编号：" + deviceString);
        String imeiString =mImei_number.getText().toString();
        Log.e(Common.Log, "IMEI编号：" + imeiString);
        Log.e(Common.Log, "运营商标识：" + MB);
        long sysTime = System.currentTimeMillis();//获取系统时间
        CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd", sysTime);//时间显示格式
        Log.e(Common.Log, "打印时间：" + sysTimeStr.toString());
        //fragment使用Mainactivity里的方法
        String BleMac=mBleMac.getText().toString();
        Log.e(Common.Log,"蓝牙地址:"+BleMac);
        if (mBleMac==null|| mBleMac.equals("")){
            ToastUtil.showShortToast("蓝牙地址不能为空");
        }else if (deviceString==null||deviceString.equals("")){
            ToastUtil.showShortToast("设备编号不能为空");
        }else if (imeiString==null||imeiString.equals("")){
            ToastUtil.showShortToast("IMEI号不能为空");
        }else {
            printState=mainActivity.printProductLab(deviceString,BleMac, imeiString, MB, sysTimeStr.toString());
            return printState;
        }
        return printState;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.edit_device_id:
                //ToastUtil.showShortToast("点击了设备编号");
                ClickEdit=Common.shell_device;
                break;
            case R.id.edit_imei_number:
                //ToastUtil.showShortToast("点击了IMEI号");
                ClickEdit=Common.shell_imei;
                break;
        }
        return false;
    }

    @Override
    public void hasPrinter(boolean printer) {
        if (printer==true){
            mPrintLable.setEnabled(false);
            mPrintLable.setBackgroundResource(R.drawable.validate_code_normal_bg);
        }else {
            mPrintLable.setEnabled(true);
            mPrintLable.setBackgroundResource(R.drawable.validate_code_press_bg);
        }
        Common.isPrinter=printer;
    }

    @Override
    public void hasQrScan(boolean qrscan) {
        if (qrscan==true){

        }

    }

    @Override
    public void hasBarScan(boolean barscan) {

    }

    @Override
    public void hasSerialPort(boolean serial) {

    }

    @Override
    public void hasUSB(boolean usb) {
        Common.isUSB=usb;
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER){
            Log.e(Common.Log,"id:"+view.getId());
            return true;
        }
        return false;
    }


    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    //在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();//获取系统时间
                    CharSequence sysTimeStr = DateFormat.format("yyyy/MM/dd hh:mm:ss", sysTime);//时间显示格式
                    mData_now.setText(sysTimeStr); //更新时间
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shelllable, container, false);
        mContext=this.getActivity();
        initView();
        new TimeThread().start(); //实时显示当前日期
        return view;
    }

    private void initView() {
        mRg_type = view.findViewById(R.id.rg_type);
        mDoor_magnet = view.findViewById(R.id.radio_door_magnet);
        mManhole_cover = view.findViewById(R.id.radio_manhole_cover);
        mPcba_lable = view.findViewById(R.id.edit_pcba_lable);
        mDevice_id = view.findViewById(R.id.edit_device_id);
        mImei_number = view.findViewById(R.id.edit_imei_number);
        mSimNum=view.findViewById(R.id.edit_simNum);
        mCarrier_identification = view.findViewById(R.id.edit_carrier_identification);
        mData_now = view.findViewById(R.id.edit_data_now);
        mPrintLable = view.findViewById(R.id.button_shell_printLable);
        mBleMac=view.findViewById(R.id.edit_bleMac);
        mPrintLable.setOnClickListener(this);
        mCarrier_identification.setText(MB);
        //禁用打印标签
//        if (Common.isPrinter==true){
//            mPrintLable.setEnabled(false);
//            mPrintLable.setBackgroundResource(R.drawable.validate_code_normal_bg);
//        }else {
//            mPrintLable.setEnabled(true);
//            mPrintLable.setBackgroundResource(R.drawable.validate_code_press_bg);
//        }
        mCarrier_identification.setOnClickListener(this);

        mDevice_id.setOnTouchListener(this);
        mImei_number.setOnTouchListener(this);
        mDevice_id.requestFocus();//请求光标
        //设置回车键监听
        mBleMac.setOnKeyListener(this);
        mData_now.setOnKeyListener(this);
        mDevice_id.setOnKeyListener(this);
        mImei_number.setOnKeyListener(this);
        mCarrier_identification.setOnKeyListener(this);

    }
    public void test(){
        //测试类 重新测试
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
        mainActivity.setFragmentKeyEventListener(this); //设置监听
        mainActivity.setFragmentDeviceEventListener(this);//设置设备监听
    }

    @Override
    public String onFragmentKeyEvent(String barcode) {
        ToastUtil.showShortToast("扫描到的条形码" + barcode);
        //mBarcode = barcode;
        mBleMac.setText("");
        mDevice_id.setText("");
        mImei_number.setText("");
        if (barcode != null) {
            //二维码返回的值\00002606000003;00A05006290E;864814043603199;DX
            if (barcode.contains("\\000026")||barcode.contains("000026")){
                mBarcode=barcode.replace("000026","");
                Log.e(Common.Log,"解析后的值:"+mBarcode);
                //解析二维码返回的内容
                String[] split = mBarcode.split(";");
                //1 设备编号 2 蓝牙mac地址 3 IMEI 4 运营商
                String devid=split[0];// 设备编号
                String bleMac=split[1];//蓝牙mac地址
                String imei=split[2];//IMEI
                String operator=split[3];//运营商
                String newBle=Util.addCrossbar(bleMac);
                mBleMac.setText(newBle);
                mDevice_id.setText(devid);
                mImei_number.setText(imei);
            }
        }
        return barcode;
    }

}
