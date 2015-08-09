package xiao.love.bar.component.net;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by guochang on 2015/8/8.
 * HTTP������
 */
public class HttpTool {
    //��������
    public enum RequestType {
        Get, Post, Put, Delete
    }
    private AsyncHttpClient httpClient;
    private NetworkCallback callback;
    //����ص�
    private JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler(){
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

        }
    };

    public HttpTool(Context context, NetworkCallback callback) {
        this.callback = callback;
        httpClient = new AsyncHttpClient();
    }

    public void loadData(RequestType type, String url, RequestParams params, final String tag, final int dataPos, final Object data){
        switch (type) {
            case Get:
                httpClient.get(url, jsonHttpResponseHandler);
                break;
            case Post:
                httpClient.post(url, params, jsonHttpResponseHandler);
                break;
            case Put:
                httpClient.put(url, params, jsonHttpResponseHandler);
                break;
            case Delete:
                httpClient.delete(url, jsonHttpResponseHandler);
                break;
        }
    }
}
