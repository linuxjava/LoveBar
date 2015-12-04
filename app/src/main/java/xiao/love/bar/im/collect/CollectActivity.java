package xiao.love.bar.im.collect;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
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
import xiao.love.bar.component.segmentcontrol.SegmentControl;
import xiao.love.bar.im.history.ChatAllHistoryAdapter;

/**
 * Created by guochang on 2015/9/28.
 * 收藏和被收藏
 */
@EActivity(R.layout.collect_main_layout)
public class CollectActivity extends BaseActivity {
    @ViewById(R.id.segment_control)
    SegmentControl mSegmentControl;
    @ViewById(R.id.viewPager)
    ViewPager mViewPager;

    @AfterViews
    void init() {
        mSegmentControl.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                mViewPager.setCurrentItem(index);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSegmentControl.setSelectedIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private Fragment[] mFragments = new Fragment[]{new CollectFragment_(), new CollectFragment_()};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }
}
