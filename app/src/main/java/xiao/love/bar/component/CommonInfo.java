package xiao.love.bar.component;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

/**
 * 获取固定通用的信息
 *
 * @author guochang
 */
public class CommonInfo {
    private static CommonInfo sAppInfo;
    //android系统SDK版本
    public int mAndroidSDK;
    //设备宽(像素)
    public int mWidthPixel;
    //设备高(像素)
    public int mHeigthPixel;
    //app包名
    public String mAppPackageName = "";
    //设备分辨率
    public float mDensity;

    private CommonInfo() {
    }

    public static synchronized CommonInfo getInstance() {
        if (sAppInfo == null) {
            sAppInfo = new CommonInfo();
        }
        return sAppInfo;
    }

    public void init(Context context) {
        mAndroidSDK = Integer.valueOf(Build.VERSION.SDK_INT);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mWidthPixel = displayMetrics.widthPixels;
        mHeigthPixel = displayMetrics.heightPixels;
        mDensity = displayMetrics.density;
        mAppPackageName = context.getApplicationContext().getPackageName();
    }

}
