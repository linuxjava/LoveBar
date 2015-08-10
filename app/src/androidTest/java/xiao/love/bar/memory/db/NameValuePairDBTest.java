package xiao.love.bar.memory.db;

import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.util.Log;

import junit.framework.TestCase;

import xiao.love.bar.memory.db.dao.NameValuePairDB;
import xiao.love.bar.memory.db.model.NameValuePair;

/**
 * Created by guochang on 2015/8/10.
 * NameValuePairDB单元测试
 */
public class NameValuePairDBTest extends AndroidTestCase {
    public void testSet(){
        NameValuePairDB nameValuePairDB = NameValuePairDB.getInstance(getContext());
        nameValuePairDB.setInt("int1", 45);
        nameValuePairDB.setLong("long1", 1000000000000L);
        nameValuePairDB.setString("string1", "test2112312");
        nameValuePairDB.setBoolean("bool", true);
    }

    public void testGet(){
        NameValuePairDB nameValuePairDB = NameValuePairDB.getInstance(getContext());
        int pair1 = nameValuePairDB.getInt("int1", -1);
        long pair2 = nameValuePairDB.getLong("long1", -1L);
        String pair3 = nameValuePairDB.getString("string1", "");
        boolean pair4 = nameValuePairDB.getBoolean("bool", false);

        Log.d("xiao1", pair1 + ":" + pair2 + ":" + pair3 + ":" + pair4);
    }
}
