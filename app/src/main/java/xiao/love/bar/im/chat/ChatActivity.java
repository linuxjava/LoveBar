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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.VoiceRecorder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import xiao.love.bar.R;
import xiao.love.bar.component.BaseActivity;
import xiao.love.bar.im.chat.emoji.EmojiLayout;
import xiao.love.bar.im.chat.more.MoreLayout;
import xiao.love.bar.im.chat.voice.SpeakLayout;
import xiao.love.bar.im.hxlib.IMHelper;
import xiao.love.bar.im.hxlib.model.GroupRemoveListener;
import xiao.love.bar.im.util.IMImageUtils;
import xiao.love.bar.im.util.UserUtils;
import xiao.love.bar.im.widget.PasteEditText;

/**
 * 聊天页面
 *
 */
public class ChatActivity extends BaseActivity implements OnClickListener, EMEventListener {
    private static final String TAG = "ChatActivity";
    public static final int REQUEST_CODE_EMPTY_HISTORY = 2;
    public static final int REQUEST_CODE_CONTEXT_MENU = 3;
    public static final int REQUEST_CODE_MAP = 4;
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_LOCATION = 8;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_FILE = 10;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_PICK_VIDEO = 12;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_VIDEO = 14;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_GROUP_DETAIL = 21;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;
    public static final int REQUEST_CODE_SELECT_FILE = 24;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;

    public static final String COPY_IMAGE = "EASEMOBIMG";
    private View recordingContainer;
    //录音时音量图片
    private ImageView micImage;
    private TextView recordingHint;
    private ListView listView;
    private PasteEditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    //private View buttonPressToSpeak;
    //private LinearLayout emojiIconContainer;
    //private LinearLayout btnContainer;
    private ImageView locationImgview;
    private View more;
    private int position;
    private ClipboardManager clipboard;
    //private ViewPager expressionViewpager;
    private InputMethodManager manager;
    //private List<String> reslist;
    private Drawable[] micImages;
    private int chatType;
    private EMConversation conversation;
    public static ChatActivity activityInstance = null;
    // 给谁发送消息
    private String toChatUsername;
    //private VoiceRecorder voiceRecorder;
    private MessageAdapter adapter;
    private File cameraFile;
    //重发的消息所在的位置
    static int resendPos;

    private GroupListener groupListener;

    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private RelativeLayout edittext_layout;
    private ProgressBar loadmorePB;
    //swipeRefreshLayout是否正在刷新
    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    private Button btnMore;
    //当前播放语音的消息id
    public String playMsgId;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            mVoiceLayout.setMicImg(msg.what);
        }
    };
    public EMGroup group;
    public EMChatRoom room;
    public boolean isRobot;
    //表情布局
    private EmojiLayout mEmojiLayout;
    //更多布局(发送图片、位置)
    private MoreLayout mMoreLayout;
    //录音布局
    private SpeakLayout mVoiceLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activityInstance = this;
        initView();
        setUpView();

        mEmojiLayout = new EmojiLayout(this, mEditTextContent);
        mMoreLayout = new MoreLayout(this, conversation);
        mVoiceLayout = new SpeakLayout(this, conversation, micImageHandler);
    }

    /**
     * initView
     */
    protected void initView() {
        listView = (ListView) findViewById(R.id.list);
        mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        btnMore = (Button) findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        more = findViewById(R.id.more);
        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
//        voiceCallBtn = (ImageView) findViewById(R.id.btn_voice_call);
//        videoCallBtn = (ImageView) findViewById(R.id.btn_video_call);

        //文本框输入相关
        mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }

            }
        });
        mEditTextContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                //emojiIconContainer.setVisibility(View.GONE);
                mEmojiLayout.hideEmojiLayout();
                //btnContainer.setVisibility(View.GONE);
                mMoreLayout.hideMoreLayout();
            }
        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //下拉加载聊天记录
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (chatType == CHATTYPE_SINGLE) {
                                    messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
                                } else {
                                    messages = conversation.loadMoreGroupMsgFromDB(adapter.getItem(0).getMsgId(), pagesize);
                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }

                            if (messages.size() > 0) {
                                adapter.notifyDataSetChanged();
                                adapter.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            Toast.makeText(ChatActivity.this, getResources().getString(R.string.no_more_messages), Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void setUpView() {
        iv_emoticons_normal.setOnClickListener(this);
        iv_emoticons_checked.setOnClickListener(this);
        // position = getIntent().getIntExtra("position", -1);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // 判断单聊还是群聊
        chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);

        if (chatType == CHATTYPE_SINGLE) { // 单聊
            //设置聊天对象的昵称
            toChatUsername = getIntent().getStringExtra("userId");
            UserUtils.setUserNick(toChatUsername, (TextView) findViewById(R.id.name));
        } else {
            // 群聊
            //显示右上角群聊图片
            findViewById(R.id.container_to_group).setVisibility(View.VISIBLE);
            findViewById(R.id.container_remove).setVisibility(View.GONE);
            //群聊不支持视频和语音
            findViewById(R.id.container_voice_call).setVisibility(View.GONE);
            findViewById(R.id.container_video_call).setVisibility(View.GONE);
            toChatUsername = getIntent().getStringExtra("groupId");

            if (chatType == CHATTYPE_GROUP) {
                //群聊
                onGroupViewCreation();
            } else {
                //聊天室
                onChatRoomViewCreation();
            }
        }

        // for chatroom type, we only init conversation and create view adapter on success
        //聊天室进入成功后会自动调用onConversationInit和onListViewCreation
        if (chatType != CHATTYPE_CHATROOM) {
            onConversationInit();
            onListViewCreation();
        }
    }

    protected void onConversationInit() {
        if (chatType == CHATTYPE_SINGLE) {
            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.Chat);
        } else if (chatType == CHATTYPE_GROUP) {
            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.GroupChat);
        } else if (chatType == CHATTYPE_CHATROOM) {
            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.ChatRoom);
        }

        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();

        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        /**
         * 如果conversation中存在消息且消息数<pagesize，那么从消息数据库中加载pagesize条消息
         */
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        //每次进入聊天页面，最多只加载pagesize条数据
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            if (chatType == CHATTYPE_SINGLE) {
                conversation.loadMoreMsgFromDB(msgId, pagesize);
            } else {
                conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
            }
        }

        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(toChatUsername)) {
                    finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                if (roomId.equals(toChatUsername)) {
                    String curUser = EMChatManager.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMChatManager.getInstance().leaveChatRoom(toChatUsername);
                        finish();
                    }
                }
            }

        });
    }

    /**
     * 聊天listview对话设置
     */
    protected void onListViewCreation() {
        adapter = new MessageAdapter(ChatActivity.this, toChatUsername, chatType);
        // 显示消息
        listView.setAdapter(adapter);

        //listView.setOnScrollListener(new ListScrollListener());
        adapter.refreshSelectLast();

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //隐藏键盘
                hideKeyboard();
                //隐藏表情
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                //emojiIconContainer.setVisibility(View.GONE);
                mEmojiLayout.hideEmojiLayout();
                //btnContainer.setVisibility(View.GONE);
                mMoreLayout.hideMoreLayout();
                return false;
            }
        });
    }

    /**
     * 初始化群聊
     */
    protected void onGroupViewCreation() {
        group = EMGroupManager.getInstance().getGroup(toChatUsername);

        if (group != null) {
            ((TextView) findViewById(R.id.name)).setText(group.getGroupName());
        } else {
            ((TextView) findViewById(R.id.name)).setText(toChatUsername);
        }

        // 监听当前会话的群聊解散被T事件
        groupListener = new GroupListener();
        EMGroupManager.getInstance().addGroupChangeListener(groupListener);
    }

    /**
     * 初始化聊天室
     */
    protected void onChatRoomViewCreation() {
        //聊天室无右上角图片
        findViewById(R.id.container_to_group).setVisibility(View.GONE);

        final ProgressDialog pd = ProgressDialog.show(this, "", "Joining......");
        EMChatManager.getInstance().joinChatRoom(toChatUsername, new EMValueCallBack<EMChatRoom>() {

            @Override
            public void onSuccess(EMChatRoom value) {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        room = EMChatManager.getInstance().getChatRoom(toChatUsername);
                        if (room != null) {
                            ((TextView) findViewById(R.id.name)).setText(room.getName());
                        } else {
                            ((TextView) findViewById(R.id.name)).setText(toChatUsername);
                        }
                        EMLog.d(TAG, "join room success : " + room.getName());

                        onConversationInit();

                        onListViewCreation();
                    }
                });
            }

            @Override
            public void onError(final int error, String errorMsg) {
                // TODO Auto-generated method stub
                EMLog.d(TAG, "join room failure : " + error);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            //菜单返回
            switch (resultCode) {
                case RESULT_CODE_COPY: // 复制消息
                    EMMessage copyMsg = ((EMMessage) adapter.getItem(data.getIntExtra("position", -1)));
                    clipboard.setText(((TextMessageBody) copyMsg.getBody()).getMessage());
                    mEditTextContent.append(((TextMessageBody) copyMsg.getBody()).getMessage());
                    break;
                default:
                    break;
            }
        }else if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {// 清空会话
                EMChatManager.getInstance().clearConversation(toChatUsername);
                adapter.refresh();
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                mMoreLayout.sendImage();
            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                mMoreLayout.sendImageByUri(data);
            } else if (requestCode == REQUEST_CODE_MAP) { // 地图
                mMoreLayout.sendLocation(data);
            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION) {
                // 重发消息
                conversation.getMessage(resendPos).status = EMMessage.Status.CREATE;
                adapter.refreshSeekTo(resendPos);
            } else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
                addUserToBlacklist(data);
            } else if (conversation.getMsgCount() > 0) {
                adapter.refresh();
                setResult(RESULT_OK);
            } else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
                adapter.refresh();
            }
        }
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
            mMoreLayout.sendText(mEditTextContent.getText().toString());
            mEditTextContent.setText("");
        } else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
            more.setVisibility(View.VISIBLE);
            iv_emoticons_normal.setVisibility(View.INVISIBLE);
            iv_emoticons_checked.setVisibility(View.VISIBLE);
            //btnContainer.setVisibility(View.GONE);
            mMoreLayout.hideMoreLayout();
            mEmojiLayout.showEmojiLayout();
            hideKeyboard();
        } else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
            //btnContainer.setVisibility(View.VISIBLE);
            mMoreLayout.showMoreLayout();
            mEmojiLayout.hideEmojiLayout();
            more.setVisibility(View.GONE);
            showKeyboard();
        }
    }

    /**
     * 事件监听
     *
     * see {@link EMNotifierEvent}
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: {
                //获取到message
                EMMessage message = (EMMessage) event.getData();

                String username = null;
                //群组消息
                if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    //单聊消息
                    username = message.getFrom();
                }

                //如果是当前会话的消息，刷新聊天页面
                if (username.equals(getToChatUsername())) {
                    refreshUIWithNewMessage();
                    //声音和震动提示有新消息
                    IMHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                } else {
                    //如果消息不是和当前聊天ID的消息
                    IMHelper.getInstance().getNotifier().onNewMsg(message);
                }

                break;
            }
            case EventDeliveryAck: {//消息送达
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                refreshUI();
                break;
            }
            case EventReadAck: {//消息已读
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                refreshUI();
                break;
            }
            case EventOfflineMessage: {//接收离线消息
                //a list of offline messages
                //List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();
                refreshUI();
                break;
            }
            default:
                break;
        }

    }


    private void refreshUIWithNewMessage() {
        if (adapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refreshSelectLast();
            }
        });
    }

    private void refreshUI() {
        if (adapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refresh();
            }
        });
    }

    /**
     * 显示语音图标按钮
     *
     * @param view
     */
    public void setModeVoice(View view) {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        btnMore.setVisibility(View.VISIBLE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        //btnContainer.setVisibility(View.VISIBLE);
        mMoreLayout.showMoreLayout();

        mEmojiLayout.hideEmojiLayout();
        mVoiceLayout.showVoiceLayout();
    }

    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        // mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
        // {
        // @Override
        // public void onFocusChange(View v, boolean hasFocus) {
        // if(hasFocus){
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        // }
        // }
        // });
        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        mVoiceLayout.hideVoiceLayout();
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }
        showKeyboard();
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
     * 点击进入群组详情
     *
     * @param view
     */
    public void toGroupDetails(View view) {
//        if (room == null && group == null) {
//            Toast.makeText(getApplicationContext(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (chatType == CHATTYPE_GROUP) {
//            startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
//                    REQUEST_CODE_GROUP_DETAIL);
//        } else {
//            startActivityForResult((new Intent(this, ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername)),
//                    REQUEST_CODE_GROUP_DETAIL);
//        }
    }

    /**
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
    public void toggleMore(View view) {
        if (more.getVisibility() == View.GONE) {
            EMLog.d(TAG, "more gone");
            hideKeyboard();
            more.setVisibility(View.VISIBLE);
            //btnContainer.setVisibility(View.VISIBLE);
            mMoreLayout.showMoreLayout();
            //emojiIconContainer.setVisibility(View.GONE);
            mEmojiLayout.hideEmojiLayout();
        } else {
            if (mEmojiLayout.getEmojiLayoutVisibility() == View.VISIBLE) {
                //emojiIconContainer.setVisibility(View.GONE);
                mEmojiLayout.hideEmojiLayout();
                //btnContainer.setVisibility(View.VISIBLE);
                mMoreLayout.showMoreLayout();
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }

        }

    }

    /**
     * 点击文字输入框
     *
     * @param v
     */
    public void editClick(View v) {
        listView.setSelection(listView.getCount() - 1);
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

    }

    @Override
    public void getNetwork(String uri, String tag) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        if (groupListener != null) {
            EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.refresh();
        }

        IMHelper sdkHelper = (IMHelper) IMHelper.getInstance();
        sdkHelper.pushActivity(this);
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck, EMNotifierEvent.Event.EventReadAck});
    }

    @Override
    protected void onStop() {
        // unregister this event listener when this activity enters the
        // background
        EMChatManager.getInstance().unregisterEventListener(this);

        IMHelper sdkHelper = (IMHelper) IMHelper.getInstance();

        // 把此activity 从foreground activity 列表里移除
        sdkHelper.popActivity(this);

        super.onStop();
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
        if(mVoiceLayout.isRecording()){
            mVoiceLayout.discardRecording();
            mVoiceLayout.hideRecordHintLayout();
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    private void showKeyboard() {
        manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 加入到黑名单
     * @param data
     */
    private void addUserToBlacklist(Intent data) {
        if(data == null){
            return;
        }

        EMMessage deleteMsg = adapter.getItem(data.getIntExtra("position", -1));
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
        if (chatType == CHATTYPE_CHATROOM) {
            EMChatManager.getInstance().leaveChatRoom(toChatUsername);
        }
        finish();
    }

    /**
     * 覆盖手机返回键
     */
    @Override
    public void onBackPressed() {
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
            if (chatType == CHATTYPE_CHATROOM) {
                EMChatManager.getInstance().leaveChatRoom(toChatUsername);
            }
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

    /**
     * 监测群组解散或者被T事件
     *
     */
    class GroupListener extends GroupRemoveListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            //用户被移除群组
//            runOnUiThread(new Runnable() {
//                String st13 = getResources().getString(R.string.you_are_group);
//
//                public void run() {
//                    if (toChatUsername.equals(groupId)) {
//                        //判断是否是自己
//                        Toast.makeText(ChatActivity.this, st13, Toast.LENGTH_LONG).show();
//                        if (GroupDetailsActivity.instance != null)
//                            GroupDetailsActivity.instance.finish();
//                        finish();
//                    }
//                }
//            });
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            // 群组解散正好在此页面，提示群组被解散，并finish此页面
//            runOnUiThread(new Runnable() {
//                String st14 = getResources().getString(R.string.the_current_group);
//
//                public void run() {
//                    if (toChatUsername.equals(groupId)) {
//                        Toast.makeText(ChatActivity.this, st14, Toast.LENGTH_LONG).show();
//                        if (GroupDetailsActivity.instance != null)
//                            GroupDetailsActivity.instance.finish();
//                        finish();
//                    }
//                }
//            });
        }

    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    public ListView getListView() {
        return listView;
    }

    public MessageAdapter getAdapter(){
        return adapter;
    }
}
