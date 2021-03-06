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

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.SaveListener;
import xiao.love.bar.component.util.FileUtils;
import xiao.love.bar.component.util.L;
import xiao.love.bar.entity.BmobProvince;
import xiao.love.bar.storage.db.base.DatabaseHelper;
import xiao.love.bar.storage.db.model.BlackList;
import xiao.love.bar.storage.db.model.Province;

/**
 * Created by xiaoguochang on 2015/12/27.
 */
public class ProvinceDB {
    private Context mContext;
    private static ProvinceDB mInstance;
    private Dao<Province, Integer> mDao = null;

    private ProvinceDB(){

    }

    private ProvinceDB(Context context){
        mContext = context;
        mDao = DatabaseHelper.getInstance(context).getDao(Province.class);
    }

    public synchronized static ProvinceDB getInstance(Context context){
        if(mInstance == null){
            mInstance = new ProvinceDB(context);
        }

        return mInstance;
    }

    public synchronized void createOrUpdate(Province province){
        try {
            mDao.createOrUpdate(province);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void insert(final List<Province> list){
        try {
            TransactionManager.callInTransaction(DatabaseHelper.getInstance(mContext).getConnectionSource(),
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            for (Province province : list) {
                                mDao.createOrUpdate(province);
                            }

                            return null;
                        }
                    });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Province queryById(int provinceID){
        try {
            return mDao.queryForId(provinceID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized List<Province> queryForAll(){
        try {
            return mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 初始化省数据
     */
    public synchronized void initData(){
        String str = FileUtils.getJson(mContext, "province.json");
        try {
            JSONArray array = new JSONObject(str).getJSONArray("RECORDS");
            List<Province> list = new ArrayList<>();
            for (int i=0; i<array.length(); i++){
                JSONObject item = (JSONObject) array.get(i);
                list.add(new Province(item.getInt("ProSort"), item.getString("ProName")));
            }
            insert(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化bmob后台省数据库
     */
    public synchronized void initBmob(){
        List<Province> list = queryForAll();
        List<BmobObject> bmobList = new ArrayList<>();

        for (Province province : list){
            BmobProvince bmobProvince = new BmobProvince();
            bmobProvince.setProvinceID(province.getProvinceID());
            bmobProvince.setProvince(province.getProvince());
            bmobList.add(bmobProvince);
        }

        for (int i=0; i<bmobList.size(); i=i+50) {
            List<BmobObject> subList;
            if(i + 50 >= bmobList.size()) {
                subList = bmobList.subList(i, bmobList.size());
            }else {
                subList = bmobList.subList(i, i + 50);
            }

            new BmobObject().insertBatch(mContext, subList, new SaveListener() {
                @Override
                public void onSuccess() {
                    L.d("Province批量添加成功");
                }

                @Override
                public void onFailure(int code, String msg) {
                    L.d("Province批量添加失败:");
                }
            });
        }
    }
}
