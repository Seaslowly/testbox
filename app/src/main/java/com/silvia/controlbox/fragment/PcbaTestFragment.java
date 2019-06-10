package com.silvia.controlbox.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.QMUIFloatLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.silvia.controlbox.MainActivity;
import com.silvia.controlbox.R;
import com.silvia.controlbox.utils.Common;
import com.silvia.controlbox.utils.CountDownUtils;
import com.silvia.controlbox.utils.FragmentDeviceEventListener;
import com.silvia.controlbox.utils.FragmentKeyEventListener;
import com.silvia.controlbox.utils.FragmentStringEventListener;
import com.silvia.controlbox.utils.SpWrapper;
import com.silvia.controlbox.utils.ToastUtil;

/**
 * @file PcbaTestFragment
 * pcba测试页面
 * Created by Silvia_cooper on 2018/12/11.
 */
public class PcbaTestFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, FragmentKeyEventListener,FragmentStringEventListener, FragmentDeviceEventListener {
    private QMUIFloatLayout mFloatLayout;
    private View view;
    private EditText mSerial_port;
    private EditText mBaud_rate;
    private EditText mDevice_number;
    private EditText mImei;
    private TextView mTvReceive;
    private Button mButton_startTest;
    private Button mButton_printLable;
    private Button mButton_clear;
    private Button mButton_testADC;
    private Button mButton_testMENCI;
    private RadioGroup mRg_communication;
    private RadioButton mRadio_chinanet;
    private RadioButton mRadio_unicom;
    private RadioButton mRadio_mobile;
    private LinearLayout L_baud_rate;
    private Context mContext;
    MainActivity mainActivity;
    private String mBarcode;
    String MB = "DX";//多功能控制盒只有电信
    private String ClickEdit = null;
    private String  imeiString;
    private String devString;
    private String nidString;
    private String bleMacString;
    private CountDownUtils mCountDownUtils;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pcbatest, container, false);
        mContext = this.getActivity();
        initView();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        mSerial_port = view.findViewById(R.id.edit_serial_port);
        mBaud_rate = view.findViewById(R.id.edit_baud_rate);
        mDevice_number = view.findViewById(R.id.edit_device_number);
        mImei = view.findViewById(R.id.edit_imei);
        mButton_startTest = view.findViewById(R.id.button_startTest);
        mButton_printLable = view.findViewById(R.id.button_printLable);
        mButton_clear = view.findViewById(R.id.button_clear);
        mButton_testADC=view.findViewById(R.id.button_testADC);
        mButton_testMENCI=view.findViewById(R.id.button_testMENCI);
        mRg_communication = view.findViewById(R.id.rg_communication);
        mRadio_chinanet = view.findViewById(R.id.radio_chinanet);
        mRadio_unicom = view.findViewById(R.id.radio_unicom);
        mRadio_mobile = view.findViewById(R.id.radio_mobile);
        L_baud_rate = view.findViewById(R.id.L_baud_rate);
        mTvReceive = view.findViewById(R.id.et_send);
        //禁用按钮
        //mButton_startTest.setEnabled(false);
        //mButton_startTest.setBackgroundResource(R.drawable.validate_code_press_bg);
        mButton_testMENCI.setEnabled(false);
        mButton_testMENCI.setBackgroundResource(R.drawable.validate_code_press_bg);
        mButton_printLable.setEnabled(false);
        mButton_printLable.setBackgroundResource(R.drawable.validate_code_press_bg);

        mBaud_rate.setShowSoftInputOnFocus(false);
        mButton_startTest.setOnClickListener(this);
        mButton_printLable.setOnClickListener(this);
        mButton_clear.setOnClickListener(this);
        mButton_testADC.setOnClickListener(this);
        mButton_testMENCI.setOnClickListener(this);
        L_baud_rate.setOnClickListener(this);
        mBaud_rate.setOnTouchListener(this);
        //mDevice_number.setOnTouchListener(this);
        mImei.setOnTouchListener(this);
        //假如点击删除键,则清除所有值
        mDevice_number.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_DEL) {
                    mDevice_number.setText("");
                }
                return false;
            }
        });
        //初始化定时器
        mCountDownUtils=new CountDownUtils(mButton_startTest,8000,1000);
        mRg_communication.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //int Selected=mRg_communication.getCheckedRadioButtonId();
                switch (i) {
                    case R.id.radio_chinanet:
                        MB = "DX";
                        ToastUtil.showShortToast("点击了电信");
                        break;
                    case R.id.radio_unicom:
                        MB = "LT";
                        ToastUtil.showShortToast("点击了联通");
                        break;
                    case R.id.radio_mobile:
                        MB = "YD";
                        ToastUtil.showShortToast("点击了移动");
                        break;
                }
            }
        });
    }

    /**
     * 参数1 设备编号
     * 参数2 IMEI号
     * 参数3 运营商缩写
     */
    public void getValue() {
        //Log.e(Common.Log, "按钮选择：" + Selected);//获取点击了门磁还是井盖
        String DevString = mDevice_number.getText().toString();
        String imeiString=SpWrapper.getIMEI(mContext,null);
        String bleMac=SpWrapper.getBleMac(mContext,null);
        String nid=SpWrapper.getNid(mContext,null);
        Log.e(Common.Log, "设备编号：" + DevString);
        Log.e(Common.Log, "IMEI号：" + imeiString);
        Log.e(Common.Log, "运营商缩写：" + MB);
        Log.e(Common.Log,"NID:"+nid);
        if (DevString == null || DevString.equals("")) {
            ToastUtil.showShortToast(getString(R.string.toast_device_num));
        }else if (imeiString==null||imeiString.equals("")){
            ToastUtil.showShortToast("IMEI号不能为空");
        }else if (bleMac==null ||bleMac.equals("")){
            ToastUtil.showShortToast("蓝牙MAC地址不能为空");
        } else {
            //判断打印机是否存在
            Log.e(Common.Log,"打印机是否存在"+Common.isPrinter);
            mainActivity.printPcbaLab(DevString, imeiString, MB,bleMac);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_startTest:
                mTvReceive.setText("");
                String devid= mDevice_number.getText().toString();
                if (devid.length()<8){
                    ToastUtil.showShortToast("设备长度不正确");
                }else {
                    //点击测试的时候 清除页面的值
                    mTvReceive.setText("");//清空日志
                    mainActivity.startTest(devid);
                }
//                Intent intent =new Intent(this.getActivity(),UsbSendActivity.class);
//                startActivity(intent);
                break;
            case R.id.button_printLable:
                getValue();
                break;
            case R.id.button_clear:
               mTvReceive.setText("");
                //Beta.checkUpgrade();//进入首页检查更新
                //Log.e(Common.Log,"执行TestJavaCrash");
                //mainActivity.testJavaCrash();
                break;
            case R.id.button_testADC:
                mTvReceive.setText("");
                //mainActivity.testADC();
                mainActivity.sendQueryADC();
                break;
            case R.id.button_testMENCI:
                mTvReceive.setText("");
                mainActivity.testMenciDialog();
                break;
            default:
                break;
        }
    }


    private void showBottomSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(getContext())
                .addItem(Common.BAUD_RATE_4800 + "")
                .addItem(Common.BAUD_RATE_9600 + "")
                .addItem(Common.BAUD_RATE_14400 + "")
                .addItem(Common.BAUD_RATE_19200 + "")
                .addItem(Common.BAUD_RATE_38400+"")
                .addItem(Common.BAUD_RATE_56000+"")
                .addItem(Common.BAUD_RATE_115200 + "")
                .addItem(Common.BAUD_RATE_921600 + "")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        switch (position) {
                            default:
                                Log.e(Common.Log, "点击了波特率");
                                mBaud_rate.setText(tag);
                                dialog.dismiss();
                                break;
                        }

                    }
                })
                .build().show();
    }

    //判断点击了哪个edit
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            return true;
        }
        switch (view.getId()) {
            case R.id.edit_device_number:
                ClickEdit = Common.pcba_device;
                break;
            case R.id.edit_imei:
                ClickEdit = Common.pcba_imei;
                break;
            case R.id.edit_baud_rate:
                showBottomSheet();
                break;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity= (MainActivity) getActivity();
        mainActivity.setFragmentStringEventListener(this);//串口监听
        mainActivity.setFragmentKeyEventListener(this); //扫码枪监听设置监听
        mainActivity.setFragmentDeviceEventListener(this);//设备监听
    }

    @Override
    public String onFragmentStringEvent(String text) {
        if (text != null || !text.equals("")) {
            String data = mTvReceive.getText().toString();
            mTvReceive.setText(data + text);
            if (text.contains("finish")){
                //一个板子参数获取正确 启动倒计时
                mCountDownUtils.start();
            }
        }
        return text;
    }

    @Override
    public String GetDeviceID(String Dev) {
        if (Dev != null || Dev.equals("")) {
//            mDevice_number.setText("");
//            mDevice_number.setText(Dev);
            devString=Dev;
            SpWrapper.setDev(mContext,devString);

        }
        return Dev;
    }

    @Override
    public String GetIMEI(String IMEI) {
        if (IMEI != null || IMEI.equals("")) {
//            mImei.setText("");
//            mImei.setText(IMEI);
            imeiString=IMEI;
            String s = IMEI.replaceAll("\r\n", "");
            SpWrapper.setIMEI(mContext,s);
        }
        return IMEI;
    }

    @Override
    public String GETNID(String NID) {
        if (NID != null || NID.equals("")) {
            //mImei.setText("");
            //mImei.setText(NID);
            nidString=NID;
            String s = NID.replaceAll("\r|\n", "");
            SpWrapper.setNid(mContext,s);
            Log.e(Common.Log,"设备内卡号："+s);
        }
        return NID;
    }

    @Override
    public String GETBLEMAC(String BLEMAC) {
        if (BLEMAC != null || BLEMAC.equals("")) {
            //mImei.setText("");
            //mImei.setText(NID);
            bleMacString=BLEMAC;
            String s = BLEMAC.replaceAll("\r\n", "");
            SpWrapper.setBleMac(mContext,s);
            Log.e(Common.Log,"蓝牙地址："+s);
        }
        return BLEMAC;
    }

    @Override
    public void getAdcResult(boolean adc) {
        if (adc==true){
            mButton_testMENCI.setEnabled(true);
            mButton_testMENCI.setBackgroundResource(R.drawable.validate_code_normal_bg);
        }else {
            mButton_testMENCI.setEnabled(false);
            mButton_testMENCI.setBackgroundResource(R.drawable.validate_code_press_bg);
        }
    }

    @Override
    public void getMenciResult(boolean menci) {
        if (menci==true){
            mButton_startTest.setEnabled(true);
            mButton_startTest.setBackgroundResource(R.drawable.validate_code_normal_bg);
        }else {
            mButton_startTest.setEnabled(false);
            mButton_startTest.setBackgroundResource(R.drawable.validate_code_press_bg);
        }
    }

    @Override
    public void getProductResult(boolean product) {
        if (product==true){
            mButton_printLable.setEnabled(true);
            mButton_printLable.setBackgroundResource(R.drawable.validate_code_normal_bg);
        }else {
            mButton_printLable.setEnabled(false);
            mButton_printLable.setBackgroundResource(R.drawable.validate_code_press_bg);
        }
    }

    @Override
    public void getPrinterResult(boolean printer) {
        if (printer==true){
            //如果打印成功 则禁用三个按钮
            mButton_printLable.setEnabled(false);
            mButton_printLable.setBackgroundResource(R.drawable.validate_code_press_bg);
            mButton_startTest.setEnabled(false);
            mButton_startTest.setBackgroundResource(R.drawable.validate_code_press_bg);
            mButton_testMENCI.setEnabled(false);
            mButton_testMENCI.setBackgroundResource(R.drawable.validate_code_press_bg);
            mDevice_number.setText("");//清空imei号
        }else{

        }
    }

    /**
     * 二维码返回的信息
     * @param barcode
     * @return
     */
    @Override
    public String onFragmentKeyEvent(String barcode) {
        mDevice_number.setText("");
        ToastUtil.showShortToast("扫描到的条形码" + barcode);
        mBarcode = barcode;
        if (barcode != null) {
            mDevice_number.setText(mBarcode);
        }
        return barcode;
    }


    @Override
    public void hasPrinter(boolean printer) {
//        if (printer==true){
//            mButton_printLable.setEnabled(true);
//            mButton_printLable.setBackgroundResource(R.drawable.validate_code_normal_bg);
//        }else {
//            mButton_printLable.setEnabled(false);
//            mButton_printLable.setBackgroundResource(R.drawable.validate_code_press_bg);
//        }
        Common.isPrinter=printer;
    }

    @Override
    public void hasQrScan(boolean qrscan) {

    }

    @Override
    public void hasBarScan(boolean barscan) {

    }

    @Override
    public void hasSerialPort(boolean serial) {
        if (serial==true){
            mButton_testADC.setEnabled(true);
            mButton_testADC.setBackgroundResource(R.drawable.validate_code_normal_bg);
        }else {
            mButton_testADC.setEnabled(false);
            mButton_testADC.setBackgroundResource(R.drawable.validate_code_press_bg);
        }
        Common.isSerial=serial;
    }
    @Override
    public void hasUSB(boolean usb) {
        Common.isUSB=usb;
    }
}
