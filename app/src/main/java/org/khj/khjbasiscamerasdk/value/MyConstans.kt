package org.khjsdk.com.khjsdk_2020.value

import android.os.Environment

data class MyConstans(
    val KanHuJiaPath: String = Environment.getExternalStorageDirectory().absolutePath + "/org.khjtec.KhjSdk/",
    val FragmentLoadActivityTagDeviceUid: String = "deviceUidFragmentLoadActivity",
    val FragmentLoadActivityFragmentTag: String = "fragmentTagFragmentLoadActivity"
)