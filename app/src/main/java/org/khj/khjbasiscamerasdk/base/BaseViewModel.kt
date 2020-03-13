package org.khj.khjbasiscamerasdk.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel :ViewModel() {
    var showToast = MutableLiveData<Boolean>()
}