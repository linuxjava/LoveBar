package xiao.love.bar.presenter;


/**
 * Created by xiaoguochang on 2015/12/3.
 */
public abstract class BaseFragmentPresenter<T> extends BasePresenter{
    /**
     * 刷新数据
     */
    public void refreshData(){

    }

    /**
     * 加载更多数据
     */
    public void loadMoreData() {
    }

    /**
     * 从数据库中加载数据
     */
    public void loadDataFromDB() {

    }

    /**
     * 保存数据到DB
     *
     * @param datas
     */
    protected void saveDataToDB(T datas) {

    }
}
