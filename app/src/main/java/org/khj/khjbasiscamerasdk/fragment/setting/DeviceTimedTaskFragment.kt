package org.khj.khjbasiscamerasdk.fragment.setting

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.databinding.FragmentMediapictureBinding
import org.khj.khjbasiscamerasdk.databinding.FragmentTimePlanBinding
import org.khj.khjbasiscamerasdk.utils.TimeUtil
import org.khj.khjbasiscamerasdk.viewmodel.DeviceTimedTaskViewModel

class DeviceTimedTaskFragment : BaseDeviceFragment<FragmentTimePlanBinding>(), View.OnClickListener {

    private var viewModel: DeviceTimedTaskViewModel? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTimePlanBinding =
        { inflater, container, attachToParent ->
            FragmentTimePlanBinding.inflate(inflater, container, attachToParent)
        }

    override fun initView() {
        super.initView()
        topBarBinding.topbar.setTitle(getString(R.string.deviceOnOff))
        topBarBinding.topbar.addLeftBackImageButton().setOnClickListener { back() }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        super.initData()
        viewModel = getViewModel(DeviceTimedTaskViewModel::class.java)
        viewModel?.run {
            setCamera(
                this@DeviceTimedTaskFragment.cameraWrapper!!,
                this@DeviceTimedTaskFragment.camera!!
            )
            showToast.observe(this@DeviceTimedTaskFragment, Observer {
                changeResultToast(it)
            })
        }
    }

    override fun setListeners() {
        super.setListeners()
        binding.rlCloseTime.setOnClickListener(this)
        binding.rlOpenTime.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_closeTime -> {

            }
            R.id.rl_openTime -> {

            }
        }
    }

    /**
     * 选择日期
     */
    fun chooseDate(textView: TextView, isOpen: Boolean) {
        var startTimeView: TimePickerView =
            TimePickerBuilder(mActivity, OnTimeSelectListener { date, v -> { //选中事件回调
                val date2String: String = TimeUtil.Date2String(date, "HH:mm")
                textView.text = date2String
                textView.tag = date.time
//                if (tvCloseTime.getTag() != null && tvOpenTime.getTag() != null) {
//                    val close = tvCloseTime.getTag() as Long
//                    val open = tvOpenTime.getTag() as Long
//                    if (close > open) {
//                        var prefix = TimeUtil.long2String(open, "HH:mm")
//                        prefix = prefix + App.context.getString(R.string.nextDay)
//                        tvOpenTime.setText(prefix)
//                    }
//                }
                }
            }).setType(booleanArrayOf(false, false, false, true, true, false))
                .build();
    }
}