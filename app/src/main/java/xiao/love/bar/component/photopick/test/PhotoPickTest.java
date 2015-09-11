package xiao.love.bar.component.photopick.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import xiao.love.bar.component.photopick.PhotoPickActivity;

/**
 * Created by guochang on 2015/8/12.
 */
public class PhotoPickTest {
    public static void test(Activity context){
        Intent intent = new Intent(context, PhotoPickActivity.class);
        intent.putExtra(PhotoPickActivity.EXTRA_MAX, 5);
        context.startActivityForResult(intent, 0);
    }
}
