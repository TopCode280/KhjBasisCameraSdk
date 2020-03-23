package org.khj.khjbasiscamerasdk.fragment.setting

import android.util.SparseArray
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.xw.repo.BubbleSeekBar
import com.xw.repo.BubbleSeekBar.OnProgressChangedListener
import kotlinx.android.synthetic.main.fragment_movedetect.*
import kotlinx.android.synthetic.main.topbar.*
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.viewmodel.MoveDetectViewModel

class MoveDetectFragment : BaseDeviceFragment() {

    protected var ignore: Boolean = false
    private val array_interval: Array<String> by lazy {
        arrayOf("1分钟", "3分钟", "10分钟", "30分钟")
    }
    private val array_time: Array<String> by lazy {
        arrayOf("60", "180", "600", "1800")
    }
    private val devcieType: Int by lazy {
        camera!!.devcieType
    }
    var moveDetectViewModel: MoveDetectViewModel? = null

    override fun contentViewId() = R.layout.fragment_movedetect

    override fun initData() {
        super.initData()
        moveDetectViewModel = getViewModel(MoveDetectViewModel()::class.java)
        moveDetectViewModel?.run {
            setCamera(cameraWrapper!!, camera!!)
            alarmSwitch.observe(this@MoveDetectFragment, Observer {
                ignore = true
                cbx_moveDetect.isChecked = it
                seekBar.visibility = if (it) View.VISIBLE else View.GONE
                ignore = false
                getAlarmDetect()
            })
            alarmDetectionValue.observe(this@MoveDetectFragment, Observer {
                ignore = true
                initSeekBar(it)
                ignore = false
            })
        }
    }

    override fun initView() {
        super.initView()
        topbar.setTitle(getString(R.string.alarmSetting))
        topbar.addLeftBackImageButton().setOnClickListener { back() }
        if (apMode) {
            rl_receiveAlarm.setVisibility(View.GONE)
        }
        if (cameraWrapper!!.getDevCap(CameraWrapper.Capability.FD)) {
            rl_faceDetect.setVisibility(View.VISIBLE)
        }
        if (cameraWrapper!!.getDevCap(CameraWrapper.Capability.PD)) {
            rl_peopleDetect.setVisibility(View.VISIBLE)
        }
    }


    override fun setListeners() {
        super.setListeners()
    }

    fun initSeekBar(current: Int) {
        seekBar.configBuilder
            .min(1f)
            .progress(current.toFloat())
            .max(5f)
            .sectionCount(4)
            .trackColor(ContextCompat.getColor(mActivity, R.color.color_gray))
            .secondTrackColor(ContextCompat.getColor(mActivity, R.color.color_blue))
            .thumbColor(ContextCompat.getColor(mActivity, R.color.color_blue))
            .showSectionText()
            .sectionTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary))
            .sectionTextSize(18)
            .showThumbText()
            .thumbTextColor(ContextCompat.getColor(mActivity, R.color.color_red))
            .thumbTextSize(18)
            .bubbleColor(ContextCompat.getColor(mActivity, R.color.color_red))
            .bubbleTextSize(18)
            .showSectionMark()
            .thumbRadiusOnDragging(16)
            .thumbRadius(16)
            .seekStepSection()
            .touchToSeek()
            .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
            .build()
        seekBar.setCustomSectionTextArray { sectionCount: Int, array: SparseArray<String?> ->
            array.clear()
            array.put(0, getString(R.string.low))
            array.put(4, getString(R.string.high))
            array
        }
        seekBar.onProgressChangedListener = object : OnProgressChangedListener {
            override fun onProgressChanged(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {
            }

            override fun getProgressOnActionUp(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float
            ) {
                camera!!.setMotionDetect(progress) {
                    seekBar.setProgress(moveDetectViewModel!!.currentSensitivity.value!!.toFloat())
                }
            }

            override fun getProgressOnFinally(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {
            }
        }
    }
}