package xiao.love.bar.component;

import android.os.Bundle;

import xiao.love.bar.component.imageloader.ImageLoadTool;
import xiao.love.bar.net.HttpTool;

/**
 * Created by guochang on 2015/9/23.
 */
public class BaseActivity extends AbstractActivity {
    //图片加载工具类
    protected ImageLoadTool mImageLoadTool;
    //http工具类
    protected HttpTool mHttpTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponent();
    }

    private void initComponent() {
        mImageLoadTool = ImageLoadTool.getInstance();
        mHttpTool = new HttpTool(this, this);
    }

}
