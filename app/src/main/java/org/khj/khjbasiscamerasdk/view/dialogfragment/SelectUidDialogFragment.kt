package org.khj.khjbasiscamerasdk.view.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.khj.Camera
import com.vise.log.ViseLog
import org.khj.khjbasiscamerasdk.R
import org.khj.khjbasiscamerasdk.adapter.UidSelectAdapter
import org.khj.khjbasiscamerasdk.bean.SearchDeviceInfoBean
import org.khj.khjbasiscamerasdk.view.SimpleMiddleDividerItemDecoration

class SelectUidDialogFragment : DialogFragment() {

    var uidListSelect: UidListSelect? = null
    var uidSelectAdapter: UidSelectAdapter? = null
    var uidList: MutableList<SearchDeviceInfoBean> = ArrayList()
    private var rv_showSearchUid: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initInstance()
        val view = inflater.inflate(R.layout.dialogfragment_lanaddlist, container, false)
        rv_showSearchUid = view.findViewById(R.id.rv_showSearchUid)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        uidList = getArguments()?.getParcelableArrayList<SearchDeviceInfoBean>("uidList") as MutableList<SearchDeviceInfoBean>
        ViseLog.i(uidList)
        uidSelectAdapter = UidSelectAdapter(uidList)
        uidSelectAdapter?.setOnItemClickListener { adapter, view, position ->
            run {
                uidListSelect?.clickUid(uidList.get(position).uid)
            }
            dismiss()
        }
        rv_showSearchUid?.run {
            setLayoutManager(LinearLayoutManager(context))
            addItemDecoration(SimpleMiddleDividerItemDecoration(context))
            setAdapter(uidSelectAdapter)
        }
    }

    private fun initInstance() {
        uidListSelect = if (parentFragment != null) {
            parentFragment as UidListSelect
        } else {
            activity as UidListSelect
        }
    }

    interface UidListSelect {
        fun clickUid(uid: String)
    }
}