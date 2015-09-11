package xiao.love.bar;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import xiao.love.bar.component.BaseActivity;
import xiao.love.bar.component.photopick.test.PhotoPickTest;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PhotoPickTest.test(this);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

    }

    @Override
    public void getNetwork(String uri, String tag) {

    }
}
