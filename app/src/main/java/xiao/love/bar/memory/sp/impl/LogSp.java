package xiao.love.bar.memory.sp.impl;

import android.content.Context;
import android.content.SharedPreferences;

import xiao.love.bar.memory.sp.BaseSp;


/**
 * Created by guochang on 2015/5/18.
 */
public class LogSp extends BaseSp {
    public static final String FILE_NAME = "log";
    private static final int FILE_MODE = Context.MODE_PRIVATE;
    //下一次product log上传时间
    public static final String KEY_PRODUCT_NEXT_UPLOAD_TIME = "product_next_upload_time";
    //是否调试模式(控制台是否输出log)
    public static final String KEY_IS_DEBUG = "is_debug";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static LogSp mInstatnce;

    private LogSp() {
        mSharedPreferences = mContext.getSharedPreferences(mContext.getPackageName() + "." +
                FILE_NAME, FILE_MODE);
        mEditor = mSharedPreferences.edit();
    }

    public synchronized static LogSp getInstance() {
        if (mInstatnce == null) {
            mInstatnce = new LogSp();
        }
        return mInstatnce;
    }

    public synchronized void putProductNextUploadTime(long value){
        putLong(mEditor, KEY_PRODUCT_NEXT_UPLOAD_TIME, value);
    }

    public synchronized long getProductNextUploadTime(long def){
        return getLong(mSharedPreferences, KEY_PRODUCT_NEXT_UPLOAD_TIME, def);
    }

    public synchronized void putDebug(boolean value){
        putBoolean(mEditor, KEY_IS_DEBUG, value);
    }

    public synchronized boolean getDebug(boolean def){
        return getBoolean(mSharedPreferences, KEY_IS_DEBUG, def);
    }

    public synchronized void clearPrefs() {
        clear(mEditor);
    }
}
