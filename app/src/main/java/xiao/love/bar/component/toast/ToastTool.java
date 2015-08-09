package xiao.love.bar.component.toast;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by guochang on 2015/8/9.
 */
public class ToastTool {
    public void showButtomToast(Context context, int messageId) {
        String message = context.getString(messageId);
        showButtomToast(context, message);
    }

    public void showButtomToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showMiddleToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
