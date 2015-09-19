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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.LatLng;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;

import xiao.love.bar.R;
import xiao.love.bar.component.util.DateUtils;
import xiao.love.bar.component.util.DensityUtils;
import xiao.love.bar.component.util.L;
import xiao.love.bar.im.chat.emoji.EmojiParse;
import xiao.love.bar.im.hxlib.IMUtil;
import xiao.love.bar.im.util.ImageCache;
import xiao.love.bar.im.util.ImageUtils;
import xiao.love.bar.im.util.UserUtils;

public class MessageAdapter extends BaseAdapter {
    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;
    private static final int MESSAGE_TYPE_SENT_ROBOT_MENU = 16;
    private static final int MESSAGE_TYPE_RECV_ROBOT_MENU = 17;
    public static final String IMAGE_DIR = "chat/image/";
    public static final String VOICE_DIR = "chat/audio/";
    public static final String VIDEO_DIR = "chat/video";

    private String username;
    private LayoutInflater inflater;
    private Activity activity;

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    // reference to conversation object in chatsdk
    private EMConversation conversation;
    EMMessage[] messages = null;

    private Context context;

    private Map<String, Timer> timers = new Hashtable<String, Timer>();

    public MessageAdapter(Context context, String username, int chatType) {
        this.username = username;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (Activity) context;
        this.conversation = EMChatManager.getInstance().getConversation(username);
    }

    Handler handler = new Handler() {
        private void refreshList() {
            // UI线程不能直接使用conversation.getAllMessages()
            // 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
            messages = (EMMessage[]) conversation.getAllMessages().toArray(new EMMessage[conversation.getAllMessages().size()]);
            for (int i = 0; i < messages.length; i++) {
                // getMessage will set message as read status
                conversation.getMessage(i);
            }
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (activity instanceof ChatActivity) {
                        ListView listView = ((ChatActivity) activity).getListView();
                        if (messages.length > 0) {
                            listView.setSelection(messages.length - 1);
                        }
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = message.arg1;
                    if (activity instanceof ChatActivity) {
                        ListView listView = ((ChatActivity) activity).getListView();
                        listView.setSelection(position);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 获取item数
     */
    public int getCount() {
        return messages == null ? 0 : messages.length;
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
    }

    /**
     * 刷新页面, 选择Position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }

    public EMMessage getItem(int position) {
        if (messages != null && position < messages.length) {
            return messages[position];
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取item类型数
     */
    public int getViewTypeCount() {
        return 18;
    }

    /**
     * 获取item类型
     */
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message == null) {
            return -1;
        }

        if (message.getType() == EMMessage.Type.TXT) {
            if (message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VOICE_CALL, false))
                return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
            else if (message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
            else
                return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
        }
        if (message.getType() == EMMessage.Type.LOCATION) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == EMMessage.Type.VIDEO) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == EMMessage.Type.FILE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }

        return -1;
    }

    /**
     * 创建消息item
     *
     * @param message
     * @return
     */
    private View createViewByMessage(EMMessage message) {
        switch (message.getType()) {
            case LOCATION:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_location, null) : inflater.inflate(
                        R.layout.row_sent_location, null);
            case IMAGE:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_picture, null) : inflater.inflate(
                        R.layout.row_sent_picture, null);
            case VOICE:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_voice, null) : inflater.inflate(
                        R.layout.row_sent_voice, null);
            case VIDEO:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_video, null) : inflater.inflate(
                        R.layout.row_sent_video, null);
            case FILE:
                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_file, null) : inflater.inflate(
                        R.layout.row_sent_file, null);
            default:
                if (message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    // 语音通话
                    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_voice_call, null) : inflater
                            .inflate(R.layout.row_sent_voice_call, null);
                } else if (message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    // 视频通话
                    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_video_call,
                            null) : inflater.inflate(R.layout.row_sent_video_call, null);
                } else {
                    //文本
                    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_message,
                            null) : inflater.inflate(R.layout.row_sent_message, null);
                }
        }
    }

    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final EMMessage message = getItem(position);
        ChatType chatType = message.getChatType();
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(message);

            holder.avatarImg = (ImageView) convertView.findViewById(R.id.iv_userhead);//头像
            holder.timestampTv = (TextView) convertView.findViewById(R.id.timestamp);//消息时间戳
            holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);//进度条
            holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);//消息发送失败图片
            holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);//聊天对象昵称
            holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);//消息已读
            holder.tv_delivered = (TextView) convertView.findViewById(R.id.tv_delivered);//消息送达

            if (message.getType() == EMMessage.Type.IMAGE) {
                holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
                holder.tv = (TextView) convertView.findViewById(R.id.percentage);
            } else if (message.getType() == EMMessage.Type.TXT) {
                holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                holder.tvList = (LinearLayout) convertView.findViewById(R.id.ll_layout);
                // 语音通话和视频通话
                if (message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VOICE_CALL, false)
                        || message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    holder.iv = (ImageView) convertView.findViewById(R.id.iv_call_icon);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                }
            } else if (message.getType() == EMMessage.Type.VOICE) {
                holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
                holder.iv_voice_rl = (RelativeLayout) convertView.findViewById(R.id.iv_voice_rl);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
                holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
            } else if (message.getType() == EMMessage.Type.LOCATION) {
                holder.tv = (TextView) convertView.findViewById(R.id.tv_location);
            } else if (message.getType() == EMMessage.Type.VIDEO) {
                holder.iv = ((ImageView) convertView.findViewById(R.id.chatting_content_iv));
                holder.tv = (TextView) convertView.findViewById(R.id.percentage);
                holder.size = (TextView) convertView.findViewById(R.id.chatting_size_iv);
                holder.timeLength = (TextView) convertView.findViewById(R.id.chatting_length_iv);
                holder.playBtn = (ImageView) convertView.findViewById(R.id.chatting_status_btn);
                holder.container_status_btn = (LinearLayout) convertView.findViewById(R.id.container_status_btn);
            } else if (message.getType() == EMMessage.Type.FILE) {
                holder.tv_file_name = (TextView) convertView.findViewById(R.id.tv_file_name);
                holder.tv_file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
                holder.tv_file_download_state = (TextView) convertView.findViewById(R.id.tv_file_state);
                holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_file_container);
                holder.tv = (TextView) convertView.findViewById(R.id.percentage);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 非群聊和聊天室
        if (chatType != ChatType.GroupChat && chatType != ChatType.ChatRoom) {
            //发送的消息，显示送达或已读状态
            if (message.direct == EMMessage.Direct.SEND) {
                if (message.isAcked) {
                    holder.tv_delivered.setVisibility(View.INVISIBLE);
                    holder.tv_ack.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_ack.setVisibility(View.INVISIBLE);
                    if (message.isDelivered) {
                        holder.tv_delivered.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_delivered.setVisibility(View.INVISIBLE);
                    }
                }
            } else {//接收的消息
                // 单聊中如果是文本或者地图消息需立即回执已读
                if (!message.isAcked) {
                    if (message.getType() == Type.TXT || message.getType() == Type.LOCATION ||
                            message.getType() == Type.IMAGE) {
                        // 非语音通话
                        if (!message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                            try {
                                EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                                // 发送已读回执
                                message.isAcked = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        //设置头像
        setUserAvatar(message, holder.avatarImg);

        switch (message.getType()) {
            case IMAGE: // 图片
                handleImageMessage(message, holder, position);
                break;
            case TXT: // 文本
                if (message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VOICE_CALL, false)
                        || message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                } else {
                    handleTextMessage(message, holder, position);
                }
                break;
            case LOCATION: // 位置
                handleLocationMessage(message, holder, position, convertView);
                break;
            case VOICE: // 语音
                handleVoiceMessage(message, holder, position);
                break;
        }

        /**
         * 发送端：未发送成功设置重发按钮
         * 接收端：设置聊天对象长按加入黑名单
         */
        if (message.direct == EMMessage.Direct.SEND) {
            //发送消息设置重发按钮
            View statusView = convertView.findViewById(R.id.msg_status);
            // 重发按钮点击事件
            statusView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    reSend(message, position);
                }
            });
        } else {
            //接收消息设置长按头像加入黑名单
            if (chatType != ChatType.ChatRoom) {
                holder.avatarImg.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showBlackListDialog(position);
                        return true;
                    }
                });
            }
        }

        if (position == 0) {
            holder.timestampTv.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
            holder.timestampTv.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            EMMessage prevMessage = getItem(position - 1);
            if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                holder.timestampTv.setVisibility(View.GONE);
            } else {
                holder.timestampTv.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                holder.timestampTv.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

//    @SuppressLint("NewApi")
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        final EMMessage message = getItem(position);
//        ChatType chatType = message.getChatType();
//        final ViewHolder holder;
//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = createViewByMessage(message);
//            if (message.getType() == EMMessage.Type.IMAGE) {
//                try {
//                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
//                    holder.avatarImg = (ImageView) convertView.findViewById(R.id.iv_userhead);
//                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
//                } catch (Exception e) {
//                }
//
//            } else if (message.getType() == EMMessage.Type.TXT) {
//
//                try {
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//                    holder.avatarImg = (ImageView) convertView.findViewById(R.id.iv_userhead);
//                    // 这里是文字内容
//                    holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
//                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
//
//                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
//                    holder.tvList = (LinearLayout) convertView.findViewById(R.id.ll_layout);
//                } catch (Exception e) {
//                }
//
//                // 语音通话及视频通话
//                if (message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VOICE_CALL, false)
//                        || message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
//                    holder.iv = (ImageView) convertView.findViewById(R.id.iv_call_icon);
//                    holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
//                }
//
//            } else if (message.getType() == EMMessage.Type.VOICE) {
//                try {
//                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
//                    holder.iv_voice_rl = (RelativeLayout) convertView.findViewById(R.id.iv_voice_rl);
//                    holder.avatarImg = (ImageView) convertView.findViewById(R.id.iv_userhead);
//                    holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
//                    holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
//                } catch (Exception e) {
//                }
//            } else if (message.getType() == EMMessage.Type.LOCATION) {
//                try {
//                    holder.avatarImg = (ImageView) convertView.findViewById(R.id.iv_userhead);
//                    holder.tv = (TextView) convertView.findViewById(R.id.tv_location);
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
//                } catch (Exception e) {
//                }
//            } else if (message.getType() == EMMessage.Type.VIDEO) {
//                try {
//                    holder.iv = ((ImageView) convertView.findViewById(R.id.chatting_content_iv));
//                    holder.avatarImg = (ImageView) convertView.findViewById(R.id.iv_userhead);
//                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//                    holder.size = (TextView) convertView.findViewById(R.id.chatting_size_iv);
//                    holder.timeLength = (TextView) convertView.findViewById(R.id.chatting_length_iv);
//                    holder.playBtn = (ImageView) convertView.findViewById(R.id.chatting_status_btn);
//                    holder.container_status_btn = (LinearLayout) convertView.findViewById(R.id.container_status_btn);
//                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
//
//                } catch (Exception e) {
//                }
//            } else if (message.getType() == EMMessage.Type.FILE) {
//                try {
//                    holder.avatarImg = (ImageView) convertView.findViewById(R.id.iv_userhead);
//                    holder.tv_file_name = (TextView) convertView.findViewById(R.id.tv_file_name);
//                    holder.tv_file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//                    holder.tv_file_download_state = (TextView) convertView.findViewById(R.id.tv_file_state);
//                    holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_file_container);
//                    // 这里是进度值
//                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
//                } catch (Exception e) {
//                }
//                try {
//                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
//                } catch (Exception e) {
//                }
//
//            }
//
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        // 群聊时，显示接收的消息的发送人的名称
//        if ((chatType == ChatType.GroupChat || chatType == ChatType.ChatRoom) && message.direct == EMMessage.Direct.RECEIVE) {
//            //demo里使用username代码nick
//            UserUtils.setUserNick(message.getFrom(), holder.tv_usernick);
//        }
//        /*********************发送方无需显示自己的昵称 by guochangxiao********************************/
//        if (message.direct == EMMessage.Direct.SEND) {
//            UserUtils.setCurrentUserNick(holder.tv_usernick);
//        }
//        // 非群聊和聊天室(处理消息的送达和已读)
//        if(chatType != ChatType.GroupChat && chatType != ChatType.ChatRoom) {
//            //发送的消息
//            if (message.direct == EMMessage.Direct.SEND) {
//                //消息是否已读
//                holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
//                //消息是否送达
//                holder.tv_delivered = (TextView) convertView.findViewById(R.id.tv_delivered);
//                if (holder.tv_ack != null) {
//                    if (message.isAcked) {
//                        if (holder.tv_delivered != null) {
//                            holder.tv_delivered.setVisibility(View.INVISIBLE);
//                        }
//                        holder.tv_ack.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.tv_ack.setVisibility(View.INVISIBLE);
//                        if (holder.tv_delivered != null) {
//                            if (message.isDelivered) {
//                                holder.tv_delivered.setVisibility(View.VISIBLE);
//                            } else {
//                                holder.tv_delivered.setVisibility(View.INVISIBLE);
//                            }
//                        }
//                    }
//                }
//            } else {//接收的消息
//                // 单聊中如果是文本或者地图消息需立即回执已读
//                if(!message.isAcked) {
//                    if (message.getType() == Type.TXT || message.getType() == Type.LOCATION ||
//                            message.getType() == Type.IMAGE) {
//                        // 非语音通话
//                        if (!message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
//                            try {
//                                EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
//                                // 发送已读回执
//                                message.isAcked = true;
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        //设置用户头像
//        setUserAvatar(message, holder.avatarImg);
//
//        switch (message.getType()) {
//            // 根据消息type显示item
//            case IMAGE: // 图片
//                handleImageMessage(message, holder, position, convertView);
//                break;
//            case TXT: // 文本
//
//                    handleTextMessage(message, holder, position);
//                break;
//            case LOCATION: // 位置
//                handleLocationMessage(message, holder, position, convertView);
//                break;
//            default:
//                // not supported
//        }
//
//        if (message.direct == EMMessage.Direct.SEND) {
//            //发送消息设置重发按钮
//            View statusView = convertView.findViewById(R.id.msg_status);
//            // 重发按钮点击事件
//            statusView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    // 显示重发消息的自定义alertdialog
//                    Intent intent = new Intent(activity, AlertDialogActivity.class);
//                    intent.putExtra("msg", activity.getString(R.string.confirm_resend));
//                    intent.putExtra("title", activity.getString(R.string.resend));
//                    intent.putExtra("cancel", true);
//                    intent.putExtra("position", position);
//                    if (message.getType() == EMMessage.Type.TXT)
//                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_TEXT);
//                    else if (message.getType() == EMMessage.Type.VOICE)
//                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VOICE);
//                    else if (message.getType() == EMMessage.Type.IMAGE)
//                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_PICTURE);
//                    else if (message.getType() == EMMessage.Type.LOCATION)
//                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_LOCATION);
//                    else if (message.getType() == EMMessage.Type.FILE)
//                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_FILE);
//                    else if (message.getType() == EMMessage.Type.VIDEO)
//                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VIDEO);
//
//                }
//            });
//
//        } else {
//            //接收消息设置长按加入黑名单
//            final String st = context.getResources().getString(R.string.Into_the_blacklist);
//            if (!((ChatActivity) activity).isRobot && chatType != ChatType.ChatRoom) {
//                // 长按头像，移入黑名单
//                holder.avatarImg.setOnLongClickListener(new OnLongClickListener() {
//
//                    @Override
//                    public boolean onLongClick(View v) {
//                        Intent intent = new Intent(activity, AlertDialogActivity.class);
//                        intent.putExtra("msg", st);
//                        intent.putExtra("cancel", true);
//                        intent.putExtra("position", position);
//                        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST);
//                        return true;
//                    }
//                });
//            }
//        }
//
//        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
//
//        if (position == 0) {
//            timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
//            timestamp.setVisibility(View.VISIBLE);
//        } else {
//            // 两条消息时间离得如果稍长，显示时间
//            EMMessage prevMessage = getItem(position - 1);
//            if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
//                timestamp.setVisibility(View.GONE);
//            } else {
//                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
//                timestamp.setVisibility(View.VISIBLE);
//            }
//        }
//        return convertView;
//    }

    /**
     * 消息发送失败时，重发消息
     *
     * @param message
     * @param position
     */
    private void reSend(EMMessage message, int position) {
        Intent intent = new Intent(activity, AlertDialogActivity.class);
        intent.putExtra("msg", activity.getString(R.string.confirm_resend));
        intent.putExtra("title", activity.getString(R.string.resend));
        intent.putExtra("cancel", true);
        intent.putExtra("position", position);
        if (message.getType() == EMMessage.Type.TXT)
            activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_TEXT);
        else if (message.getType() == EMMessage.Type.VOICE)
            activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VOICE);
        else if (message.getType() == EMMessage.Type.IMAGE)
            activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_PICTURE);
        else if (message.getType() == EMMessage.Type.LOCATION)
            activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_LOCATION);
        else if (message.getType() == EMMessage.Type.FILE)
            activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_FILE);
        else if (message.getType() == EMMessage.Type.VIDEO)
            activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_VIDEO);
    }

    /**
     * 是否加入黑名单
     *
     * @param position
     */
    private void showBlackListDialog(int position) {
        String st = context.getResources().getString(R.string.Into_the_blacklist);

        Intent intent = new Intent(activity, AlertDialogActivity.class);
        intent.putExtra("msg", st);
        intent.putExtra("cancel", true);
        intent.putExtra("position", position);
        activity.startActivityForResult(intent, ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST);
    }

    /**
     * 显示头像
     *
     * @param message
     * @param imageView
     */
    private void setUserAvatar(final EMMessage message, ImageView imageView) {
        if (message.direct == Direct.SEND) {
            //显示自己头像
            UserUtils.setCurrentUserAvatar(context, imageView);
        } else {
            UserUtils.setUserAvatar(context, message.getFrom(), imageView);
        }
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(context, UserProfileActivity.class);
//                intent.putExtra("username", message.getFrom());
//                context.startActivity(intent);
            }
        });
    }

    /**
     * 文本消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleTextMessage(EMMessage message, ViewHolder holder, final int position) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        Spannable span = EmojiParse.parseString(context, txtBody.getMessage());
        // 设置内容
        holder.tv.setText(span, BufferType.SPANNABLE);
        // 设置长按事件监听
        holder.tv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenuActivity.class)).putExtra("position", position).putExtra("type",
                                EMMessage.Type.TXT.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS: // 发送成功
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:// 发送消息
                    sendMsg(message, holder);
            }
        }
    }

    /**
     * 图片消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position) {
        holder.pb.setTag(position);
        holder.iv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenuActivity.class)).putExtra("position", position).putExtra("type",
                                EMMessage.Type.IMAGE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });


        if (message.direct == EMMessage.Direct.RECEIVE) {
            // 接收
            if (message.status == EMMessage.Status.INPROGRESS) {
                //先下载缩略图图片，大图只有点击查看大图时才下载
                downloadMsg(message, holder);
            } else {
                holder.iv.setImageResource(R.drawable.default_image);
                holder.pb.setVisibility(View.GONE);
                holder.tv.setVisibility(View.GONE);
                ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                showReceiveImageView(message, holder.iv);
//                if (imgBody.getLocalUrl() != null) {
//                    String remotePath = imgBody.getRemoteUrl();
//                    String filePath = ImageUtils.getImagePath(remotePath);
//                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
//                    if (TextUtils.isEmpty(thumbRemoteUrl) && !TextUtils.isEmpty(remotePath)) {
//                        thumbRemoteUrl = remotePath;
//                    }
//                    String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
//                    showImageView(thumbnailPath, holder.iv, filePath, remotePath, message);
//                }
            }
        } else {
            // 发送
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.tv.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.tv.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                default:
                    sendMsg(message, holder);
            }
            // 显示图片
            holder.iv.setImageResource(R.drawable.default_image);
            showSendImageView(message, holder.iv);
//            ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
//            String filePath = imgBody.getLocalUrl();
//            if (filePath != null && new File(filePath).exists()) {
//                showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message);
//            } else {
//                showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, IMAGE_DIR, message);
//
//            }
        }
    }

    /**
     * 发送端显示图片
     * 1.发送端，本地只有大图，没有缩略图，缩略图需要利用大图裁剪
     * 2.接收端，下载成功后本地只有缩略图，没有大图,大图需使用远程url下载
     * @param message
     * @param iv
     */
    private void showSendImageView(final EMMessage message, final ImageView iv) {
        final ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
        //对发送端，本地只有原始图片的url
        final String localUrl = imgBody.getLocalUrl();
        if(TextUtils.isEmpty(localUrl)){
            return;
        }

        String localThumbUrl = ImageUtils.getThumbnailImagePath(localUrl);
        if(TextUtils.isEmpty(localThumbUrl)){
            return;
        }

        Bitmap bitmap = ImageCache.getInstance().get(localThumbUrl);
        if (bitmap == null) {//内存缓存中不存在缩略图
            int thumbImageSize = DensityUtils.dp2px(activity, 70f);
            //在图片缓存目录中，缩略图本地文件是否存在
            if (new File(localThumbUrl).exists()) {
                bitmap = com.easemob.util.ImageUtils.decodeScaleImage(localThumbUrl, thumbImageSize, thumbImageSize);
            } else {
                bitmap = com.easemob.util.ImageUtils.decodeScaleImage(localUrl, thumbImageSize, thumbImageSize);
            }
        }

        if (bitmap != null) {
            //内存缓存缩略图
            ImageCache.getInstance().put(localThumbUrl, bitmap);
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickImage(message, localUrl, imgBody.getRemoteUrl());
                }
            });
        }
    }

    /**
     * 接收端显示图片
     * 1.发送端，本地只有大图，没有缩略图，缩略图需要利用大图裁剪
     * 2.接收端，下载成功后本地只有缩略图，没有大图,大图需使用远程url下载
     * @param message
     * @param iv
     */
    private void showReceiveImageView(final EMMessage message, final ImageView iv) {
        final ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
        //对接收端，本地下载并存储了缩略图
        final String remoteThumbUrl = imgBody.getThumbnailUrl();
        if(TextUtils.isEmpty(remoteThumbUrl)){
            return;
        }

        final String localThumbUrl = ImageUtils.getThumbnailImagePath(remoteThumbUrl);
        if(TextUtils.isEmpty(localThumbUrl)){
            return;
        }

        Bitmap bitmap = ImageCache.getInstance().get(localThumbUrl);
        if (bitmap == null) {//内存缓存中不存在缩略图
            int thumbImageSize = DensityUtils.dp2px(activity, 70f);
            //在图片缓存目录中，缩略图本地文件是否存在
            if (new File(localThumbUrl).exists()) {
                bitmap = com.easemob.util.ImageUtils.decodeScaleImage(localThumbUrl, thumbImageSize, thumbImageSize);
            }
        }

        if (bitmap != null) {
            //内存缓存缩略图
            ImageCache.getInstance().put(localThumbUrl, bitmap);
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickImage(message, localThumbUrl, imgBody.getRemoteUrl());
                }
            });
        }
    }

    /**
     * 处理位置消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleLocationMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
        TextView locationView = ((TextView) convertView.findViewById(R.id.tv_location));
        LocationMessageBody locBody = (LocationMessageBody) message.getBody();
        locationView.setText(locBody.getAddress());
        LatLng loc = new LatLng(locBody.getLatitude(), locBody.getLongitude());
        locationView.setOnClickListener(new MapClickListener(loc, locBody.getAddress()));
        locationView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenuActivity.class)).putExtra("position", position).putExtra("type",
                                EMMessage.Type.LOCATION.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return false;
            }
        });

        if (message.direct == Direct.SEND) {
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    holder.pb.setVisibility(View.VISIBLE);
                    break;
                default:
                    sendMsg(message, holder);
            }
        }
    }

    /**
     * 语音消息
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position) {
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        int len = voiceBody.getLength();
        if (len > 0) {
            holder.tv.setText(len + "\"");
            holder.tv.setVisibility(View.VISIBLE);
        } else {
            holder.tv.setVisibility(View.INVISIBLE);
        }

        VoiceClickListener listener = new VoiceClickListener(message, holder.iv, holder.iv_read_status, this, activity);
        holder.iv_voice_rl.setOnClickListener(listener);
        holder.iv_voice_rl.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenuActivity.class)).putExtra("position", position).putExtra("type",
                                EMMessage.Type.VOICE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });

        if (VoiceClickListener.isPlaying) {//如果有语音正在播放
            if (((ChatActivity) activity).playMsgId != null
                    && !((ChatActivity) activity).playMsgId.equals(message.getMsgId())) {
                //且播放的语音与刷新的语音不相同，那么设置其静态图片
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
                } else {
                    holder.iv.setImageResource(R.drawable.chatto_voice_playing);
                }
            }
        }

        if (message.direct == EMMessage.Direct.RECEIVE) {//接收端
            //判断消息是否已读
            if (message.isAcked) {
                holder.iv_read_status.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_read_status.setVisibility(View.VISIBLE);
            }

            if (message.status == EMMessage.Status.INPROGRESS) {
                downloadMsg(message, holder);
            } else {
                holder.pb.setVisibility(View.INVISIBLE);
            }
        } else {
            //发送端
            switch (message.status) {
                case SUCCESS:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    sendMsg(message, holder);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param message
     * @param holder
     */
    public void sendMsg(final EMMessage message, final ViewHolder holder) {
        holder.staus_iv.setVisibility(View.GONE);
        holder.pb.setVisibility(View.VISIBLE);

        if (message.getType() == EMMessage.Type.IMAGE) {
            holder.tv.setVisibility(View.VISIBLE);
            holder.tv.setText("0%");
        }

        // 调用sdk异步发送方法
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.pb.setVisibility(View.GONE);
                        if (message.getType() == EMMessage.Type.IMAGE) {
                            holder.tv.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onError(int code, String error) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.pb.setVisibility(View.GONE);
                        holder.staus_iv.setVisibility(View.VISIBLE);
                        if (message.getType() == EMMessage.Type.IMAGE) {
                            holder.tv.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                if (message.getType() == EMMessage.Type.IMAGE) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            holder.tv.setText(progress + "%");
                        }
                    });
                }
            }

        });
    }

    /*
     * chat sdk will automatic download thumbnail image for the image message we
     * need to register callback show the download progress
     * 下载消息(图片和语音)
     */
    private void downloadMsg(final EMMessage message, final ViewHolder holder) {
        //FileMessageBody有setDownloadCallback方法，所以需先转换
        final FileMessageBody msgbody = (FileMessageBody) message.getBody();

        holder.pb.setVisibility(View.VISIBLE);
        if (message.getType() == Type.IMAGE) {
            holder.iv.setImageResource(R.drawable.default_image);
            holder.tv.setVisibility(View.VISIBLE);
        }

        //此处的图片下载只是下载了缩略图，并存储在图片缓存目录中
        //"/storage/emulated/0/Android/data/com.easemob.chatuidemo/xiao#sogoutest/xiaoguochang/image/"
        msgbody.setDownloadCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.pb.setVisibility(View.GONE);
                        if (message.getType() == EMMessage.Type.IMAGE) {
                            holder.tv.setVisibility(View.GONE);
                            showReceiveImageView(message, holder.iv);
                            //设置缩略图
//                            String thumbRemoteUrl = ((ImageMessageBody) message.getBody()).getThumbnailUrl();
//                            if (!TextUtils.isEmpty(thumbRemoteUrl)) {
//                                String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
//                                Bitmap bitmap = BitmapFactory.decodeFile(thumbnailPath);
//                                if (bitmap != null) {
//                                    holder.iv.setImageBitmap(bitmap);
//                                }
//                            }
                        }
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.pb.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onProgress(final int progress, String status) {
                if (message.getType() == EMMessage.Type.IMAGE) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tv.setText(progress + "%");
                        }
                    });
                }

            }
        });
    }

    /**
     * 发送图片
     *
     * @param message
     * @param holder
     */
//    private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
//        try {
//            holder.staus_iv.setVisibility(View.GONE);
//            holder.pb.setVisibility(View.VISIBLE);
//            holder.tv.setVisibility(View.VISIBLE);
//            holder.tv.setText("0%");
//
//            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
//
//                @Override
//                public void onSuccess() {
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            // send success
//                            holder.pb.setVisibility(View.GONE);
//                            holder.tv.setVisibility(View.GONE);
//                        }
//                    });
//                }
//
//                @Override
//                public void onError(int code, String error) {
//
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            holder.pb.setVisibility(View.GONE);
//                            holder.tv.setVisibility(View.GONE);
//                            holder.staus_iv.setVisibility(View.VISIBLE);
//                        }
//                    });
//                }
//
//                @Override
//                public void onProgress(final int progress, String status) {
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            holder.tv.setText(progress + "%");
//                        }
//                    });
//                }
//
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 更新ui上消息发送状态
     *
     * @param message
     * @param holder
     */
//    private void updateSendedView(final EMMessage message, final ViewHolder holder) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // send success
//                if (message.getType() == EMMessage.Type.VIDEO) {
//                    holder.tv.setVisibility(View.GONE);
//                }
//
//                if (message.status == EMMessage.Status.SUCCESS) {
//                    // if (message.getType() == EMMessage.Type.FILE) {
//                    // holder.pb.setVisibility(View.INVISIBLE);
//                    // holder.staus_iv.setVisibility(View.INVISIBLE);
//                    // } else {
//                    // holder.pb.setVisibility(View.GONE);
//                    // holder.staus_iv.setVisibility(View.GONE);
//                    // }
//
//                } else if (message.status == EMMessage.Status.FAIL) {
//                    // if (message.getType() == EMMessage.Type.FILE) {
//                    // holder.pb.setVisibility(View.INVISIBLE);
//                    // } else {
//                    // holder.pb.setVisibility(View.GONE);
//                    // }
//                    // holder.staus_iv.setVisibility(View.VISIBLE);
//
//                    if (message.getError() == EMError.MESSAGE_SEND_INVALID_CONTENT) {
//                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_invalid_content), Toast.LENGTH_SHORT)
//                                .show();
//                    } else if (message.getError() == EMError.MESSAGE_SEND_NOT_IN_THE_GROUP) {
//                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_not_in_the_group), Toast.LENGTH_SHORT)
//                                .show();
//                    } else {
//                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
//                                .show();
//                    }
//                }
//
//                notifyDataSetChanged();
//            }
//        });
//    }

    /**
     * load image into image view
     *
     * @param thumbernailPath   缩略土本地路径
     * @param iv                ImageView
     * @param localFullSizePath 本地图片路径
     * @param remote            远程图片路径
     * @param message
     * @return
     */
//    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath,
//                                  final String remote, final EMMessage message) {
//        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
//        if (bitmap != null) {//内存缓存中存在缩略图
//            iv.setImageBitmap(bitmap);
//            iv.setClickable(true);
//            iv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clickImage(message, localFullSizePath, remote);
//                }
//            });
//            return true;
//        } else {
//            new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, message.getChatType(), iv, activity, message);
//            return true;
//        }
//    }

    /**
     * 1.发送端，本地只有大图，没有缩略图
     * 2.接收端，下载成功后本地只有缩略图，没有大图
     */
//    private void showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath,
//                               final String remote, final EMMessage message) {
//        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
//        if (bitmap == null) {//内存缓存中不存在缩略图
//            File file = new File(thumbernailPath);
//            int thumbImageSize = DensityUtils.dp2px(activity, 70f);
//            //在图片缓存目录中，缩略图本地文件是否存在
//            if (file.exists()) {
//                bitmap = com.easemob.util.ImageUtils.decodeScaleImage(thumbernailPath, thumbImageSize, thumbImageSize);
//            } else {
//                if (message.direct == EMMessage.Direct.SEND) {
//                    bitmap = com.easemob.util.ImageUtils.decodeScaleImage(localFullSizePath, thumbImageSize, thumbImageSize);
//                }
//            }
//        }
//
//        if (bitmap != null) {
//            iv.setImageBitmap(bitmap);
//            iv.setClickable(true);
//            iv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    clickImage(message, localFullSizePath, remote);
//                }
//            });
//        }
//    }

    /**
     * 图片点击
     *
     * @param message
     * @param localFullSizePath 图片本地路径
     * @param remote            图片远程url
     */
    private void clickImage(EMMessage message, String localFullSizePath, String remote) {
        Intent intent = new Intent(activity, ShowBigImageActivity.class);
        File file = new File(localFullSizePath);
        //本地图片是否存在
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
        } else {
            //本地大图不存在，需要先通过远程地址下载
            ImageMessageBody body = (ImageMessageBody) message.getBody();
            intent.putExtra("secret", body.getSecret());
            intent.putExtra("remotepath", remote);
        }

        activity.startActivity(intent);
    }

    public static class ViewHolder {
        //语音电话、视频电话、图片、语音发送等的图标
        ImageView iv;
        //语音背景
        RelativeLayout iv_voice_rl;
        //文本聊天内容或进度百分比
        TextView tv;
        //进度条
        ProgressBar pb;
        //消息未发送成功叹号图片
        ImageView staus_iv;
        //头像
        ImageView avatarImg;
        //消息时间戳
        TextView timestampTv;
        //显示在头像下的用户昵称
        TextView tv_usernick;
        //播放按钮
        ImageView playBtn;
        //视频时长
        TextView timeLength;
        //视频大小
        TextView size;
        //视频画面
        LinearLayout container_status_btn;
        //发送文件布局
        LinearLayout ll_container;
        //视频是否已看
        ImageView iv_read_status;
        // 显示已读回执状态
        TextView tv_ack;
        // 显示送达回执状态
        TextView tv_delivered;
        //文件名称
        TextView tv_file_name;
        //文件大小
        TextView tv_file_size;
        //文件下载状态
        TextView tv_file_download_state;
        //以下是menu菜单内容
        TextView tvTitle;
        LinearLayout tvList;
    }

    /*
     * 点击地图消息listener
     */
    class MapClickListener implements View.OnClickListener {
        LatLng location;
        String address;

        public MapClickListener(LatLng loc, String address) {
            location = loc;
            this.address = address;

        }

        @Override
        public void onClick(View v) {
            Intent intent;
            intent = new Intent(context, BaiduMapActivity.class);
            intent.putExtra("latitude", location.latitude);
            intent.putExtra("longitude", location.longitude);
            intent.putExtra("address", address);
            activity.startActivity(intent);
        }

    }

}