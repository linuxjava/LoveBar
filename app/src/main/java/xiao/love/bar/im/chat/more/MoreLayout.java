package xiao.love.bar.im.chat.more;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.PathUtil;

import java.io.File;

import xiao.love.bar.R;
import xiao.love.bar.component.util.SDCardUtils;
import xiao.love.bar.im.chat.BaiduMapActivity;
import xiao.love.bar.im.chat.ChatActivity;
import xiao.love.bar.im.util.UserUtils;

/**
 * Created by guochang on 2015/9/21.
 */
public class MoreLayout implements View.OnClickListener {
    private ChatActivity mActivity;
    private LinearLayout mMoreLL;
    private ImageView mTakePictureBtn;
    private ImageView mImageBtn;
    private ImageView mLocationBtn;
    //拍照文件
    private File mTakePicture;
    private EMConversation mConversation;
    private String mChatUserName;

    public MoreLayout(ChatActivity activity, EMConversation conversation) {
        mActivity = activity;
        mConversation = conversation;
        mChatUserName = activity.getToChatUsername();
        mMoreLL = (LinearLayout) activity.findViewById(R.id.ll_btn_container);
        mTakePictureBtn = (ImageView) activity.findViewById(R.id.btn_take_picture);
        mImageBtn = (ImageView) activity.findViewById(R.id.btn_picture);
        mLocationBtn = (ImageView) activity.findViewById(R.id.btn_location);

        mTakePictureBtn.setOnClickListener(this);
        mImageBtn.setOnClickListener(this);
        mLocationBtn.setOnClickListener(this);
    }

    public void showMoreLayout() {
        mMoreLL.setVisibility(View.VISIBLE);
    }

    public void hideMoreLayout() {
        mMoreLL.setVisibility(View.GONE);
    }

    public int getMoreLayoutVisibility(){
        return mMoreLL.getVisibility();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_picture:
                takePicture();
                break;
            case R.id.btn_picture:
                selectImageFromLocal();
                break;
            case R.id.btn_location:
                mActivity.startActivityForResult(new Intent(mActivity, BaiduMapActivity.class),
                        ChatActivity.REQUEST_CODE_MAP);
                break;
        }
    }

    /**
     * 拍照
     */
    private void takePicture() {
        if (!SDCardUtils.isSDCardEnable()) {
            String st = mActivity.getResources().getString(R.string.sd_card_does_not_exist);
            Toast.makeText(mActivity, st, Toast.LENGTH_SHORT).show();
            return;
        }

        mTakePicture = new File(PathUtil.getInstance().getImagePath(), UserUtils.getUserName()
                + System.currentTimeMillis() + ".jpg");

        mTakePicture.getParentFile().mkdirs();
        mActivity.startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTakePicture)),
                ChatActivity.REQUEST_CODE_CAMERA);
    }

    /**
     * 从图库获取图片
     */
    private void selectImageFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        mActivity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_LOCAL);
    }

    /**
     * 发送文字和表情
     * @param content
     */
    public void sendText(String content) {
        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            message.setChatType(EMMessage.ChatType.Chat);
            TextMessageBody txtBody = new TextMessageBody(content);
            message.addBody(txtBody);
            message.setReceipt(mChatUserName);
            mConversation.addMessage(message);

            if(mActivity.getAdapter() != null) {
                mActivity.getAdapter().refreshSelectLast();
            }
        }
    }

    /**
     * 发送图片
     *
     */
    public void sendImage() {
        if (mTakePicture == null || !mTakePicture.exists()) {
            return;
        }

        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        message.setChatType(EMMessage.ChatType.Chat);
        message.setReceipt(mChatUserName);
        ImageMessageBody body = new ImageMessageBody(mTakePicture);
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        //body.setSendOriginalImage(true);
        message.addBody(body);
        mConversation.addMessage(message);
    }

    /**
     * 根据图库图片uri发送图片
     * @param intent
     */
    public void sendImageByUri(Intent intent) {
        if (intent == null && intent.getData() == null) {
            return;
        }

        Uri selectedImage = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        String st8 = mActivity.getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            mTakePicture = new File(picturePath);
            if (!mTakePicture.exists()) {
                Toast toast = Toast.makeText(mActivity, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                sendImage();
            }
        } else {
            mTakePicture = new File(selectedImage.getPath());
            if (!mTakePicture.exists()) {
                Toast toast = Toast.makeText(mActivity, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                sendImage();
            }
        }
    }

    /**
     * 发送点位信息
     * @param intent
     */
    public void sendLocation(Intent intent) {
        if(intent == null){
            return;
        }

        double latitude = intent.getDoubleExtra("latitude", 0);//纬度
        double longitude = intent.getDoubleExtra("longitude", 0);//经度
        String locationAddress = intent.getStringExtra("address");//地址
        if (TextUtils.isEmpty(locationAddress)) {
            String st = mActivity.getResources().getString(R.string.unable_to_get_loaction);
            Toast.makeText(mActivity, st, Toast.LENGTH_LONG).show();
            return;
        }

        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
        LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
        message.addBody(locBody);
        message.setReceipt(mChatUserName);
        mConversation.addMessage(message);
    }
}
