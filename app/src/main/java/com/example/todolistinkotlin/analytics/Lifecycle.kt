package com.example.todolistinkotlin.analytics

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.example.todolistinkotlin.enqueueAndroidEvent
import org.json.JSONObject

class Lifecycle : Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private val fragmentLifecycles:MutableMap<String, FragmentLifeCycle?> = mutableMapOf()
    override fun onActivityCreated(mActivity: Activity, p1: Bundle?) {
        (mActivity as? AppCompatActivity)?.let { activity ->
            fragmentLifecycles[activity.javaClass.simpleName]?.let { lifecycle ->
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(lifecycle)
            }
            fragmentLifecycles[activity.javaClass.simpleName] = FragmentLifeCycle{  currentFragmentName  ->
            }
            fragmentLifecycles[activity.javaClass.simpleName]?.let {  lifecycle ->
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(lifecycle,true)
            }
        }
    }

    override fun onActivityDestroyed(mActivity: Activity) {
        (mActivity as? AppCompatActivity)?.let { activity ->
            fragmentLifecycles[activity.javaClass.simpleName]?.let { lifecycle ->
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(lifecycle)
            }
            fragmentLifecycles[activity.javaClass.simpleName] = null
        }
    }

    override fun onActivityResumed(activity: Activity) {
        activity.application.enqueueAndroidEvent(
            AnalyticsEvent(
                Event.MAIN_ACTIVITY_SHOWN,
                JSONObject()
            )
        )

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

}