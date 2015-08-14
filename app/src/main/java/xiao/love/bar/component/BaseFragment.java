package xiao.love.bar.component;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import xiao.love.bar.component.dialog.DialogTool;
import xiao.love.bar.component.image.ImageLoadTool;
import xiao.love.bar.component.net.HttpTool;
import xiao.love.bar.component.net.NetworkCallback;
import xiao.love.bar.component.toast.ToastTool;

/**
 * Created by guochang on 2015/8/12.
 */
public abstract class BaseFragment extends Fragment implements NetworkCallback{
    //��ʾ�Ի����loading������
    protected DialogTool mDialogTool;
    //toast������
    protected ToastTool mToastTool;
    //ͼƬ���ع�����
    protected ImageLoadTool mImageLoadTool;
    //http������
    protected HttpTool mHttpTool;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponent();
    }

    private void initComponent(){
        mImageLoadTool = ImageLoadTool.getInstance();
        mHttpTool = new HttpTool(getActivity(),this);
    }
}
