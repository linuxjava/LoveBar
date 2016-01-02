package xiao.love.bar.fragment.main;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import butterknife.Bind;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import xiao.love.bar.R;
import xiao.love.bar.activity.im.CollectActivity;
import xiao.love.bar.adapter.XGCOnItemChildClickListener;
import xiao.love.bar.adapter.XGCOnRVItemClickListener;
import xiao.love.bar.adapter.impl.ChatAllHistoryAdapter;
import xiao.love.bar.component.util.KeyBoardUtils;
import xiao.love.bar.fragment.BaseFragment;
import xiao.love.bar.im.chat.ChatActivity;
import xiao.love.bar.mvppresenter.BaseFragmentPresenter;

/**
 * Created by guochang on 2015/9/24.
 */
public class ChatHistoryFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate,
        XGCOnItemChildClickListener, XGCOnRVItemClickListener {
    @Bind(R.id.recyclerview_refresh)
    BGARefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.rl_error_hint)
    RelativeLayout mErrorItemRL;
    @Bind(R.id.text_net_error_msg)
    TextView mErrorText;

    private List<EMConversation> mConversationList;
    private ChatAllHistoryAdapter mAdapter;


    public TextView getErrorText() {
        return mErrorText;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_conversation_history;
    }

    @Override
    protected void initWidgets() {
        mConversationList = new ArrayList<EMConversation>();
        mConversationList.addAll(loadConversationsWithRecentChat());

        mAdapter = new ChatAllHistoryAdapter(getActivity());
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setOnRVItemClickListener(this);
        mAdapter.setDatas(mConversationList);

        BGANormalRefreshViewHolder viewHolder = new BGANormalRefreshViewHolder(mContext, false);
        mRefreshLayout.setRefreshViewHolder(viewHolder);
        mRefreshLayout.setDelegate(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mAdapter.closeOpenedSwipeItemLayoutWithAnim();
                KeyBoardUtils.hideKeyboard(mContext);
                return false;
            }
        });
    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        mConversationList.clear();
        mConversationList.addAll(loadConversationsWithRecentChat());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * fragment被hide或show时被调用
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refresh();
        } else {
            //fragment被隐藏时，关闭所有item
            mAdapter.closeOpenedSwipeItemLayoutWithAnim();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * 获取所有会话
     *
     * @return
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<EMConversation> list = new ArrayList<EMConversation>();
        //回话列表里，最顶部是"收藏"
        list.add(new EMConversation("收藏"));
        //添加测试数据
//        for (int i=0; i<15; i++){
//            list.add(new EMConversation(i+""));
//        }
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }

        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {
                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    /**
     * 点击删除按钮
     *
     * @param parent
     * @param childView
     * @param position
     */
    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        EMConversation conversation = mAdapter.getItem(position);
        if (conversation != null && !TextUtils.isEmpty(conversation.getUserName())) {
            //删除会话
            EMChatManager.getInstance().deleteConversation(conversation.getUserName(), conversation.isGroup(), true);
            mAdapter.removeItem(position);
        }
    }

    /**
     * 下拉刷新
     *
     * @param bgaRefreshLayout
     */
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.endRefreshing();
            }
        }, 1000);

    }

    /**
     * recyclerview item点击
     *
     * @param parent
     * @param itemView
     * @param position
     */
    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        if (position == 0) {//点击收藏
            startActivity(new Intent(mContext, CollectActivity.class));
            return;
        }
        EMConversation conversation = mAdapter.getItem(position);
        String username = conversation.getUserName();

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userId", username);
        startActivity(intent);
    }

    /**
     * 加载更多
     *
     * @param bgaRefreshLayout
     * @return
     */
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.endLoadingMore();
            }
        }, 1000);

        return true;
    }


}
