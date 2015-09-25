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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;

import java.util.List;

import xiao.love.bar.R;
import xiao.love.bar.component.AbstractActivity;
import xiao.love.bar.component.BaseActivity;
import xiao.love.bar.component.util.KeyBoardUtils;
import xiao.love.bar.component.util.L;
import xiao.love.bar.component.util.T;
import xiao.love.bar.im.chat.emoji.EmojiLayout;
import xiao.love.bar.im.chat.emoji.EmojiParse;
import xiao.love.bar.im.chat.more.MoreLayout;
import xiao.love.bar.im.chat.voice.SpeakLayout;
import xiao.love.bar.im.hxlib.IMHelper;
import xiao.love.bar.im.util.UserUtils;
import xiao.love.bar.im.widget.PasteEditText;

/**
 * 聊天页面
 */
public class ChatActivity extends BaseActivity implements OnClickListener, EMEventListener {
    public static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    public static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;

    private ClipboardManager mClipboard;
    private EMConversation mConversation;
    // 给谁发送消息
    private String toChatUsername;
    private MessageAdapter mMessageAdapter;
    //重发的消息所在的位置
    static int mResendPos;
    private final int mPagesize = 20;
    private boolean haveMoreData = true;
    //当前播放语音的消息id
    public String playMsgId;

    private Activity mContext;
    private ListView mListView;
    private PasteEditText mEditText;
    //键盘图标
    private Button mKeyboardBtn;
    //语音图标
    private Button mVoiceBtn;
    //发送按钮
    private Button mSendBtn;
    //更多按钮
    private Button mMoreBtn;
    //输入框布局
    private RelativeLayout mEditTextlayout;
    //更多和表情布局
    private LinearLayout mMoreLL;
    private ProgressBar mLoadMorePB;
    private ImageView mEmojiNormalImg;
    private ImageView mEmojiCheckedImg;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //表情布局
    private EmojiLayout mEmojiLayout;
    //更多布局(发送图片、位置)
    private MoreLayout mMoreLayout;
    //录音布局
    private SpeakLayout mVoiceLayout;
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            mVoiceLayout.setMicImg(msg.what);
        }
    };
    //输入框获取焦点监听
    private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mEditTextlayout.setBackgroundResource(R.drawable.input_bar_bg_active);
            } else {
                mEditTextlayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
            }

            mMoreLL.setVisibility(View.GONE);
            mEmojiLayout.hideEmojiLayout();
            mMoreLayout.hideMoreLayout();
        }
    };
    //输入框输入监听
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                mMoreBtn.setVisibility(View.GONE);
                mSendBtn.setVisibility(View.VISIBLE);
            } else {
                mMoreBtn.setVisibility(View.VISIBLE);
                mSendBtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (mListView.getFirstVisiblePosition() == 0 && haveMoreData) {
                        List<EMMessage> messages;
                        try {
                            messages = mConversation.loadMoreMsgFromDB(mMessageAdapter.getItem(0).getMsgId(), mPagesize);
                        } catch (Exception e1) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            return;
                        }

                        if (messages.size() > 0) {
                            mMessageAdapter.refreshSeekTo(messages.size() - 1);
                            if (messages.size() != mPagesize) {
                                haveMoreData = false;
                            }
                        } else {
                            haveMoreData = false;
                        }
                    } else {
                        T.showShort(mContext, "没有更多的消息了");
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    };

    public String getToChatUsername() {
        return toChatUsername;
    }

    public ListView getListView() {
        return mListView;
    }

    public MessageAdapter getMessageAdapter() {
        return mMessageAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initConversation();
        initModule();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMessageAdapter != null) {
            mMessageAdapter.refresh();
        }

        ((IMHelper) IMHelper.getInstance()).pushActivity(this);
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMChatManager.getInstance().unregisterEventListener(this);
        // 把此activity 从foreground activity 列表里移除
        ((IMHelper) IMHelper.getInstance()).popActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //释放WakeLock
        mVoiceLayout.releaseWakeLock();
        //停止正在播放的语音
        if (VoiceClickListener.isPlaying && VoiceClickListener.currentPlayListener != null) {
            VoiceClickListener.currentPlayListener.stopPlayVoice();
        }
        //是否正在录音
        if (mVoiceLayout.isRecording()) {
            mVoiceLayout.discardRecording();
            mVoiceLayout.hideRecordHintLayout();
        }
    }

    private void initView() {
        mContext = this;
        mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mListView = (ListView) findViewById(R.id.list);
        mEditText = (PasteEditText) findViewById(R.id.et_sendmessage);
        mKeyboardBtn = (Button) findViewById(R.id.btn_set_mode_keyboard);
        mEditTextlayout = (RelativeLayout) findViewById(R.id.edittext_layout);
        mVoiceBtn = (Button) findViewById(R.id.btn_set_mode_voice);
        mSendBtn = (Button) findViewById(R.id.btn_send);

        mEmojiNormalImg = (ImageView) findViewById(R.id.iv_emoticons_normal);
        mEmojiCheckedImg = (ImageView) findViewById(R.id.iv_emoticons_checked);
        mLoadMorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        mMoreBtn = (Button) findViewById(R.id.btn_more);
        mMoreLL = (LinearLayout) findViewById(R.id.more);

        //文本框输入相关
        mEditText.setOnFocusChangeListener(mFocusChangeListener);
        mEditText.setOnClickListener(this);
        mEditText.addTextChangedListener(mTextWatcher);

        mEmojiNormalImg.setOnClickListener(this);
        mEmojiCheckedImg.setOnClickListener(this);

        //下拉加载聊天记录
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);

        //设置聊天对象的昵称
        toChatUsername = getIntent().getStringExtra("userId");
        UserUtils.setUserNick(toChatUsername, (TextView) findViewById(R.id.name));
    }

    /**
     * 初始化会话
     */
    protected void initConversation() {
        mConversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.Chat);
        // 把此会话的未读数置为0
        mConversation.markAllMessagesAsRead();

        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        /**
         * 如果conversation中存在消息且消息数<mPagesize，那么从消息数据库中加载pagesize条消息
         */
        final List<EMMessage> msgs = mConversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        //每次进入聊天页面，最多只加载pagesize条数据
        if (msgCount > 0 && msgCount < mPagesize && msgCount < mConversation.getAllMsgCount()) {
            String msgId = msgs.get(0).getMsgId();
            mConversation.loadMoreMsgFromDB(msgId, mPagesize);
        }
    }

    /**
     * 初始化模块
     */
    protected void initModule() {
        //表情模块
        mEmojiLayout = new EmojiLayout(this, mEditText);
        //更多模块
        mMoreLayout = new MoreLayout(this, mConversation);
        //录音模块
        mVoiceLayout = new SpeakLayout(this, mConversation, micImageHandler);
        //listview消息模块
        mMessageAdapter = new MessageAdapter(mContext, toChatUsername);
        mListView.setAdapter(mMessageAdapter);
        mMessageAdapter.refreshSelectLast();
        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //隐藏键盘
                KeyBoardUtils.hideKeyboard(mContext);
                mEmojiNormalImg.setVisibility(View.VISIBLE);
                mEmojiCheckedImg.setVisibility(View.INVISIBLE);

                //隐藏更多和表情
                mMoreLL.setVisibility(View.GONE);
                mEmojiLayout.hideEmojiLayout();
                mMoreLayout.hideMoreLayout();
                return false;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            //菜单返回
            switch (resultCode) {
                case RESULT_CODE_COPY: // 复制消息
                    EMMessage copyMsg = ((EMMessage) mMessageAdapter.getItem(data.getIntExtra("position", -1)));
                    String copystr = ((TextMessageBody) copyMsg.getBody()).getMessage();
                    if (!TextUtils.isEmpty(copystr)) {
                        mClipboard.setText(copystr);
                        mEditText.append(EmojiParse.parseString(mContext, copystr));
                    }
                    break;
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {// 清空会话
                EMChatManager.getInstance().clearConversation(toChatUsername);
                mMessageAdapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                mMoreLayout.sendImage();
            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                mMoreLayout.sendImageByUri(data);
            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                mMoreLayout.sendLocation(data);
            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION) {
                // 重发消息
                mConversation.getMessage(mResendPos).status = EMMessage.Status.CREATE;
                mMessageAdapter.refreshSeekTo(mResendPos);
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
                addUserToBlacklist(data);
            } else if (mConversation.getMsgCount() > 0) {
                mMessageAdapter.refresh();
            }
        }
    }

    /**
     * IM事件监听
     *
     * @param event
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: {
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                String username = message.getFrom();

                //如果是当前会话的消息，刷新聊天页面
                if (!TextUtils.isEmpty(username) && username.equals(getToChatUsername())) {
                    refreshSelectLast();
                    //声音和震动提示有新消息
                    IMHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                } else {
                    //如果消息不是和当前聊天ID的消息
                    IMHelper.getInstance().getNotifier().onNewMsg(message);
                }
                break;
            }
            case EventDeliveryAck: {//消息送达
                refresh();
                break;
            }
            case EventReadAck: {//消息已读
                refresh();
                break;
            }
            case EventOfflineMessage: {//接收离线消息
                refresh();
                break;
            }
        }
    }

    private void refreshSelectLast() {
        if (mMessageAdapter == null) {
            return;
        }
        mMessageAdapter.refreshSelectLast();
    }

    private void refresh() {
        if (mMessageAdapter == null) {
            return;
        }
        mMessageAdapter.refresh();
    }


    /**
     * 点击清空聊天记录
     *
     * @param view
     */
    public void emptyHistory(View view) {
        String st5 = getResources().getString(R.string.Whether_to_empty_all_chats);
        startActivityForResult(new Intent(this, AlertDialog.class).putExtra("titleIsCancel", true).putExtra("msg", st5)
                .putExtra("cancel", true), REQUEST_CODE_EMPTY_HISTORY);
    }

    /**
     * 消息图标点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
            mMoreLayout.sendText(mEditText.getText().toString());
            mEditText.setText("");
        } else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
            mEmojiNormalImg.setVisibility(View.INVISIBLE);
            mEmojiCheckedImg.setVisibility(View.VISIBLE);
            mMoreLL.setVisibility(View.VISIBLE);
            mMoreLayout.hideMoreLayout();
            mEmojiLayout.showEmojiLayout();
            KeyBoardUtils.hideKeyboard(mContext);
        } else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
            mEmojiNormalImg.setVisibility(View.VISIBLE);
            mEmojiCheckedImg.setVisibility(View.INVISIBLE);
            mMoreLayout.hideMoreLayout();
            mEmojiLayout.hideEmojiLayout();
            mMoreLL.setVisibility(View.GONE);
            KeyBoardUtils.showKeyboard(mContext);
        } else if (id == R.id.et_sendmessage) {//输入框点击
            mEditTextlayout.setBackgroundResource(R.drawable.input_bar_bg_active);
            mEmojiNormalImg.setVisibility(View.VISIBLE);
            mEmojiCheckedImg.setVisibility(View.INVISIBLE);

            mMoreLL.setVisibility(View.GONE);
            mEmojiLayout.hideEmojiLayout();
            mMoreLayout.hideMoreLayout();
        }
    }

    /**
     * 点击语音图标按钮
     *
     * @param view
     */
    public void onClickVoice(View view) {
        KeyBoardUtils.hideKeyboard(mContext);

        mMoreLL.setVisibility(View.GONE);
        mEmojiLayout.hideEmojiLayout();
        mMoreLayout.hideMoreLayout();

        mEditTextlayout.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        mSendBtn.setVisibility(View.GONE);
        mEmojiCheckedImg.setVisibility(View.GONE);

        mVoiceLayout.showVoiceLayout();
        mKeyboardBtn.setVisibility(View.VISIBLE);
        mMoreBtn.setVisibility(View.VISIBLE);
        mEmojiNormalImg.setVisibility(View.VISIBLE);
    }

    /**
     * 点击键盘按钮
     *
     * @param view
     */
    public void onClickKeyboard(View view) {
        KeyBoardUtils.showKeyboard(mContext);

        mMoreLL.setVisibility(View.GONE);
        mEmojiLayout.hideEmojiLayout();
        mMoreLayout.hideMoreLayout();

        //隐藏语音输入布局
        mVoiceLayout.hideVoiceLayout();
        //显示输入框布局
        mEditTextlayout.setVisibility(View.VISIBLE);
        //隐藏键盘图标
        mKeyboardBtn.setVisibility(View.GONE);
        //显示语音图标
        mVoiceBtn.setVisibility(View.VISIBLE);
        //输入框请求焦点
        mEditText.requestFocus();

        if (TextUtils.isEmpty(mEditText.getText())) {
            mMoreBtn.setVisibility(View.VISIBLE);
            mSendBtn.setVisibility(View.GONE);
        } else {
            mMoreBtn.setVisibility(View.GONE);
            mSendBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击更多按钮
     *
     * @param view
     */
    public void onClickMore(View view) {
        if (mMoreLL.getVisibility() == View.VISIBLE) {
            if (mEmojiLayout.getEmojiLayoutVisibility() == View.VISIBLE) {
                mEmojiLayout.hideEmojiLayout();
                mMoreLayout.showMoreLayout();
            } else {
                mMoreLayout.hideMoreLayout();
            }
        } else {
            mMoreLL.setVisibility(View.VISIBLE);
            KeyBoardUtils.hideKeyboard(mContext);
            mEmojiLayout.hideEmojiLayout();
            mMoreLayout.showMoreLayout();

            if (mVoiceLayout.getVoiceLayoutVisibility() == View.VISIBLE) {
                mVoiceLayout.hideVoiceLayout();
                mEditTextlayout.setVisibility(View.VISIBLE);

                //隐藏键盘图标
                mKeyboardBtn.setVisibility(View.GONE);
                //显示语音图标
                mVoiceBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 加入到黑名单
     *
     * @param data
     */
    private void addUserToBlacklist(Intent data) {
        if (data == null) {
            return;
        }

        EMMessage deleteMsg = mMessageAdapter.getItem(data.getIntExtra("position", -1));
        final String username = deleteMsg.getFrom();
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.Is_moved_into_blacklist));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().addUserToBlackList(username, false);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_failure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        EMChatManager.getInstance().unregisterEventListener(this);
        finish();
    }

    /**
     * 覆盖手机返回键
     */
    @Override
    public void onBackPressed() {
        if (mMoreLL.getVisibility() == View.VISIBLE) {
            mMoreLL.setVisibility(View.GONE);
            mEmojiLayout.hideEmojiLayout();
            mMoreLayout.hideMoreLayout();
            mEmojiNormalImg.setVisibility(View.VISIBLE);
            mEmojiCheckedImg.setVisibility(View.INVISIBLE);
        } else {
            EMChatManager.getInstance().unregisterEventListener(this);
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            //通知消息当前聊天页面相同
            super.onNewIntent(intent);
        else {
            //通知消息当前聊天页面不同
            finish();
            startActivity(intent);
        }
    }


}
