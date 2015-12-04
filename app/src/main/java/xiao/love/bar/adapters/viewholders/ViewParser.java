package xiao.love.bar.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xiaoguochang on 2015/12/4.
 */
public interface ViewParser {
    public View inflate(Context context, ViewGroup parent, boolean attachToRoot);
}
