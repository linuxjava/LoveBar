package xiao.love.bar.memory.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import xiao.love.bar.component.util.Util;
import xiao.love.bar.memory.db.base.DatabaseHelper;
import xiao.love.bar.memory.db.model.NameValuePair;

/**
 * Created by guochang on 2015/8/10.
 */
public class NameValuePairDB {
    /******表列名*******/
    private static final String COLUMN_KEY = "key";
    private static final String COLUMN_VALUE = "value";
    /******表参数key*******/
    private static NameValuePairDB mInstance;
    private Dao<NameValuePair, Integer> mDao = null;

    private NameValuePairDB(){

    }

    private NameValuePairDB(Context context){
        mDao = DatabaseHelper.getInstance(context).getDao(NameValuePair.class);
    }

    public synchronized static NameValuePairDB getInstance(Context context){
        if(mInstance == null){
            mInstance = new NameValuePairDB(context);
        }

        return mInstance;
    }

    /**
     * 插入
     * @param pair
     */
    private void insert(NameValuePair pair){
        try {
            mDao.create(pair);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过key查找
     * @param key
     * @return
     */
    private NameValuePair findByKey(String key){
        try {
            return mDao.queryForSameId(new NameValuePair(key, ""));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 更新
     * @param pair
     */
    private void update(NameValuePair pair){
        try {
            mDao.update(pair);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setInt(String key, int value){
        NameValuePair pair = new NameValuePair(key, value + "");

        if(findByKey(key) == null){
            insert(pair);
        }else {
            update(pair);
        }
    }

    public synchronized int getInt(String key, int defaultValue){
        NameValuePair pair = findByKey(key);
        if(pair != null){
            String value = pair.getValue();
            if(Util.isInteger(value)){
                return Integer.valueOf(value);
            }
        }

        return defaultValue;
    }

    public synchronized void setLong(String key, long value){
        NameValuePair pair = new NameValuePair(key, value + "");

        if(findByKey(key) == null){
            insert(pair);
        }else {
            update(pair);
        }
    }

    public synchronized long getLong(String key, long defaultValue){
        NameValuePair pair = findByKey(key);
        if(pair != null){
            String value = pair.getValue();
            if(Util.isNumeric(value)){
                return Long.valueOf(value);
            }
        }

        return defaultValue;
    }

    public synchronized void setString(String key, String value){
        NameValuePair pair = new NameValuePair(key, value);

        if(findByKey(key) == null){
            insert(pair);
        }else {
            update(pair);
        }
    }

    public synchronized String getString(String key, String defaultValue){
        NameValuePair pair = findByKey(key);
        if(pair != null){
            return pair.getValue();
        }

        return defaultValue;
    }

    public synchronized void setBoolean(String key, boolean value){
        NameValuePair pair = new NameValuePair(key, value + "");

        if(findByKey(key) == null){
            insert(pair);
        }else {
            update(pair);
        }
    }

    public synchronized boolean getBoolean(String key, boolean defaultValue){
        NameValuePair pair = findByKey(key);
        if(pair != null){
            String value =  pair.getValue();

            if("true".equals(value)){
                return true;
            }else if("false".equals(value)){
                return false;
            }
        }

        return defaultValue;
    }
}
