package xiao.love.bar.memory.db;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import xiao.love.bar.memory.db.dao.BlackListDB;
import xiao.love.bar.memory.db.dao.ContactDB;
import xiao.love.bar.memory.db.model.BlackList;
import xiao.love.bar.memory.db.model.Contact;

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
