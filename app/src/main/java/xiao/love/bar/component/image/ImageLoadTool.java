package xiao.love.bar.component.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
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
    private static final int LOADING_IMAGE = R.drawable.ic_launcher;
    private static final int EMPTY_IMAGE = R.drawable.ic_launcher;
    private static final int FAIL_IMAGE = R.drawable.ic_launcher;
    private Context context;
    private static ImageLoadTool imageLoadTool;
    private ImageLoader imageLoader;
    public DisplayImageOptions options;
    public DisplayImageOptions optionsRound;
    //从相册选择图片时使用
    public DisplayImageOptions optionsPhotoPick;

    private ImageLoadTool(){

    }

    public synchronized static ImageLoadTool getInstance(){
        if(imageLoadTool == null){
            imageLoadTool = new ImageLoadTool();
        }

        return imageLoadTool;
    }

    public void init(Context context){
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        options = new DisplayImageOptions
                .Builder()
                .showImageOnLoading(LOADING_IMAGE)
                .showImageForEmptyUri(EMPTY_IMAGE)
                .showImageOnFail(FAIL_IMAGE)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        optionsPhotoPick = new DisplayImageOptions
                .Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        optionsRound = new DisplayImageOptions.Builder()
                .showImageOnLoading(LOADING_IMAGE)
                .showImageForEmptyUri(EMPTY_IMAGE)
                .showImageOnFail(FAIL_IMAGE)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(DensityUtils.dp2px(context, IMAGE_ROUND_DP)))
                .build();
    }

    public DiskCache getDiskCache(){
        return imageLoader.getDiskCache();
    }

    public void loadImage(String url, SimpleImageLoadingListener listener){
        imageLoader.loadImage(url, listener);
    }

    public void displayImage(ImageView imageView, String url, DisplayImageOptions displayImageOptions, SimpleImageLoadingListener listener) {
        imageLoader.displayImage(url, imageView, displayImageOptions, listener);

    }

    public void displayImageFromUrl(ImageView imageView, String url) {
        imageLoader.displayImage(url, imageView, options);
    }

    public void displayImageFromUrl(ImageView imageView, String url, DisplayImageOptions displayImageOptions) {
        imageLoader.displayImage(url, imageView, displayImageOptions);
    }
}
