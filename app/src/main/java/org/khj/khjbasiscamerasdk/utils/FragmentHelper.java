package org.khj.khjbasiscamerasdk.utils;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.base.BaseFragment;

import java.util.List;
import java.util.Stack;

/**
 * Created by zzw on 2017/5/25.
 * Version:
 * Des:Fragment辅助类
 */

public class FragmentHelper {
    private Stack<BaseFragment> fragmentStack;

    private FragmentManager mFragmentManager;
    private int mContainerViewId;//容器布局id
    /**
     * @param fragmentManager Fragment管理类
     * @param containerViewId 容器布局id
     */
    public FragmentHelper(@NonNull FragmentManager fragmentManager, @IdRes int containerViewId) {
        this.mFragmentManager = fragmentManager;
        this.mContainerViewId = containerViewId;
        fragmentStack=new Stack<>();
    }

    /**
     * 添加Fragment
     *
     * @param fragment
     */
    public void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.add(mContainerViewId, fragment);

        fragmentTransaction.commit();
    }
    public void hideFragment(Fragment fragment){
       mFragmentManager.beginTransaction().hide(fragment).commit();
    }
    public Fragment findFragmentByTag(String tag){
        Fragment fragmentByTag = mFragmentManager.findFragmentByTag(tag);
        return fragmentByTag;
    }

    public void addFragment(Fragment fragment,String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.add(mContainerViewId, fragment,tag);

        fragmentTransaction.commit();
    }
    public void addFragmentToBackStackByHide(Fragment hide, Fragment show) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction()  .setCustomAnimations( R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        fragmentTransaction.add(mContainerViewId, show).hide(hide).addToBackStack(show.getTag());
        fragmentTransaction.commit();
    }
    public void addFragmentToBackStackByHide(Fragment hide, Fragment show,String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction()  .setCustomAnimations( R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        fragmentTransaction.add(mContainerViewId, show).hide(hide).addToBackStack(tag);
        fragmentTransaction.commit();
    }
    public void addFragmentToBackStackByReplace(Fragment hide, Fragment show) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction()  .setCustomAnimations( R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        fragmentTransaction.add(mContainerViewId, show).hide(hide).addToBackStack(show.getTag());
        fragmentTransaction.commit();
    }
    public void addFragmentByReplace(Fragment remove, Fragment show) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction()  .setCustomAnimations( R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        fragmentTransaction.remove(remove);
        fragmentTransaction.add(mContainerViewId,show);
        fragmentTransaction.commit();
    }
    public void addFragmentByReplace(Fragment remove, Fragment show,String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction()  .setCustomAnimations( R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        fragmentTransaction.remove(remove);
        fragmentTransaction.add(mContainerViewId,show,tag);
        fragmentTransaction.commitAllowingStateLoss();
    }
    public void detachFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.detach(fragment);
        fragmentTransaction.commit();
    }
    public void detachFragment(String tag) {
        Fragment fragmentByTag = mFragmentManager.findFragmentByTag(tag);
        if (fragmentByTag!=null){
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.detach(fragmentByTag);
            fragmentTransaction.commit();
        }

    }

    /**
     * 删除当前fragment，通过tag找到隐藏的fragment显示
     * @param fragment
     */
    public Fragment detachFragmentAndFind(Fragment fragment,String tag) {
        Fragment fragmentByTag = mFragmentManager.findFragmentByTag(tag);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (fragmentByTag==null){
            fragmentTransaction.detach(fragment);
        }else {
            fragmentTransaction.detach(fragment).show(fragmentByTag);
        }

        fragmentTransaction.commit();
        return fragmentByTag;
    }

    /**
     * 切换显示Fragment
     *
     * @param fragment
     */
    public void switchFragment(Fragment fragment,boolean showAnimation) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (showAnimation){
            fragmentTransaction.setCustomAnimations( R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                    R.anim.slide_out_right);
        }
        List<Fragment> childFragments = mFragmentManager.getFragments();
        //先影藏所有的已经有的Fragment
        for (Fragment childFragment : childFragments) {
            fragmentTransaction.hide(childFragment);
        }

        //如果不包含这个fragment，就先添加
        if (!childFragments.contains(fragment)) {

            fragmentTransaction.add(mContainerViewId, fragment);
        } else {//有的话就直接提交
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
    /**
     * 切换显示Fragment
     *
     * @param fragment
     */
    public void switchFragment(Fragment fragment,boolean showAnimation,String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (showAnimation){
            fragmentTransaction.setCustomAnimations( R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_in_left,
                    R.anim.slide_out_right);
        }
        List<Fragment> childFragments = mFragmentManager.getFragments();

        //先影藏所有的已经有的Fragment
        for (Fragment childFragment : childFragments) {
            fragmentTransaction.hide(childFragment);
        }

        //如果不包含这个fragment，就先添加
        if (!childFragments.contains(fragment)) {
            fragmentTransaction.add(mContainerViewId, fragment,tag);
        } else {//有的话就直接提交
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
    public void switchFragment(String tag) {
        Fragment fragmentByTag = mFragmentManager.findFragmentByTag(tag);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        List<Fragment> childFragments = mFragmentManager.getFragments();
        //先影藏所有的已经有的Fragment
        for (Fragment childFragment : childFragments) {
            fragmentTransaction.hide(childFragment);
        }
        //如果不包含这个fragment，就先添加
        if (!childFragments.contains(fragmentByTag)) {
            fragmentTransaction.add(mContainerViewId, fragmentByTag);
        } else {//有的话就直接提交
            fragmentTransaction.show(fragmentByTag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     *
     * @return
     */
    public int pop(){
        int size = fragmentStack.size();
        if (size<2){
            return 0;
        }else {
            BaseFragment pop = fragmentStack.pop();
            BaseFragment peek = fragmentStack.peek();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction()  .setCustomAnimations( R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left,
                    R.anim.slide_out_right);
            fragmentTransaction.detach(pop).show(peek);
            fragmentTransaction.commit();
            return size-1;
        }
    }

    public void addToStack(BaseFragment fragment){
        fragmentStack.add(fragment);
    }

    /**
     * detach一个fragment并从回退栈删除，添加一个新fragment到回退栈
     * @param detach
     * @param add
     * @param tag
     */
    public void detachAndAddNewToStack(BaseFragment detach,BaseFragment add,String tag){
        fragmentStack.remove(detach);
        detachFragment(detach);
        addToStack(add);
        switchFragment(add,false,tag);

    }

    public BaseFragment getCurrentFragment(){
        if (fragmentStack==null||fragmentStack.empty()){
            return null;
        }
       return fragmentStack.peek();
    }

}
