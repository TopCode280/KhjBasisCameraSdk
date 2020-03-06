package org.khj.khjbasiscamerasdk.adapter;

import android.net.wifi.ScanResult;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.khj.Camera;

import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper;
import org.khj.khjbasiscamerasdk.bean.SearchDeviceInfoBean;

import java.util.List;

public class UidSelectAdapter extends BaseQuickAdapter<SearchDeviceInfoBean, BaseViewHolder> {


    public UidSelectAdapter(List<SearchDeviceInfoBean> data) {
        super(R.layout.item_ap_device,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchDeviceInfoBean result) {
        TextView tvSsid = helper.getView(R.id.tv_ssid);
        tvSsid.setText(result.getUid());
    }
}
