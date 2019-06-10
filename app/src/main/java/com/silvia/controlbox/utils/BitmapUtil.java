package com.silvia.controlbox.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * @file BitmapUtil
 * Created by Silvia_cooper on 2019/1/16.
 */
public class BitmapUtil {
    //private final static int WIDTH = 400;
    private final static float SMALL_TEXT = 18;
    private final static float LARGE_TEXT = 21;
    private final static int START_LEFT = 0;
    //private final static int START_RIGHT = WIDTH;
    //private final static int START_CENTER = WIDTH / 2;


    /**
     * 特殊需求：
     */
    public final static int IS_LARGE = 10;
    public final static int IS_SMALL = 11;
    public final static int IS_RIGHT = 100;
    public final static int IS_LEFT = 101;
    public final static int IS_CENTER = 102;
    private static float x = START_LEFT, y;

    /**
     * 生成图片
     */
    public static Bitmap StringListtoBitmap(Context context, int WIDTH, ArrayList<StringBitmapParameter> AllString) {
        if (AllString.size() <= 0)
            return Bitmap.createBitmap(WIDTH, WIDTH / 4, Bitmap.Config.RGB_565);
        ArrayList<StringBitmapParameter> mBreakString = new ArrayList<>();

        Paint paint = new Paint();
        paint.setAntiAlias(false);
        paint.setTextSize(SMALL_TEXT);
        //设置网络字体库
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/simhei.ttf");
        Typeface font = Typeface.create(typeface, Typeface.BOLD);
        paint.setTypeface(font);
        for (StringBitmapParameter mParameter : AllString) {
            int ALineLength = paint.breakText(mParameter.getText(), true, WIDTH, null);//检测一行多少字
            int lenght = mParameter.getText().length();
            if (ALineLength < lenght) {
                int num = lenght / ALineLength;
                String ALineString = new String();
                String RemainString = new String();

                for (int j = 0; j < num; j++) {
                    ALineString = mParameter.getText().substring(j * ALineLength, (j + 1) * ALineLength);
                    mBreakString.add(new StringBitmapParameter(ALineString, mParameter.getIsRightOrLeft(), mParameter.getIsSmallOrLarge()));
                }

                RemainString = mParameter.getText().substring(num * ALineLength, mParameter.getText().length());
                mBreakString.add(new StringBitmapParameter(RemainString, mParameter.getIsRightOrLeft(), mParameter.getIsSmallOrLarge()));
            } else {
                mBreakString.add(mParameter);
            }
        }

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int FontHeight = (int) Math.abs(fontMetrics.leading) + (int) Math.abs(fontMetrics.ascent) + (int) Math.abs(fontMetrics.descent);
        y = (int) Math.abs(fontMetrics.leading) + (int) Math.abs(fontMetrics.ascent);

        int bNum = 0;
        int newbNum = 0;
        //遍历循环间隔字符串
        for (StringBitmapParameter mParameter : mBreakString) {
            String bStr = mParameter.getText();
            if (bStr.isEmpty() | bStr.contains("\n") | mParameter.getIsSmallOrLarge() == IS_LARGE) {
                bNum++;
            }
        }
        //如果是大文字则减3
        for (StringBitmapParameter mParameter : mBreakString) {
            if (mParameter.getIsSmallOrLarge() == IS_LARGE) {
                newbNum = bNum - 3;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(WIDTH, FontHeight * (mBreakString.size() + newbNum), Bitmap.Config.RGB_565);

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                bitmap.setPixel(i, j, Color.WHITE);
            }
        }
        Canvas canvas = new Canvas(bitmap);

        for (StringBitmapParameter mParameter : mBreakString) {

            String str = mParameter.getText();

            if (mParameter.getIsSmallOrLarge() == IS_SMALL) {
                paint.setTextSize(SMALL_TEXT);

            } else if (mParameter.getIsSmallOrLarge() == IS_LARGE) {
                paint.setTextSize(LARGE_TEXT);
            }
            if (mParameter.getIsRightOrLeft() == IS_RIGHT) {
                x = WIDTH - paint.measureText(str);
            } else if (mParameter.getIsRightOrLeft() == IS_LEFT) {
                x = START_LEFT;
            } else if (mParameter.getIsRightOrLeft() == IS_CENTER) {
                x = (WIDTH - paint.measureText(str)) / 2.0f;
            }
            //如果是大的字体 则加回车或者加高
            if (str.isEmpty() | str.contains("\n") | mParameter.getIsSmallOrLarge() == IS_LARGE) {
                //canvas.drawText(str, x, y, paint);
                y = y + (FontHeight / 3);
            } else {
                // canvas.drawText(str, x, y, paint);
            }
            y = y + FontHeight;
            canvas.drawText(str, x, y, paint);

        }
        //canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    /**
     * 合并图片
     */
    public static Bitmap BitmapMerge(Bitmap first, Bitmap second) {
        int width = Math.max(first.getWidth(), second.getWidth());
        int startWidth = (width - first.getWidth()) / 2; //x 可以单独设置x
        int height = first.getHeight() + second.getHeight();//y 可以单独设置y
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {
                result.setPixel(i, j, Color.WHITE);
            }
        }

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, startWidth, 0, null);
        canvas.drawBitmap(second, 0, first.getHeight(), null);
        return result;
    }

    /**
     * 将Bitmap存为 .bmp格式图片
     *
     * @param bitmap
     */
    public static void saveBmp(Bitmap bitmap) {
        if (bitmap == null)
            return;
        // 位图大小
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
        try {
            // 存储文件名
            String filename = "test.bmp";
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/多功能控制盒", filename);

            if (!file.exists()) {
                file.mkdirs();
                file.createNewFile();
            } else {

            }
            FileOutputStream fileos = new FileOutputStream(filename);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // 保存bmp文件头
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头
            writeDword(fileos, biSize);
            writeLong(fileos, biWidth);
            writeLong(fileos, biHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
            // 像素扫描
            byte bmpData[] = new byte[bufferSize];
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                }

            fileos.write(bmpData);
            fileos.flush();
            fileos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 800) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
// 这里压缩options%，把压缩后的数据存放到baos中
            if (options > 10) {//设置最小值，防止低于0时出异常
                options -= 10;// 每次都减少10
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//
// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 保存图片返回路径
     */
    public static String saveBitmap2(Bitmap bm) {
        String path = null;
        Log.e(Common.Log, "保存图片");
        String filename = "test.png";
        File f = new File(Environment.getExternalStorageDirectory() + "/多功能控制盒/", filename);
        //生成文件夹之后，再生成文件，不然会出错
        FileUtils.makeFilePath("/sdcard/多功能控制盒/", filename);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            path = f.getPath();
            Log.i(Common.Log, "已经保存" + f.getPath());
            return path;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return path;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return path;
        }
    }

    /**
     * 保存图片返回路径
     */
    public static String saveBitmap(Bitmap bm) {
        String path = null;
        Log.e(Common.Log, "保存图片");
        String filename = "testpng.png";
        File f = new File(Environment.getExternalStorageDirectory() + "/多功能控制盒/", filename);
        //生成文件夹之后，再生成文件，不然会出错
        FileUtils.makeFilePath("/sdcard/多功能控制盒/", filename);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            path = f.getPath();
            Log.i(Common.Log, "已经保存" + f.getPath());
            return path;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return path;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return path;
        }
    }


    public static void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    public static void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    public static void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    /***
     * 使用两个方法的原因是：
     * logo标志需要居中显示，如果直接使用同一个方法是可以显示的，但是不会居中
     */
    public static Bitmap addBitmapInFoot(Bitmap bitmap, Bitmap image) {
        int width = Math.max(bitmap.getWidth(), image.getWidth());
        int startWidth = (width - image.getWidth()) / 2;
        int height = bitmap.getHeight() + image.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {
                result.setPixel(i, j, Color.WHITE);
            }
        }
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(image, startWidth, bitmap.getHeight(), null);
        return result;
    }

    /***
     * 使用两个方法的原因是：
     * logo标志需要居中显示，如果直接使用同一个方法是可以显示的，但是不会居中
     */
    public static Bitmap addBitmapInRight(Bitmap bitmap, Bitmap image, float imageX, float imageY) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {
                result.setPixel(i, j, Color.WHITE);
            }
        }
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(image, imageX, imageY, null);
        return result;
    }

    public static Bitmap bitmap2Gray(Bitmap bmSrc) {
        int width = bmSrc.getWidth();
        int height = bmSrc.getHeight();
        Bitmap bmpGray = null;
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmSrc, 0.0F, 0.0F, paint);
        return bmpGray;
    }

    public static byte[] getPixels(Bitmap bit) {
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
            int rgb = (red + green + blue) / 3;
            if (rgb < 128) {
                newpixels[i] = 0;
            } else {
                newpixels[i] = 1;
            }
        }
        Log.e(Common.Log, Arrays.toString(newpixels));
        //Log.e(Common.Log,Arrays.toString(newpixels));
        String u = Util.ArrayTransformString(newpixels);//int数组转字符串
        byte[] result = Util.getDecimal(u);
        Log.e(Common.Log, Util.Bytes2HexString(result));
        //由于TSC打印机无法打印00
        byte[] bitmap = new byte[result.length];
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 0) {
                result[i] = 1;
                bitmap[i] = (byte) 254;
            } else {
                bitmap[i] = (byte) 255;
            }
        }
        return result;
    }

    public static Bitmap gray(Bitmap bitmap, int schema) {
        Bitmap bm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int pixel = bitmap.getPixel(col, row);// ARGB
                int red = Color.red(pixel); // same as (pixel >> 16) &0xff
                int green = Color.green(pixel); // same as (pixel >> 8) &0xff
                int blue = Color.blue(pixel); // same as (pixel & 0xff)
                int alpha = Color.alpha(pixel); // same as (pixel >>> 24)
                int gray = 0;
                if (schema == 0) {
                    gray = (Math.max(blue, Math.max(red, green)) +
                            Math.min(blue, Math.min(red, green))) / 2;
                } else if (schema == 1) {
                    gray = (red + green + blue) / 3;
                } else if (schema == 2) {
                    gray = (int) (0.3 * red + 0.59 * green + 0.11 * blue);
                }
                bm.setPixel(col, row, Color.argb(alpha, gray, gray, gray));
            }
        }
        return bm;
    }


    /**
     * 获得图片的像素方法
     *
     * @param bitmap
     */

    private void getPicturePixel(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 保存所有的像素的数组，图片宽×高
        int[] pixels = new int[width * height];
        int[] pixels2 = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            pixels[i] = 1;

        }

    }

    /**
     * Bitmap转数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 创建二维码位图
     *
     * @param content 字符串内容(支持中文)
     * @param width   位图宽度(单位:px)
     * @param height  位图高度(单位:px)
     * @return
     */
    @Nullable
    public static Bitmap createQRCodeBitmap(String content, int width, int height) {
        return createQRCodeBitmap(content, width, height, "UTF-8", "L", "0", Color.BLACK, Color.WHITE);
    }

    /**
     * 创建二维码位图 (支持自定义配置和自定义样式)
     *
     * @param content          字符串内容
     * @param width            位图宽度,要求>=0(单位:px)
     * @param height           位图高度,要求>=0(单位:px)
     * @param character_set    字符集/字符转码格式 (支持格式:{@link CharacterSetECI })。传null时,zxing源码默认使用 "ISO-8859-1"
     * @param error_correction 容错级别 (支持级别:{@link ErrorCorrectionLevel })。传null时,zxing源码默认使用 "L"
     * @param margin           空白边距 (可修改,要求:整型且>=0), 传null时,zxing源码默认使用"4"。
     * @param color_black      黑色色块的自定义颜色值
     * @param color_white      白色色块的自定义颜色值
     * @return
     */
    @Nullable
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            @Nullable String character_set, @Nullable String error_correction, @Nullable String margin,
                                            @ColorInt int color_black, @ColorInt int color_white) {

        /** 1.参数合法性判断 */
        if (TextUtils.isEmpty(content)) { // 字符串内容判空
            return null;
        }

        if (width < 0 || height < 0) { // 宽和高都需要>=0
            return null;
        }

        try {
            /** 2.设置二维码相关配置,生成BitMatrix(位矩阵)对象 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();

            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set); // 字符转码格式设置
            }

            if (!TextUtils.isEmpty(error_correction)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction); // 容错级别设置
            }

            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin); // 空白边距设置
            }
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black; // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white; // 白色色块像素设置
                    }
                }
            }

            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,之后返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }
}
