package xiao.love.bar.storage.sp.impl;

import android.content.Context;
import android.content.SharedPreferences;

import xiao.love.bar.storage.sp.BaseSp;

/**
 * Created by guochang on 2015/9/10.
 * IM状态值
 */
public class IMSp extends BaseSp {
    public static final String FILE_NAME = "im";
    private static final int FILE_MODE = Context.MODE_PRIVATE;
    //通知使能
    private String KEY_NOTIFICATION_ENABLE = "notification_enable";
    //通知铃声
    private String KEY_NOTIFICATION_SOUND = "notification_sound";
    //通知震动
    private String KEY_NOTIFICATION_VIBRATE = "notification_vibrate";
    //播放语音是否使用扬声器
    private String KEY_VOICE_SPEAKER = "voice_speaker";
    //群组是否同步
    private String KEY_GROUP_SYNCED = "group_synced";
    //通讯录是否同步
    private String KEY_CONTACT_SYNCED = "contact_synced";
    //黑名单是否同步
    private String KEY_BALCKLIST_SYNCED = "balcklist_synced";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static IMSp mInstatnce;

    private IMSp() {
        mSharedPreferences = mContext.getSharedPreferences(mContext.getPackageName() + "." +
                FILE_NAME, FILE_MODE);
        mEditor = mSharedPreferences.edit();
    }

    public synchronized static IMSp getInstance() {
        if (mInstatnce == null) {
            mInstatnce = new IMSp();
        }

        return mInstatnce;
    }

    public synchronized void putNotificationEnable(boolean value){
        putBoolean(mEditor, KEY_NOTIFICATION_ENABLE, value);
    }

    public synchronized boolean getNotificationEnable(boolean def){
        return getBoolean(mSharedPreferences, KEY_NOTIFICATION_ENABLE, def);
    }

    public synchronized void putNotificationSound(boolean value){
        putBoolean(mEditor, KEY_NOTIFICATION_SOUND, value);
    }

    public synchronized boolean getNotificationSound(boolean def){
        return getBoolean(mSharedPreferences, KEY_NOTIFICATION_SOUND, def);
    }

    public synchronized void putNotificationVibrate(boolean value){
        putBoolean(mEditor, KEY_NOTIFICATION_VIBRATE, value);
    }

    public synchronized boolean getNotificationVibrate(boolean def){
        return getBoolean(mSharedPreferences, KEY_NOTIFICATION_VIBRATE, def);
    }

    public synchronized void putVoiceSpeaker(boolean value){
        putBoolean(mEditor, KEY_VOICE_SPEAKER, value);
    }

    public synchronized boolean getVoiceSpeaker(boolean def){
        return getBoolean(mSharedPreferences, KEY_VOICE_SPEAKER, def);
    }

    public synchronized void putGroupSynced(boolean value){
        putBoolean(mEditor, KEY_GROUP_SYNCED, value);
    }

    public synchronized boolean getGroupSynced(boolean def){
        return getBoolean(mSharedPreferences, KEY_GROUP_SYNCED, def);
    }

    public synchronized void putContactSynced(boolean value){
        putBoolean(mEditor, KEY_CONTACT_SYNCED, value);
    }

    public synchronized boolean getContactSynced(boolean def){
        return getBoolean(mSharedPreferences, KEY_CONTACT_SYNCED, def);
    }

    public synchronized void putBalcklistSynced(boolean value){
        putBoolean(mEditor, KEY_BALCKLIST_SYNCED, value);
    }

    public synchronized boolean getBalcklistSynced(boolean def){
        return getBoolean(mSharedPreferences, KEY_BALCKLIST_SYNCED, def);
    }
}
