package xiao.love.bar.im.chat.voice;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.VoiceRecorder;

import java.io.File;

import xiao.love.bar.R;
import xiao.love.bar.component.util.SDCardUtils;
import xiao.love.bar.im.chat.ChatActivity;
import xiao.love.bar.im.chat.VoiceClickListener;

/**
 * Created by guochang on 2015/9/21.
 */
public class SpeakLayout {
    private ChatActivity mActivity;
    private EMConversation mConversation;
    private VoiceRecorder mVoiceRecorder;
    private PowerManager.WakeLock mWakeLock;
    //语音按住布局
    private LinearLayout mVoiceLL;
    //录音提示布局
    private RelativeLayout mRecordHintRL;
    //音量图片
    private ImageView mMicImg;
    //录音问题提示
    private TextView mRecordHintTv;
    //MIC音量切换图片
    private Drawable[] mMicImages;
    private String mChatUserName;

    public SpeakLayout(ChatActivity activity, EMConversation conversation, Handler handler) {
        mActivity = activity;
        mConversation = conversation;
        mVoiceRecorder = new VoiceRecorder(handler);
        mChatUserName = activity.getToChatUsername();

        mVoiceLL = (LinearLayout)mActivity.findViewById(R.id.btn_press_to_speak);
        mRecordHintRL = (RelativeLayout) mActivity.findViewById(R.id.recording_container);
        mMicImg = (ImageView) mActivity.findViewById(R.id.mic_image);
        mRecordHintTv = (TextView) mActivity.findViewById(R.id.recording_hint);

        mVoiceLL.setOnTouchListener(new PressToSpeakListen());

        mWakeLock = ((PowerManager) mActivity.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");

        mMicImages = new Drawable[]{
                mActivity.getResources().getDrawable(R.drawable.record_animate_01),
                mActivity.getResources().getDrawable(R.drawable.record_animate_02),
                mActivity.getResources().getDrawable(R.drawable.record_animate_03),
                mActivity.getResources().getDrawable(R.drawable.record_animate_04),
                mActivity.getResources().getDrawable(R.drawable.record_animate_05),
                mActivity.getResources().getDrawable(R.drawable.record_animate_06),
                mActivity.getResources().getDrawable(R.drawable.record_animate_07),
                mActivity.getResources().getDrawable(R.drawable.record_animate_08),
                mActivity.getResources().getDrawable(R.drawable.record_animate_09),
                mActivity.getResources().getDrawable(R.drawable.record_animate_10),
                mActivity.getResources().getDrawable(R.drawable.record_animate_11),
                mActivity.getResources().getDrawable(R.drawable.record_animate_12),
                mActivity.getResources().getDrawable(R.drawable.record_animate_13),
                mActivity.getResources().getDrawable(R.drawable.record_animate_14)
        };
    }

    /**
     * 设置MIC音量图片
     * @param index
     */
    public void setMicImg(int index){
        if(index >= 0 && index < mMicImages.length) {
            mMicImg.setImageDrawable(mMicImages[index]);
        }
    }

    public void releaseWakeLock(){
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    /**
     * 是否正在录音
     * @return
     */
    public boolean isRecording(){
        return mVoiceRecorder.isRecording();
    }

    /**
     * 丢弃录音
     */
    public void discardRecording(){
        mVoiceRecorder.discardRecording();
    }

    public void showVoiceLayout() {
        mVoiceLL.setVisibility(View.VISIBLE);
    }

    public void hideVoiceLayout() {
        mVoiceLL.setVisibility(View.GONE);
    }

    public int getVoiceLayoutVisibility() {
        return mVoiceLL.getVisibility();
    }

    public void showRecordHintLayout() {
        mRecordHintRL.setVisibility(View.VISIBLE);
    }

    public void hideRecordHintLayout() {
        mRecordHintRL.setVisibility(View.GONE);
    }


    private void sendVoice(String filePath, int length) {
        if (!(new File(filePath).exists())) {
            return;
        }

        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
        message.setReceipt(mChatUserName);
        VoiceMessageBody body = new VoiceMessageBody(new File(filePath), length);
        message.addBody(body);
        mConversation.addMessage(message);
        //刷新listview
        if(mActivity.getMessageAdapter() != null) {
            mActivity.getMessageAdapter().refreshSelectLast();
        }
    }

    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!SDCardUtils.isSDCardEnable()) {
                        String st4 = mActivity.getResources().getString(R.string.Send_voice_need_sdcard_support);
                        Toast.makeText(mActivity, st4, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        mWakeLock.acquire();
                        //如果录音时有语音正在播放，立刻停止
                        if (VoiceClickListener.isPlaying) {
                            VoiceClickListener.currentPlayListener.stopPlayVoice();
                        }
                        mRecordHintRL.setVisibility(View.VISIBLE);
                        mRecordHintTv.setText(mActivity.getString(R.string.move_up_to_cancel));
                        mRecordHintTv.setBackgroundColor(Color.TRANSPARENT);
                        mVoiceRecorder.startRecording(null, mChatUserName, mActivity);
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (mWakeLock.isHeld())
                            mWakeLock.release();
                        if (mVoiceRecorder != null)
                            mVoiceRecorder.discardRecording();
                        mRecordHintRL.setVisibility(View.INVISIBLE);
                        Toast.makeText(mActivity, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        mRecordHintTv.setText(mActivity.getResources().getString(R.string.release_to_cancel));
                        mRecordHintTv.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        mRecordHintTv.setText(mActivity.getResources().getString(R.string.move_up_to_cancel));
                        mRecordHintTv.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    mRecordHintRL.setVisibility(View.INVISIBLE);
                    if (mWakeLock.isHeld())
                        mWakeLock.release();
                    if (event.getY() < 0) {
                        mVoiceRecorder.discardRecording();
                    } else {
                        String st1 = mActivity.getResources().getString(R.string.Recording_without_permission);
                        String st2 = mActivity.getResources().getString(R.string.The_recording_time_is_too_short);
                        String st3 = mActivity.getResources().getString(R.string.send_failure_please);
                        try {
                            int length = mVoiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(mVoiceRecorder.getVoiceFilePath(), length);
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(mActivity, st1, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mActivity, st2, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mActivity, st3, Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                default:
                    mRecordHintRL.setVisibility(View.INVISIBLE);
                    if (mVoiceRecorder != null)
                        mVoiceRecorder.discardRecording();
                    return false;
            }
        }
    }
}
