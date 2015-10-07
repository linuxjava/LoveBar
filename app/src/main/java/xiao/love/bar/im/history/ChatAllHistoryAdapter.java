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
package xiao.love.bar.im.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import xiao.love.bar.R;
import xiao.love.bar.component.widget.BGASwipeItemLayout;
import xiao.love.bar.im.chat.emoji.EmojiParse;
import xiao.love.bar.im.hxlib.IMUtil;

/**
 * 显示所有聊天记录adpater
 */
public class ChatAllHistoryAdapter extends BGARecyclerViewAdapter<EMConversation> {
    private Context mContext;
    //当前处于打开状态的item
    private List<BGASwipeItemLayout> mOpenedSil = new ArrayList<>();
    public BGASwipeItemLayout.ViewGroupListener mViewGroupListener = new BGASwipeItemLayout.ViewGroupListener() {
        @Override
        public boolean isOpenItem() {
            return mOpenedSil.size() > 0 ? true : false;
        }

        @Override
        public void closeAllItem() {
            closeOpenedSwipeItemLayoutWithAnim();
        }
    };

    public ChatAllHistoryAdapter(Context context, RecyclerView recyclerView) {
        super(recyclerView, R.layout.row_chat_history);
        mContext = context;
    }

    @Override
    protected void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
        BGASwipeItemLayout swipeItemLayout = viewHolderHelper.getView(R.id.swipe_root);
        swipeItemLayout.setListener(mViewGroupListener);
        swipeItemLayout.setDelegate(new BGASwipeItemLayout.BGASwipeItemLayoutDelegate() {
            @Override
            public void onBGASwipeItemLayoutOpened(BGASwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
                mOpenedSil.add(swipeItemLayout);
            }

            @Override
            public void onBGASwipeItemLayoutClosed(BGASwipeItemLayout swipeItemLayout) {
                mOpenedSil.remove(swipeItemLayout);
            }

            @Override
            public void onBGASwipeItemLayoutStartOpen(BGASwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
            }
        });
        viewHolderHelper.setItemChildClickListener(R.id.item_swipe_delete);
    }

    @Override
    protected void fillData(BGAViewHolderHelper bgaViewHolderHelper, int position, EMConversation conversation) {
        if (position == 0) {
            ((BGASwipeItemLayout) bgaViewHolderHelper.getConvertView()).setSwipeAble(false);
            bgaViewHolderHelper.setText(R.id.name, "收藏");
            bgaViewHolderHelper.setText(R.id.message, "我收藏的人和收藏我的人");
            bgaViewHolderHelper.setImageResource(R.id.avatar, R.drawable.base_avatar_follow);
            bgaViewHolderHelper.setVisibility(R.id.unread_msg_number, View.INVISIBLE);
            bgaViewHolderHelper.setVisibility(R.id.time, View.INVISIBLE);
            bgaViewHolderHelper.setVisibility(R.id.msg_state, View.GONE);
            return;
        }

		bgaViewHolderHelper.setVisibility(R.id.unread_msg_number, View.VISIBLE);
		bgaViewHolderHelper.setVisibility(R.id.time, View.VISIBLE);
		bgaViewHolderHelper.setVisibility(R.id.msg_state, View.VISIBLE);

		bgaViewHolderHelper.setText(R.id.name, conversation.getUserName());
        bgaViewHolderHelper.setImageResource(R.id.avatar, R.drawable.default_avatar);
        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
            bgaViewHolderHelper.setText(R.id.unread_msg_number, String.valueOf(conversation.getUnreadMsgCount()));
            bgaViewHolderHelper.setVisibility(R.id.unread_msg_number, View.VISIBLE);
        } else {
            bgaViewHolderHelper.setVisibility(R.id.unread_msg_number, View.INVISIBLE);
        }

        if (conversation.getMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            bgaViewHolderHelper.setText(R.id.message, EmojiParse.parseString(mContext, getMessageDigest(lastMessage, mContext)),
                    BufferType.SPANNABLE);

            bgaViewHolderHelper.setText(R.id.time, DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
                bgaViewHolderHelper.setVisibility(R.id.msg_state, View.VISIBLE);
            } else {
                bgaViewHolderHelper.setVisibility(R.id.msg_state, View.GONE);
            }
        }
    }

    /**
     * 不管所有打开的item
     */
    public void closeOpenedSwipeItemLayoutWithAnim() {
        for (BGASwipeItemLayout sil : mOpenedSil) {
            sil.closeWithAnim();
        }
        mOpenedSil.clear();
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    // 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_recv");
                    digest = getStrng(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_prefix");
                    digest = getStrng(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                digest = getStrng(context, R.string.picture) + imageBody.getFileName();
                break;
            case VOICE:// 语音消息
                digest = getStrng(context, R.string.voice);
                break;
            case TXT: // 文本消息
                if (message.getBooleanAttribute(IMUtil.MESSAGE_ATTR_IS_VOICE_CALL, false)) {

                } else {
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                }
                break;
        }

        return digest;
    }

    String getStrng(Context context, int resId) {
        return context.getResources().getString(resId);
    }
}
