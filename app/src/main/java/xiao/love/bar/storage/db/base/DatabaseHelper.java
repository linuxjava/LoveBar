package xiao.love.bar.storage.db.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import xiao.love.bar.storage.db.model.BlackList;
import xiao.love.bar.storage.db.model.City;
import xiao.love.bar.storage.db.model.Contact;
import xiao.love.bar.storage.db.model.NameValuePair;
import xiao.love.bar.storage.db.model.Province;
import xiao.love.bar.storage.db.model.Zone;

/**
 * Created by guochang on 2015/8/9.
 * ormlite数据库工具类
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static DatabaseHelper mInstance;
    private static final String DATABASE_NAME = "lovebar.db";
    private static final int DATABASE_VERSION = 1;
    private Map<String, Dao> daos = new HashMap<String, Dao>();

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        context = context.getApplicationContext();
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }

        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            //创建键值对表
            TableUtils.createTable(connectionSource, NameValuePair.class);
            //创建联系人表
            TableUtils.createTable(connectionSource, Contact.class);
            //黑名单
            TableUtils.createTable(connectionSource, BlackList.class);
            //省
            TableUtils.createTable(connectionSource, Province.class);
            //市
            TableUtils.createTable(connectionSource, City.class);
            //区
            TableUtils.createTable(connectionSource, Zone.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, NameValuePair.class, true);
            TableUtils.dropTable(connectionSource, Contact.class, true);
            TableUtils.dropTable(connectionSource, BlackList.class, true);
            TableUtils.dropTable(connectionSource, Province.class, true);
            TableUtils.dropTable(connectionSource, City.class, true);
            TableUtils.dropTable(connectionSource, Zone.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Dao getDao(Class clazz) {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }

        if (dao == null) {
            try {
                dao = super.getDao(clazz);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            daos.put(className, dao);
        }

        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close()
    {
        super.close();
        for (String key : daos.keySet())
        {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
