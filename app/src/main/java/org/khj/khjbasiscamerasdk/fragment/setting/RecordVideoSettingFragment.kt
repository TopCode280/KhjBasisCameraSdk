package org.khj.khjbasiscamerasdk.fragment.setting

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
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
import kotlinx.android.synthetic.main.fragment_recording_setting.*
import kotlinx.android.synthetic.main.topbar.*
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.adapter.RecordPlanAdapter
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.utils.TimePlanManager
import org.khj.khjbasiscamerasdk.view.SimpleMiddleDividerItemDecoration
import org.khj.khjbasiscamerasdk.viewmodel.RecordVideoSettingViewModel

class RecordVideoSettingFragment : BaseDeviceFragment(), View.OnClickListener {

    private val recorQualityArray: Array<String> by lazy {
        App.context.getResources().getStringArray(R.array.videoRecordQuality)
    }

    private val recorModeArray: Array<String> by lazy {
        App.context.getResources().getStringArray(R.array.recordMode)
    }

    private val queryPlanSub: Disposable? = null
    private val currentQuality: Int = 0//当前录像质量1->3
    private val videoRecordStatus: Int = 0//1是全天录像，0是定时录像
    private var recordPlanAdapter: RecordPlanAdapter? = null

    var recordVideoSettingViewModel: RecordVideoSettingViewModel? = null

    override fun contentViewId() = R.layout.fragment_recording_setting

    override fun initView() {
        super.initView()
        topbar.setTitle(getString(R.string.recordSet))
        topbar.addLeftBackImageButton().setOnClickListener { back() }
    }

    override fun setListeners() {
        super.setListeners()
        rl_recording.setOnClickListener(this)
        rl_videoQuality.setOnClickListener(this)
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
                    tvVideoQuality.text = recorQualityArray[it]
                }
            })
            videoRecordStatus.observe(this@RecordVideoSettingFragment, Observer {
                if (it > 0) {
                    tv_record_mode.text = recorModeArray.get(it)
                }
            })
            showRecodPlanViews.observe(this@RecordVideoSettingFragment, Observer {
                tv_tip_mode.visibility = if (it) View.VISIBLE else View.GONE
                recyclerView_record.visibility = if (it) View.VISIBLE else View.GONE
            })
            deviceInfo.observe(this@RecordVideoSettingFragment, Observer { ss ->
                val total = ss[0]
                val cardFree = ss[1]
                cameraWrapper!!.deviceInfo?.run {
                    if (total.trim() == "0") {
                        tv_sdCapacity.setText(R.string.noSDcard)
                        tv_sdFreeSize.setText(R.string.noSDcard)
                        hasSdcard = false
                    } else {
                        tv_sdCapacity.setText(R.string.normal)
                        tv_sdFreeSize.text = cardFree + "MB"
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun initRecycler() {
        recyclerView_record.layoutManager = LinearLayoutManager(
            mActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView_record.addItemDecoration(SimpleMiddleDividerItemDecoration(mActivity))
        recordPlanAdapter = RecordPlanAdapter()
        recordPlanAdapter?.apply {
            val footer = TextView(mActivity)
            footer.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                QMUIDisplayHelper.dpToPx(45)
            )
            footer.gravity = Gravity.CENTER
            footer.text = getString(R.string.addPlan)
            footer.setTextColor(mActivity.getColor(R.color.title_backBlue))
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
            recyclerView_record.adapter = this
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