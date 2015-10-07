package xiao.love.bar.im.collect;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import xiao.love.bar.R;
import xiao.love.bar.component.BaseActivity;
import xiao.love.bar.im.history.ChatAllHistoryAdapter;

/**
 * Created by guochang on 2015/9/28.
 * 收藏和被收藏
 */
@EActivity(R.layout.collect_main_layout)
public class CollectActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnItemChildClickListener, BGAOnRVItemClickListener {
    @ViewById(R.id.recyclerview_refresh)
    public BGARefreshLayout mRefreshLayout;
    @ViewById(R.id.recyclerview)
    public RecyclerView mRecyclerView;
    @ViewById(R.id.tab_left_img)
    ImageView mTabLeftImg;
    @ViewById(R.id.tab_right_img)
    ImageView mTabRightImg;
    @ViewById(R.id.tab_left_tv)
    TextView mTabLeftTv;
    @ViewById(R.id.tab_right_tv)
    TextView mTabRightTv;

    private Context mContext;
    private CollectAdapter mAdapter;

    @AfterViews
    void init() {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new Integer(i));
        }

        mContext = this;
        mAdapter = new CollectAdapter(mContext, mRecyclerView);
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setOnRVItemClickListener(this);
        mAdapter.setDatas(list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        BGANormalRefreshViewHolder viewHolder = new BGANormalRefreshViewHolder(mContext, false);
        mRefreshLayout.setRefreshViewHolder(viewHolder);
        mRefreshLayout.setDelegate(this);

        mTabLeftImg.setBackgroundResource(R.drawable.ic_button_left_pressed);
        mTabLeftTv.setTextColor(getResources().getColor(R.color.holo_blue_dark));
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
                mRefreshLayout.endLoadingMore();
            }
        }, 1000);

        return true;
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if(childView.getId() == R.id.collect_img){
            mAdapter.removeItem(position);
        }
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {

    }

}
