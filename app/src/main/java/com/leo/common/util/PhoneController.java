package com.leo.common.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

/**
 * Created by leo on 2016/8/4.
 * 手机控制相关工具类
 */
public class PhoneController {

    private final static String TAG = PhoneController.class.getSimpleName();

    /**
     * 判断是否锁屏
     * @param context
     * @return
     */
    public static boolean isLockScreen(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    /**
     * 唤醒手机并解锁(不能有锁屏密码)
     * @param context
     */
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
