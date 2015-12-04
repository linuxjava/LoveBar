package xiao.love.bar.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by xiaoguochang on 2015/12/4.
 * 继承自FragmentActivity的Activity类型
 * Fragment的add、remove、replace、hide、show、detach
 */
public class BaseFragmentActivity extends FragmentActivity {
    /**
     * Fragment管理器
     */
    protected FragmentManager mFragmentManager = null;
    /**
     * 默认Fragment的容器
     */
    protected int mFragmentContainer;
    /**
     * 当前显示的Fragment
     */
    public Fragment mCurrentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
    }

    /*
     * [ 不要删除该函数 ],该函数的空实现修复了FragmentActivity中的bug
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    /**
     * 需要在调用任何函数前设置
     *
     * @param container 用于放置fragment的布局id
     */
    public void setFragmentContainerId(int container) {
        mFragmentContainer = container;
    }

    /**
     * 判断一个Fragment是否已经添加
     *
     * @param fragment
     * @return
     */
    public boolean isFragmentAdded(Fragment fragment){
        return fragment != null && mFragmentManager.findFragmentByTag(fragment.getClass().getName()) != null;
    }

    /**
     * 添加fragment到mFragmentContainer容器中
     * @param fragment
     */
    public void addFragment(Fragment fragment) {
        checkContainer();
        addFragment(mFragmentContainer, fragment);
    }

    /**
     * 添加fragment到container容器中
     *
     * @param container 用于放置fragment的布局id
     * @param fragment 要添加的Fragment
     */
    public void addFragment(int container, Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (!isFragmentAdded(fragment)) {
            fragmentTransaction
                    .add(container, fragment,
                            fragment.getClass().getName()).commitAllowingStateLoss();
            mCurrentFragment = fragment;
        } else {
            fragmentTransaction.show(fragment).commitAllowingStateLoss();
        }
    }

    /**
     * mFragmentContainer布局中显示Fragment，并且把上一个隐藏
     *
     * @param fragmentShow
     */
    public void showFragment(Fragment fragmentShow) {
        checkContainer();
        showFragment(mFragmentContainer, fragmentShow);
    }

    /**
     * 将fragmentShow显示在一个新的container上,而不覆盖mFragmentContainer。
     * 这种情况适用于Fragment中又嵌套Fragment
     *
     * @param container
     * @param fragmentShow
     */
    public void showFragment(int container, Fragment fragmentShow) {
        if (mCurrentFragment != fragmentShow) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            if (mCurrentFragment != null) {
                // 首先隐藏原来显示的Fragment
                transaction.hide(mCurrentFragment);
            }
            // 然后再显示传递进来的Fragment
            if (mFragmentManager.findFragmentByTag(fragmentShow.getClass().getName()) == null) {
                transaction
                        .add(container, fragmentShow, fragmentShow.getClass().getName());
            } else {
                transaction.show(fragmentShow);
            }
            transaction.commitAllowingStateLoss();
            mCurrentFragment = fragmentShow;
        }
    }

    /**
     * 替换容器mFragmentContainer中的fragment
     * @param fragment
     */
    public void replaceFragment(Fragment fragment) {
        checkContainer();
        replaceFragment(mFragmentContainer, fragment, false);
    }

    /**
     * 替换容器mFragmentContainer中的fragment
     * @param fragment
     * @param isAddToBackStack
     */
    public void replaceFragment(int container, Fragment fragment, boolean isAddToBackStack) {
        if (mCurrentFragment != fragment) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(container, fragment);
            if (isAddToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.commitAllowingStateLoss();
            mCurrentFragment = fragment;
        }
    }

    /**
     * 移除fragment
     * @param fragment
     */
    public void remove(Fragment fragment) {
        if (null != fragment) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * @param fragment
     */
    public void detach(Fragment fragment) {
        if (null != fragment) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.detach(fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 检查放置fragment的布局id
     */
    private void checkContainer() {
        if (mFragmentContainer <= 0) {
            throw new RuntimeException(
                    "请调用setFragmentContainerId函数来设置fragment container id");
        }
    }
}
