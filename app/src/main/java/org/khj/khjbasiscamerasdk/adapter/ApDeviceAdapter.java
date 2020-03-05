package org.khj.khjbasiscamerasdk.adapter;

import android.net.wifi.ScanResult;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.khj.khjbasiscamerasdk.R;

public class ApDeviceAdapter extends BaseQuickAdapter<ScanResult, BaseViewHolder> {


    public ApDeviceAdapter() {
        super(R.layout.item_ap_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScanResult result) {
        TextView tvSsid = helper.getView(R.id.tv_ssid);
        tvSsid.setText(result.SSID);

    }


}
