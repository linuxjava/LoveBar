package xiao.love.bar.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by xiaoguochang on 2015/12/3.
 */
public abstract class BasePresenter {
    protected Context mContext;

    public void attach(Context context) {
        mContext = context;
    }

    /**
     * 在Fragment、Activity的onDestroy中调用.用于注销广播接收器等
     */
    public void detach() {
        mContext = null;
    }

    protected boolean isActivityAlive() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            return !activity.isFinishing();
        }
        return false;
    }
}
