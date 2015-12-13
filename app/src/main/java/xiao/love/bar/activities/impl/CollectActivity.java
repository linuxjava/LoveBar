package xiao.love.bar.activities.impl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import butterknife.Bind;
import xiao.love.bar.R;
import xiao.love.bar.activities.BaseFragmentActivity;
import xiao.love.bar.component.segmentcontrol.SegmentControl;
import xiao.love.bar.fragments.impl.CollectFragment;

/**
 * Created by guochang on 2015/9/28.
 * 收藏和被收藏主页
 */
public class CollectActivity extends BaseFragmentActivity {
    @Bind(R.id.segment_control)
    SegmentControl mSegmentControl;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    @Override
    protected int getLayout() {
        return R.layout.collect_main_layout;
    }

    @Override
    protected void initWidgets() {
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

        mViewPager.setAdapter(new ViewPagerAdapter(mFragmentManager));
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] mFragments = new Fragment[]{new CollectFragment(), new CollectFragment()};

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
