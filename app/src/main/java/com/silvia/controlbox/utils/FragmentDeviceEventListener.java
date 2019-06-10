package com.silvia.controlbox.utils;

public interface FragmentDeviceEventListener {
    void hasPrinter(boolean printer);//是否有打印机
    void hasQrScan(boolean qrscan);//是否有二维码扫描枪
    void hasBarScan(boolean barscan);//是否有条形码扫码枪
    void hasSerialPort(boolean serial);//是否有串口设备
    void hasUSB(boolean usb);//是否有U盘设备
}
