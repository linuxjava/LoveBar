package xiao.love.bar.im.history;

import android.animation.AnimatorInflater;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import xiao.love.bar.R;
import xiao.love.bar.component.BaseFragment;
import xiao.love.bar.component.util.DensityUtils;
import xiao.love.bar.component.util.KeyBoardUtils;
import xiao.love.bar.im.chat.ChatActivity;
import xiao.love.bar.im.collect.CollectActivity_;
import xiao.love.bar.im.collect.CollectFragment_;

/**
 * Created by guochang on 2015/9/24.
 */
@EFragment(R.layout.fragment_conversation_history)
public class ChatHistoryFragment extends BaseFragment {
    @ViewById(R.id.list)
    public SwipeMenuListView mListView;
    @ViewById(R.id.rl_error_item)
    public RelativeLayout mErrorItem;
    @ViewById(R.id.tv_connect_errormsg)
    public TextView mErrorText;

    private Activity mContext;
    private List<EMConversation> mConversationList;
    private ChatAllHistoryAdapter mAdapter;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {//点击收藏
                startActivity(new Intent(mContext, CollectActivity_.class));
                return;
            }
            EMConversation conversation = mAdapter.getItem(position);
            String username = conversation.getUserName();

            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("userId", username);
            startActivity(intent);
        }
    };
    //滑动菜单点击
    private SwipeMenuListView.OnMenuItemClickListener mMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu swipeMenu, int index) {
            //删除会话
            EMConversation conversation = mAdapter.getItem(position);

            if (conversation != null && !TextUtils.isEmpty(conversation.getUserName())) {
                EMChatManager.getInstance().deleteConversation(conversation.getUserName(), conversation.isGroup(), true);
                //删除listview中的item, remove会自动notifyDataSetChanged
                //mAdapter.remove(conversation);
                deletePattern(mListView.getChildAt(position-mListView.getFirstVisiblePosition()), position);
                //removeListItem(mListView.getChildAt(position-mListView.getFirstVisiblePosition()), position);
            }
            return false;
        }
    };
    //创建滑动菜单
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu swipeMenu) {
            switch (swipeMenu.getViewType()) {
                case 0://不创建滑动菜单
                    break;
                case 1://创建包含"删除"按钮的菜单
                    SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
                    deleteItem.setBackground(R.color.history_item_del_bg);
                    deleteItem.setWidth(DensityUtils.dp2px(mContext, 70f));
                    deleteItem.setIcon(R.drawable.ic_delete);
                    swipeMenu.addMenuItem(deleteItem);
                    break;
            }
        }
    };

    protected void removeListItem(View rowView, final int position) {

        final Animation animation = (Animation) AnimationUtils.loadAnimation(rowView.getContext(), R.anim.item_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}

            public void onAnimationRepeat(Animation animation) {}

            public void onAnimationEnd(Animation animation) {
                mAdapter.remove(mAdapter.getItem(position));
            }
        });


        rowView.startAnimation(animation);

    }

    private void deletePattern(final View view, final int position) {

        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.remove(mAdapter.getItem(1));
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        collapse(view, al, position);
    }

    private void collapse(final View view, Animation.AnimationListener al, final int position) {
        final int originHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1.0f) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = originHeight - (int) (originHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        if (al != null) {
            animation.setAnimationListener(al);
        }
        animation.setDuration(10000);
        view.startAnimation(animation);
    }

    @AfterViews
    void init() {
        mContext = getActivity();
        mConversationList = new ArrayList<EMConversation>();
        mConversationList.addAll(loadConversationsWithRecentChat());

        mAdapter = new ChatAllHistoryAdapter(getActivity(), 1, mConversationList);

        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        mListView.setCloseInterpolator(new BounceInterpolator());
        mListView.setMenuCreator(mSwipeMenuCreator);
        mListView.setOnMenuItemClickListener(mMenuItemClickListener);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                KeyBoardUtils.hideKeyboard(mContext);
                return false;
            }
        });

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
     * @return +
     */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**c
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
        list.add(new EMConversation("收藏"));
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

}
