package xiao.love.bar.component.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;

import com.easemob.util.PathUtil;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;

import java.io.File;

import xiao.love.bar.R;
import xiao.love.bar.component.photopick.PhotoPickActivity;
import xiao.love.bar.component.resource.ResTool;
import xiao.love.bar.component.util.SDCardUtils;
import xiao.love.bar.component.util.T;

/**
 * Created by xiaoguochang on 2015/12/26.
 * 图片选择管理类
 */
public class PhotoPickerManager implements OnOperItemClickL {
    private String[] items = {"拍照", "从相册中选取"};
    private static final int MAX_PHOTO_NUM = 6;
    //照相机
    public static final int REQUEST_CODE_CAMERA = 1000;
    //本地相册
    public static final int REQUEST_CODE_GALLERY = 1001;
    private static PhotoPickerManager sInstance;
    private Activity mContext;
    private ActionSheetDialog mDialog;
    //拍照文件
    private File mLocalPicture;
    private int mPhotoNum = MAX_PHOTO_NUM;

    public static synchronized PhotoPickerManager getInstance(Activity context) {
        if (sInstance == null) {
            sInstance = new PhotoPickerManager(context);
        }

        return sInstance;
    }

    private PhotoPickerManager() {

    }

    private PhotoPickerManager(Activity context) {
        mContext = context;
        mDialog = new ActionSheetDialog(mContext, items, null);
        mDialog.isTitleShow(false).layoutAnimation(null).setOnOperItemClickL(this);
    }

    public void setPhotoNum(int mPhotoNum) {
        this.mPhotoNum = mPhotoNum;
    }

    public void showPickerDialog() {
        mDialog.show();
    }

    @Override
    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {//拍照
            takePicture();
        } else if (position == 1) {//从相册中选取
            startGallery();
        }
        mDialog.dismiss();
    }

    /**
     * 得到拍照后本地文件
     *
     * @return
     */
    public File getLocalPicture() {
        return mLocalPicture;
    }

    /**
     * 拍照
     */
    public void takePicture() {
        if (!SDCardUtils.isSDCardEnable()) {
            String st = ResTool.getString(mContext, R.string.toast_sdcard_not_exist);
            T.showShort(mContext, st);
            return;
        }

        mLocalPicture = new File(PathUtil.getInstance().getImagePath(), System.currentTimeMillis() + ".jpg");

        mLocalPicture.getParentFile().mkdirs();
        mContext.startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).
                        putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mLocalPicture)),
                REQUEST_CODE_CAMERA);

    }

    /**
     * 启动相册
     */
    public void startGallery() {
        Intent intent = new Intent(mContext, PhotoPickActivity.class);
        intent.putExtra(PhotoPickActivity.EXTRA_MAX, mPhotoNum);
        mContext.startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }
}
