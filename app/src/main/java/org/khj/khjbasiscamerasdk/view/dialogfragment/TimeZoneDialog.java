package org.khj.khjbasiscamerasdk.view.dialogfragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.khj.Camera;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.khj.khjbasiscamerasdk.App;
import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.av_modle.CameraManager;
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper;
import org.khj.khjbasiscamerasdk.bean.TimeZoneBean;
import org.khj.khjbasiscamerasdk.utils.WiFiUtil;
import org.khj.khjbasiscamerasdk.view.dialogfragment.callBackInterface.SettingTimeZoneCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.EmptyDisposable;

/**
 * Created by Administrator on 2017-05-18.
 */

public class TimeZoneDialog extends DialogFragment {

    private List<TimeZoneBean> dataList = new ArrayList<>();

    private String uid;
    private TimezoneAdapter adapter;
    private QMUITipDialog tipDialog;
    private EmptyDisposable disposable;
    private Camera camera;
    private float time_zone;
    private String ssid;
    private boolean apMode;
    private CameraWrapper cameraWrapper;
    private static long lastClickTime;
    private SettingTimeZoneCallBack timeZoneCallBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = getArguments().getString("UID");
        time_zone = getArguments().getFloat("TIME_ZONE");
        ssid = WiFiUtil.getInstance(App.context).getSSID();
        if (ssid != null && ssid.contains("camera_")) {
            apMode = true;
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_timezone, null);
        RecyclerView recyclerViewTimezone = view.findViewById(R.id.recyclerView_timezone);
        for (String timeZone : Objects.requireNonNull(getContext()).getResources().getStringArray(R.array.timeZone)) {
            dataList.add(new TimeZoneBean(timeZone));
        }
        for (TimeZoneBean timezoneBean : dataList) {
            float time = Float.parseFloat(timezoneBean.getTime());
            if (time == time_zone) {
                timezoneBean.setSelectedTime(true);
            }
        }

        adapter = new TimezoneAdapter(R.layout.item_simple_checkbox, dataList);
        adapter.setOnItemClickListener((adapter, view1, position) -> {
            long c = System.currentTimeMillis();
            if (c - lastClickTime < 2000) {
                lastClickTime = c;
                ViseLog.e("快速点击不动作");
                return;
            }
            lastClickTime = c;
            int timezoneMinute = (int) (Float.parseFloat(dataList.get(position).getTime()) * 60);
            ViseLog.e("timezone" + timezoneMinute);
            if (apMode) {
                cameraWrapper = CameraManager.getInstance().getCameraWrapper(uid);
                if (cameraWrapper == null) {
                    cameraWrapper = CameraManager.getInstance().getApCamera(uid);
                }
            } else {
                cameraWrapper = CameraManager.getInstance().getCameraWrapper(uid);
            }
            camera = cameraWrapper.getmCamera();
            camera.setTimezone(timezoneMinute, b -> io.reactivex.Observable.just(b)
                    .doOnSubscribe(disposable -> showLoading())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> {
                        try {
                            tipDialog.dismiss();
                            TimeZoneDialog.this.dismiss();
                        } catch (Exception e) {
                            ViseLog.e(e.getMessage());
                        }

                    })
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (timeZoneCallBack != null) {
                                if (aBoolean) {
                                    Toasty.success(App.context, getString(R.string.modifySuccess)).show();
                                    timeZoneCallBack.onSuccess(timezoneMinute);
                                } else {
                                    Toasty.error(App.context, getString(R.string.modifyFailure)).show();
                                    timeZoneCallBack.onFailure();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        });
        recyclerViewTimezone.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTimezone.setAdapter(adapter);
        return view;
    }

    private class TimezoneAdapter extends BaseQuickAdapter<TimeZoneBean, BaseViewHolder> {

        public TimezoneAdapter(int layoutResId, @Nullable List<TimeZoneBean> data) {
            super(layoutResId, data);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void convert(BaseViewHolder helper, TimeZoneBean item) {
            TextView time = helper.getView(R.id.tv_timezone);
            time.setText("UTC" + item.getTime());
            CheckBox checkBox = helper.getView(R.id.cbx_time);
            checkBox.setChecked(item.isSelectedTime());

        }
    }

    private void showLoading() {
        tipDialog = new QMUITipDialog(getActivity());
        tipDialog.setContentView(R.layout.dialog_loading);

        tipDialog.setCancelable(true);
        tipDialog.setOnCancelListener(dialog -> disposable.dispose());
        tipDialog.show();
    }

    public void setTimeZoneCallBack(SettingTimeZoneCallBack callBack) {
        this.timeZoneCallBack = callBack;
    }
}

