/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xiao.love.bar.im.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.VoiceMessageBody;

import java.io.File;

import xiao.love.bar.R;
import xiao.love.bar.component.util.NetUtils;
import xiao.love.bar.im.hxlib.IMHelper;

/**
 * 点击播放语音监听
 */
public class VoiceClickListener implements View.OnClickListener {
    private EMMessage message;
    private VoiceMessageBody voiceBody;
    private ImageView voiceIconView;

    private AnimationDrawable voiceAnimation = null;
    private MediaPlayer mediaPlayer = null;
    private ImageView iv_read_status;
    private Activity activity;
    private ChatType chatType;
    private BaseAdapter adapter;
    //是否正在播放语音
    public static boolean isPlaying = false;
    public static VoiceClickListener currentPlayListener = null;

    public VoiceClickListener(EMMessage message, ImageView v, ImageView iv_read_status, BaseAdapter adapter, Activity activity) {
        this.message = message;
        this.voiceBody = (VoiceMessageBody) message.getBody();
        this.iv_read_status = iv_read_status;
        this.adapter = adapter;
        this.voiceIconView = v;
        this.activity = activity;
        this.chatType = message.getChatType();
    }

    public void stopPlayVoice() {
        voiceAnimation.stop();
        if (message.direct == EMMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.chatfrom_voice_playing);
        } else {
            voiceIconView.setImageResource(R.drawable.chatto_voice_playing);
        }
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
        ((ChatActivity) activity).playMsgId = null;
    }

    public void playVoice(String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }

        ((ChatActivity) activity).playMsgId = message.getMsgId();
        isPlaying = true;
        currentPlayListener = this;
        startMediaPlayer(filePath);
        startAnimation();

        // 如果是接收的消息
        if (message.direct == EMMessage.Direct.RECEIVE) {
            try {
                if (chatType != ChatType.GroupChat && chatType != ChatType.ChatRoom) {
                    if (!message.isAcked) {
                        message.isAcked = true;
                        // 隐藏自己未播放这条语音消息的标志
                        iv_read_status.setVisibility(View.INVISIBLE);
                        // 告知对方已读这条消息
                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                    }
                }
            } catch (Exception e) {
                message.isAcked = false;
                e.printStackTrace();
            }
        }

    }

    /**
     * 播放语音
     * @param filePath
     */
    private void startMediaPlayer(String filePath){
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();
        if (IMHelper.getInstance().getModel().getVoiceSpeaker()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice();
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放动画
     */
    private void startAnimation() {
        if (message.direct == EMMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.voice_from_icon);
        } else {
            voiceIconView.setImageResource(R.drawable.voice_to_icon);
        }

        voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
        voiceAnimation.start();
    }

    @Override
    public void onClick(View v) {
        String st = activity.getResources().getString(R.string.Is_download_voice_click_later);
        if (isPlaying) {
            if (((ChatActivity) activity).playMsgId != null && ((ChatActivity) activity).playMsgId.equals(message.getMsgId())) {
                //表示2次点击所播放的语音是同一个，那么停止播放并返回
                currentPlayListener.stopPlayVoice();
                return;
            } else {
                //如果不是同一个，那么停止当前的语音播放但不返回
                currentPlayListener.stopPlayVoice();
            }
        }

        if (message.direct == EMMessage.Direct.SEND) {
            playVoice(voiceBody.getLocalUrl());
        } else {
            //接收语音，需要对语音的状态进行判断
            if (message.status == EMMessage.Status.SUCCESS) {
                playVoice(voiceBody.getLocalUrl());
            } else if (message.status == EMMessage.Status.INPROGRESS) {
                Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
            } else if (message.status == EMMessage.Status.FAIL) {
                Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
                //下载语音
                if (NetUtils.isConnected(activity)) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            EMChatManager.getInstance().asyncFetchMessage(message);
                        }
                    }).start();
                }
            }

        }
    }
}