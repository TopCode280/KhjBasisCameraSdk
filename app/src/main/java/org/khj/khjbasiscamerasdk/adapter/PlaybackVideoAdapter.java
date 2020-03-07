package org.khj.khjbasiscamerasdk.adapter;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.khj.Camera;

import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.bean.SearchDeviceInfoBean;
import org.khj.khjbasiscamerasdk.utils.DateUtils;

import java.util.List;

public class PlaybackVideoAdapter extends BaseQuickAdapter<Camera.fileTimeInfo, BaseViewHolder> {

    public PlaybackVideoAdapter() {
        super(R.layout.item_playbackvideo);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(BaseViewHolder helper, Camera.fileTimeInfo result) {
        TextView tv_videoStartTime = helper.getView(R.id.tv_videoStartTime);
        TextView tv_videoEndTime = helper.getView(R.id.tv_videoEndTime);
        tv_videoStartTime.setText("起始时间 " + DateUtils.getDateTime(result.videofiletime * 1000));
        tv_videoEndTime.setText("结束时间 " + DateUtils.getDateTime(result.videofiletime * 1000 + result.playbackTotalTime * 1000));
    }
}

