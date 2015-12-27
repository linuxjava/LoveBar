package xiao.love.bar.storage.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import xiao.love.bar.component.util.FileUtils;
import xiao.love.bar.storage.db.base.DatabaseHelper;
import xiao.love.bar.storage.db.model.City;
import xiao.love.bar.storage.db.model.Zone;

/**
 * Created by xiaoguochang on 2015/12/27.
 */
public class ZoneDB {
    private Context mContext;
    private static ZoneDB mInstance;
    private Dao<Zone, Integer> mDao = null;

    private ZoneDB() {

    }

    private ZoneDB(Context context) {
        mContext = context;
        mDao = DatabaseHelper.getInstance(context).getDao(Zone.class);
    }

    public synchronized static ZoneDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ZoneDB(context);
        }

        return mInstance;
    }

    public synchronized void createOrUpdate(Zone zone) {
        try {
            mDao.createOrUpdate(zone);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void insert(final List<Zone> list){
        try {
            TransactionManager.callInTransaction(DatabaseHelper.getInstance(mContext).getConnectionSource(),
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            for (Zone zone : list) {
                                mDao.createOrUpdate(zone);
                            }

                            return null;
                        }
                    });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Zone queryById(int zoneID) {
        try {
            return mDao.queryForId(zoneID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 初始化区/县数据
     */
    public synchronized void initData(){
        String str = FileUtils.getJson(mContext, "zone.json");
        try {
            JSONArray array = new JSONObject(str).getJSONArray("RECORDS");
            List<Zone> list = new ArrayList<>();
            for (int i=0; i<array.length(); i++){
                JSONObject item = (JSONObject) array.get(i);
                list.add(new Zone(item.getInt("ZoneID"), item.getString("ZoneName"), item.getInt("CityID")));
            }
            insert(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
