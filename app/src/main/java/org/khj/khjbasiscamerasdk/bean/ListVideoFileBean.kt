package org.khj.khjbasiscamerasdk.bean

import com.khj.Camera

class ListVideoFileBean {
    var info :ArrayList<Camera.fileInfo>? = null
    var result :Boolean? = null

    constructor(info: ArrayList<Camera.fileInfo>, result: Boolean) {
        this.info = info
        this.result = result
    }
}