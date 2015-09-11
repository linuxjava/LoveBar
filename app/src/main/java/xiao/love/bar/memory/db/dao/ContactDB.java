package xiao.love.bar.memory.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import xiao.love.bar.memory.db.base.DatabaseHelper;
import xiao.love.bar.memory.db.model.Contact;

/**
 * Created by guochang on 2015/9/10.
 */
public class ContactDB {
    private Context mContext;
    private static ContactDB mInstance;
    private Dao<Contact, Integer> mDao = null;

    private ContactDB(){

    }

    private ContactDB(Context context){
        mContext = context;
        mDao = DatabaseHelper.getInstance(context).getDao(Contact.class);
    }

    public synchronized static ContactDB getInstance(Context context){
        if(mInstance == null){
            mInstance = new ContactDB(context);
        }

        return mInstance;
    }

    public synchronized void createOrUpdate(Contact contact){
        try {
            mDao.createOrUpdate(contact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void insert(final List<Contact> list){
        try {
            TransactionManager.callInTransaction(DatabaseHelper.getInstance(mContext).getConnectionSource(),
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            for (Contact c : list) {
                                mDao.createOrUpdate(c);
                            }

                            return null;
                        }
                    });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void delete(Contact contact){
        try {
            mDao.delete(contact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void delete(List<Contact> list){
        try {
            mDao.delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<Contact> getAll(){
        try {
            return mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
