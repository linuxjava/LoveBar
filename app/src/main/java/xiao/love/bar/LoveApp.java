package xiao.love.bar;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.Bmob;
import xiao.love.bar.component.imageloader.ImageLoadTool;
import xiao.love.bar.config.Config;
import xiao.love.bar.im.hxlib.IMHelper;
import xiao.love.bar.storage.db.dao.CityDB;
import xiao.love.bar.storage.db.dao.ProvinceDB;
import xiao.love.bar.storage.db.dao.ZoneDB;

/**
 * Created by guochang on 2015/8/9.
 */
public class LoveApp extends Application{
    private Context mContext;
    public static LoveApp sAppInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init(){
        sAppInstance = this;
        mContext = this;
        //初始化ImageLoad工具类（工具类其次）
        ImageLoadTool.getInstance().init(mContext);
        //im初始化
        new IMHelper().onInit(mContext);
        Bmob.initialize(mContext, Config.BMOB_APP_ID);
        initDB();
    }

    /**
     * 初始化DB
     */
    private void initDB(){
        //ProvinceDB.getInstance(mContext).initBmob();
        //CityDB.getInstance(mContext).initBmob();
        //ZoneDB.getInstance(mContext).initBmob();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ProvinceDB.getInstance(mContext).initData();
                CityDB.getInstance(mContext).initData();
                ZoneDB.getInstance(mContext).initData();
            }
        }).start();
    }
}
