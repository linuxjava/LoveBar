package xiao.love.bar.component.toast;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by guochang on 2015/8/9.
 */
public class ToastTool {
    public static void showButtomToast(Context context, int messageId) {
        String message = context.getString(messageId);
        showButtomToast(context, message);
    }

    public static void showButtomToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showMiddleToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
