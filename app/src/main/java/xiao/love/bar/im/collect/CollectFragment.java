package xiao.love.bar.im.collect;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import xiao.love.bar.R;
import xiao.love.bar.component.BaseFragment;

/**
 * Created by xiaoguochang on 2015/12/3.
 */
@EFragment(R.layout.collect_fg)
public class CollectFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnItemChildClickListener, BGAOnRVItemClickListener {
    private Context mContext;
    @ViewById(R.id.recyclerview_refresh)
    public BGARefreshLayout mRefreshLayout;
    @ViewById(R.id.recyclerview)
    public RecyclerView mRecyclerView;

    private CollectAdapter mAdapter;

    @AfterViews
    void init() {
        //构造测试数据
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new Integer(i));
        }

        mContext = getActivity();
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
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (childView.getId() == R.id.collect_img) {
            mAdapter.removeItem(position);
        }
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {

    }
}
