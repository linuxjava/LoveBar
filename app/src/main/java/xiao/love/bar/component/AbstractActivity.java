package xiao.love.bar.component;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import xiao.love.bar.net.NetworkCallback;

/**
 * Created by guochang on 2015/8/13.
 */
public abstract class AbstractActivity extends FragmentActivity implements NetworkCallback {
    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

    }

    @Override
    public void getNetwork(String uri, String tag) {

    }
}
