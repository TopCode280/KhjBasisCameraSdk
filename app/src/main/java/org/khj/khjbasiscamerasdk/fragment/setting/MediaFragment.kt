package org.khj.khjbasiscamerasdk.fragment.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.vise.log.ViseLog
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.adapter.SDVideoAdapter
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.databinding.FragmentDevicesBinding
import org.khj.khjbasiscamerasdk.databinding.FragmentMediapictureBinding
import org.khj.khjbasiscamerasdk.utils.TimeUtil
import org.khj.khjbasiscamerasdk.utils.ToastUtil
import org.khj.khjbasiscamerasdk.view.SimpleMiddleDividerItemDecoration
import org.khj.khjbasiscamerasdk.viewmodel.MediaViewModel
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class MediaFragment : BaseDeviceFragment<FragmentMediapictureBinding>() {

    val deviceFolder: File by lazy {
        File(myconstans.KanHuJiaPath + App.userAccount);
    }
    val sdVideoAdapter: SDVideoAdapter by lazy {
        SDVideoAdapter()
    }
    var mediaViewModel: MediaViewModel? = null

    var end: Long = 0
    private val noMoreFile = AtomicBoolean(false)

    override fun initData() {
        super.initData()
        cameraWrapper?.run {
            val date2String: String = TimeUtil.Date2String(Date(), "yyyy_MM_dd")
            ViseLog.e("今天 $date2String")
            end = TimeUtil.date2Long(date2String, "yyyy_MM_dd") / 1000
            arguments?.let {
                end = arguments!!.getLong("DATE")
            }
            ViseLog.e("end$end")
        }
        deviceFolder.run {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMediapictureBinding =
        { inflater, container, attachToParent ->
            FragmentMediapictureBinding.inflate(inflater, container, attachToParent)
        }


    override fun initView() {
        super.initView()
        binding.ivBack.setOnClickListener { back() }
        binding.recyclerView.setLayoutManager(LinearLayoutManager(mActivity))
        binding.recyclerView.setAdapter(sdVideoAdapter)
        binding.refreshLayout.setEnableRefresh(false)
        binding.recyclerView.addItemDecoration(SimpleMiddleDividerItemDecoration(mActivity))
        binding.refreshLayout.setOnLoadMoreListener {
            mediaViewModel?.refreshLayoutData(end, deviceFolder, false)
        }
        binding.refreshLayout.setRefreshFooter(ClassicsFooter(mActivity)) //设置Footer
        binding.refreshLayout.isEnableLoadMore = true
        binding.refreshLayout.setEnableAutoLoadMore(true)
        binding.refreshLayout.setEnableLoadMoreWhenContentNotFull(true)
        sdVideoAdapter.setNewData(null)
        mediaViewModel = getViewModel(MediaViewModel()::class.java)
        mediaViewModel?.run {
            setCamera(
                this@MediaFragment.cameraWrapper!!,
                this@MediaFragment.camera!!,
                this@MediaFragment.end,
                this@MediaFragment.deviceFolder
            )
            noMoreFile.observe(this@MediaFragment) {
                this@MediaFragment.noMoreFile.set(it.get())
                if (it.get()) {
                    binding.refreshLayout.setNoMoreData(true)
                } else {
                    binding.refreshLayout.setNoMoreData(false)
                }
            }
            dataList.observe(this@MediaFragment) {
                binding.emptyView.hide()
                sdVideoAdapter.setNewData(it)
            }
            errorToast.observe(this@MediaFragment) {
                ToastUtil.showToast(context, it)
                binding.refreshLayout.setNoMoreData(false)
                binding.emptyView.show()
            }
            refreshLayoutData(this@MediaFragment.end, this@MediaFragment.deviceFolder, true)
        }
    }


    override fun setListeners() {
        super.setListeners()
    }
}