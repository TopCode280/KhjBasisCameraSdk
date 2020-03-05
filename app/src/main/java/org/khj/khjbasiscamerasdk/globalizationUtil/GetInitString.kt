package org.khjsdk.com.khjsdk_2020.globalizationUtil

import org.khjsdk.com.khjsdk_2020.value.InitString

class GetInitString private constructor() {
    companion object {
        private var instance: GetInitString? = null
            get() {
                if (field == null) {
                    field = GetInitString()
                }
                return field
            }
        fun get(): GetInitString{
            //细心的小伙伴肯定发现了，这里不用getInstance作为为方法名，是因为在伴生对象声明时，内部已有getInstance方法，所以只能取其他名字
            return instance!!
        }
    }

    fun getString(uidPrefix: String): String {
        val initString: String
        val initstring = InitString()
        when (uidPrefix) {
            "KHJ" -> initString = initstring.CN_INIT
            "AIS" -> initString = initstring.AISA_INIT //亚洲区
            "KEU" -> initString = initstring.EUR_INIT //欧洲
            "KUS" -> initString = initstring.USA_INIT //北美
            "KBR" -> initString = initstring.KBR_INIT //南美巴西
            else -> initString = initstring.CN_INIT
        }
        return ",$initString"
    }
}