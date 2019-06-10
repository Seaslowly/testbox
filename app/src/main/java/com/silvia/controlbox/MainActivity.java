package com.silvia.controlbox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.tscdll.TSCUSBActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.silvia.controlbox.fragment.ConfigParamFragment;
import com.silvia.controlbox.fragment.PcbaTestFragment;
import com.silvia.controlbox.fragment.ShellLableFragment;
import com.silvia.controlbox.ui.BottomBar;
import com.silvia.controlbox.utils.AppUtils;
import com.silvia.controlbox.utils.BitmapUtil;
import com.silvia.controlbox.utils.Common;
import com.silvia.controlbox.utils.DialogListenner;
import com.silvia.controlbox.utils.FragmentDeviceEventListener;
import com.silvia.controlbox.utils.FragmentKeyEventListener;
import com.silvia.controlbox.utils.FragmentStringEventListener;
import com.silvia.controlbox.utils.MyDialog;
import com.silvia.controlbox.utils.ScanGunKeyEventHelper;
import com.silvia.controlbox.utils.SpWrapper;
import com.silvia.controlbox.utils.StringBitmapParameter;
import com.silvia.controlbox.utils.ToastUtil;
import com.silvia.controlbox.utils.UsbService;
import com.silvia.controlbox.utils.Util;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements ScanGunKeyEventHelper.OnScanSuccessListener {
    public static int imeiPos=0;
    public static Context mContext;
    private QMUITopBar mTopBar;
    private BottomBar mBottomBar;
    static TSCUSBActivity TscUSB = new TSCUSBActivity();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static UsbManager mUsbManager;
    private static PendingIntent mPermissionIntent;
    public static boolean hasPermissionToCommunicate = false;
    private static UsbDevice device;
    private static int printNum = 0;
    FragmentKeyEventListener fragmentKeyeventListener;
    static FragmentStringEventListener fragmentStringeventListener;
    static FragmentDeviceEventListener fragmentDeviceEventListener;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.MOUNT_UNMOUNT_FILESYSTEMS"
    };
    private MyHandler mHandler;
    static Thread mThread;
    public static UsbService usbService;
    public static int pos = 0;
    public static int MenciPos = 0;
    static String dev;
    static String imei;
    static String nid;
    static String bleMac;
    static MyDialog myDialog;

    IntentFilter filterAttached_and_Detached = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
    // 接收广播
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UsbService.ACTION_NO_USB)) {
                //如果设备没有插入USB 则禁用按钮
                ToastUtil.showShortToast("请插入USB设备");
                fragmentDeviceEventListener.hasBarScan(false);
                fragmentDeviceEventListener.hasQrScan(false);
                fragmentDeviceEventListener.hasPrinter(false);
                fragmentDeviceEventListener.hasUSB(false);
                fragmentDeviceEventListener.hasSerialPort(false);
            } else if (action.equals(UsbService.ACTION_PLUGIN_BAR_SCAN)) {
                ToastUtil.showShortToast("条形码扫码枪接入");
                fragmentDeviceEventListener.hasBarScan(true);
            } else if (action.equals(UsbService.ACTION_PLUGIN_QR_SCAN)) {
                ToastUtil.showShortToast("二维码扫码枪接入");
                fragmentDeviceEventListener.hasQrScan(true);
            } else if (action.equals(UsbService.ACTION_PLUGIN_PRINTER)) {
                ToastUtil.showShortToast("打印机接入");
                fragmentDeviceEventListener.hasPrinter(true);
            } else if (action.equals(UsbService.ACTION_PLUGIN_USB)) {
                ToastUtil.showShortToast("U盘接入");
                fragmentDeviceEventListener.hasUSB(true);
            } else if (action.equals(UsbService.ACTION_PLUGIN_SERIAL)) {
                ToastUtil.showShortToast("串口接入");
                fragmentDeviceEventListener.hasSerialPort(true);
            } else if (action.equals(UsbService.ACTION_USB_DISCONNECTED)) {
                ToastUtil.showShortToast("拔出usb设备");
                //拔出设备
                device = intent.getParcelableExtra(mUsbManager.EXTRA_DEVICE);
                if (device != null) {
                    Log.e(Common.Log, "设备的ProductId值为：" + device.getProductId());
                    Log.e(Common.Log, "设备的VendorId值为：" + device.getVendorId());
                }
            } else if (ACTION_USB_PERMISSION.equals(action)) {
                //申请USB权限
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            hasPermissionToCommunicate = true;
                        }
                    }
                }
            }
        }
    };
    //创建一个Handler
    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //String a = mTvReceive.getText().toString();
            String data = (String) msg.obj;
            Log.e("data", data);
            switch (msg.what) {
                case Common.SetDevID:
                    pos = Common.SetDevID;
                    break;
                case Common.WriteIp:
                    //ToastUtil.showShortToast((String) msg.obj);
                    String ip = SpWrapper.getHost(mContext, null);
                    if (ip != null) {
                        Util.setIP(usbService, ip);
                        fragmentStringeventListener.onFragmentStringEvent(data);
                        pos = Common.WriteIp;
                    } else {
                        ToastUtil.showShortToast("IP值为空");
                    }
                    break;
                case Common.QueryIp:
                    pos = Common.QueryIp;
                    Util.queryIp(usbService);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    break;
                case Common.WritePort:
                    int port = SpWrapper.getHostPort(mContext, -1);
                    Util.setPort(usbService, port);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    pos = Common.WritePort;
                    break;
                case Common.QueryPort:
                    pos = Common.QueryPort;
                    Util.queryPort(usbService);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    break;
                case Common.WriteWorkT:
                    int workt = SpWrapper.getDeviceWorkTime(mContext, -1);
                    Util.setWorkTime(usbService, workt);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    pos = Common.WriteWorkT;
                    break;
                case Common.WriteCollT:
                    int collt = SpWrapper.getDeviceTimingAcquisition(mContext, -1);
                    Util.setCollT(usbService, collt);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    pos = Common.WriteCollT;
                    break;
                case Common.WriteReportT:
                    int report = SpWrapper.getDeviceTimingReport(mContext, -1);
                    Util.setUP_T(usbService, report);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    pos = Common.WriteReportT;
                    break;
                case Common.WriteInstall:
                    int install = SpWrapper.getDeviceInstallMethod(mContext, -1);
                    Util.setInstall(usbService, install);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    pos = Common.WriteInstall;
                    break;
                case Common.GetDevID:
                    Util.queryDevId(usbService);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    pos = Common.GetDevID;
                    break;
                case Common.GetImei:
                    pos = Common.GetImei;
                    if (imeiPos<3){
                        Util.queryIMEI(usbService);
                        imeiPos++;
                        fragmentStringeventListener.onFragmentStringEvent(data);
                    }else{
                        pos =0;
                        imeiPos=0;//弹出错误
                        ProductResult(false);//弹出结果错误
                        fragmentStringeventListener.onFragmentStringEvent(data);
                    }
                    break;
                case Common.GetNID:
                    Util.queryInnerCard(usbService);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    pos = Common.GetNID;
                    break;
                case Common.GetBleMac:
                    Util.queryMAC(usbService);//获取NID成功
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    pos = Common.GetBleMac;
                    break;
                case Common.DEVICE_ID:
                    Log.e(Common.Log, data);
                    fragmentStringeventListener.onFragmentStringEvent(data);
                    pos = Common.DEVICE_ID;
                    break;
                default:
                    break;
            }
        }

    };

    //动态获取内存存储权限
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
            // Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QMUIStatusBarHelper.translucent(this);// 沉浸式状态栏
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        mContext = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        usbList();
        verifyStoragePermissions(this);
        initView();
        initBottomBar();
    }


    /**
     * usb列表
     */
    public void usbList() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        UsbAccessory[] accessoryList = mUsbManager.getAccessoryList();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Log.d("Detect ", deviceList.size() + " USB device(s) found");
        if (deviceList.size() == 0) {
            //判断设备不存在 禁用按钮可点击 不然点击会闪退
        } else {
            Log.e(Common.Log, deviceList.size() + "");
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                device = deviceIterator.next();
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                if (device.getVendorId() == 4611 && device.getProductId() == 370) {
                    Log.e(Common.Log, device.toString());
                } else if (device.getVendorId() == 7851 && device.getProductId() == 32771) {
                    Log.e(Common.Log, device.toString());
                } else if (device.getVendorId() == 7851 && device.getProductId() == 33539) {
                    Log.e(Common.Log, "二维码扫码枪");
                } else if (deviceVID != 0x1d6b && (devicePID != 0x0001 && devicePID != 0x0002 && devicePID != 0x0003)) {
                    Log.e(Common.Log, device.toString());
                } else if (device.getInterface(0).getInterfaceClass() == 8) {
                    Log.e(Common.Log, "U盘插入");
                }
            }
        }
    }

    /**
     * 寻找打印机
     */
    public static void findPrinter() {
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Log.d("Detect ", deviceList.size() + " USB device(s) found");
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            device = deviceIterator.next();
            if (device.getVendorId() == 4611) {
                //Toast.makeText(MainActivity.this, device.toString(), 0).show();
                Log.e(Common.Log, "找到打印机");
                ToastUtil.showShortToast("申请打印机权限成功");
                break;
            }
        }
        requestUserPermission();
    }

    public static void startTest(final String devid) {
        //设置默认值为空
        SpWrapper.setBleMac(mContext, null);
        SpWrapper.setDev(mContext, null);
        SpWrapper.setIMEI(mContext, null);
        SpWrapper.setNid(mContext, null);

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(Common.Log, "页面设备号:" + devid);
                Util.setDevid(usbService, devid);
                Message message = new Message();
                message.what = Common.SetDevID;
                String msg1 = "发送设置设备编号成功\n";
                message.obj = msg1;
                handler.sendMessage(message);
            }
        });
        mThread.start();
    }

    private void initView() {
        //Beta.checkUpgrade();
        ToastUtil.showShortToast("更新后的版本:"+AppUtils.getVersionName(mContext));
        mTopBar = findViewById(R.id.topBar);
        mTopBar.setTitle("多功能控制盒");
        mTopBar.addRightTextButton(R.string.right_button, R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先判断U盘是否存在
                usbService.redUDiskDevsList(mContext);
            }
        });
        mBottomBar = findViewById(R.id.bottom_bar);
        mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
        mHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //返回的信息
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    try {
                        byte[] arg0 = (byte[]) msg.obj;
                        final String str = new String(arg0, "UTF-8");
                        if (str.contains("AT+ADC=")) {
                            String param = Util.getEqual(str);
                            TestADCdialog(param);
                        }
                        if (str.contains("AT+MenCi=")) {
                            if (MenciPos == 0) {
                                //五个参数为01
                                String param = Util.getEqual(str);
                                if (param.length() >= 14) {
                                    TestMenciDialog(param);
                                } else {
                                    ToastUtil.showShortToast("返回测试开关量参数不正确");
                                }
                            } else {
                                //五个参数为00
                                String param = Util.getEqual(str);
                                if (param.length() >= 14) {
                                    TestMenciSuccess(param);
                                } else {
                                    ToastUtil.showShortToast("返回测试开关量参数不正确");
                                }

                            }

                        }
                        if (str.contains("Weak_up") || str.contains("CERE") || str.contains("CFU") || str.contains("rejoin") || str.contains("Give")) {
                            //都是板子传过来乱七八糟的值
                        } else {
                            Log.e(Common.Log, "pos:" + pos);
                            Log.e("test", "pos:" + pos + "str:" + str);
                            if (pos == Common.SetDevID && str.contains("OK")) {
                                if (str.contains("OK")) {
                                    String msg1 = "发送设置服务器ip成功\n";
                                    MsgSend(Common.WriteIp, msg1);
                                } else {
                                    String msg1 = "发送设备ID\n";
                                    MsgSend(Common.SetDevID, msg1);
                                }
                            } else if (pos == Common.WriteIp) {
                                if (str.contains("OK")) {
                                    String msg1 = "设置服务器ip端口成功\n";
                                    MsgSend(Common.QueryIp, msg1);
                                } else {
                                    String msg1 = "设置服务器ip\n";
                                    MsgSend(Common.WriteIp, msg1);
                                }
                            }else if (pos == Common.QueryIp) {
                                //如果获取到的ip相同 则继续往下发送指令
                                String getIp = Util.getEqual(str);
                                String localIp = SpWrapper.getHost(mContext, null);
                                Log.e(Common.Log, "IP:" + getIp);
                                getIp.replace("\r|\n", "");
                                if (getIp.contains(localIp)) {
                                    //如果获取到的ip相同 则继续往下发送指令
                                    String msg1 = "查询设备端口号成功\n";
                                    MsgSend(Common.WritePort, msg1);
                                } else {
                                    //如若不同则需要重新发送设备ip指令
                                    String msg1 = "获取设备IP错误,重新发送指令\n";
                                    MsgSend(Common.WriteIp, msg1);
                                }
                            }
                            else if (pos == Common.WritePort) {
                                if (str.contains("OK")) {
                                    String msg1 = "查询设备端口号成功\n";
                                    MsgSend(Common.QueryPort, msg1);
                                } else {
                                    String msg1 = "设置设备端口号\n";
                                    MsgSend(Common.WritePort, msg1);
                                }
                            }  else if (pos == Common.QueryPort) {
                                int localPort = SpWrapper.getHostPort(mContext, -1);
                                String Port = Util.getNumber(str);
                                Port.replaceAll(" ", "");
                                Log.e(Common.Log,"PORT:"+Port);
                                try{
                                    int getPort = Integer.parseInt(Port);
                                    if (localPort == getPort) {
                                        String msg1 = "查询工作时间成功\n";
                                        MsgSend(Common.WriteWorkT, msg1);
                                    } else {
                                        String msg1 = "查询设备端口号错误，重新发送指令\n";
                                        MsgSend(Common.WritePort, msg1);
                                    }
                                }catch (NumberFormatException e){
                                    Log.e(Common.Log,e.getMessage());
                                }

                            }
                            else if (pos == Common.WriteWorkT) {
                                if (str.contains("OK")) {
                                    String msg1 = "设置设备定时采集时间成功\n";
                                    MsgSend(Common.WriteCollT, msg1);
                                } else {
                                    String msg1 = "设置工作时间\n";
                                    MsgSend(Common.WriteWorkT, msg1);
                                }
                            } else if (pos == Common.WriteCollT) {
                                if (str.contains("OK")) {
                                    String msg1 = "设置设备定时上报时间成功\n";
                                    MsgSend(Common.WriteReportT, msg1);
                                } else {
                                    String msg1 = "设置定时采集时间\n";
                                    MsgSend(Common.WriteCollT, msg1);
                                }
                            } else if (pos == Common.WriteReportT) {
                                if (str.contains("OK")) {
                                    String msg1 = "设置设备安装方式成功\n";
                                    MsgSend(Common.WriteInstall, msg1);
                                } else {
                                    String msg1 = "设置设备定时上报时间\n";
                                    MsgSend(Common.WriteReportT, msg1);
                                }

                            } else if (pos == Common.WriteInstall) {
                                if (str.contains("OK")) {
                                    String msg1 = "设置设备安装方式成功\n";
                                    MsgSend(Common.GetDevID, msg1);
                                } else {
                                    String msg1 = "获取设置安装方向\n";
                                    MsgSend(Common.WriteInstall, msg1);
                                }
                            } else if (pos == Common.GetDevID) {
                                if (str.contains("DEV_ID=")) {
                                    dev = Util.getNumber(str);
                                    Log.e(Common.Log, "设备编号:" + dev);
                                    String msg1 = "获取设备编号成功\n";
                                    MsgSend(Common.GetImei, msg1);
                                } else {
                                    String msg1 = "获取设备编号\n";
                                    MsgSend(Common.GetDevID, msg1);
                                }
                            } else if (pos == Common.GetImei) {
                                if (str.contains("+CGSN")) {
                                    imei = Util.getNumber(str);
                                    if (imei.length() < 15) {
                                        //参数不正确需要重发
                                        MsgSend(Common.GetImei, "获取IMEI号\n");
                                    } else {
                                        Log.e(Common.Log, "IMEI:" + imei);
                                        String msg1 = "获取设备内卡号号成功\n";
                                        MsgSend(Common.GetNID, msg1);
                                    }
                                } else {
                                    //等待5秒 imei号不返回则重发
                                    MsgSend(Common.GetImei, "获取IMEI号\n");
                                }
                            } else if (pos == Common.GetNID) {
                                if (str.contains("AT+NID=")) {
                                    //AT+NID=ERROR
                                    if (str.contains("ERROR")) {
                                        //返回错误代表SIM卡号不存在
                                        pos=0;//错误线程结束
                                        ProductResult(false);
                                    } else {
                                        nid = Util.getEqual(str);
                                        if (nid.length() < 19) {
                                            //如果NID的参数小于19 则需要重发参数
                                            String msg1 = "获取设备内卡号\n";
                                            MsgSend(Common.GetNID, msg1);
                                        } else {
                                            Log.e(Common.Log, "设备内卡号:" + nid);
                                            String msg1 = "获取NID成功\n";
                                            MsgSend(Common.GetBleMac, msg1);

                                        }
                                    }
                                } else {
                                    String msg1 = "获取设备内卡号\n";
                                    MsgSend(Common.GetNID, msg1);
                                }
                            } else if (pos == Common.GetBleMac) {
                                if (str.contains("AT+BLE_MAC=")) {
                                    bleMac = Util.getEqual(str);
                                    if (bleMac.length() < 19) {
                                        //重发蓝牙mac地址
                                        String msg1 = "获取蓝牙Mac";
                                        MsgSend(Common.GetBleMac, msg1);
                                    } else {
                                        Log.e(Common.Log, "蓝牙mac地址:" + bleMac);
                                        String msg1 = "finish";
                                        MsgSend(Common.DEVICE_ID, msg1);
                                    }

                                    //获取设备编号 nid 还有蓝牙地址 imei的值是否正确
                                    Log.e(Common.Log, dev.length() + ";" + nid.length() + ";" + bleMac.length() + ";" + imei.length() + ";");
                                    fragmentStringeventListener.GetDeviceID(dev);
                                    fragmentStringeventListener.GETNID(nid);
                                    fragmentStringeventListener.GETBLEMAC(bleMac);
                                    fragmentStringeventListener.GetIMEI(imei);
                                    //返回结束代表成功
                                    ProductResult(true);
                                    fragmentStringeventListener.getProductResult(true);
                                } else {
                                    String msg1 = "获取蓝牙Mac";
                                    MsgSend(Common.GetBleMac, msg1);
                                }
                            } else if (pos >= 12) {
                                pos = 0;
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        Log.e(Common.Log, "ERROR:" + e.getMessage());
                    }
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * 发送查询ADC命令
     */
    public static void sendQueryADC() {
        Util.testADC(usbService);
    }

    /**
     * 发送adc返回信息
     *
     * @param paramString
     */
    public static void TestADCdialog(String paramString) {
        if (paramString.length() >= 11) {
            boolean AdcResult = Util.getAdcParam(mContext, paramString);
            fragmentStringeventListener.getAdcResult(AdcResult);
        } else {
            ToastUtil.showShortToast("返回参数不正确");
        }
    }

    /**
     * 发送查询门磁返回参数
     *
     * @param paramString
     */
    public static void TestMenciDialog(String paramString) {
        String btnStr = "";
        MenciPos = 1;
        boolean param1 = false;
        boolean param2 = false;
        boolean param3 = false;
        boolean param4 = false;
        boolean param5 = false;
        //00-00-00-00-00
        String voltage = paramString.substring(0, 2);
        String tiltsensor = paramString.substring(3, 5);
        String temper = paramString.substring(6, 8);
        String humidity = paramString.substring(9, 11);
        String waterban = paramString.substring(12, 14);
        if (voltage.equals(Common.ON)) {
            param1 = true;
        }
        if (tiltsensor.equals(Common.ON)) {
            param2 = true;
        }
        if (temper.equals(Common.ON)) {
            param3 = true;
        }
        if (humidity.equals(Common.ON)) {
            param4 = true;
        }
        if (waterban.equals(Common.ON)) {
            param5 = true;
        }
        if (param1 == false || param2 == false || param3 == false || param4 == false || param5 == false) {
            fragmentStringeventListener.getMenciResult(false);
            btnStr = "测试错误";
            MenciPos = 0;
        }else {
            btnStr = "我已把开关拨到1";
        }
        String aString = "门磁01";
        String bString = "门磁02";
        String cString = "唤醒";
        String dString = "锁舌";
        String eString = "水禁";
        boolean isShow = true;
        //不比对每个值
        final String finalBtnStr = btnStr;
        myDialog = new MyDialog(mContext, new DialogListenner() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button_cancel:
                        break;
                    case R.id.button_next:
                        if (finalBtnStr.equals("测试错误")){
                            myDialog.dismiss();
                        }else {
                            Util.testMENCI(usbService);
                            myDialog.dismiss();
                        }
                        break;
                }
            }
        });
        myDialog.MenciDialog(isShow, btnStr, param1, aString, param2, bString, param3, cString, param4, dString, param5, eString);

    }

    /**
     * 测试门磁成功
     *
     * @param paramString
     */
    public static void TestMenciSuccess(String paramString) {
        MenciPos = 0;
        boolean param1 = false;
        boolean param2 = false;
        boolean param3 = false;
        boolean param4 = false;
        boolean param5 = false;
        //00-00-00-00-00
        String voltage = paramString.substring(0, 2);
        String tiltsensor = paramString.substring(3, 5);
        String temper = paramString.substring(6, 8);
        String humidity = paramString.substring(9, 11);
        String waterban = paramString.substring(12, 14);
        if (voltage.equals(Common.OFF)) {
            param1 = true;
        }
        if (tiltsensor.equals(Common.OFF)) {
            param2 = true;
        }
        if (temper.equals(Common.OFF)) {
            param3 = true;
        }
        if (humidity.equals(Common.OFF)) {
            param4 = true;
        }
        if (waterban.equals(Common.OFF)) {
            param5 = true;
        }

        String aString = "门磁01";
        String bString = "门磁02";
        String cString = "唤醒";
        String dString = "锁舌";
        String eString = "水禁";
        boolean isShow = true;
        String btnStr;
        if (param1 == false || param2 == false || param3 == false || param4 == false || param5 == false) {
            btnStr = "失败";
            fragmentStringeventListener.getMenciResult(false);
        } else {
            btnStr = "成功";
            fragmentStringeventListener.getMenciResult(true);
        }
        //不比对每个值
        myDialog = new MyDialog(mContext, new DialogListenner() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button_cancel:
                        break;
                    case R.id.button_next:
                        myDialog.dismiss();
                        break;
                }
            }
        });

        myDialog.MenciDialog(isShow, btnStr, param1, aString, param2, bString, param3, cString, param4, dString, param5, eString);

//        boolean isTrue = Util.getMenciParam(paramString);
//        if (isTrue == true) {
//            testMenci2SuccessDialog(paramString);//如果 按0 返回true 则测试开
//        } else {
//            testMenciErrorDialog();//如果门磁测试错误，则结束
//        }
    }

    /**
     * 点击触发传感器按钮
     */
    public static void testMenciErrorDialog() {
        new QMUIDialog.MessageDialogBuilder(mContext)
                .setTitle("触发传感器检测")
                .setMessage("门磁测试错误")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void customizeDialog(String result) {
        new QMUIDialog.MessageDialogBuilder(mContext)
                .setTitle("操作U盘")
                .setMessage("复制数据到U盘" + result)
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    /**
     * 点击触发传感器按钮
     */
    public static void testMenciDialog() {
        new QMUIDialog.MessageDialogBuilder(mContext)
                .setTitle("触发传感器检测")
                .setMessage("请先把开关拨到0")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("我已把开关拨到0", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //发送查询门磁参数
                        Util.testMENCI(usbService);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void ProductResult(boolean result) {
        String str;
        if (result == true) {
            str = "成功";
        } else {
            str = "失败";
        }
        new QMUIDialog.MessageDialogBuilder(mContext)
                .setTitle("写入生产信息结果")
                .setMessage("结果为" + str)
                .addAction("完成", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 接收值返回
     */
    public static void testMenci2SuccessDialog(String paramString) {
        new QMUIDialog.MessageDialogBuilder(mContext)
                .setTitle("触发传感器检测")
                .setMessage("门磁测试成功,\r\n" +
                        "参数:" + paramString + "\r\n")
                .addAction("完成", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 接收值返回
     */
    public static void testMenci2Dialog(String paramString) {
        new QMUIDialog.MessageDialogBuilder(mContext)
                .setTitle("触发传感器检测")
                .setMessage("门磁测试成功,\r\n" +
                        "参数:" + paramString + "\r\n" +
                        "现在请先把开关拨到1 \r\n")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("我已把开关拨到1", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        Util.testMENCI(usbService);
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 配置导航栏
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void initBottomBar() {
        mBottomBar.setContainer(R.id.fl_container)
                .setTitleBeforeAndAfterColor("#999999", "#008577")
                .addItemWithNoIcon(PcbaTestFragment.class,
                        getString(R.string.pcba_test))
                .addItemWithNoIcon(ShellLableFragment.class,
                        getString(R.string.shell_lable))
                .addItemWithNoIcon(ConfigParamFragment.class,
                        getString(R.string.config_param))
                .buildNoIcon();
    }

    /**
     * 封装Message
     *
     * @param target
     */
    public static void MsgSend(int target, String value) {
        Message message = new Message();
        message.what = target;
        String msg1 = value;
        message.obj = msg1;
        handler.sendMessage(message);
    }

    /**
     * 获取打印机状态
     */
    public void Printerstatus() {
        String status = TscUSB.printerstatus();
        switch (status) {
            case "00":
                ToastUtil.showShortToast(Common.PrinterStatus00);
                break;
            case "01":
                ToastUtil.showShortToast(Common.PrinterStatus01);
                break;
            case "02":
                ToastUtil.showShortToast(Common.PrinterStatus02);
                break;
            case "03":
                ToastUtil.showShortToast(Common.PrinterStatus03);
                break;
            case "04":
                ToastUtil.showShortToast(Common.PrinterStatus04);
                break;
            case "05":
                ToastUtil.showShortToast(Common.PrinterStatus05);
                break;
            case "08":
                ToastUtil.showShortToast(Common.PrinterStatus08);
                break;
            case "09":
                ToastUtil.showShortToast(Common.PrinterStatus09);
                break;
            case "0A":
                ToastUtil.showShortToast(Common.PrinterStatus0A);
                break;
            case "0B":
                ToastUtil.showShortToast(Common.PrinterStatus0B);
                break;
            case "0C":
                ToastUtil.showShortToast(Common.PrinterStatus0C);
                break;
            case "0D":
                ToastUtil.showShortToast(Common.PrinterStatus0D);
                break;
            case "10":
                ToastUtil.showShortToast(Common.PrinterStatus10);
                break;
            case "20":
                ToastUtil.showShortToast(Common.PrinterStatus20);
                break;
            case "80":
                ToastUtil.showShortToast(Common.PrinterStatus80);
                break;
            default:
                ToastUtil.showShortToast(Common.PrinterStatusError);
                break;
        }
    }

    /**
     * 打印标签
     *
     * @param Astring    产品名称
     * @param Bstring    产品型号
     * @param Cstring    联网方式
     * @param Dstring    设备编号
     * @param Estring    IMEI
     * @param Fstring    生产批次
     * @param Gstring    生产厂商
     * @param CodeString 二维码内容
     * @return
     */
    public static Bitmap createLable(String Astring, String Bstring, String Cstring, String Dstring, String Mstring, String Estring, String Fstring, String Gstring, String CodeString) {
        ArrayList<StringBitmapParameter> mParameters = new ArrayList<>();
        mParameters.add(new StringBitmapParameter(Astring, BitmapUtil.IS_LARGE));
        mParameters.add(new StringBitmapParameter(Bstring, BitmapUtil.IS_LARGE));
        mParameters.add(new StringBitmapParameter(Cstring, BitmapUtil.IS_LARGE));
        mParameters.add(new StringBitmapParameter(Dstring, BitmapUtil.IS_LARGE));
        mParameters.add(new StringBitmapParameter(Estring, BitmapUtil.IS_LARGE));
        mParameters.add(new StringBitmapParameter(Fstring, BitmapUtil.IS_LARGE));
        mParameters.add(new StringBitmapParameter(Mstring, BitmapUtil.IS_LARGE));
        mParameters.add(new StringBitmapParameter(Gstring, BitmapUtil.IS_LARGE));
        Bitmap codeBitmap = Create2dCode(CodeString, 130);
        Bitmap textBitmap = BitmapUtil.StringListtoBitmap(mContext, 400, mParameters);
        Bitmap mergeBitmap = BitmapUtil.addBitmapInRight(textBitmap, codeBitmap, 240, 45);
        return mergeBitmap;
    }

    private static void requestUserPermission() {
        //如果是打印机请求USB权限
        if (device != null) {
            mPermissionIntent = PendingIntent.getBroadcast(mContext, 0,
                    new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT);
            mUsbManager.requestPermission(device, mPermissionIntent);
        } else {
            ToastUtil.showShortToast("");
        }


    }

    /**
     * width 30 height 20
     *
     * @param Astring    设备编号
     * @param Bstring    IMEI号
     * @param CodeString 设备编号、IMEI号、运营商标识
     * @return
     */
    public static Bitmap createPcbaLable(String Astring, String Bstring, String Cstring, String CodeString) {
        ArrayList<StringBitmapParameter> mParameters = new ArrayList<>();
        mParameters.add(new StringBitmapParameter("设备编号:", BitmapUtil.IS_SMALL));
        mParameters.add(new StringBitmapParameter(Astring, BitmapUtil.IS_SMALL));
        mParameters.add(new StringBitmapParameter("蓝牙MAC:", BitmapUtil.IS_SMALL));
        String bleFront = Cstring.substring(0, 8);
        String bleBehind = Cstring.substring(8, 17);
        mParameters.add(new StringBitmapParameter(bleFront, BitmapUtil.IS_SMALL));
        mParameters.add(new StringBitmapParameter(bleBehind, BitmapUtil.IS_SMALL));
        mParameters.add(new StringBitmapParameter("IMEI:", BitmapUtil.IS_SMALL));
        mParameters.add(new StringBitmapParameter(Bstring, BitmapUtil.IS_SMALL));
        mParameters.add(new StringBitmapParameter("\n"));
        Bitmap codeBitmap = Create2dCode(CodeString, 110);
        Bitmap textBitmap = BitmapUtil.StringListtoBitmap(mContext, 200, mParameters);
        Bitmap mergeBitmap = BitmapUtil.addBitmapInRight(textBitmap, codeBitmap, 80, 20);

        return mergeBitmap;
    }


    /**
     * 设备编号、蓝牙MAC地址、IMEI号、运营商标识
     *
     * @param DevID
     * @param IMEI
     * @param MB
     * @param BLEMAC
     */
    public static void printPcbaLab(String DevID, String IMEI, String MB, String BLEMAC) {
        String codeString = DevID + ";" + BLEMAC + ";" + IMEI + ";" + MB;
        Bitmap mergeBitmap = createPcbaLable(DevID, IMEI, BLEMAC, codeString);
        String path = BitmapUtil.saveBitmap(mergeBitmap);//保存图片到本地
        if (path != null) {
            findPrinter();//查找打印机
            if (device.getVendorId() == 4611 && device.getProductId() == 370) {
                //由于有多个串口设备 需要判断
                String saveStr = DevID + ";" + BLEMAC + ";" + IMEI + ";" + MB + ";" + nid + ";";
                Util.saveStr(saveStr); //每打印一条则保存一条
                if (mUsbManager.hasPermission(device)) {
                    TscUSB.openport(mUsbManager, device);
                    Bitmap bit = BitmapFactory.decodeFile(path);
                    pcbaPrinter(bit, 24, 2);//生成测试打印标签
                    //执行打印成功后
                    fragmentStringeventListener.getPrinterResult(true);
                }
            }
        }
    }

    //打印产品标签二维码
    public static boolean printProductLab(String DevID, String BleMac, String IMEI, String MB, String DateTime) {
        {
            String strMBType = "";
            String strMBName = Common.strChinanet;

            String ProductName = "";
            if (MB == Common.Chinanet) {
                ProductName = Common.PRODUCT_CTRLBOX;
                strMBType = Common.CHINANET_MODEL;
            }
            String A = "产品名称：NB" + ProductName + strMBName;
            String B = "产品型号：" + strMBType;
            String C = "联网方式：NB-IoT";
            String D = "设备编号：" + DevID;
            String M = "蓝牙MAC:" + BleMac;
            String E = "IMEI：" + IMEI;
            String F = "生产批次：" + DateTime;
            String G = "生产厂商：上海思敦信息科技有限公司";
            String codeString = DevID + ";" + BleMac + ";" + IMEI + ";" + MB;
            Bitmap mergeBitmap = createLable(A, B, C, D, M, E, F, G, codeString);
            String path = BitmapUtil.saveBitmap2(mergeBitmap);//保存图片到本地
            if (path != null) {
                findPrinter();//查找打印机
                if (device.getVendorId() == 4611 && device.getProductId() == 370) {
                    //由于有多个串口设备 需要判断
                    if (mUsbManager.hasPermission(device)) {
                        TscUSB.openport(mUsbManager, device);
                        Bitmap bit = BitmapFactory.decodeFile(path);
                        shellPrinter(bit, 48, 28);//生成外壳标签
                        //执行打印成功后
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    /**
     * 根据字符串内容生成二维码
     *
     * @return
     */
    private static Bitmap Create2dCode(String s, int cell) {
        Bitmap bitmap = BitmapUtil.createQRCodeBitmap(s, cell, cell);
        return bitmap;
    }

    /**
     * bitmap封装
     *
     * @param bit bitmap 图片
     * @param x   坐标X
     * @param y   坐标Y
     */
    public static void sendBitmap(Bitmap bit, int x, int y) {
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;//图片不缩放
        int[] pixels = new int[bit.getWidth() * bit.getHeight()];//保存所有的像素的数组，图片宽×高
        Log.e(Common.Log, Arrays.toString(pixels));
        bit.getPixels(pixels, 0, bit.getWidth(), 0, 0, bit.getWidth(), bit.getHeight());
        Log.e(Common.Log, "bit宽" + bit.getWidth());
        Log.e(Common.Log, "bit高" + bit.getHeight());
        int[] newpixels = new int[bit.getWidth() * bit.getHeight()];
        Log.e(Common.Log, "newpixel长度：" + newpixels.length);
        for (int i = 0; i < pixels.length; i++) {
            int clr = pixels[i];
            int red = (clr & 0x00ff0000) >> 16;    //取高两位
            int green = (clr & 0x0000ff00) >> 8; //取中两位
            int blue = clr & 0x000000ff; //取低两位
            //Log.e("rgb","r=" + red + ",g=" + green + ",b=" + blue);
            int rgb = (red + green + blue) / 3; //rgb值除于3 保存这个值可以得到一张灰度值的图片
            if (rgb < 128) {
                newpixels[i] = 0;
            } else {
                newpixels[i] = 1;
            }
        }
        String u = Util.ArrayTransformString(newpixels);//int数组转字符串
        byte[] result = Util.getDecimal(u);
        Log.e(Common.Log, Util.Bytes2HexString(result));
        //由于TSC打印机无法打印00 所以要将00 打印成254 和1
        byte[] bitmap = new byte[result.length];
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 00) {
                result[i] = (byte) 1;
                bitmap[i] = (byte) 254;
            } else {
                bitmap[i] = (byte) 255;
            }
        }
        if (bit.getHeight() % 2 == 1) {
            byte[] endBytes = new byte[bit.getWidth() / 8];
            for (int j = 0; j < endBytes.length; j++) {
                endBytes[j] = (byte) 255;
            }
            result = Util.unitByteArray(result, endBytes);
            bitmap = Util.unitByteArray(bitmap, endBytes);
        }
        int bitWidth = bit.getWidth() / 8;
        int bitHeight = bit.getHeight() / 2;
        int newY = bitHeight + y;
        String initHalf = "BITMAP " + x + "," + newY + "," + bitWidth + "," + bitHeight + ",1,";
        String initBitmap = "BITMAP " + x + "," + y + "," + bitWidth + "," + bitHeight + ",1,";
        Log.e(Common.Log, "initBitmap:" + initBitmap);
        Log.e(Common.Log, "initHalf:" + initHalf);
        //String initHalf = "BITMAP 0," + (bitHeight / 2)+1 + "," + bitWidth + "," + bitHeight / 2 + ",1,";
        String end = "\0\n";

        byte[] foreBytes = new byte[result.length / 2];
        byte[] lastBytes = new byte[result.length / 2];
        //执行复制操作
        System.arraycopy(result, 0, foreBytes, 0, result.length / 2);
        System.arraycopy(result, result.length / 2, lastBytes, 0, result.length / 2);
        //执行复制偏差值操作
        byte[] foreOffset = new byte[bitmap.length / 2];
        byte[] lastOffset = new byte[bitmap.length / 2];
        System.arraycopy(bitmap, 0, foreOffset, 0, bitmap.length / 2);
        System.arraycopy(bitmap, bitmap.length / 2, lastOffset, 0, bitmap.length / 2);
        Log.e(Common.Log, "总长度:" + result.length);
        Log.e(Common.Log, "fore 长度：" + foreBytes.length);
        Log.e(Common.Log, "last 长度" + lastBytes.length);
        byte[] halfA = Util.byteMergerAll(initBitmap.getBytes(), foreBytes, end.getBytes());
        byte[] halfB = Util.byteMergerAll(initHalf.getBytes(), lastBytes, end.getBytes());

        byte[] halfC = Util.byteMergerAll(initBitmap.getBytes(), foreOffset, end.getBytes());
        byte[] halfD = Util.byteMergerAll(initHalf.getBytes(), lastOffset, end.getBytes());
        TscUSB.sendcommand(halfA);//图片太大 发送上一半数据
        TscUSB.sendcommand(halfC);//发送补全上一半
        TscUSB.sendcommand(halfB);//图片太大，发送下一半数据
        TscUSB.sendcommand(halfD);//发送补全下一半
    }

    /**
     * 打印外壳标签
     *
     * @param bit
     * @param x
     * @param y
     */
    public static void shellPrinter(Bitmap bit, int x, int y) {
        //根据图片地址
        TscUSB.sendcommand("CLS\r\n");//清除
        TscUSB.setup(62, 35, 2, 8, 0, 0, 0);
        String SizeString = "SIZE " + 62 + " mm," + 35 + " mm \r\n";
        //TscUSB.sendcommand(SizeString);//设置宽高
        TscUSB.sendcommand("GAP 2 mm,0 mm \r\n");//设置条码间隙
        TscUSB.sendcommand("CLS\r\n");
        TscUSB.sendcommand("DENSITY 15\r\n");//打印浓度
        TscUSB.sendcommand("SPEED 2\r\n");//打印浓度
        TscUSB.sendcommand("DIRECTION 1 \r\n");//设置打印方向
        TscUSB.sendcommand("SET RIBBON ON\r\n");//开启热转印
        sendBitmap(bit, x, y);//发送图片参数
        TscUSB.sendcommand("PRINT 1\r\n");
        TscUSB.closeport(3000);//关闭串口
        printNum = 2;
    }

    /**
     * pcba测试打印图片
     */
    public static void pcbaPrinter(Bitmap bit, int x, int y) {
        //根据图片地址
        TscUSB.sendcommand("CLS\r\n");//清除
        TscUSB.setup(62, 20, 2, 8, 0, 0, 0);
        String SizeString = "SIZE " + 62 + " mm," + 20 + " mm \r\n";
        //TscUSB.sendcommand(SizeString);//设置宽高
        TscUSB.sendcommand("GAP 2 mm,0 mm \r\n");//设置条码间隙
        TscUSB.sendcommand("CLS\r\n");
        TscUSB.sendcommand("DENSITY 15\r\n");//打印浓度
        TscUSB.sendcommand("SPEED 2\r\n");//打印浓度
        TscUSB.sendcommand("DIRECTION 1 \r\n");//设置打印方向
        TscUSB.sendcommand("SET RIBBON ON\r\n");//开启热转印

        if (printNum % 2 == 0) {
            sendBitmap(bit, x, y);
        } else {
            TscUSB.sendcommand("BACKFEED 420\r\n");//设置回退
            int newX = x + 256;
            sendBitmap(bit, newX, y);
        }
        TscUSB.sendcommand("PRINT 1\r\n");
        TscUSB.closeport(3000);//关闭串口
        printNum++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mUsbReceiver);
        mScanGunKeyEventHelper.onDestroy();
    }

    /**
     * 测试传感器
     */
    public static void testADC() {
        Util.testADC(usbService);
    }

    /**
     * 测试门磁
     */
    public static void testMenci() {
        Util.testMENCI(usbService);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mScanGunKeyEventHelper.analysisKeyEvent(event);
        return super.dispatchKeyEvent(event);
    }

    public static void testJavaCrash(){
        //测试bugly结果
        //CrashReport.testJavaCrash();
    }
    /**
     * 显示扫描内容
     */
    @Override
    public void onScanSuccess(String barcode) {
        //TODO 显示扫描内容
        Log.e(Common.Log, "扫描到的内容为：" + barcode);
        String s = barcode.replaceAll("\r|\n|\\\\", "");
        ToastUtil.showShortToast("扫描到的内容为：" + barcode);
        //判断扫描设备存在 把event时间传给fragment
        fragmentKeyeventListener.onFragmentKeyEvent(s);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFilters();
        startService(UsbService.class, usbConnection, null);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_USB_READY);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        filter.addAction(UsbService.ACTION_PLUGIN_BAR_SCAN);
        filter.addAction(UsbService.ACTION_PLUGIN_QR_SCAN);
        filter.addAction(UsbService.ACTION_PLUGIN_PRINTER);
        filter.addAction(UsbService.ACTION_PLUGIN_USB);
        filter.addAction(UsbService.ACTION_PLUGIN_SERIAL);
        registerReceiver(mUsbReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    public void setFragmentKeyEventListener(FragmentKeyEventListener fragmentKeyeventListener) {
        this.fragmentKeyeventListener = fragmentKeyeventListener;
    }

    public void setFragmentStringEventListener(FragmentStringEventListener fragmentStringEventListener) {
        this.fragmentStringeventListener = fragmentStringEventListener;
    }

    public void setFragmentDeviceEventListener(FragmentDeviceEventListener fragmentDeviceEventListener) {
        this.fragmentDeviceEventListener = fragmentDeviceEventListener;
    }
}
