package com.silvia.controlbox.utils;

/**
 * @file FileName
 * Created by Silvia_cooper on 2018/12/11.
 */
public class Common {
    /**
     * 波特率
     */
    public static int BAUD_RATE_4800=4800;
    public static int BAUD_RATE_9600=9600;
    public static int BAUD_RATE_14400=14400;
    public static int BAUD_RATE_19200=19200;
    public static int BAUD_RATE_38400=38400;
    public static int BAUD_RATE_56000=56000;
    public static int BAUD_RATE_115200=115200;
    public static int BAUD_RATE_921600=921600;

    public static String HOST="HOST";//目标服务器ip
    public static String HOST_PORT="HOST_PORT";//目标服务器端口
    public static String  DEVICE_WORK_TIME="DEVICE_WORK_TIME";//设备工作时间 分钟
    public static String DEVICE_TIMING_ACQUISITION="DEVICE_TIMEING_ACQUISITION";//设备定时采集时间 小时
    public static String DEVICE_TIMEING_REPORT="DEVICE_TIMEING_REPORT";//设备定时上报 小时
    public static String DEVICE_INSTALL_METHOD="DEVICE_INSTALL_METHOD";
    public static String NID="NID";
    public static String BLEMAC="BLEMAC";
    public static String IMEI="IMEI";
    public static String DEV="DEV";

    public static String SENSOR_Z="SENSOR_Z";//传感器z轴
    public static String SENSOR_X="SENSOR_X";//传感器x轴
    public static String SENSOR_Y="SENSOR_Y";//传感器y轴

    /**
     * 门磁报警器产品型号对应
     */
//    public static String CHINANET_MODEL="MB-M100";
//    public static String UNICOM__MODEL="MB-M110";
//    public static String MOBILE_MODEL="MB-M110";
    /**
     * 门磁报警器产品型号对应
     */
    public static String CHINANET_JMODEL="MB-J100";
    public static String UNICOM__JMODEL="MB-J110";
    public static String MOBILE_JMODEL="MB-J110";

    public static String PRODUCT_DOORMAGNET="单门磁";
    public static String PRODUCT_MANHOLECOVER="井盖";

    public static String CHINANET_MODEL="MB-C81";
    public static String PRODUCT_CTRLBOX="多功能控制器";
    public static String strChinanet="(电信版)";
    public static String strUnicom="(联通版)";
    public static String strMobile="(移动版)";

    public static String Log="Silvia";
    public static String receiveLog="Silvia receive:";
    public static String Chinanet="DX";
    public static String Mobile="YD";
    public static String unicom="LT";

    public static String shell_device="设备编号";
    public static String shell_imei="IMEI号";
    public static String pcba_device="设备编号";
    public static String pcba_imei="IMEI号";

    public static String BUGLY_APPID="f90782bd38";
    public static String BUGLY_APPKEY="18c8538a-f520-4238-8d6a-2e76d3462ddc";
    /**
    *字体
    */
    public static String songti="Font001";//宋体  微软雅黑
    public static String heiti="Font002";//黑体
    public static String ximen="Font001";//微软雅黑
    public static String songti10="Songti10";//宋体10号
    public static String songti12="Songti12";//宋体10号
    public static String PrinterStatus00="打印机准备就绪";
    public static String PrinterStatus01="打印头开启!";
    public static String PrinterStatus02="纸张卡纸!";
    public static String PrinterStatus03="打印头开启并且纸张卡纸!";
    public static String PrinterStatus04="纸张缺纸!";
    public static String PrinterStatus05="打印头开启并且纸张缺纸!";
    public static String PrinterStatus08="无碳带!";
    public static String PrinterStatus09="打印头开启并且无碳带!";
    public static String PrinterStatus0A="纸张卡纸并且无碳带!";
    public static String PrinterStatus0B="打印头开启、纸张卡纸并且无碳带!";
    public static String PrinterStatus0C="纸张缺纸并且无碳带!";
    public static String PrinterStatus0D="打印头开启、纸张缺纸并且无碳带!";
    public static String PrinterStatus10="打印机暂停!";
    public static String PrinterStatus20="打印中!";
    public static String PrinterStatus80="打印机发生错误!";
    public static String PrinterStatusError="获取打印机状态失败!";

    /**
     *
     */


    public static final int SetDevID=1;//设置设备编号
    public static final int WriteIp = 2;//设置服务器IP
    public static final int QueryIp=3;//查询IP
    public static final int WritePort = 4;//设置服务器端口号
    public static final int QueryPort=5;//查询服务器端口号
    public static final int WriteWorkT = 6;//设置工作时间
    public static final int WriteCollT = 7;//设置定时采集时间
    public static final int WriteReportT = 8;//设置定时上报时间
    public static final int WriteInstall = 9;//设置安装方向
    public static final int GetDevID = 10;//获取设备ID
    public static final int GetImei = 11;//获取IMEI号
    public static final int GetNID = 12;//Sim卡号
    public static final int GetBleMac = 13;//获取蓝牙地址
    public static final int DEVICE_ID = 14;//设备ID

    //测试门磁
    public static final String ON="01";
    public static final String OFF="00";

    public static boolean isPrinter=false;//打印机设备
    public static boolean isSerial=false;//串口设备
    public static boolean isUSB=false;//U盘

}
