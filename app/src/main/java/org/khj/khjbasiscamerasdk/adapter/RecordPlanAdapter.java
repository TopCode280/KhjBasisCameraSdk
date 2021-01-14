package org.khj.khjbasiscamerasdk.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.khj.khjbasiscamerasdk.App;
import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.bean.TimeSlot;
import org.khj.khjbasiscamerasdk.utils.TimeUtil;

public class RecordPlanAdapter extends BaseQuickAdapter<TimeSlot, BaseViewHolder> {
    public RecordPlanAdapter() {
        super(R.layout.item_record_plan);
    }

    @Override
    protected void convert(BaseViewHolder helper, TimeSlot timeSlot) {
        TextView tvTime = helper.getView(R.id.tv_time);
        String startTime = TimeUtil.long2String(timeSlot.startTime, "HH:mm");
        String stopTime = TimeUtil.long2String(timeSlot.endTime, "HH:mm");
        long temStart = TimeUtil.date2Long(startTime, "HH:mm");
        long temStop = TimeUtil.date2Long(stopTime, "HH:mm");
        if (temStart > temStop) {
            stopTime = stopTime + App.context.getResources().getString(R.string.nextDay);
        }
        tvTime.setText(startTime + "－" + stopTime);
    }


}
