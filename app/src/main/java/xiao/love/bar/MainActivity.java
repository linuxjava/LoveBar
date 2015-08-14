package xiao.love.bar;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import xiao.love.bar.component.BaseActivity;
import xiao.love.bar.component.dialog.DialogTool;
import xiao.love.bar.component.photopick.PhotoPickActivity;
import xiao.love.bar.component.photopick.test.Test;
import xiao.love.bar.memory.db.dao.NameValuePairDB;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Test.test(this);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

    }

    @Override
    public void getNetwork(String uri, String tag) {

    }
}
