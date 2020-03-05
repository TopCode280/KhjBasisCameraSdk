package org.khj.khjbasiscamerasdk.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.vise.log.ViseLog;

import org.khj.khjbasiscamerasdk.App;
import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper;
import org.khj.khjbasiscamerasdk.database.entity.DeviceEntity;

import java.util.ArrayList;
import java.util.List;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class DeviceListAdapter extends BaseQuickAdapter<CameraWrapper, BaseViewHolder> {

    private boolean ignore;
    private ArrayList<Integer> InvalidConnectStateArray = new ArrayList<>();

    public DeviceListAdapter(List<CameraWrapper> data) {
        super(R.layout.item_ipc, data);
        InvalidConnectStateArray.add(2);
        InvalidConnectStateArray.add(4);
        InvalidConnectStateArray.add(6);
        InvalidConnectStateArray.add(7);
        InvalidConnectStateArray.add(8);
    }


    @SuppressLint("CheckResult")
    @Override
    protected void convert(BaseViewHolder helper, CameraWrapper cameraWrapper) {
        String deviceName;
        helper.addOnClickListener(R.id.iv_pic);
        helper.addOnClickListener(R.id.btnDelete);
        boolean hasTurnOff = cameraWrapper.getDeviceInfo().hasTurnOff;
        DeviceEntity deviceEntity = cameraWrapper.getDeviceEntity();
        CheckBox checkBox = helper.getView(R.id.cbx_turnOnCamera);
        if (deviceEntity.getIsAdmin() && !cameraWrapper.isApMode()) {
            ignore = true;
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(!hasTurnOff);
            ignore = false;
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!ignore) {
                    if (cameraWrapper.getStatus() != 0) {
                        ignore = true;
                        checkBox.setChecked(!isChecked);
                        ignore = false;
                        Toasty.error(App.context, mContext.getString(R.string.connectDeviceFirst)).show();
                        return;
                    }
                    Observable.create((ObservableOnSubscribe<Boolean>) emitter -> cameraWrapper.getmCamera().forceOpenCamera(isChecked, b -> {
                        emitter.onNext(b);
                        emitter.onComplete();
                    })).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    Toasty.success(App.context, App.context.getString(R.string.modifySuccess)).show();
                                    cameraWrapper.getDeviceInfo().hasTurnOff = !isChecked;
                                } else {
                                    ignore = true;
                                    checkBox.setChecked(!isChecked);
                                    ignore = false;
                                    Toasty.error(App.context, App.context.getString(R.string.modifyFailure)).show();
                                }
                                notifyDataSetChanged();
                            });
                }
            });
        } else {
            checkBox.setVisibility(View.GONE);
        }

        if (hasTurnOff) {
            helper.setText(R.id.tv_online, R.string.cameraOff);
        } else {
            String connectStatusMessage = "";
            if (!InvalidConnectStateArray.contains(cameraWrapper.getStatus())) {
                if (cameraWrapper.getStatus() == 1) {
                    if (cameraWrapper.getCheckOnLineStatus() == 0) {
                        connectStatusMessage = App.context.getString(R.string.online);
                    } else if (cameraWrapper.getCheckOnLineStatus() == -1) {
                        connectStatusMessage = "";
                    } else {
                        connectStatusMessage = App.context.getString(R.string.offline);
                    }
                } else {
                    connectStatusMessage = cameraWrapper.getConnectStatusMessage();
                }
                helper.setText(R.id.tv_online, connectStatusMessage);
            }
            ViseLog.d("连接camera", connectStatusMessage, "status = " + cameraWrapper.getStatus());
        }
    }

}
