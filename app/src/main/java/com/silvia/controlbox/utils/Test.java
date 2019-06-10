package com.silvia.controlbox.utils;

public class Test {

    public static void main(String[] args){
        String codeString="45781236;00-A0-50-06-29-0E;864814043603199;DX";
        String[] split = codeString.split(";");
        //1 设备编号 2 蓝牙mac地址 3 IMEI 4 运营商
        String dev=split[0];// 设备编号
        String ble=split[1];//蓝牙mac地址
        String imei=split[2];//IMEI
        String operator=split[3];//运营商
        System.out.println(dev);
        System.out.println(ble);
        System.out.println(imei);
        System.out.println(operator);

        String blemac="00A05006290E";
        char[] ca = blemac.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < ca.length; j++) {
            sb.append(ca[j]);
            if ((j + 1) % 2 == 0) {
                sb.append("-");
            }
        }
        String str=sb.toString();
       String newStr=str.substring(0,str.length()-1);
        System.out.println(newStr);


    }
}
