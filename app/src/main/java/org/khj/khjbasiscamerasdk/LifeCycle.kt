package org.khj.khjbasiscamerasdk

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

/**
 * Created by xiaolei on 2018/1/16.
 */
object LifeCycle : Application.ActivityLifecycleCallbacks
{
    private val activitys = LinkedList<Activity>()
    fun add(activity: Activity) = activitys.add(activity)
    fun remove(activity: Activity) = activitys.remove(activity)
    fun finishAll()
    {
        activitys.filter { activity ->
            !activity.isFinishing
        }.forEach { ac ->
            ac.finish()
        }
    }

    fun exit()
    {
        finishAll()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    override fun onActivityDestroyed(activity: Activity)
    {
        remove(activity)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?)
    {
        add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }
}