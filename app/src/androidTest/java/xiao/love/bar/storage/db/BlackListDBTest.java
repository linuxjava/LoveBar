package xiao.love.bar.storage.db;

import android.test.AndroidTestCase;
import android.util.Log;

import xiao.love.bar.storage.db.dao.BlackListDB;
import xiao.love.bar.storage.db.model.BlackList;

/**
 * Created by guochang on 2015/8/10.
 */
public class BlackListDBTest extends AndroidTestCase {
    public void testCreateOrUpdate(){
        BlackList blackList = new BlackList("123");
        BlackListDB.getInstance(getContext()).createOrUpdate(blackList);
    }

    public void testContainId(){
        boolean flag = BlackListDB.getInstance(getContext()).containId("123");
        Log.d("xiao1", "" + flag);
    }
}
