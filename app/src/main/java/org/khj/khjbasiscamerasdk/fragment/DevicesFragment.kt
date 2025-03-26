package org.khjsdk.com.khjsdk_2020.mvvm.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.qmuiteam.qmui.util.QMUIViewHelper
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.vise.log.ViseLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.khj.khjbasiscamerasdk.App
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.activity.addDevices.AddDeviceActivity
import org.khj.khjbasiscamerasdk.activity.video.WatchVideoActivity
import org.khj.khjbasiscamerasdk.adapter.DeviceListAdapter
import org.khj.khjbasiscamerasdk.av_modle.CameraManager
import org.khj.khjbasiscamerasdk.av_modle.CameraWrapper
import org.khj.khjbasiscamerasdk.base.BaseFragment
import org.khj.khjbasiscamerasdk.database.EntityManager
import org.khj.khjbasiscamerasdk.database.entity.DeviceEntity
import org.khj.khjbasiscamerasdk.databinding.ActivityMainBinding
import org.khj.khjbasiscamerasdk.databinding.FragmentDevicesBinding
import org.khj.khjbasiscamerasdk.eventbus.DevicesListRefreshEvent
import org.khj.khjbasiscamerasdk.greendao.DeviceEntityDao
import org.khj.khjbasiscamerasdk.view.MyLinearLayoutManager
import org.khj.khjbasiscamerasdk.view.SimpleDividerItemDecoration
import org.khjsdk.com.khjsdk_2020.eventbus.CameraStatusEvent
import org.khjsdk.com.khjsdk_2020.eventbus.CheckOnLineEvent

class DevicesFragment : BaseFragment<FragmentDevicesBinding>(), OnRefreshListener {

    private var deviceListAdapter: DeviceListAdapter? = null
    private var deviceList: MutableList<CameraWrapper>? = ArrayList()

    override fun userEventbus() = true

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDevicesBinding {
        return FragmentDevicesBinding.inflate(inflater, container, false)
    }

    override fun initView() {
       topBarBinding.topbar.setTitle(R.string.deviceList)
        deviceList = CameraManager.getInstance().cameras
        deviceListAdapter = DeviceListAdapter(deviceList)
        binding.refreshLayout.setEnableLoadMore(false)
        binding.recyclerView.setLayoutManager(MyLinearLayoutManager(context))
        binding.recyclerView.setAdapter(deviceListAdapter)
        binding.recyclerView.addItemDecoration(SimpleDividerItemDecoration(context))
        binding.refreshLayout.autoRefresh()
    }

    override fun setListeners() {
        topBarBinding.topbar.addRightImageButton(R.mipmap.add, QMUIViewHelper.generateViewId())
            .setOnClickListener { v ->
                val intent = Intent(activity, AddDeviceActivity::class.java)
                intent.putExtra("INDEX", 0)
                startActivity(intent)
            }

        binding.ivAddNew.setOnClickListener { v ->
            val intent = Intent(activity, AddDeviceActivity::class.java)
            startActivity(intent)
        }
        deviceListAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            if (position >= deviceListAdapter!!.getData().size) {
                return@setOnItemChildClickListener
            }
            try {
                val cameraWrapper = deviceListAdapter!!.getData()[position]
                when (view.id) {
                    R.id.iv_pic -> {
                        val intent = Intent(context, WatchVideoActivity::class.java)
                        intent.putExtra("uid", cameraWrapper!!.uid)
                        startActivity(intent)
                    }
                    R.id.btnDelete -> {
                        deviceListAdapter!!.data.clear()
                        deviceList!!.clear()
                        CameraManager.getInstance()
                            .removeCamera(cameraWrapper.deviceEntity.getDeviceUid())
                        EntityManager.getInstance()
                            .deviceEntityDao.deleteByKey(cameraWrapper.deviceEntity.id)
                        binding.refreshLayout.autoRefresh()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        binding.refreshLayout.setOnRefreshListener(this)
    }

    override fun initData() {
    }

    override fun loadData() {
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        val localDisposable = Observable.create<List<DeviceEntity>> {
            val deviceEntityDao = EntityManager.getInstance().getDeviceEntityDao()
            val localDevices = deviceEntityDao.queryBuilder()
                .where(DeviceEntityDao.Properties.UserId.eq(App.userAccount)).list()
            it.onNext(localDevices)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.size > 0) {
                    getLocalDeviceList(it)
                    binding.emptyView.visibility = View.GONE
                } else {
                    context?.let {
                        Toast.makeText(
                            it, "该用户ID" +
                                    "没有绑定过设备", Toast.LENGTH_LONG
                        ).show()
                    }
                    binding.emptyView.visibility = View.VISIBLE
                }
            }
        mDisposable!!.add(localDisposable)
        binding.refreshLayout.finishRefresh()
    }

    private fun getLocalDeviceList(deviceEntityList: List<DeviceEntity>) {
        CameraManager.getInstance().updateCameras(deviceEntityList)
        deviceList = CameraManager.getInstance().getCameras()
        ViseLog.e(deviceList!!.size.toString() + "---------------------------------" + deviceEntityList.size)
        deviceListAdapter!!.getData().clear()
        deviceListAdapter!!.setNewData(deviceList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDisposable?.let {
            mDisposable!!.dispose()
        }
    }


    @Subscribe
    fun ListRefresh(refreshEvent: DevicesListRefreshEvent?) {
        binding.refreshLayout.autoRefresh()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun checkOnLineStatus(checkOnLineEvent: CheckOnLineEvent) {
        ViseLog.i("checkOnline eventbus 发出消息 刷新 ${checkOnLineEvent.uid} 状态")
        for (index in 0 until deviceListAdapter!!.data.size) {
            if (deviceListAdapter!!.data[index].uid.equals(checkOnLineEvent.uid)) {
                deviceListAdapter!!.notifyItemChanged(index)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCameraStausChanged(statusEvent: CameraStatusEvent) { //        if (!App.isIsRunInBackground()) {
        ViseLog.i("收到刷新设备列表EventBus消息")
        deviceListAdapter!!.notifyDataSetChanged()
    }
}
