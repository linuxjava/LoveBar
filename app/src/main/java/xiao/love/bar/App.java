package xiao.love.bar;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import xiao.love.bar.component.CommonInfo;
import xiao.love.bar.component.image.ImageLoadTool;

/**
 * Created by guochang on 2015/8/9.
 */
public class App extends Application{
    public static App sAppInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init(){
        sAppInstance = this;
        //初始化通用信息
        CommonInfo.getInstance().init(this);
        //初始化ImageLoad工具类
        ImageLoadTool.getInstance().init(this);
    }
}
