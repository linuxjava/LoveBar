package xiao.love.bar.im.collect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import xiao.love.bar.R;
import xiao.love.bar.component.BaseActivity;
import xiao.love.bar.component.BaseFragment;

/**
 * Created by guochang on 2015/9/28.
 */
@EFragment(R.layout.collect_fragment)
public class CollectFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnItemChildClickListener{
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
        mContext = getActivity();
        mRefreshLayout.setDelegate(CollectFragment.this);
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
                List<Object> list = new ArrayList<>();
                for (int i = 10; i < 20; i++) {
                    list.add(new Integer(i));
                }
                mAdapter.addNewDatas(list);
                mRefreshLayout.endRefreshing();
            }
        }, 2000);

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
//                List<Object> list = new ArrayList<>();
//                for (int i = 20; i < 30; i++) {
//                    list.add(new Integer(i));
//                }
//
//                mAdapter.addMoreDatas(list);
                mRefreshLayout.endLoadingMore();
            }
        }, 10000);

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
