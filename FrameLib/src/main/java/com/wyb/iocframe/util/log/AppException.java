package com.wyb.iocframe.util.log;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import com.wyb.iocframe.MyApplication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;


/**
 * wyb
 */
public class AppException implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static AppException instance;
    private UncaughtExceptionHandler mDefaultHandler;
    private Logger yunLogger = new Logger("exit");

    private AppException() {
        init();
    }

    public static AppException getInstance() {
        if (instance == null) {
            instance = new AppException();
        }
        return instance;
    }

    private void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        Intent intent = MyApplication.getInstance().getApplicationContext()

                .getPackageManager()
                .getLaunchIntentForPackage(
                        MyApplication.getInstance().getApplicationContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AlarmManager mgr = (AlarmManager) MyApplication.getInstance().getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        PendingIntent restartIntent = PendingIntent.getActivity(
                MyApplication.getInstance().getApplicationContext(), 0, intent,
                Intent.FLAG_ACTIVITY_NEW_TASK);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000,
                restartIntent);
        exit();
    }

    public static void exit() {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getInstance().getApplicationContext().startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) MyApplication.getInstance().getApplicationContext()
                    .getSystemService(
                            MyApplication.getInstance().getApplicationContext().ACTIVITY_SERVICE);
            am.restartPackage(MyApplication.getInstance().getApplicationContext().getPackageName());
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
//		Constant.getInstance().setBugMsg(ex.getMessage());  д�뻺��
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String msg = sw.toString();
        yunLogger.log(msg);
        return true;
    }
}