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
import xiao.love.bar.storage.db.model.Province;

/**
 * Created by xiaoguochang on 2015/12/27.
 */
public class CityDB {
    private Context mContext;
    private static CityDB mInstance;
    private Dao<City, Integer> mDao = null;

    private CityDB() {

    }

    private CityDB(Context context) {
        mContext = context;
        mDao = DatabaseHelper.getInstance(context).getDao(City.class);
    }

    public synchronized static CityDB getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CityDB(context);
        }

        return mInstance;
    }

    public synchronized void createOrUpdate(City city) {
        try {
            mDao.createOrUpdate(city);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void insert(final List<City> list){
        try {
            TransactionManager.callInTransaction(DatabaseHelper.getInstance(mContext).getConnectionSource(),
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            for (City city : list) {
                                mDao.createOrUpdate(city);
                            }

                            return null;
                        }
                    });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized City queryById(int cityID){
        try {
            return mDao.queryForId(cityID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized List<City> queryByProvinceID(int provinceID) {
        try {
            return mDao.queryBuilder().where().eq("provinceID", provinceID).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized List<City> queryForAll(){
        try {
            return mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 初始化市数据
     */
    public synchronized void initData(){
        String str = FileUtils.getJson(mContext, "city.json");
        try {
            JSONArray array = new JSONObject(str).getJSONArray("RECORDS");
            List<City> list = new ArrayList<>();
            for (int i=0; i<array.length(); i++){
                JSONObject item = (JSONObject) array.get(i);
                list.add(new City(item.getInt("CitySort"), item.getString("CityName"), item.getInt("ProID")));
            }
            insert(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
