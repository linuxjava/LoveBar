package xiao.love.bar.component.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by guochang on 2014/7/27.
 */
public class ImageUtils {

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

    // 通过文件头来判断是否gif
    public static boolean isGifByFile(File file) {
        try {
            int length = 10;
            InputStream is = new FileInputStream(file);
            byte[] data = new byte[length];
            is.read(data);
            String type = getType(data);
            is.close();

            if (type.equals("gif")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static String getType(byte[] data) {
        String type = "";
        if (data[1] == 'P' && data[2] == 'N' && data[3] == 'G') {
            type = "png";
        } else if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F') {
            type = "gif";
        } else if (data[6] == 'J' && data[7] == 'F' && data[8] == 'I'
                && data[9] == 'F') {
            type = "jpg";
        }
        return type;
    }

    /**
     * 创建bitmap的倒影
     * @param c
     * @param resId
     * @return
     */
    public static Bitmap createReflectedBitmapById(Context c, int resId) {
        Bitmap bitmap = decodeResource(c.getResources(), resId);
        Bitmap reflectedBitmap = createReflectedBitmap(c, bitmap);

        return reflectedBitmap;
    }

    public static Bitmap decodeResource(Resources resources, int id) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, id, opts);
        int sampleSize = computeSampleSize(opts, -1, (int) (200 * 1024));
        opts.inJustDecodeBounds = false;
        opts.inDither = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //L.d("sampleSize=" + sampleSize);
        opts.inSampleSize = sampleSize;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 创建bitmap的倒影
     * @param c
     * @param srcBitmap
     * @return
     */
    public static Bitmap createReflectedBitmap(Context c, Bitmap srcBitmap) {
        if (null == srcBitmap) {
            return null;
        }

        // The gap between the reflection bitmap and original bitmap.
        final int REFLECTION_GAP = (int) (15 * c.getResources().getDisplayMetrics().density);

        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        int reflectionWidth = srcBitmap.getWidth();
        int reflectionHeight = srcBitmap.getHeight() / 2;

        if (0 == srcWidth || srcHeight == 0) {
            return null;
        }

        // The matrix
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        try {
            // The reflection bitmap, width is same with original's, height is half of original's.
            Bitmap reflectionBitmap = Bitmap.createBitmap(
                    srcBitmap,
                    0,
                    reflectionHeight,
                    srcWidth,
                    reflectionHeight,
                    matrix,
                    false);

            if (null == reflectionBitmap) {
                return null;
            }

            // Create the bitmap which contains original and reflection bitmap.
            Bitmap bitmapWithReflection = Bitmap.createBitmap(
                    reflectionWidth,
                    srcHeight + reflectionHeight + REFLECTION_GAP,
                    Bitmap.Config.ARGB_8888);

            if (null == bitmapWithReflection) {
                return null;
            }

            // Prepare the canvas to draw stuff.
            Canvas canvas = new Canvas(bitmapWithReflection);

            // Draw the original bitmap.
            canvas.drawBitmap(srcBitmap, 0, 0, null);

            // Draw the reflection bitmap.
            canvas.drawBitmap(reflectionBitmap, 0, srcHeight + REFLECTION_GAP, null);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            LinearGradient shader = new LinearGradient(
                    0,
                    srcHeight,
                    0,
                    bitmapWithReflection.getHeight() + REFLECTION_GAP,
                    0x70FFFFFF,
                    0x00FFFFFF,
                    Shader.TileMode.MIRROR);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));

            // Draw the linear shader.
            canvas.drawRect(
                    0,
                    srcHeight,
                    srcWidth,
                    bitmapWithReflection.getHeight() + REFLECTION_GAP,
                    paint);

            return bitmapWithReflection;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
