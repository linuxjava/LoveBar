package xiao.love.bar;

import android.app.Application;

import xiao.love.bar.component.image.ImageLoadTool;
import xiao.love.bar.im.hxlib.IMHelper;

/**
 * Created by guochang on 2015/8/9.
 */
public class LoveApp extends Application{
    public static LoveApp sAppInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init(){
        sAppInstance = this;
        //初始化ImageLoad工具类（工具类其次）
        ImageLoadTool.getInstance().init(this);
        //im初始化
        new IMHelper().onInit(this);
    }
}
