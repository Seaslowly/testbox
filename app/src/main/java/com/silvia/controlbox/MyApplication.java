package com.silvia.controlbox;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Environment;
import android.view.WindowManager;

import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;
import com.silvia.controlbox.utils.AppUtils;
import com.silvia.controlbox.utils.Common;
import com.silvia.controlbox.utils.FileUtils;
import com.silvia.controlbox.utils.ToastUtil;


/**
 * @file FileName
 * Created by Silvia_cooper on 2018/12/11.
 */
public class MyApplication extends Application {
    private static Context context;
    public static MyApplication application;
    public final static float DESIGN_WIDTH = 750; //绘制页面时参照的设计图宽度

    public static Context getContext() {
        return context;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        QMUISwipeBackActivityManager.init(this);
        context=getApplicationContext();
        application = this;
        ToastUtil t = new ToastUtil(application);
        resetDensity();//注意不要漏掉
        //initBugly();//Bugly异常上报
        //setUpgrade();//Bugly升级
    }

//    private void initBugly() {
//        /* Bugly SDK初始化
//         * 参数1：上下文对象
//         * 参数2：APPID，平台注册时得到,注意替换成你的appId
//         * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
//         * 注意：如果您之前使用过Bugly SDK，请将以下这句注释掉。
//         */
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
//        strategy.setAppVersion(AppUtils.getVersionName(context));
//        strategy.setAppPackageName(AppUtils.getPackageName(context));
//        strategy.setAppReportDelay(20000);                          //Bugly会在启动20s后联网同步数据
//
//        /*  第三个参数为SDK调试模式开关，调试模式的行为特性如下：
//            输出详细的Bugly SDK的Log；
//            每一条Crash都会被立即上报；
//            自定义日志将会在Logcat中输出。
//            建议在测试阶段建议设置成true，发布时设置为false。*/
//        //CrashReport.initCrashReport(getApplicationContext(), Common.BUGLY_APPID, true ,strategy);
//    }
//    public void setUpgrade(){
//        Bugly.init(getApplicationContext(), Common.BUGLY_APPID, false);
//        //true表示app启动自动初始化升级模块;
//        //false不自动初始化
//        Beta.autoInit = true;
//        //true表示初始化时自动检查升级
//        //false表示不会自动检查升级，需要手动调用Beta.checkUpgrade()方法
//        Beta.autoCheckUpgrade = true;
//        //设置升级周期为60s（默认检查周期为0s），60s内SDK不重复向后台请求策略
//        Beta.initDelay = 1 * 1000;
//        //设置通知栏大图标，largeIconId为项目中的图片资源；
//        Beta.largeIconId = R.drawable.chips;
//        //设置状态栏小图标，smallIconId为项目中的图片资源id;
//        Beta.smallIconId =R.drawable.chips;
//        /**
//         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
//         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
//         */
//        Beta.defaultBannerId = R.drawable.chips;
//        /**
//         * 设置sd卡的Download为更新资源保存目录;
//         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
//         */
//        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        //点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
//        Beta.showInterruptedStrategy = true;
//        // 设置是否显示消息通知
//        Beta.enableNotification = true;
//        //使用默认弹窗
//        Beta.canShowApkInfo = true;
//        //关闭或开启热更新能力,默认开启
//        Beta.enableHotfix = false;
//        /**
//         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
//         * 不设置会默认所有activity都可以显示弹窗;
//         */
//        Beta.canNotShowUpgradeActs.add(MainActivity.class);
//    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetDensity();//这个方法重写也是很有必要的
    }

    public void resetDensity(){
        Point size = new Point();
        ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        getResources().getDisplayMetrics().xdpi = size.x/DESIGN_WIDTH*72f;
    }
}
