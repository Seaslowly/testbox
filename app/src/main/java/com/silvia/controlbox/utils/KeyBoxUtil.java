package com.silvia.controlbox.utils;

import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KeyBoxUtil {
    private static int itedbm;

    /**
     * 模糊搜索
     *
     * @param name
     * @param list
     * @return
     */
    public List search(String name, List list) {
        List results = new ArrayList();
        Pattern pattern = Pattern.compile(name);
        for (int i = 0; i < list.size(); i++) {
            Matcher matcher = pattern.matcher((String) list.get(i));
            if (matcher.find()) {
                results.add(list.get(i));
            }
        }
        return results;
    }

    /**
     * 合并多个数组为一个数组
     *
     * @param values
     * @return
     */
    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    /**
     * 给一个数组不够的补0
     *
     * @param bys    原始数组
     * @param length 需要补0的长度
     * @return
     */
    public static byte[] fillupZero(byte[] bys, int length) {
        byte[] fillbys = new byte[length];
        //先把所有的数组长度填充为0x00
        for (int i = 0; i < fillbys.length; i++) {
            fillbys[i] = 0x00;
        }
        System.arraycopy(bys, 0, fillbys, 0, bys.length);
        return fillbys;
    }

    /**
     * 读取bin文件
     *
     * @param assetsPath
     * @return
     * @throws IOException
     */
    public static ArrayList<byte[]> readBin(String assetsPath) throws IOException {
        //String assetsPath = "E:\\drink\\app\\src\\main\\assets\\ABController_New.bin";
        ArrayList<byte[]> allData = new ArrayList<>();
        File file = new File(assetsPath);
        int size = (int) file.length();
        System.out.println("fileSize:" + size);
        InputStream in = new FileInputStream(assetsPath);
        byte[] buf = new byte[size];
        int j = 0;
        while (in.read(buf) != -1) {
            for (byte b : buf) {
                //System.out.println(Integer.toHexString(b&0xff));
            }
        }
        System.out.println(Arrays.toString(buf));
        int updatePacketNum = size;
        int remainder = 0;//判断是否有余数
        //判断有余数包数加1 无则等于
        int modNum = size / 57;
        if (modNum != 0) {
            System.out.println("有余数");
            updatePacketNum = size / 57 + 1;
            remainder = 1;
        } else {
            System.out.println("无余数");
            updatePacketNum = size / 57;
            remainder = 0;
        }
        System.out.println("升级包数量" + updatePacketNum);

        if (remainder == 0) {
            byte[] data = new byte[57];
            int pos = 0;
            for (int i = 0; i < updatePacketNum; i++) {
                System.arraycopy(buf, pos, data, 0, 57);
                //byte[]  new_bts= Arrays.copyOfRange(buf, 57, buf.length);//移除byte[] 前面57
                byte[] bytes1 = new byte[]{0x7f, 0x04, (byte) buf.length};
                byte[] bytes2 = intToByteArray(i);
                byte[] bytesAll = byteMergerAll(bytes1, bytes2, data);
                System.out.println(Arrays.toString(bytesAll));
                pos = pos + 57;
                allData.add(bytesAll);
            }
        } else {
            byte[] data = new byte[57];
            int pos = 0;
            for (int i = 0; i < updatePacketNum - 1; i++) {
                System.arraycopy(buf, pos, data, 0, 57);
                byte[] bytes1 = new byte[]{0x7f, 0x04, (byte) buf.length};
                byte[] bytes2 = intToByteArray(i);//包号
                byte[] bytesAll = byteMergerAll(bytes1, bytes2, data);
                pos = pos + 57;
                allData.add(bytesAll);
            }
            int end = size - pos;//最后的值减去余数
            System.out.println(end);
            byte[] lastdata = new byte[end];
            System.arraycopy(buf, pos, lastdata, 0, end);
            byte[] bytes1 = new byte[]{0x7f, 0x04, (byte) buf.length};
            byte[] bytes2 = intToByteArray(updatePacketNum - 1);//包号
            System.out.println(Arrays.toString(lastdata));
            byte[] bytesAll = byteMergerAll(bytes1, bytes2, lastdata);
            allData.add(bytesAll);
            System.out.println(Arrays.toString(bytesAll));
        }

        //这里转hex
                /*StringBuilder sb = new StringBuilder();
                for (byte b : emmm) {
                    sb.append(String.format("%02x ", b));
                }
                System.out.println(sb.toString());*/
        return allData;
    }

    private boolean sort(String currentVersion, String targetVersion) {
        List<String> list = new ArrayList<>();
        list.add(currentVersion);
        list.add(targetVersion);
        Collections.sort(list);
        return list.get(0).equals(currentVersion) ? true : false;
    }

    /**
     * 二进制字符串转byte
     */
    public static byte decodeBinaryString(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * @param b byte[] byte[] b = { 0x12, 0x34, 0x56 };
     * @return String 123456
     */
    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase() + " ";
        }
        return ret;
    }

    public static int byte2int16(byte[] bytes) {
        int res = ((bytes[0] << 8) | ((bytes[1] << 24) >>> 24));
        return res;
    }

    /**
     * 16进制转10进制
     *
     * @param b
     * @return
     */
    public static int iSixteen2iTen(int b) {
        int iTen = Integer.parseInt(b + "", 10);
        return iTen;
    }

    /**
     * 10进制转16进制
     *
     * @param b
     * @return
     */
    public static int iTen2iSixteen(int b) {
        String iSixteen = Integer.toHexString(b & 0xff);
        int i = Integer.parseInt(iSixteen);
        return i;
    }

    /**
     * string转 byte数组
     *
     * @param str
     * @return
     */
    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        byte[] byteArray = str.getBytes();
        return byteArray;
    }

    /**
     * 16进制字符串转为十进制字符串
     *
     * @param
     * @return
     */
    public static String hexString2String(String str) {
        int i = Integer.parseInt(str, 16);
        return String.valueOf(i);
    }

    /**
     * 用于数据转换 不是偶数在前面补0 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" -->
     * byte[]{0x2B, 0x44, 0xEF, 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        if (src.length() % 2 == 1) {
            src = "0" + src;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = unionBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /*
    用于crc16效验  可能效验后长度为1  导致越界  不足补0x00
     */
    public static byte[] hexString2Byte(String src) {
        if (src.length() % 2 == 1) {
            src = "0" + src;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = unionBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        if (ret.length != 2) {
            byte[] bytes = new byte[2];
            bytes[0] = 0x00;
            bytes[1] = ret[0];
            return bytes;
        }
        return ret;
    }


    public static byte[] md5String2bytes(String md5String) {
        byte[] md5 = new byte[16];
        ArrayList<Integer> integers = new ArrayList<Integer>();
        for (int i = 0; i < md5String.length(); i += 2) {
            String sub = md5String.substring(i, i + 2);
            String addFF = "0x" + sub;
            int radix16 = Integer.parseInt(addFF.substring(2), 16);//将0x01转成1
            integers.add(radix16);
        }
        for (int j = 0; j < integers.size(); j++) {
            System.out.println(integers.get(j));
            int test = integers.get(j);
            md5[j] = (byte) test;
        }
        //System.out.println(Arrays.toString(md5));
        return md5;
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte unionBytes(byte src0, byte src1) {
        byte b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        b0 = (byte) (b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (b0 ^ _b1);
        return ret;
    }

    public static String Bytes2HexString_noblack(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /*
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        //将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     * ZJTT-03-L000 -->5A 4A 54 54 2D 30 33 2D 4C 30 30 30
     */
    private static String hexString = "0123456789ABCDEF";

    public static String encode(String str) {
        //根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        //将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * 生成随机数
     *
     * @return
     */
    public static byte[] getRandomByte(int random) {
        byte[] bytes = new byte[random];
        Random ran = new Random();
        ran.nextBytes(bytes);
        return bytes;
    }


    /**
     * 获取16进制的时间表示
     *
     * @return
     */
    public static byte[] getTimeHexString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");
        // 20151007205012
        String time = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        String s = time.substring(0, 2);
        if (s.length() % 2 == 1) {
            s = "0" + s;
        }
        String yearString2 = time.substring(2, 4);
        if (yearString2.length() % 2 == 1) {
            yearString2 = "0" + yearString2;
        }

        String month = time.substring(4, 6);
        if (month.length() % 2 == 1) {
            month = "0" + month;
        }
        String day = time.substring(6, 8);
        if (day.length() % 2 == 1) {
            day = "0" + day;
        }
        String hour = time
                .substring(8, 10);
        if (hour.length() % 2 == 1) {
            hour = "0" + hour;
        }
        String minute = time.substring(10,
                12);
        if (minute.length() % 2 == 1) {
            minute = "0" + minute;
        }
        String second = time.substring(12);
        if (second.length() % 2 == 1) {
            second = "0" + second;
        }
        String timString = s + yearString2 + month + day + hour + minute + second;

        return HexString2Bytes(timString);
    }

    /**
     * 16进制时间显示
     *
     * @param time 时间参数  但是是没有补0的
     * @return newTime 时分秒 补零的 不需要判断
     */
    public static byte[] timeHexString(String time, String newTime) {
        String[] split = time.split("-");
        if (split[1].length() % 2 == 1) {
            split[1] = "0" + split[1];
        }
        if (split[2].length() % 2 == 1) {
            split[2] = "0" + split[2];
        }
        time = split[0] + split[1] + split[2] + newTime;
        //20170508 185424
        String yearString1 = time.substring(0, 2);
        if (yearString1.length() % 2 == 1) {
            yearString1 = "0" + yearString1;
        }
        String yearString2 = time.substring(2, 4);
        if (yearString2.length() % 2 == 1) {
            yearString2 = "0" + yearString2;
        }

        String month = time.substring(4, 6);
        if (month.length() % 2 == 1) {
            month = "0" + month;
        }
        String day = time.substring(6, 8);
        if (day.length() % 2 == 1) {
            day = "0" + day;
        }
        String hour = time
                .substring(8, 10);
        if (hour.length() % 2 == 1) {
            hour = "0" + hour;
        }
        String minute = time.substring(10,
                12);
        if (minute.length() % 2 == 1) {
            minute = "0" + minute;
        }
        String second = time.substring(12);
        if (second.length() % 2 == 1) {
            second = "0" + second;
        }
        String timString = yearString1 + yearString2 + month + day + hour + minute + second;
        return HexString2Bytes(timString);
    }

    /**
     * @param strPart 字符串
     * @return 16进制字符串
     * @throws
     * @Title:string2HexString
     * @Description:字符串转16进制字符串
     */
    public static String string2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }

    /**
     * 将一个byte数组转换为无空格的16进制字符串
     * 带空格转换为Byte时为解析异常
     */
    public static String bytes2NoBlankHexString(byte[] data) {
        String s = Bytes2HexString(data);
        String[] split = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            sb.append(split[i]);
        }
        return sb.toString();
    }

    //判断是都否在主线程 true是主线程 false是子线程
    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * <pre>
     * 根据指定的日期字符串获取星期几
     * </pre>
     *
     * @param strDate 指定的日期字符串(yyyy-MM-dd 或 yyyy/MM/dd)
     * @return week
     * 星期几(MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY)
     */
    public static String getWeekByDateStr(String strDate) {
        int year = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(5, 7));
        int day = Integer.parseInt(strDate.substring(8, 10));

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);

        String week = "";
        int weekIndex = c.get(Calendar.DAY_OF_WEEK);

        switch (weekIndex) {
            case 1:
                week = "周日";
                break;
            case 2:
                week = "周一";
                break;
            case 3:
                week = "周二";
                break;
            case 4:
                week = "周三";
                break;
            case 5:
                week = "周四";
                break;
            case 6:
                week = "周五";
                break;
            case 7:
                week = "周六";
                break;
        }
        return week;
    }

    /**
     * 获取指定格式的当期日期
     *
     * @param format
     * @return
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                format);
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    /**
     * int数组转字符串
     *
     * @param SafetyMeasure
     * @return
     */
    public static String ArrayTransformString(int[] SafetyMeasure) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < SafetyMeasure.length; i++) {
            sb.append(SafetyMeasure[i] + "");
        }
        return sb.toString();
    }


    public static byte[] getDecimal(String string) {
        char[] ca = string.toCharArray();
        //拼接2进制 每隔8位添加一个空格
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < ca.length; j++) {
            sb.append(ca[j]);
            if ((j + 1) % 8 == 0) {
                sb.append(" ");
            }
        }

        StringBuilder result = new StringBuilder();
        String[] split = sb.toString().split(" ");
        byte[] bytes = new byte[split.length];
        for (int i = 0; i < split.length; i++) {
            int btd = binaryToDecimal(split[i].toString());
            result.append(btd + " ");
            System.out.println(btd);
            bytes[i] = (byte) btd;
        }
        //return result.toString();
        return bytes;
    }


    //二进制转成10进制
    public static int binaryToDecimal(String sb) {
        char[] ca1 = sb.toCharArray();
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < ca1.length; i++) {
            map.put(i, Integer.parseInt(String.valueOf(ca1[i])));
        }
        int sum = 0;
        for (int i = 0; i < ca1.length; i++) {
            int value = (int) map.get(i);
            if (i == 7) {
                sum += value;
            }
            if (i == 6) {
                sum += value * 2;
            }
            if (i == 5) {
                sum += value * 2 * 2;
            }
            if (i == 4) {
                sum += value * 2 * 2 * 2;
            }
            if (i == 3) {
                sum += value * 2 * 2 * 2 * 2;
            }
            if (i == 2) {
                sum += value * 2 * 2 * 2 * 2 * 2;
            }
            if (i == 1) {
                sum += value * 2 * 2 * 2 * 2 * 2 * 2;
            }
            if (i == 0) {
                sum += value * 2 * 2 * 2 * 2 * 2 * 2 * 2;
            }
        }
        return sum;
    }


    /**
     * 获取指定格式的当期日期
     *
     * @param
     * @return
     */
    public static Date getCurrentTime() {
        return new Date(System.currentTimeMillis());
    }


    /**
     * 日历选择的格式需要处理
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     */
    public static String checkCalendar(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
    }

    /**
     * 合并byte数组
     */
    public static byte[] unitByteArray(byte[] byte1, byte[] byte2) {
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }

    /**
     * byte数组转Ascii
     *
     * @param bytes
     * @return
     */
    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    /**
     * 浮点转换为字节
     *
     * @param f
     * @return
     */
    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

    }

    /**
     * 字符串转换为Ascii 拼接成byte数组
     *
     * @param value
     * @return
     */
    public static byte[] stringToAsciiByte(String value) {
        byte[] bytes = new byte[7];
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length) {
                bytes[i] = charToByteAscii(chars[i]);
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return bytes;

    }

    /**
     * char转byte数组
     *
     * @param c
     * @return
     */
    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    /***
     * char数组转byte 数组
     * @param chars
     * @return
     */
    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }
    /**
     * 方法一：将char 强制转换为byte
     *
     * @param ch
     * @return
     */
    /**
     * 方法一：将char 强制转换为byte
     *
     * @param ch
     * @return
     */
    public static byte charToByteAscii(char ch) {
        byte byteAscii = (byte) ch;
        return byteAscii;
    }

    /**
     * /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 关闭流
     *
     * @param closeable
     */
    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.d(Common.Log, "流关闭失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 中断线程
     */
    public static void interruptThread(Thread thread) {
        //停止线程 --在关闭流之前停止线程 因为无法确定在关闭流时 子线程是否在进行流操作
        if (thread != null) {
            thread.interrupt();
            Log.d(Common.Log, "停止线程命令");
        }
    }

    /**
     * int到byte[] 2个字节
     *
     * @param i
     * @return
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[2];
        // 由高位到低位
        // result[0] = (byte) ((i >> 24) & 0xFF);
        // result[1] = (byte) ((i >> 16) & 0xFF);
        result[0] = (byte) ((i >> 8) & 0xFF);
        result[1] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * int转byte数组 4个字节
     *
     * @param
     * @return
     */
    public static byte[] IntToByte(int num) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((num >> 24) & 0xff);
        bytes[1] = (byte) ((num >> 16) & 0xff);
        bytes[2] = (byte) ((num >> 8) & 0xff);
        bytes[3] = (byte) (num & 0xff);
        return bytes;
    }


    /**
     * byte[]转int
     *
     * @param bytes
     * @return
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < 2; i++) {
            int shift = (2 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;// 往高位游
        }
        return value;
    }

    /**
     * ip地址转byte数组
     *
     * @param ipString
     * @return
     */
    public static byte[] ipToByteArray(String ipString) {
        String[] ip = ipString.split("\\.");
        byte[] ipAddressBys = new byte[4];
        ArrayList<Integer> integers = new ArrayList<Integer>();
        for (int i = 0; i < ip.length; i++) {
            String iSixteen = Integer.toHexString(Integer.parseInt(ip[i]) & 0xff);
            String addFF = "0x" + iSixteen;
            int radix16 = Integer.parseInt(addFF.substring(2), 16);//将0x01转成1
            System.out.println(addFF);
            integers.add(radix16);
        }
        for (int j = 0; j < integers.size(); j++) {
            System.out.println(integers.get(j));
            int test = integers.get(j);
            ipAddressBys[j] = (byte) test;
        }
        return ipAddressBys;
    }

    /**
     * mac转
     *
     * @param macString
     * @return
     */
    public static byte[] macToByteArray(String macString) {
        String[] mac = macString.split("-");
        byte[] ipAddressBys = new byte[mac.length];
        ArrayList<Integer> integers = new ArrayList<Integer>();
        for (int i = 0; i < mac.length; i++) {
            String addFF = "0x" + mac[i];
            int radix16 = Integer.parseInt(addFF.substring(2), 16);//将0x01转成1
            System.out.println(addFF);
            integers.add(radix16);
        }
        for (int j = 0; j < integers.size(); j++) {
            System.out.println(integers.get(j));
            int test = integers.get(j);
            ipAddressBys[j] = (byte) test;
        }
        return ipAddressBys;
    }

    /**
     * 应用程序运行命令获取   Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean RequestRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        Runtime mRuntime = Runtime.getRuntime();
        try {
            process = mRuntime.exec("su");   //   切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("cp /system/app/Drink/libmosa.so /system/lib/ \n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.d(Common.Log, "执行cmd命令错误：" + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public static boolean is_root() {
        boolean res = false;
        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                res = false;
            } else {
                res = true;
            }
            ;
        } catch (Exception e) {
        }
        return res;
    }

    /**
     * 获取链接的后缀名
     *
     * @return
     */
    public static String parseSuffix(String url) {
        Pattern pattern = Pattern.compile("\\S*[?]\\S*");
        Matcher matcher = pattern.matcher(url);

        String[] spUrl = url.toString().split("/");
        int len = spUrl.length;
        String endUrl = spUrl[len - 1];

        if (matcher.find()) {
            String[] spEndUrl = endUrl.split("\\?");
            return spEndUrl[0].split("\\.")[1];
        }
        return endUrl.split("\\.")[1];
    }

    /**
     * 30钥匙箱
     * 设置设备编号
     *
     * @param usbService
     * @param devid      8个字符串
     */
    public static void setDevInfo(UsbService usbService, String devid) {
        byte[] foreBytes = new byte[]{0x0A, 0x04};
        //8个字符串每两个转成byte数组
        byte[] devidBytes = HexString2Bytes(devid);
        //合并字符串
        byte[] bytes = unitByteArray(foreBytes, devidBytes);
        //Modbus校验
        byte[] bys = ModbusCheck(bytes);
        if (usbService != null) {
            usbService.write(bys);
            Log.e(Common.Log, "发送设置设备信息成功");
        }
    }

    public void queryDevInfo(UsbService usbService) {
        byte[] foreBytes = new byte[]{0x01, 0x00};
        byte[] bys = ModbusCheck(foreBytes);
        if (usbService != null) {
            usbService.write(bys);
            Log.e(Common.Log, "查询设备信息成功");
        }
    }

    /**
     * 设置远程网络信息
     *
     * @param usbService
     * @param ipAddress  ip地址 4个字节  120.55.76.3转78 37 4C 03
     * @param ipPort     中心ip端口号 0x1e 0xc6 7878
     * @param host1      49个字节 字符串转ascii 再转16进制 剩下补0 不够补0
     * @param port2      端口号
     * @param host1      49个字节 字符串转ascii 再转16进制 剩下补0 不够补0
     * @param port2      端口号
     */
    public static void setRemoteNetwork(UsbService usbService, String ipAddress, String ipPort, byte hasWired, String host1, String port1, String host2, String port2) {
        byte[] foreBytes = new byte[]{0x42, 0x4F, hasWired};
        byte[] ipBytes = ipToByteArray(ipAddress);// ip地址转4个字节的byte数组
        byte[] portBytes = HexString2Bytes(ipPort); //每两个转成byte数组
        //域名转
        byte[] hostArray1 = stringToAsciiByte(host1);
        byte[] host1Length = new byte[]{(byte) hostArray1.length};//实际域名长度
        byte[] host1Bytes = fillupZero(hostArray1, 49);//不足的补0

        byte[] hostArray2 = stringToAsciiByte(host1);
        byte[] host2Length = new byte[]{(byte) hostArray2.length};//实际域名长度
        byte[] host2Bytes = fillupZero(hostArray1, 49);//不足的补0
        //合并所有数组
        byte[] byteMergerAll = byteMergerAll(foreBytes, ipBytes, portBytes, host1Length, host1Bytes, host2Length, host2Bytes);
        byte[] bys = ModbusCheck(byteMergerAll);//Modbus校验
        if (usbService != null) {
            usbService.write(bys);
            Log.e(Common.Log, "发送设置远程网络信息成功");
        }
    }


    /**
     * 读取网络信息
     *
     * @param usbService
     */
    public static void queryRemoteNetwork(UsbService usbService) {
        byte[] foreBytes = new byte[]{0x43, 0x00};
        byte[] bys = ModbusCheck(foreBytes);
        if (usbService != null) {
            usbService.write(bys);
            Log.e(Common.Log, "读取远程网络信息");
        }
    }

    /**
     * @param usbService
     * @param localIp    本地ip地址 192.168.1.5
     * @param serverPort 服务端端口
     * @param clientPort 客户端端口
     * @param subnetMask 子网掩码 255.255.255.255
     * @param gateWay    网关 192.168.6.1
     * @param macAddress mac地址 8C-16-45-0C-EC-16
     * @param ipMode     0x00 静态ip 0x01 动态ip
     */
    public static void setLocalNetworkParam(UsbService usbService, String localIp, String serverPort, String clientPort, String subnetMask, String gateWay, String macAddress, byte ipMode) {
        byte[] forBytes = new byte[]{0x46, 0x17};
        byte[] ipBytes = ipToByteArray(localIp);
        byte[] serverPortBytes = HexString2Bytes(serverPort); //每两个转成byte数组
        byte[] clientPortBytes = HexString2Bytes(clientPort);

        byte[] subnetMaskBytes = ipToByteArray(subnetMask);
        byte[] gateWayBytes = ipToByteArray(gateWay);

        byte[] macBytes = macToByteArray(macAddress);
        byte[] modeByte = new byte[]{ipMode};

        byte[] byteMergerAll = byteMergerAll(forBytes, ipBytes, serverPortBytes, clientPortBytes, subnetMaskBytes, gateWayBytes, macBytes, modeByte);

        byte[] bys = ModbusCheck(byteMergerAll);//modbus校验

        if (usbService != null) {
            usbService.write(bys);
            Log.e(Common.Log, "设置本地网络参数");
        }
    }

    /**
     * 查询本地网络参数
     *
     * @param usbService
     */
    public void queryLocalNetworkParam(UsbService usbService) {
        byte[] foreBytes = new byte[]{0x47, 0x00};
        byte[] bys = ModbusCheck(foreBytes);
        if (usbService != null) {
            usbService.write(bys);
            Log.e(Common.Log, "查询本地网络参数");
        }
    }

    /**
     * 将ModBus校验过的数组合并
     *
     * @param src 需要校验的byte数组
     * @return
     */
    public static byte[] ModbusCheck(byte[] src) {
        byte[] checkValue = ModBus.getCRC(src);
        byte[] unitByteArray = unitByteArray(src, checkValue);
        return unitByteArray;
    }
}
