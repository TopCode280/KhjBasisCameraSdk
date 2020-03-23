package org.khj.khjbasiscamerasdk.fragment.setting

import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.vise.log.ViseLog
import kotlinx.android.synthetic.main.fragment_mediapicture.*
import kotlinx.android.synthetic.main.topbar.*
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.adapter.SDVideoAdapter
import org.khj.khjbasiscamerasdk.base.BaseDeviceFragment
import org.khj.khjbasiscamerasdk.utils.TimeUtil
import org.khj.khjbasiscamerasdk.utils.ToastUtil
import org.khj.khjbasiscamerasdk.view.SimpleMiddleDividerItemDecoration
import org.khj.khjbasiscamerasdk.viewmodel.MediaViewModel
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class MediaFragment : BaseDeviceFragment() {

    val deviceFolder: File by lazy {
        File(myconstans.KanHuJiaPath + App.userAccount);
    }
    val sdVideoAdapter: SDVideoAdapter by lazy {
        SDVideoAdapter()
    }
    var mediaViewModel:MediaViewModel? = null

    var end: Long = 0
    private val noMoreFile = AtomicBoolean(false)

    override fun contentViewId() = R.layout.fragment_mediapicture

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
            if (!exists()){
                mkdirs()
            }
        }
    }

    override fun initView() {
        super.initView()
        iv_back.setOnClickListener { back() }
        recyclerView.setLayoutManager(LinearLayoutManager(mActivity))
        recyclerView.setAdapter(sdVideoAdapter)
        refreshLayout.setEnableRefresh(false)
        recyclerView.addItemDecoration(SimpleMiddleDividerItemDecoration(mActivity))
        refreshLayout.setOnLoadMoreListener {
            mediaViewModel?.refreshLayoutData(end,deviceFolder,false)
        }
        refreshLayout.setRefreshFooter(ClassicsFooter(mActivity)) //设置Footer
        refreshLayout.isEnableLoadMore = true
        refreshLayout.setEnableAutoLoadMore(true)
        refreshLayout.setEnableLoadMoreWhenContentNotFull(true)
        sdVideoAdapter.setNewData(null)
        mediaViewModel = getViewModel(MediaViewModel()::class.java)
        mediaViewModel?.run {
            setCamera(this@MediaFragment.cameraWrapper!!,this@MediaFragment.camera!!,this@MediaFragment.end,this@MediaFragment.deviceFolder)
            noMoreFile.observe(this@MediaFragment, androidx.lifecycle.Observer {
                this@MediaFragment.noMoreFile.set(it.get())
                if (it.get()){
                    refreshLayout.setNoMoreData(true)
                } else {
                    refreshLayout.setNoMoreData(false)
                }
            })
            dataList.observe(this@MediaFragment, androidx.lifecycle.Observer {
                emptyView.hide()
                sdVideoAdapter.setNewData(it)
            })
            errorToast.observe(this@MediaFragment, androidx.lifecycle.Observer {
                ToastUtil.showToast(context,it)
                refreshLayout.setNoMoreData(false)
                emptyView.show()
            })
            refreshLayoutData(this@MediaFragment.end,this@MediaFragment.deviceFolder,true)
        }
    }


    override fun setListeners() {
        super.setListeners()
    }
}