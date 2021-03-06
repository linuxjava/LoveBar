package xiao.love.bar.storage.db;

import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.List;

import xiao.love.bar.storage.db.dao.ContactDB;
import xiao.love.bar.storage.db.model.Contact;

/**
 * Created by guochang on 2015/8/10.
 * NameValuePairDB单元测试
 */
public class ContactDBTest extends AndroidTestCase {
    public void testCreateOrUpdate(){
        Contact contact = new Contact("123", "4576");
        ContactDB.getInstance(getContext()).createOrUpdate(contact);
    }

    public void testInsert(){
        List<Contact> list = new ArrayList<>();
        Contact contact = new Contact("test1", "xiao1");
        list.add(contact);
        contact = new Contact("test2", "xiao2");
        list.add(contact);
        ContactDB.getInstance(getContext()).insert(list);
    }
}
