package org.khj.khjbasiscamerasdk.fragment.setting;


import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.khj.Camera;
import com.vise.log.ViseLog;

import org.khj.khjbasiscamerasdk.av_modle.CameraManager;
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper;
import org.khj.khjbasiscamerasdk.base.BaseFragment;
import org.khj.khjbasiscamerasdk.base.BaseFragment2;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseDeviceSetFragment extends BaseFragment2 {
    protected CameraWrapper cameraWrapper;
    protected Camera camera;
    protected String ssid;
    protected boolean apMode;
    protected String uid;
    protected changeVisbility changeVisbility;

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        if (ssid != null && ssid.contains("camera_")) {
            apMode = true;
        }
        ViseLog.e("是Ap模式：" + apMode);
        if (apMode) {
            cameraWrapper = CameraManager.getInstance().getApCamera(uid);
            if (cameraWrapper == null) {
                cameraWrapper = CameraManager.getInstance().getCameraWrapper(uid);
            }
            ViseLog.i("cameraWrapper" + apMode + cameraWrapper);
        } else {
            cameraWrapper = CameraManager.getInstance().getCameraWrapper(uid);
            ViseLog.i("cameraWrapper" + apMode + cameraWrapper);
        }
        if (cameraWrapper == null) {
            finish();
            return;
        }
        camera = cameraWrapper.getmCamera();
        if (camera == null) {
            finish();
            return;
        }
    }

    public void setChangeVisbility(changeVisbility changeVisbility) {
        this.changeVisbility = changeVisbility;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (cameraWrapper != null && camera != null) {
            super.onViewCreated(view, savedInstanceState);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && changeVisbility != null) {
            changeVisbility.show();
        }
    }
}
