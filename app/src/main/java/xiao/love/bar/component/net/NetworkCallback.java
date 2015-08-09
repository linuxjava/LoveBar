package xiao.love.bar.component.net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guochang on 2015/8/8.
 * ÍøÂç·ÃÎÊ»Øµ÷
 */
public interface NetworkCallback {
    void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException;
    void getNetwork(String uri, String tag);
}
