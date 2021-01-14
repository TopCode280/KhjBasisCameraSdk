package org.khj.khjbasiscamerasdk.fragment.setting;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.vise.log.ViseLog;

import org.khj.khjbasiscamerasdk.App;
import org.khj.khjbasiscamerasdk.R;
import org.khj.khjbasiscamerasdk.bean.TimeSlot;
import org.khj.khjbasiscamerasdk.utils.CommonUtil;
import org.khj.khjbasiscamerasdk.utils.TimePlanManager;
import org.khj.khjbasiscamerasdk.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordPlanFragment extends BaseDeviceSetFragment {
    @BindView(R.id.topbar)
    QMUITopBar topbar;
    @BindView(R.id.tv_startRecord)
    TextView tvStartRecord;
    @BindView(R.id.rl_startRecord)
    RelativeLayout rlStartRecord;
    @BindView(R.id.tv_stopRecord)
    TextView tvStopRecord;
    @BindView(R.id.rl_stopRecord)
    RelativeLayout rlStopRecord;
    @BindView(R.id.btn_delete)
    Button btnDelete;

    private int index;
    private boolean ifAddNew = true;
    private TimeSlot timeSlot;

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        Bundle arguments = getArguments();

        if (arguments != null) {
            index = arguments.getInt("INDEX");
            ifAddNew = false;
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_record_plan;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initTitle() {
        if (ifAddNew) {
            btnDelete.setVisibility(View.GONE);
        }
        topbar.setTitle(getString(R.string.recordPlan));
        topbar.addLeftBackImageButton().setOnClickListener(v -> back());
        topbar.addRightTextButton(getString(R.string.save), QMUIViewHelper.generateViewId())
                .setOnClickListener(v -> {
                    String startTime = tvStartRecord.getText().toString().trim();
                    String stopTime = tvStopRecord.getText().toString().trim();
                    if (CommonUtil.isNull(startTime) || CommonUtil.isNull(stopTime)) {
                        Toasty.error(App.context, getString(R.string.slectStartAndEndTime)).show();
                        return;
                    }
                    long start = (long) tvStartRecord.getTag();
                    long stop = (long) tvStopRecord.getTag();
                    if (start == stop) {
                        Toasty.error(App.context, getString(R.string.planInvalid)).show();
                        return;
                    }
                    //如果停止录像时间小于开始录像时间，则认为停止录制时间为第二天
                    if (stop < start) {
                        stop += 24 * 60 * 60 * 1000;
                    }
                    TimeSlot timeSlot = new TimeSlot(0, start, stop);
                    boolean contained;
                    if (ifAddNew) {
                        contained = TimePlanManager.getInstance().checkIfContained(timeSlot);
                    } else {
                        contained = TimePlanManager.getInstance().checkIfContained(timeSlot, index);
                    }
                    if (contained) {
                        Toasty.error(App.context, getString(R.string.planRepeated)).show();
                    } else {
                        if (!ifAddNew) {
                            TimePlanManager.getInstance().getSlotArrayList().remove(index);
                        }
                        TimePlanManager.getInstance().addTimeSlot(timeSlot);
                        String plan = TimePlanManager.getInstance().toPlan();
                        ViseLog.e("addTimedRecordVideoTask" + plan);
                        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                            camera.addTimedRecordVideoTask(plan, b -> {
                                emitter.onNext(b);
                                emitter.onComplete();
                            });
                        }).observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe(disposable -> showLoading(disposable))
                                .doFinally(() -> {
                                    dismissLoading();
                                })
                                .subscribe(aBoolean -> {
                                    if (aBoolean) {
                                        Toasty.success(App.context, getString(R.string.modifySuccess)).show();
                                        back();
                                    } else {
                                        TimePlanManager.getInstance().getSlotArrayList().remove(timeSlot);
                                        Toasty.error(App.context, getString(R.string.modifyFailure)).show();
                                    }
                                });
                    }


                });
    }

    @Override
    protected void initView(View contentView) {
        super.initView(contentView);
        if (!ifAddNew) {
            timeSlot = TimePlanManager.getInstance().getSlotArrayList().get(index);
            long startTime = timeSlot.startTime;
            tvStartRecord.setText(TimeUtil.long2String(startTime, "HH:mm"));
            tvStartRecord.setTag(startTime);
            long endTime = timeSlot.endTime;
            tvStopRecord.setText(TimeUtil.long2String(endTime, "HH:mm"));
            tvStopRecord.setTag(endTime);

        } else {
            tvStartRecord.setTag(new Long(24 * 60 * 60 * 1000));
            tvStopRecord.setTag(new Long(24 * 60 * 60 * 1000));
        }
    }

    @OnClick({R.id.rl_startRecord, R.id.rl_stopRecord, R.id.btn_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_startRecord:
                chooseDate(tvStartRecord);
                break;
            case R.id.rl_stopRecord:
                chooseDate(tvStopRecord);
                break;
            case R.id.btn_delete:
                TimePlanManager.getInstance().deleteTimeSlot(timeSlot);
                String plan = TimePlanManager.getInstance().toPlan();
                camera.addTimedRecordVideoTask(plan, b -> {
                    ViseLog.d("删除一个录像计划" + b);
                });

                back();
                break;
        }
    }

    /**
     * 选择日期
     */
    private void chooseDate(TextView textView) {
        TimePickerView startTimeView = new TimePickerBuilder(mActivity, (date, v) -> {//选中事件回调
            String date2String = TimeUtil.Date2String(date, "HH:mm");
            textView.setText(date2String);
            textView.setTag(date.getTime());
        }).setType(new boolean[]{false, false, false, true, true, false})
                .build();
        //所有时间都转为UTC第0天的时刻
        Calendar instance = Calendar.getInstance();
        if (ifAddNew && textView == tvStopRecord) {
            instance.setTime(new Date((Long) tvStartRecord.getTag()));
        } else {
            instance.setTime(new Date((Long) textView.getTag()));
        }

        startTimeView.setDate(instance);
        startTimeView.show();
    }
}
