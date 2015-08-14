package xiao.love.bar;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import xiao.love.bar.component.image.ImageLoadTool;

/**
 * Created by guochang on 2015/8/9.
 */
public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoadTool.getInstance().init(this);
    }
}
