package xiao.love.bar.memory.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import xiao.love.bar.memory.db.base.DatabaseHelper;
import xiao.love.bar.memory.db.model.BlackList;
import xiao.love.bar.memory.db.model.Contact;

/**
 * Created by guochang on 2015/9/10.
 */
public class BlackListDB {
    private Context mContext;
    private static BlackListDB mInstance;
    private Dao<BlackList, Integer> mDao = null;

    private BlackListDB(){

    }

    private BlackListDB(Context context){
        mContext = context;
        mDao = DatabaseHelper.getInstance(context).getDao(BlackList.class);
    }

    public synchronized static BlackListDB getInstance(Context context){
        if(mInstance == null){
            mInstance = new BlackListDB(context);
        }

        return mInstance;
    }

    public synchronized void createOrUpdate(BlackList blackContact){
        try {
            mDao.createOrUpdate(blackContact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void insert(final List<BlackList> list){
        try {
            TransactionManager.callInTransaction(DatabaseHelper.getInstance(mContext).getConnectionSource(),
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            for (BlackList blackContact : list) {
                                mDao.createOrUpdate(blackContact);
                            }

                            return null;
                        }
                    });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void delete(BlackList blackContact){
        try {
            mDao.delete(blackContact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void delete(List<BlackList> list){
        try {
            mDao.delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过用户名查找
     * @param username
     * @return
     */
    public synchronized BlackList queryById(String username){
        BlackList blackList = null;

        try {
            blackList = mDao.queryForSameId(new BlackList(username));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return blackList;
    }

    /**
     * 是否包含该用户
     * @param username
     * @return
     */
    public synchronized boolean containId(String username){
        return queryById(username) == null ? false : true;
    }

    public List<BlackList> getAll(){
        try {
            return mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
