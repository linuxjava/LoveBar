package xiao.love.bar.fragments.impl;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import xiao.love.bar.R;
import xiao.love.bar.adapters.XGCOnItemChildClickListener;
import xiao.love.bar.adapters.XGCOnRVItemClickListener;
import xiao.love.bar.adapters.XGCOnRVItemLongClickListener;
import xiao.love.bar.adapters.impl.CollectAdapter;
import xiao.love.bar.component.util.T;
import xiao.love.bar.entity.TestData;
import xiao.love.bar.fragments.BaseFragment;
import xiao.love.bar.presenter.BaseFragmentPresenter;

/**
 * Created by xiaoguochang on 2015/12/3.
 * 收藏和被收藏fragment
 */
public class CollectFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate,
        XGCOnRVItemClickListener, XGCOnRVItemLongClickListener, XGCOnItemChildClickListener {
    @Bind(R.id.recyclerview_refresh)
    BGARefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private CollectAdapter mAdapter;


    @Override
    protected int getLayout() {
        return R.layout.collect_fg;
    }

    @Override
    protected void initWidgets() {
        //构造测试数据
        List<TestData> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            TestData data = new TestData(i, i + "");
            list.add(data);
        }

        mAdapter = new CollectAdapter(mContext);
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setOnRVItemClickListener(this);
        mAdapter.setOnRVItemLongClickListener(this);
        mAdapter.setDatas(list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        BGANormalRefreshViewHolder viewHolder = new BGANormalRefreshViewHolder(mContext, false);
        mRefreshLayout.setRefreshViewHolder(viewHolder);
        mRefreshLayout.setDelegate(this);
    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
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
     * 上拉加载更多
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

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        T.showShort(mContext, position + "");
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        T.showShort(mContext, position + "");
        if (childView.getId() == R.id.collect_img) {
            mAdapter.removeItem(position);
        }
    }


    @Override
    public boolean onRVItemLongClick(ViewGroup parent, View itemView, int position) {
        T.showShort(mContext, position + "");
        return true;
    }
}
