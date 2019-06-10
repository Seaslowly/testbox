package com.silvia.controlbox.utils;


import android.content.Context;
import android.os.Environment;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UsbManagerUtils {

    public static UsbManagerUtils instance=null;
    private static String  usbPath;
    private static String result;

    public UsbManagerUtils(Context context) {
    }
    /**
     * @description 把本地文件写入到U盘中
     * @author ldm
     * @time 2017/8/22 10:22
     */
    public static boolean saveSDFile2OTG(final File f, final UsbFile usbFile) {
        UsbFile uFile = null;
        FileInputStream fis = null;
        try {//开始写入
            fis = new FileInputStream(f);//读取选择的文件的
            if (usbFile.isDirectory()) {//如果选择是个文件夹
                UsbFile[] usbFiles = usbFile.listFiles();
                if (usbFiles != null && usbFiles.length > 0) {
                    for (UsbFile file : usbFiles) {
                        if (file.getName().equals(f.getName())) {
                            file.delete();
                        }
                    }
                }
                String a=Util.GetNowDate();//获取当前时间
                String fileName="ControlBoxLog"+a+".txt";
                uFile = usbFile.createFile(fileName);
                UsbFileOutputStream uos = new UsbFileOutputStream(uFile);
                try {
                    redFileStream(uos, fis);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static void redFileStream(OutputStream os, InputStream is) throws IOException {
        int bytesRead = 0;
        byte[] buffer = new byte[1024 * 8];
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        is.close();
    }

    public static UsbManagerUtils getInstance(Context context) {
        if (instance == null) {
            instance = new UsbManagerUtils(context);
        }
        return instance;
    }

    /**
     * 是否挂载
     */
    public static boolean isMounted(){
        boolean mounted= Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        return  mounted;
    }

    /**
     * 得到外部存储可用的空间
     *
     * @return 剩余空间的大小，单位是Byte
     */
    public long getExternalStoreAvailableSize() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_SHARED)) {
            // 取得sdcard文件路径
            File pathFile = Environment.getExternalStorageDirectory();
            android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
            // 获取SDCard上每个block的SIZE
            long nBlocSize = statfs.getBlockSize();
            // 获取可供程序使用的Block的数量
            long nAvailaBlock = statfs.getAvailableBlocks();
            // 计算 SDCard 剩余大小Byte
            long nSDFreeSize = nAvailaBlock * nBlocSize;
            if (nSDFreeSize > 1024 * 1024 * 1024) {
                result += "外部存储可用的空间:" + nSDFreeSize / (1024 * 1024 * 1024) + "G\n";
            } else {
                result += "外部存储可用的空间:" + nSDFreeSize + "Byte\n";
            }
            return nSDFreeSize;
        } else {
            result += "size:" + "0" + "\n";
        }
        return 0;
    }
}
