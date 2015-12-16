package xiao.love.bar.memory.sp.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.Map;

import xiao.love.bar.LoveApp;

public class BaseSp {
    protected Context mContext = LoveApp.sAppInstance;

    public void putString(SharedPreferences.Editor editor, String key, String value){
        if(editor == null || TextUtils.isEmpty(key)){
            return;
        }

        editor.putString(key, value).commit();
    }

    public void putMap(SharedPreferences.Editor editor, Map<String, Object> map){
        if(editor == null || map == null || map.size() <= 0){
            return;
        }

        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = map.get(key);
            if(value instanceof String) {
                editor.putString(key, (String) value);
            }else if(value instanceof Long){
                editor.putLong(key, (Long) value);
            }else if(value instanceof Integer){
                editor.putInt(key, (Integer) value);
            }else if(value instanceof Boolean){
                editor.putBoolean(key, (Boolean) value);
            }else if(value instanceof Float){
                editor.putFloat(key, (Float) value);
            }
        }

        editor.commit();
    }

    public String getString(SharedPreferences sp, String key, String def){
        if(sp == null || TextUtils.isEmpty(key)){
            return "";
        }

        return sp.getString(key, def);
    }

    public void putLong(SharedPreferences.Editor editor, String key, long value){
        if(editor == null || TextUtils.isEmpty(key)){
            return;
        }

        editor.putLong(key, value).commit();
    }

    public long getLong(SharedPreferences sp, String key, long def){
        if(sp == null || TextUtils.isEmpty(key)){
            return def;
        }

        return sp.getLong(key, def);
    }

    public void putInt(SharedPreferences.Editor editor, String key, int value){
        if(editor == null || TextUtils.isEmpty(key)){
            return;
        }

        editor.putInt(key, value).commit();
    }

    public int getInt(SharedPreferences sp, String key, int def){
        if(sp == null || TextUtils.isEmpty(key)){
            return def;
        }

        return sp.getInt(key, def);
    }

    public void putBoolean(SharedPreferences.Editor editor, String key, boolean value){
        if(editor == null || TextUtils.isEmpty(key)){
            return;
        }

        editor.putBoolean(key, value).commit();
    }

    public boolean getBoolean(SharedPreferences sp, String key, boolean def){
        if(sp == null || TextUtils.isEmpty(key)){
            return def;
        }

        return sp.getBoolean(key, def);
    }

    public void remove(SharedPreferences.Editor editor, String key){
        if(editor == null){
            return;
        }

        editor.remove(key).commit();
    }

    public void clear(SharedPreferences.Editor editor){
        if(editor == null){
            return;
        }

        editor.clear().commit();
    }
}
