package xiao.love.bar.component.photopick;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoView;
import xiao.love.bar.R;
import xiao.love.bar.component.image.ImageLoadTool;
import xiao.love.bar.component.util.ImageUtils;

/**
 * Created by guochang on 2015/8/11.
 */
public class ImagePagerFragment extends Fragment{
    private DonutProgress donutProgress;
    private ImageView loadFailImage;
    private GifImageView gifImageView;
    private PhotoView photoView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image_pager_item, null);

        donutProgress = (DonutProgress) view.findViewById(R.id.circleLoading);
        loadFailImage = (ImageView) view.findViewById(R.id.fail_image);
        gifImageView = (GifImageView) view.findViewById(R.id.gifImageView);
        photoView = (PhotoView) view.findViewById(R.id.photoView);

        showPhoto();

        return view;
    }

    private void showPhoto(){
        if (!isAdded()) {
            return;
        }

        final String uri = getArguments().getString("uri");
        if(TextUtils.isEmpty(uri)){
            donutProgress.setVisibility(View.INVISIBLE);
            loadFailImage.setVisibility(View.VISIBLE);
            return;
        }

        ImageLoadTool.getInstance().loadImage(uri, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if (!isAdded()) {
                    return;
                }

                donutProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (!isAdded()) {
                    return;
                }

                donutProgress.setVisibility(View.INVISIBLE);
                loadFailImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (!isAdded()) {
                    return;
                }

                File file;
                if (ImageInfo.isLocalFile(uri)) {
                    file = ImageInfo.getLocalFile(uri);
                } else {
                    file = ImageLoadTool.getInstance().getDiskCache().get(imageUri);
                }

                donutProgress.setVisibility(View.INVISIBLE);
                if (ImageUtils.isGifByFile(file)) {
                    Uri uri1 = Uri.fromFile(file);
                    gifImageView.setImageURI(uri1);
                    gifImageView.setVisibility(View.VISIBLE);
                }else {
                    photoView.setImageBitmap(loadedImage);
                    photoView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
