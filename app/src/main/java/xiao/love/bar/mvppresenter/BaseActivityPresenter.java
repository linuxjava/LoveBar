package xiao.love.bar.mvppresenter;

import android.os.Bundle;

/**
 * Created by xiaoguochang on 2015/12/3.
 */
public interface BaseActivityPresenter {
    public void onCreate(Bundle bundle);

    public void onResume();

    public void onDestroy();
}
