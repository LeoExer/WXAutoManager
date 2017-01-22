package com.leo.common.util;

import android.content.Context;

/**
 * Created by Leo on 2017/1/22.
 */

public class DensityUtils {

    public static int dp2px(Context context, float val) {
        float f = context.getResources().getDisplayMetrics().density;
        return (int) (val * f + 0.5F);
    }

    public static int px2dp(Context context, float val) {
        float f = context.getResources().getDisplayMetrics().density;
        return (int) (val / f + 0.5F);
    }

    public static int sp2px(Context context, float val) {
        float f = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (val * f + 0.5F);
    }

    public static int px2sp(Context context, float val) {
        float f = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (val / f + 0.5F);
    }
}
