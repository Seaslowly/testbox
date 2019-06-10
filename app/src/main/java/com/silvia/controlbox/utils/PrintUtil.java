package com.silvia.controlbox.utils;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.example.tscdll.TSCUSBActivity;

import java.io.UnsupportedEncodingException;

/**
 * @file FileName
 * Created by Silvia_cooper on 2019/1/10.
 */
public class PrintUtil {
    public static String Succcess = "-1";
    public static String Failed = "1";

    /**
     * 添加文字
     *
     * @param mUsbConnection
     * @param x
     * @param y
     * @param size
     * @param rotation
     * @param x_multiplication
     * @param y_multiplication
     * @param string
     * @return
     */
    public static String addText(TSCUSBActivity mUsbConnection, int x, int y, String size, int rotation, int x_multiplication, int y_multiplication, String string) {
        if (mUsbConnection == null) {
            return Failed;
        } else {
            String message = "";
            String text = "TEXT ";
            String position = x + "," + y;
            String size_value = "\"" + size + "\"";
            String rota = "" + rotation;
            String x_value = "" + x_multiplication;
            String y_value = "" + y_multiplication;
            String string_value = "\"" + string + "\"";
            message = text + position + " ," + size_value + " ," + rota + " ," + x_value + " ," + y_value + " ," + string_value + "\r\n";
            mUsbConnection.sendcommandGB2312(message);
            return Succcess;
        }
    }

    /**
     * @param x        坐标X
     * @param y        坐标Y
     * @param ecc      错误能力纠正等级 L(7%) M(15%) Q(25%) H(35%) M
     * @param cell     1-10 1
     * @param mode     A 自动生成 M手动生成
     * @param rotation 旋转角度
     * @param model    M1原始版本 M2扩大版本
     * @param mask     S0到S8 预设S7
     * @param content
     */
    public static String addQrcode(TSCUSBActivity mUsbConnection, int x, int y, String ecc, String cell, String mode, String rotation, String model, String mask, String content) {
        if (mUsbConnection == null) {
            return Failed;
        } else {

            String message = "QRCODE " + x + "," + y + "," + ecc + "," + cell + "," + mode + "," + rotation + "," + model + "," + mask + "," + "\"";
            byte[] messageBuff = new byte[1024];
            try {
                messageBuff = content.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mUsbConnection.sendcommand(message);
            mUsbConnection.sendcommand(messageBuff);
            mUsbConnection.sendcommand("\"\n");
            return Succcess;
        }
    }

    public static String Tear(TSCUSBActivity mUsbConnection, boolean state) {
        if (mUsbConnection == null) {
            return Failed;
        } else {
            if (state == true) {
                mUsbConnection.sendcommand("SET TEAR ON\n");
            } else {
                mUsbConnection.sendcommand("SET TEAR OFF\n");
            }
            return Succcess;
        }
    }

    public static String Clearbuffer(TSCUSBActivity mUsbConnection) {
        if (mUsbConnection == null) {
            return Failed;
        } else {
            mUsbConnection.clearbuffer();
            return Succcess;
        }
    }

    //打印PCBA板子二维码
    public static void printPCBALab(TSCUSBActivity mUsbConnection, String DevID, String IMEI, String QR, Boolean LeftOrRight) {
        mUsbConnection.sendcommand("CLS");//需要清除上一次的打印记忆
        mUsbConnection.sendcommand("DIRECTION 1"); //设置相对起点
        int x = 10;
        if (LeftOrRight) {
            //a-x起始点,b-y起始点
            //c-字体高度（piont表示）,d-逆时针旋转角度（0，90，180，270）
            //e-字体（0-标准 1-斜体 2-粗体 3-斜粗体）
            //f-0-无底线 1-加底线
            //g-字体名称 h-文字内容
            addText(mUsbConnection, x, 10, Common.heiti, 0, 0, 0, "设备编号:");
            addText(mUsbConnection, x, 40, Common.heiti, 0, 0, 0, DevID);
            addText(mUsbConnection, x, 80, Common.heiti, 0, 0, 0, "IMEI:");
            addText(mUsbConnection, x, 105, Common.heiti, 0, 0, 0, IMEI);
            //QRCODE x,y,ECC Level,cell width,mode,rotation,[model,mask,]"content"
            mUsbConnection.sendcommand("QRCODE 140,20,M,3,B,0,M2,S7,\"" + QR + "\"");
        } else {
            int add = 260;
            addText(mUsbConnection, x + add, 10, Common.heiti, 0, 0, 0, "设备编号:");
            addText(mUsbConnection, x + add, 40, Common.heiti, 0, 0, 0, DevID);
            addText(mUsbConnection, x + add, 80, Common.heiti, 0, 0, 0, "IMEI:");
            addText(mUsbConnection, x + add, 105, Common.heiti, 0, 0, 0, IMEI);
            //QRCODE x,y,ECC Level,cell width,mode,rotation,[model,mask,]"content"
            mUsbConnection.sendcommand("QRCODE 400,20,M,3,B,0,M2,S7,\"" + QR + "\"");
            mUsbConnection.sendcommand("BACKUP 420"); //越大越往底部走
        }
        //a-列数 b-份数
        mUsbConnection.printlabel(1, 1);
    }

    /**
     * 打开端口
     *
     * @param mUsbConnection
     * @param mUsbManager
     * @param device
     */
    public static void OpenPort(TSCUSBActivity mUsbConnection, UsbManager mUsbManager, UsbDevice device) {
        if (mUsbManager.hasPermission(device)) {
            mUsbConnection.openport(mUsbManager, device);
            mUsbConnection.clearbuffer();
            mUsbConnection.sendcommand("SET TEAR ON\n");
            //String FFF="TEXT 100,100,\"FONT001\",0,5,5,\"" + AA + "\"";
            mUsbConnection.setup(300, 200, 2, 10, 0, 2, 0);
            mUsbConnection.sendcommand("GAP 0,0\r\n");//设置标签间隙
            mUsbConnection.sendcommand("SIZE 3,1\r\n");//设置标签尺寸
            mUsbConnection.sendcommand("CLS\n");
        } else {
            //设备未连接
        }
    }

    public static void Print(TSCUSBActivity mUsbConnection,int num){
        mUsbConnection.sendcommand("PRINT"+num+"\n");
    }
    public static void BasicSetup(TSCUSBActivity mUsbConnection){
        mUsbConnection.setup(62, 35, 3, 15, 0, 0, 0);//设置媒体大小和传感器类型信息
        //TSCLIB_DLL.sendcommand("SIZE 40 mm,60 mm");//设置条码大小
        mUsbConnection.sendcommand("GAP 2 mm,0");//设置条码间隙
        mUsbConnection.sendcommand("DERECTION 1");//设置相对起点
        mUsbConnection.sendcommand("REFERENCE 0 mm,0 mm");//设置偏移边框
        mUsbConnection.sendcommand("CLS");//清除记忆（每次打印新的条码时先清除上一次的打印记忆）
    }

    public static void ClosePort(TSCUSBActivity mUsbConnection) {
        mUsbConnection.closeport();
    }



    /**
     * 获取打印机状态
     * @param mUsbConnection
     */
    public static void PrinterStatus(TSCUSBActivity mUsbConnection) {
        String status = mUsbConnection.printerstatus();
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
}
