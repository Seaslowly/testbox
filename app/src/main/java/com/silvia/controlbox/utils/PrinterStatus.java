package com.silvia.controlbox.utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @file FileName
 * Created by Silvia_cooper on 2019/1/7.
 */
public class PrinterStatus {
    public static void main(String[] args) throws Exception {
        System.out.println(new PrinterStatus().status("192.168.1.130", 9100).getMessage());
    }

    public Result status(String ipaddress, int portnumber) throws Exception {
        Result result = new Result();
        InputStream inputStream;
        OutputStream outputStream;
        Socket socket;
        byte[] readBuf = new byte[1024];

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ipaddress, portnumber), 2000);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(0);
            result.setMessage("打印机连接失败!");
            return result;
        }
        byte[] message = new byte[]{27, 33, 63};

        String query = "";

        try {
            outputStream.write(message);
        } catch (IOException e) {
            result.setCode(0);
            result.setMessage("获取打印机状态失败!");
            return result;
        }


        try {
            int i = inputStream.available();
            while (i == 0) {
                i = inputStream.available();
            }
            inputStream.read(readBuf);
        } catch (IOException var7) {
            result.setCode(0);
            result.setMessage("读取打印机状态失败!");
            return result;
        }

        if (readBuf[0] == 0) {
            result.setCode(1);
            result.setMessage("打印机准备就绪");
        } else if (readBuf[0] == 1) {
            result.setCode(0);
            result.setMessage("打印头开启!");
        } else if (readBuf[0] == 2) {
            result.setCode(0);
            result.setMessage("纸张卡纸!");
        } else if (readBuf[0] == 3) {
            result.setCode(0);
            result.setMessage("打印头开启并且纸张卡纸!");
        } else if (readBuf[0] == 4) {
            result.setCode(0);
            result.setMessage("纸张缺纸!");
        } else if (readBuf[0] == 5) {
            result.setCode(0);
            result.setMessage("打印头开启并且纸张缺纸!");
        } else if (readBuf[0] == 8) {
            result.setCode(0);
            result.setMessage("无碳带!");
        } else if (readBuf[0] == 9) {
            result.setCode(0);
            result.setMessage("打印头开启并且无碳带!");
        } else if (readBuf[0] == 10) {
            result.setCode(0);
            result.setMessage("纸张卡纸并且无碳带!");
        } else if (readBuf[0] == 11) {
            result.setCode(0);
            result.setMessage("打印头开启、纸张卡纸并且无碳带!");
        } else if (readBuf[0] == 12) {
            result.setCode(0);
            result.setMessage("纸张缺纸并且无碳带!");
        } else if (readBuf[0] == 13) {
            result.setCode(0);
            result.setMessage("打印头开启、纸张缺纸并且无碳带!");
        } else if (readBuf[0] == 16) {
            result.setCode(0);
            result.setMessage("打印机暂停!");
        } else if (readBuf[0] == 32) {
            result.setCode(1);
            result.setMessage("打印中!");
        } else if (readBuf[0] == 128) {
            result.setCode(0);
            result.setMessage("打印机发生错误!");
        }
        socket.close();
        return result;

    }

}
