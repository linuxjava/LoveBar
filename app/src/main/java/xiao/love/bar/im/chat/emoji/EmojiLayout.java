package xiao.love.bar.im.chat.emoji;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;


import java.util.HashMap;
import java.util.Map;

import xiao.love.bar.R;

/**
 * Created by guochang on 2015/9/8.
 * 表情显示类
 */
public class EmojiLayout implements View.OnClickListener{
    private FragmentActivity mActivity;
    //当前表情类型
    private int mCurrentEmojiType = -1;
    //表情布局
    private LinearLayout mEmojiLayout;
    //表情tab(key：表情type；value：表情布局)
    private Map<Integer, LinearLayout> mEmojiTabMap;
    //表情tab adapter(key：表情type；value：表情adapter)
    private Map<Integer, EmojiPagerAdapter> mPagerAdapterMap;
    //表情页码指示器
    private LinearLayout mEmojiIndicator;
    private ViewPager mViewPager;
    //输入框
    private EditText mEditText;
    //viewpage页面滑动监听
    private PageChangeListener mPageChangeListener;

    public EmojiLayout(FragmentActivity activity, EditText editText) {
        mActivity = activity;
        mEditText = editText;
        mPagerAdapterMap = new HashMap<Integer, EmojiPagerAdapter>();
        mEmojiTabMap = new HashMap<Integer, LinearLayout>();
        mPageChangeListener = new PageChangeListener();
        mEmojiLayout = (LinearLayout) mActivity.findViewById(R.id.ll_face_container);
        mEmojiIndicator = (LinearLayout) mActivity.findViewById(R.id.emojiIndicator);
        mViewPager = (ViewPager) mActivity.findViewById(R.id.vPager);

        LinearLayout emojiTab1 = (LinearLayout) mActivity.findViewById(R.id.emoji_tab1_ll);
        LinearLayout emojiTab2 = (LinearLayout) mActivity.findViewById(R.id.emoji_tab2_ll);
        mEmojiTabMap.put(EmojiConstant.EMOJI_TYPE_CLASSICAL, emojiTab1);
        mEmojiTabMap.put(EmojiConstant.EMOJI_TYPE_PEACH, emojiTab2);
        //为每一个表情tab添加点击事件
        for (Map.Entry<Integer, LinearLayout> entry : mEmojiTabMap.entrySet()){
            entry.getValue().setOnClickListener(this);
        }

        mViewPager.setOnPageChangeListener(mPageChangeListener);

        //触发表情tab1的点击事件
        emojiTab1.performClick();
    }

    /**
     * 获取表情布局显示状态
     * @return
     */
    public int getEmojiLayoutVisibility(){
        return mEmojiLayout.getVisibility();
    }

    /**
     * 显示表情布局显示状态
     */
    public void showEmojiLayout(){
        mEmojiLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏表情布局显示状态
     */
    public void hideEmojiLayout(){
        mEmojiLayout.setVisibility(View.GONE);
    }

    /**
     * 初始化表情页面指示器
     *
     * @param count
     */
    private void initEmojiIndicator(int count) {
        mEmojiIndicator.removeAllViews();
        int pointWidth = mActivity.getResources().getDimensionPixelSize(R.dimen.point_width);
        int pointMargin = mActivity.getResources().getDimensionPixelSize(R.dimen.point_margin);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(pointWidth, pointWidth);
        lp.leftMargin = pointWidth;
        lp.rightMargin = pointMargin;
        for (int i = 0; i < count; ++i) {
            View pointView = mActivity.getLayoutInflater().inflate(R.layout.common_point, null);
            mEmojiIndicator.addView(pointView, lp);
        }
        mEmojiIndicator.getChildAt(0).setBackgroundResource(R.drawable.ic_point_select);

        //置位指示器的初始位置
        mPageChangeListener.resetPos();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emoji_tab1_ll:
                if(mCurrentEmojiType != EmojiConstant.EMOJI_TYPE_CLASSICAL) {
                    //设置旧的表情tab背景颜色
                    if(mEmojiTabMap.get(mCurrentEmojiType) != null) {
                        mEmojiTabMap.get(mCurrentEmojiType).setBackgroundResource(android.R.color.white);
                    }
                    //设置点击的表情tab背景颜色
                    mEmojiTabMap.get(EmojiConstant.EMOJI_TYPE_CLASSICAL).setBackgroundResource(R.color.emoji_bg);
                    //初始化指示器
                    initEmojiIndicator(EmojiConstant.CLASSICAL_EMOJI.length);

                    if (mPagerAdapterMap.get(EmojiConstant.EMOJI_TYPE_CLASSICAL) != null) {
                        mViewPager.setAdapter(mPagerAdapterMap.get(EmojiConstant.EMOJI_TYPE_CLASSICAL));
                    }else {
                        EmojiPagerAdapter pagerAdapter = new EmojiPagerAdapter(mActivity.getSupportFragmentManager(),
                                EmojiConstant.EMOJI_TYPE_CLASSICAL);
                        mPagerAdapterMap.put(EmojiConstant.EMOJI_TYPE_CLASSICAL, pagerAdapter);
                        mViewPager.setAdapter(pagerAdapter);
                    }

                    mCurrentEmojiType = EmojiConstant.EMOJI_TYPE_CLASSICAL;
                }
                break;
            case R.id.emoji_tab2_ll:
                if(mCurrentEmojiType != EmojiConstant.EMOJI_TYPE_PEACH) {
                    //设置旧的表情tab背景颜色
                    if(mEmojiTabMap.get(mCurrentEmojiType) != null) {
                        mEmojiTabMap.get(mCurrentEmojiType).setBackgroundResource(android.R.color.white);
                    }
                    //设置点击的表情tab背景颜色
                    mEmojiTabMap.get(EmojiConstant.EMOJI_TYPE_PEACH).setBackgroundResource(R.color.emoji_bg);
                    //初始化指示器
                    initEmojiIndicator(EmojiConstant.PEACH_EMOJI.length);

                    if (mPagerAdapterMap.get(EmojiConstant.EMOJI_TYPE_PEACH) != null) {
                        mViewPager.setAdapter(mPagerAdapterMap.get(EmojiConstant.EMOJI_TYPE_PEACH));
                    } else {
                        EmojiPagerAdapter pagerAdapter = new EmojiPagerAdapter(mActivity.getSupportFragmentManager(),
                                EmojiConstant.EMOJI_TYPE_PEACH);
                        mPagerAdapterMap.put(EmojiConstant.EMOJI_TYPE_PEACH, pagerAdapter);
                        mViewPager.setAdapter(pagerAdapter);
                    }

                    mCurrentEmojiType = EmojiConstant.EMOJI_TYPE_PEACH;
                }
                break;
        }
    }

    class EmojiPagerAdapter extends FragmentStatePagerAdapter {
        private int currentEmojiType;
        private int pageCount;
        private String emojis[][];

        EmojiPagerAdapter(FragmentManager fm, int emojiType) {
            super(fm);

            currentEmojiType = emojiType;

            switch (emojiType){
                case EmojiConstant.EMOJI_TYPE_CLASSICAL:
                    emojis = EmojiConstant.CLASSICAL_EMOJI;
                    pageCount = EmojiConstant.CLASSICAL_EMOJI.length;
                    break;
                case EmojiConstant.EMOJI_TYPE_PEACH:
                    emojis = EmojiConstant.PEACH_EMOJI;
                    pageCount = EmojiConstant.PEACH_EMOJI.length;
                    break;
            }
        }

        @Override
        public Fragment getItem(int i) {
            EmojiFragment fragment = new EmojiFragment();
            fragment.init(mActivity, mEditText, emojis[i], currentEmojiType);
            return fragment;
        }

        @Override
        public int getCount() {
            return pageCount;
        }
    }

    /**
     * viewpage滑动监听
     */
    private class PageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        int oldPos = 0;

        public void resetPos() {
            oldPos = 0;
        }

        @Override
        public void onPageSelected(int position) {
            View oldPoint = mEmojiIndicator.getChildAt(oldPos);
            View newPoint = mEmojiIndicator.getChildAt(position);
            oldPoint.setBackgroundResource(R.drawable.ic_point_normal);
            newPoint.setBackgroundResource(R.drawable.ic_point_select);

            oldPos = position;
        }
    }
}
