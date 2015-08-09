package xiao.love.bar.component.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by guochang on 2014/7/27.
 */
public class ImageUtil {

    /**
     * 从资源id获得图片的Drawable
     * @param c
     * @param id
     * @return
     */
    public static Drawable getDrawableFromResourceId(Context c, int id) {
        return bitmapToDrawable(getBitmapFromResourceId(c, id));
    }

    /**
     * 从资源id获得图片的Bitmap
     * @param c
     * @param id
     * @return
     */
    public static Bitmap getBitmapFromResourceId(Context c, int id) {
        return BitmapFactory.decodeResource(c.getResources(), id);
    }

    /**
     * 将Bitmap转化为Drawable
     * @param bm
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bm) {
        BitmapDrawable bd= new BitmapDrawable(bm);
        return bd;
    }

    /**
     * 将Drawable转化为Bitmap
     * @param bm
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable bm) {
        BitmapDrawable bd= (BitmapDrawable) bm;
        return bd.getBitmap();
    }

//    /**
//     * bitmap转字节
//     * @param bm
//     * @return
//     */
//    public static byte[] Bitmap2Bytes(Bitmap bm){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        return baos.toByteArray();
//    }

    /**
     * 将手机中的文件转换为Bitmap类型
     * @param path
     * @return
     */
    public static Bitmap getBitmapFromFile(String path) {
        try {
            return BitmapFactory.decodeFile(path);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 读取bitmap中字节数组
     * @param bitmap
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bitmap){
        if(bitmap == null)
            return null;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    /**
     * 读取图片文件中字节数组
     * @param path
     * @return
     */
    public static byte[] getBytesFromFile(String path){
        Bitmap bitmap = getBitmapFromFile(path);
        return Bitmap2Bytes(bitmap);
    }
}
