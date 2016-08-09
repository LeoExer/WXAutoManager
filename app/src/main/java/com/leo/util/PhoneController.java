package com.leo.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.leo.common.Config;

import java.io.IOException;
import java.util.List;

/**
 * Created by leo on 2016/8/4.
 * 手机控制器，可控制手机亮屏，后退等
 */
public class PhoneController {

    private final static String TAG = PhoneController.class.getSimpleName();


    /**
     * BACK
     */
    public static void pressBackButton() {
        Log.i(TAG, "press back btn");
        try {
            Runtime.getRuntime().exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
    }

    /**
     * HOME
     * @param context
     */
    public static void pressHomeButton(Context context) {
        Log.i(TAG, "press home btn");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }


    public static boolean isForeground(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName name = am.getRunningTasks(1).get(0).topActivity;
        String topPackageName = name.getPackageName();
        if(!TextUtils.isEmpty(topPackageName) && topPackageName.equalsIgnoreCase(packageName)) {
            Log.i(TAG, "topPgName: " + packageName + "is in foreground");
            return true;
        }
        Log.i(TAG, "topPgName: " + packageName + "is in background");
        return false;
    }


    public static void bringToForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = am.getRunningTasks(3);
        for(RunningTaskInfo info : runningTaskInfos) {
            Log.i(TAG, info.topActivity.getPackageName());
            if(Config.WX_PACKAGE_NAME.equalsIgnoreCase(info.topActivity.getPackageName())) {
                am.moveTaskToFront(info.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }


    public static boolean isLockScreen(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }


    public static void wakeAndUnlockScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        wakeLock.acquire(1000); // 点亮屏幕
        wakeLock.release();

        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = km.newKeyguardLock("unlock");
        lock.disableKeyguard(); // 解锁
    }
}
