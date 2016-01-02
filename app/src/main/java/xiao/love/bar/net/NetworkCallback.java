package xiao.love.bar.net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guochang on 2015/8/8.
 * 网络访问回调
 */
public interface NetworkCallback {
    void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException;
    void getNetwork(String uri, String tag);
}
