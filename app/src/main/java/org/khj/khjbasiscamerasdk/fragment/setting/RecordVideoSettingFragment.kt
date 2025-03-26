package org.khj.khjbasiscamerasdk.fragment.setting

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import es.dmoral.toasty.Toasty
import io.reactivex.disposables.Disposable
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.adapter.RecordPlanAdapter
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.databinding.FragmentDevicesBinding
import org.khj.khjbasiscamerasdk.databinding.FragmentMovedetectBinding
import org.khj.khjbasiscamerasdk.databinding.FragmentRecordingSettingBinding
import org.khj.khjbasiscamerasdk.utils.TimePlanManager
import org.khj.khjbasiscamerasdk.view.SimpleMiddleDividerItemDecoration
import org.khj.khjbasiscamerasdk.viewmodel.RecordVideoSettingViewModel

class RecordVideoSettingFragment: BaseDeviceFragment<FragmentRecordingSettingBinding>(), View.OnClickListener {

    private val recorQualityArray: Array<String> by lazy {
        App.context.getResources().getStringArray(R.array.videoRecordQuality)
    }

    private val recorModeArray: Array<String> by lazy {
        App.context.getResources().getStringArray(R.array.recordMode)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRecordingSettingBinding =
        { inflater, container, attachToParent ->
            FragmentRecordingSettingBinding.inflate(inflater, container, attachToParent)
        }

    private val queryPlanSub: Disposable? = null
    private val currentQuality: Int = 0//当前录像质量1->3
    private val videoRecordStatus: Int = 0//1是全天录像，0是定时录像
    private var recordPlanAdapter: RecordPlanAdapter? = null

    var recordVideoSettingViewModel: RecordVideoSettingViewModel? = null


    override fun initView() {
        super.initView()
        topBarBinding.topbar.setTitle(getString(R.string.recordSet))
        topBarBinding.topbar.addLeftBackImageButton().setOnClickListener { back() }
    }

    override fun setListeners() {
        super.setListeners()
        binding.rlRecording.setOnClickListener(this)
       binding.rlVideoQuality.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        super.initData()
        recordVideoSettingViewModel = getViewModel(RecordVideoSettingViewModel()::class.java)
        recordVideoSettingViewModel?.run {
            setCamera(
                this@RecordVideoSettingFragment.cameraWrapper!!,
                this@RecordVideoSettingFragment.camera!!
            )
            currentQuality.observe(this@RecordVideoSettingFragment, Observer {
                if (it > 0) {
                  binding.tvVideoQuality.text = recorQualityArray[it]
                }
            })
            videoRecordStatus.observe(this@RecordVideoSettingFragment, Observer {
                if (it > 0) {
                  binding.tvRecordMode.text = recorModeArray.get(it)
                }
            })
            showRecodPlanViews.observe(this@RecordVideoSettingFragment, Observer {
                binding.tvTipMode.visibility = if (it) View.VISIBLE else View.GONE
                binding.recyclerViewRecord.visibility = if (it) View.VISIBLE else View.GONE
            })
            deviceInfo.observe(this@RecordVideoSettingFragment, Observer { ss ->
                val total = ss[0]
                val cardFree = ss[1]
                cameraWrapper!!.deviceInfo?.run {
                    if (total.trim() == "0") {
                       binding.tvSdCapacity.setText(R.string.noSDcard)
                       binding.tvSdFreeSize.setText(R.string.noSDcard)
                        hasSdcard = false
                    } else {
                        binding.tvSdCapacity.setText(R.string.normal)
                        binding.tvSdFreeSize.text = cardFree + "MB"
                        cameraWrapper!!.deviceInfo.let {
                            hasSdcard = true
                            sdcardTotal = total.toInt();
                            sdcardFree = cardFree.toInt();
                        }
                    }
                }
            })
            slotArrayList.observe(this@RecordVideoSettingFragment, Observer { ss ->
                recordPlanAdapter?.apply {
                    setNewData(ss)
                }
            })
            loaddingStatus.observe(this@RecordVideoSettingFragment, Observer { show ->
                if (show) showLoading() else dismissLoading()
            })
            settingResultToastShow.observe(this@RecordVideoSettingFragment, Observer { success ->
                changeResultToast(success)
            })
            settingResultToastMsg.observe(this@RecordVideoSettingFragment, Observer { msg ->
                Toasty.info(mActivity, msg, Toast.LENGTH_LONG)
            })
        }
        initRecycler()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_recording -> {
                recordVideoSettingViewModel?.recordingClicklisten(mActivity, recorModeArray)
            }
            R.id.rl_videoQuality -> {
                recordVideoSettingViewModel?.videoQualityClicklisten(mActivity, recorQualityArray)
            }
            R.id.rl_formatSdcard -> {
                if (!cameraWrapper!!.deviceInfo.hasSdcard) {
                    Toasty.error(App.context, App.context.getString(R.string.sdcardNotAvailable))
                        .show()
                    return
                }
                recordVideoSettingViewModel?.showFormatDialog(mActivity)
            }
        }
    }

    fun initRecycler() {
       binding.recyclerViewRecord.layoutManager = LinearLayoutManager(
            mActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerViewRecord.addItemDecoration(SimpleMiddleDividerItemDecoration(mActivity))
        recordPlanAdapter = RecordPlanAdapter()
        recordPlanAdapter?.apply {
            val footer = TextView(mActivity)
            footer.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                QMUIDisplayHelper.dpToPx(45)
            )
            footer.gravity = Gravity.CENTER
            footer.text = getString(R.string.addPlan)
            footer.setTextColor(mActivity.resources.getColor(R.color.title_backBlue))
            footer.setBackgroundColor(Color.WHITE)
            footer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            footer.setOnClickListener { v: View? ->
                fragmentHelp?.apply {
                    val recordPlanFragment = RecordPlanFragment()
                    addToStack(recordPlanFragment)
                    switchFragment(recordPlanFragment, true, "RecordPlanFragment")
                }
            }
            addFooterView(footer)
            binding.recyclerViewRecord.adapter = this
            setOnItemClickListener { adapter, view, position ->
                fragmentHelp?.apply {
                    val recordPlanFragment = RecordPlanFragment()
                    val bundle = Bundle()
                    bundle.putInt("INDEX", position)
                    recordPlanFragment.arguments = bundle
                    addToStack(recordPlanFragment)
                    switchFragment(recordPlanFragment, true, "TimePlanFragment")
                }
            }
            setNewData(TimePlanManager.getInstance().slotArrayList)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            recordPlanAdapter?.setNewData(TimePlanManager.getInstance().slotArrayList)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        TimePlanManager.getInstance().release()
    }

}