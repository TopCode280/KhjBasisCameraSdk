package org.khj.khjbasiscamerasdk.fragment.setting

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.xw.repo.BubbleSeekBar
import com.xw.repo.BubbleSeekBar.OnProgressChangedListener
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.databinding.FragmentMediapictureBinding
import org.khj.khjbasiscamerasdk.databinding.FragmentMovedetectBinding
import org.khj.khjbasiscamerasdk.viewmodel.MoveDetectViewModel

class MoveDetectFragment : BaseDeviceFragment<FragmentMovedetectBinding>() {

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

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMovedetectBinding =
        { inflater, container, attachToParent ->
            FragmentMovedetectBinding.inflate(inflater, container, attachToParent)
        }

    var moveDetectViewModel: MoveDetectViewModel? = null

    override fun initData() {
        super.initData()
        moveDetectViewModel = getViewModel(MoveDetectViewModel()::class.java)
        moveDetectViewModel?.run {
            setCamera(cameraWrapper!!, camera!!)
            alarmSwitch.observe(this@MoveDetectFragment, Observer {
                ignore = true
                binding.cbxMoveDetect.isChecked = it
                binding.seekBar.visibility = if (it) View.VISIBLE else View.GONE
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
        topBarBinding.topbar.setTitle(getString(R.string.alarmSetting))
        topBarBinding.topbar.addLeftBackImageButton().setOnClickListener { back() }
        if (apMode) {
            binding.rlReceiveAlarm.setVisibility(View.GONE)
        }
        if (cameraWrapper!!.getDevCap(CameraWrapper.Capability.FD)) {
           binding.rlFaceDetect.setVisibility(View.VISIBLE)
        }
        if (cameraWrapper!!.getDevCap(CameraWrapper.Capability.PD)) {
           binding.rlPeopleDetect.setVisibility(View.VISIBLE)
        }
    }


    override fun setListeners() {
        super.setListeners()
    }

    fun initSeekBar(current: Int) {
        binding.seekBar.configBuilder
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
        binding.seekBar.setCustomSectionTextArray { sectionCount: Int, array: SparseArray<String?> ->
            array.clear()
            array.put(0, getString(R.string.low))
            array.put(4, getString(R.string.high))
            array
        }
        binding.seekBar.onProgressChangedListener = object : OnProgressChangedListener {
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
                    binding.seekBar.setProgress(moveDetectViewModel!!.currentSensitivity.value!!.toFloat())
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