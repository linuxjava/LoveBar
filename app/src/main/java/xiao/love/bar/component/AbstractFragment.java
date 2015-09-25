package xiao.love.bar.component;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import xiao.love.bar.component.dialog.DialogTool;
import xiao.love.bar.component.image.ImageLoadTool;
import xiao.love.bar.component.net.HttpTool;
import xiao.love.bar.component.net.NetworkCallback;
import xiao.love.bar.component.toast.ToastTool;

/**
 * Created by guochang on 2015/8/12.
 */
public abstract class AbstractFragment extends Fragment implements NetworkCallback{
    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

    }

    @Override
    public void getNetwork(String uri, String tag) {

    }
}
