package xiao.love.bar.im.collect;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import xiao.love.bar.R;
import xiao.love.bar.component.BaseActivity;

/**
 * Created by guochang on 2015/9/28.
 * 收藏和被收藏
 */
@EActivity(R.layout.collect_main_layout)
public class CollectActivity extends BaseActivity  implements BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnItemChildClickListener {
//    @ViewById(R.id.viewpager)
//    ViewPager mViewPager;
    @ViewById(R.id.tab_left_img)
    ImageView mTabLeftImg;
    @ViewById(R.id.tab_right_img)
    ImageView mTabRightImg;
    @ViewById(R.id.tab_left_tv)
    TextView mTabLeftTv;
    @ViewById(R.id.tab_right_tv)
    TextView mTabRightTv;
    @ViewById(R.id.listview_refresh)
    BGARefreshLayout mRefreshLayout;
    @ViewById(R.id.listview)
    ListView mListView;

    private Context mContext;
    private CollectAdapter mAdapter;
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    @AfterViews
    void init() {
        mContext = this;
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(mContext, true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new Integer(i));
        }
        mAdapter = new CollectAdapter(mContext);
        mAdapter.setDatas(list);
        mAdapter.setOnItemChildClickListener(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);

        mTabLeftImg.setBackgroundResource(R.drawable.ic_button_left_pressed);
        mTabLeftTv.setTextColor(getResources().getColor(R.color.holo_blue_dark));

        LayoutTransition mTransition = new LayoutTransition();
        mTransition.setAnimator(LayoutTransition.APPEARING, null);
        mTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, null);
        mTransition.setAnimator(LayoutTransition.DISAPPEARING, mTransition.getAnimator(LayoutTransition.DISAPPEARING));
        mTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, null);

        mListView.setLayoutTransition(mTransition);

    }

    @Click({R.id.tab_left_img, R.id.tab_right_img})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_left_img:
                mTabLeftImg.setBackgroundResource(R.drawable.ic_button_left_pressed);
                mTabLeftTv.setTextColor(getResources().getColor(R.color.holo_blue_dark));
                mTabRightImg.setBackgroundResource(R.drawable.ic_button_right);
                mTabRightTv.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case R.id.tab_right_img:
                mTabRightImg.setBackgroundResource(R.drawable.ic_button_right_pressed);
                mTabRightTv.setTextColor(getResources().getColor(R.color.holo_blue_dark));
                mTabLeftImg.setBackgroundResource(R.drawable.ic_button_left);
                mTabLeftTv.setTextColor(getResources().getColor(android.R.color.white));
                break;
        }
    }

    /**
     * 下拉刷新
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
     * 上拉加载更多
     * @param bgaRefreshLayout
     * @return
     */
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Object> list = new ArrayList<>();
                for (int i = 20; i < 30; i++) {
                    list.add(new Integer(i));
                }

                mAdapter.addMoreDatas(list);
                mRefreshLayout.endLoadingMore();
            }
        }, 500);

        return true;
    }

    @Override
    public void onItemChildClick(ViewGroup viewGroup, View view, int i) {
        switch (view.getId()) {
            case R.id.collect_img:
                mAdapter.removeItem(i);
                break;
            case R.id.chat_img:
                break;
        }
    }
}
