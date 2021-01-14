package org.khj.khjbasiscamerasdk.base;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.khj.khjbasiscamerasdk.App;
import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.utils.FragmentHelper;
import org.khj.khjbasiscamerasdk.view.MyTopBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/3/22.
 */

public abstract class BaseFragment2 extends Fragment {
    /**
     * 用户设置的ContentView
     */
    protected View mContentView;
    /**
     * View有没有加载过
     */
    protected boolean isViewInitiated;
    /**
     * 页面是否可见
     */
    protected boolean isVisibleToUser;
    /**
     * 是不是加载过
     */
    protected boolean isDataInitiated;

    protected BaseActivity mActivity;
    protected String Tag;
    protected FragmentHelper helper;
    protected CompositeDisposable mDisposable;
    protected QMUITipDialog tipDialog;
    //设置是否允许按返回键的时候detach当前fragment
    public boolean ifAllowDetach = true;
    protected MyTopBar topBar;
    protected Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
        Tag = getLogTag();
    }

    protected String getLogTag() {
        return "BaseFragment";
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDisposable = new CompositeDisposable();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initData(savedInstanceState);

        if (userEventbus()) {
            EventBus.getDefault().register(this);
        }
        mContentView = createContentView(container);
        bindView(mContentView);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (!isViewInitiated) {

                initTitle();
                initView(view);
                initNet();
            }
            isViewInitiated = true;

            loadData();
        } catch (Exception e) {
            ViseLog.e("fragment初始化异常" + e.getMessage());
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isVisibleToUser = !hidden;

    }

    @Override
    public void onResume() {
        super.onResume();
        isVisibleToUser = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisibleToUser = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null) {
            mDisposable.dispose();
        }


        if (userEventbus()) {
            EventBus.getDefault().unregister(this);
        }
        dismissLoading();

        unbindView(mContentView);
        mContentView = null;
        isViewInitiated = false;
    }

    @Override
    public void onDestroy() {
        mActivity = null;


        super.onDestroy();
    }

    /**
     * 创建View
     */
    private View createContentView(ViewGroup parent) {

        View contentView = null;

        contentView = getLayoutInflater().inflate(getContentLayout(), parent, false);

        if (contentView == null) {
            new IllegalArgumentException("getContentLayout must View or LayoutId");
        }
        return contentView;
    }

    protected FragmentHelper getFragmentHelper() {
        return mActivity.getFragmentHelper();
    }

    protected void addFragment(Fragment fragment) {
        if (getFragmentHelper() != null) {
            getFragmentHelper().addFragment(fragment);
        }
    }

    protected void addFragment(Fragment fragment, String tag) {
        if (getFragmentHelper() != null) {
            getFragmentHelper().addFragment(fragment, tag);
        }
    }

    protected void addFragmentToBackStackByHide(Fragment hide, Fragment show) {
        if (getFragmentHelper() != null) {
            getFragmentHelper().addFragmentToBackStackByHide(hide, show);
        }
    }

    protected void addFragmentToBackStackByHide(Fragment hide, Fragment show, String tag) {
        if (getFragmentHelper() != null) {
            getFragmentHelper().addFragmentToBackStackByHide(hide, show, tag);
        }
    }

    protected void addFragmentByReplace(Fragment remove, Fragment show) {
        if (getFragmentHelper() != null) {
            getFragmentHelper().addFragmentByReplace(remove, show);
        }
    }

    protected void addFragmentByReplace(Fragment remove, Fragment show, String tag) {
        if (getFragmentHelper() != null) {
            getFragmentHelper().addFragmentByReplace(remove, show, tag);
        }
    }

    protected void detachFragment(Fragment fragment) {
        if (getFragmentHelper() != null) {
            getFragmentHelper().detachFragment(fragment);
        }
    }

    protected void detachFragment(String tag) {
        if (getFragmentHelper() != null) {
            getFragmentHelper().detachFragment(tag);
        }
    }

    protected Fragment detachFragmentAndFind(Fragment fragment, String tag) {
        if (getFragmentHelper() != null) {
            return getFragmentHelper().detachFragmentAndFind(fragment, tag);
        }
        return null;
    }

    protected void switchFragment(Fragment fragment) {
        if (getFragmentHelper() != null) {
            getFragmentHelper().switchFragment(fragment, true);
        }
    }

    protected void back() {
        if (mActivity != null) {
            mActivity.onBackPressed();
        }

    }


    protected abstract int getContentLayout();

    /**
     * 1. 初始化数据，包括上个页面传递过来的数据在这个方法做
     */
    protected void initData(Bundle savedInstanceState) {

    }

    /**
     * 3.1 如果要创建标题
     */
    protected abstract void initTitle();


    /**
     * 3.2绑定View
     */
    protected void bindView(View contentView) {
        unbinder = ButterKnife.bind(this, mContentView);
    }

    /**
     * 4. 初始化View
     */
    protected void initView(View contentView) {

    }

    /**
     * 5. 初始化网络
     */
    protected void initNet() {

    }

    /**
     * 懒加载
     */
    private void loadData() {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated)) {
            isDataInitiated = true;
            lazyLoad();
        }
    }

    /**
     * 6. 懒加载，Fragment可见的时候调用这个方法，而且只调用一次
     */
    protected void lazyLoad() {

    }

    /**
     * 解绑contentView
     */
    protected void unbindView(View contentView) {
        unbinder.unbind();
    }


    /**
     * 打开Activity
     */
    public final void startActivity(Class<?> clazz) {
        startActivity(clazz, null);
    }

    /**
     * 打开Activity
     */
    public final void startActivity(Class<?> clazz, @Nullable Bundle options) {
        if (getAppActivity() != null) {
            Intent intent = new Intent(getAppActivity(), clazz);
            if (options != null) {
                intent.putExtras(options);
            }
            startActivity(intent);
        }

    }

    /**
     * 获取当前的Activity
     */
    public final Activity getAppActivity() {
        return mActivity;
    }

    /**
     * 设置TextView
     */
    public void setText(TextView textView, CharSequence text) {
        if (textView != null && text != null) {
            textView.setText(text);
        }
    }

    /**
     * 闭关页面
     */
    public void finish() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }


    protected boolean userEventbus() {
        return false;
    }

    protected void showLoading() {
        if (isAdded()) {
            tipDialog = new QMUITipDialog(mActivity);
            tipDialog.setCanceledOnTouchOutside(false);
            View inflate = View.inflate(App.context, R.layout.dialog_loading, null);
            tipDialog.setContentView(inflate, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            tipDialog.setCancelable(true);
            tipDialog.setOnCancelListener(dialog -> {
                mDisposable.dispose();
            });
            tipDialog.show();
        }

    }

    protected void showLoading(String Msg) {
        if (isAdded()) {
            tipDialog = new QMUITipDialog.Builder(mActivity).setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).setTipWord(Msg).create();
            tipDialog.setCanceledOnTouchOutside(false);
            tipDialog.setCancelable(true);
            tipDialog.setOnCancelListener(dialog -> {
                mDisposable.dispose();
            });
            tipDialog.show();
        }
    }

    public void showLoading(Disposable disposable) {
        if (isAdded()) {
            tipDialog = new QMUITipDialog(mActivity);
            tipDialog.setCanceledOnTouchOutside(false);
            tipDialog.setContentView(R.layout.dialog_loading);


            tipDialog.setCancelable(true);
            tipDialog.setOnCancelListener(dialog ->
                    {
                        if (disposable != null) {
                            disposable.dispose();
                        }

                    }

            );
            tipDialog.show();

        }


    }

    public void dismissLoading() {
        if (isAdded()) {
            if (tipDialog != null && tipDialog.isShowing()) {
                tipDialog.dismiss();
                tipDialog = null;
            }
        }

    }

    /**
     * 如果ifDetach为false，则调用fragment的该方法
     */
    public void onBackPressed() {

    }


    public void setIfAllowDetach(boolean ifAllowDetach) {
        this.ifAllowDetach = ifAllowDetach;
    }

}
