package xiao.love.bar.activity;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.NetUtils;

import butterknife.Bind;
import butterknife.OnClick;
import xiao.love.bar.R;
import xiao.love.bar.component.resource.ResTool;
import xiao.love.bar.fragment.main.ChatHistoryFragment;
import xiao.love.bar.fragment.main.ContactFragment;
import xiao.love.bar.fragment.main.HomeFragment;
import xiao.love.bar.fragment.main.MyFragment;
import xiao.love.bar.im.hxlib.IMHelper;

public class MainActivity extends BaseFragmentActivity implements EMEventListener {
    // 未读消息textview
    @Bind(R.id.text_unread_msg_number)
    TextView mUnreadText;
    // 未读通讯录textview
    @Bind(R.id.text_unread_contact_number)
    TextView mUnreadContactText;
    @Bind({R.id.btn_home, R.id.btn_contact, R.id.btn_conversation, R.id.btn_my})
    Button[] mTabArray;
    //所有Fragment页面
    private Fragment[] mFragmentArray;
    // 历史聊天页面的index
    private int mChatHistoryTabIndex = 2;
    //历史聊天页面
    private ChatHistoryFragment mChatHistoryFragment;
    // 当前fragment的index
    private int mCurrentTabIndex = 0;
    //IM连接监听
    private EMConnectionListener mConnectionListener = new EMConnectionListener() {
        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCurrentTabIndex == mChatHistoryTabIndex) {
                        //tab显示历史聊天页面时，才显示信息提示
                        mChatHistoryFragment.getErrorText().setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                    } else {
                        if (mCurrentTabIndex == 2) {
                            //tab显示历史聊天页面时，才显示信息提示
                            mChatHistoryFragment.getErrorText().setVisibility(View.VISIBLE);
                            if (NetUtils.hasNetwork(MainActivity.this))
                                mChatHistoryFragment.getErrorText().setText(ResTool.getString(mContext, R.string.can_not_connect_chat_server_connection));
                            else
                                mChatHistoryFragment.getErrorText().setText(ResTool.getString(mContext, R.string.the_current_network));
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        updateUnreadLabel();
        EMChatManager.getInstance().activityResumed();

        ((IMHelper) IMHelper.getInstance()).pushActivity(this);
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
    protected void onDestroy() {
        super.onDestroy();

        EMChatManager.getInstance().removeConnectionListener(mConnectionListener);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidgets() {
        // 把第一个tab设为选中状态
        mTabArray[mCurrentTabIndex].setSelected(true);

        mChatHistoryFragment = new ChatHistoryFragment();
        mFragmentArray = new Fragment[]{new HomeFragment(), new ContactFragment(), mChatHistoryFragment, new MyFragment()};
        //设置fragment的容器
        setFragmentContainerId(R.id.rl_container);
        // 添加并显示首页fragment
        addFragment(mFragmentArray[0]);
        //添加IM连接状态监听
        EMChatManager.getInstance().addConnectionListener(mConnectionListener);
    }

    @OnClick({R.id.btn_home, R.id.btn_contact, R.id.btn_conversation, R.id.btn_my})
    public void onTabClick(View v) {
        int index = 0;
        switch (v.getId()) {
            case R.id.btn_home:
                index = 0;
                break;
            case R.id.btn_contact:
                index = 1;
                break;
            case R.id.btn_conversation:
                index = 2;
                break;
            case R.id.btn_my:
                index = 3;
                break;
        }

        if (mCurrentTabIndex != index) {
            // 添加并显示fragment
            showFragment(mFragmentArray[index]);

            mTabArray[mCurrentTabIndex].setSelected(false);
            // 把当前tab设为选中状态
            mTabArray[index].setSelected(true);
            mCurrentTabIndex = index;
        }
    }

    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        switch (emNotifierEvent.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) emNotifierEvent.getData();
                // 提示新消息
                IMHelper.getInstance().getNotifier().onNewMsg(message);
                refreshUI();
                break;
            }
            case EventOfflineMessage: {
                refreshUI();
                break;
            }
            case EventConversationListChanged: {
                //当会话被删除时，产生此事件，那么需要更新未读消息数
                updateUnreadLabel();
                //refreshUI();
                break;
            }
        }
    }

    private void refreshUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                updateUnreadLabel();
                if (mCurrentTabIndex == mChatHistoryTabIndex) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (mChatHistoryFragment != null) {
                        mChatHistoryFragment.refresh();
                    }
                }
            }
        });
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            mUnreadText.setText(String.valueOf(count));
            mUnreadText.setVisibility(View.VISIBLE);
        } else {
            mUnreadText.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        int chatroomUnreadMsgCount = 0;
        for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount += conversation.getUnreadMsgCount();
        }

        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }
}
