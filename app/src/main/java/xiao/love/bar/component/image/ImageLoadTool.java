package xiao.love.bar.component.image;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import xiao.love.bar.R;
import xiao.love.bar.component.util.DensityUtils;

/**
 * Created by guochang on 2015/8/7.
 * 图片加载工具类
 */
public class ImageLoadTool {
    //图片圆角弧度
    private static final int IMAGE_ROUND_DP = 3;
    private static final int LOADING_IMAGE = R.mipmap.ic_launcher;
    private static final int EMPTY_IMAGE = R.mipmap.ic_launcher;
    private static final int FAIL_IMAGE = R.mipmap.ic_launcher;
    private Context context;
    private ImageLoader imageLoader;


    public ImageLoadTool(Context context) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
    }

    public DisplayImageOptions options = new DisplayImageOptions
            .Builder()
            .showImageOnLoading(LOADING_IMAGE)
            .showImageForEmptyUri(EMPTY_IMAGE)
            .showImageOnFail(FAIL_IMAGE)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

    public final DisplayImageOptions optionsRound = new DisplayImageOptions.Builder()
            .showImageOnLoading(LOADING_IMAGE)
            .showImageForEmptyUri(EMPTY_IMAGE)
            .showImageOnFail(FAIL_IMAGE)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .displayer(new RoundedBitmapDisplayer(DensityUtils.dp2px(context, IMAGE_ROUND_DP)))
            .build();

    public void loadImage(ImageView imageView, String url, DisplayImageOptions displayImageOptions, SimpleImageLoadingListener listener) {
        imageLoader.displayImage(url, imageView, displayImageOptions, listener);
    }

    public void loadImageFromUrl(ImageView imageView, String url) {
        imageLoader.displayImage(url, imageView, options);
    }

    public void loadImageFromUrl(ImageView imageView, String url, DisplayImageOptions displayImageOptions) {
        imageLoader.displayImage(url, imageView, displayImageOptions);
    }
}
