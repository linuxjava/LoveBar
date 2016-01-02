package xiao.love.bar.im.util;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import xiao.love.bar.R;

/**
 * Created by guochang on 2015/9/14.
 */
public class UserUtils {
    /**
     * 设置聊天对象的头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_love_default_avatar);
    }

    /**
     * 设置当前用户头像
     */
    public static void setCurrentUserAvatar(Context context, ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_love_default_avatar);
    }

    /**
     * 设置聊天对象的昵称
     */
    public static void setUserNick(String username, TextView textView) {
        textView.setText("聊天对象昵称");
    }

    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView) {
        if(textView != null) {
            textView.setText("自己的昵称");
        }
    }

    public static String getUserName() {
        return "xiao";
    }
}
