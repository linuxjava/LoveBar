package xiao.love.bar.component;

import android.os.Bundle;

import com.gitonway.lee.niftymodaldialogeffects.lib.DialogTool;

import xiao.love.bar.component.image.ImageLoadTool;
import xiao.love.bar.component.net.HttpTool;
import xiao.love.bar.component.toast.ToastTool;

/**
 * Created by guochang on 2015/9/23.
 */
public class BaseFragment extends AbstractFragment {
    //提示对话框和loading工具类
    protected DialogTool mDialogTool;
    //toast工具类
    public ToastTool mToastTool;
    //图片加载工具类
    protected ImageLoadTool mImageLoadTool;
    //http工具类
    protected HttpTool mHttpTool;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponent();

    }

    private void initComponent() {
        mImageLoadTool = ImageLoadTool.getInstance();
        mHttpTool = new HttpTool(getActivity(), this);
    }

}
