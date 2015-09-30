package xiao.love.bar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import xiao.love.bar.component.BaseActivity;
import xiao.love.bar.im.history.ChatHistoryFragment_;
import xiao.love.bar.im.history.ContactFragment_;
import xiao.love.bar.im.history.Test2Fragment;
import xiao.love.bar.im.hxlib.IMHelper;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements EMEventListener {
    // 未读消息textview
    @ViewById(R.id.unread_msg_number_tv)
    TextView mUnreadLabel;
    // 未读通讯录textview
    @ViewById(R.id.unread_contact_number_tv)
    TextView mUnreadContactLable;

    private Fragment[] mFragments;
    private ChatHistoryFragment_ mChatHistoryFragment;
    private Button[] mTabs;
    // 当前fragment的index
    private int mCurrentTabIndex;
    //IM连接监听
    private EMConnectionListener mConnectionListener = new EMConnectionListener() {
        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mChatHistoryFragment.mErrorItem.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onDisconnected(final int error) {
            final String st1 = getResources().getString(R.string.can_not_connect_chat_server_connection);
            final String st2 = getResources().getString(R.string.the_current_network);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                    } else {
                        mChatHistoryFragment.mErrorItem.setVisibility(View.VISIBLE);
                        if (NetUtils.hasNetwork(MainActivity.this))
                            mChatHistoryFragment.mErrorText.setText(st1);
                        else
                            mChatHistoryFragment.mErrorText.setText(st2);
                    }
                }
            });


        }
    };

    private void test() {
        //测试相册
        //PhotoPickTest.test(this);
    }

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

    /**
     * 初始化组件
     */
    @AfterViews
    void init() {
        mTabs = new Button[3];
        mTabs[0] = (Button) findViewById(R.id.conversation_btn);
        mTabs[1] = (Button) findViewById(R.id.contact_btn);
        mTabs[2] = (Button) findViewById(R.id.setting_btn);
        // 把第一个tab设为选中状态
        mTabs[0].setSelected(true);

        EMChatManager.getInstance().addConnectionListener(mConnectionListener);

        mChatHistoryFragment = new ChatHistoryFragment_();
        mFragments = new Fragment[] {mChatHistoryFragment, new ContactFragment_(), new Test2Fragment() };
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mFragments[0])
                .add(R.id.fragment_container, mFragments[1])
                .hide(mFragments[1])
                .show(mFragments[0])
                .commit();
    }

    public void onTabClicked(View v) {
        int index = 0;
        switch (v.getId()) {
            case R.id.conversation_btn:
                index = 0;
                break;
            case R.id.contact_btn:
                index = 1;
                break;
            case R.id.setting_btn:
                index = 2;
                break;
        }

        if (mCurrentTabIndex == index) {

        } else {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[mCurrentTabIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commit();

            mTabs[mCurrentTabIndex].setSelected(false);
            // 把当前tab设为选中状态
            mTabs[index].setSelected(true);
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
                if (mCurrentTabIndex == 0) {
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
            mUnreadLabel.setText(String.valueOf(count));
            mUnreadLabel.setVisibility(View.VISIBLE);
        } else {
            mUnreadLabel.setVisibility(View.INVISIBLE);
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
