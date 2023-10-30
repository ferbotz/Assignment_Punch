package com.example.todolistinkotlin.analytics

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.example.todolistinkotlin.enqueueAndroidEvent
import org.json.JSONObject

/**
 * The LifeCycle class implements the activity's lifecycle events and also connected with application's backstack making it
 * easy to call analytics events on every ui lifecycle events for every activity
 * */
class Lifecycle(val uiComponentShown: (AnalyticsEvent) -> Unit) : Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private val fragmentLifecycles:MutableMap<String, FragmentLifeCycle?> = mutableMapOf()
    override fun onActivityCreated(mActivity: Activity, p1: Bundle?) {
        (mActivity as? AppCompatActivity)?.let { activity ->
            fragmentLifecycles[activity.javaClass.simpleName]?.let { lifecycle ->
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(lifecycle)
            }
            fragmentLifecycles[activity.javaClass.simpleName] = FragmentLifeCycle{  currentFragmentName  ->
                uiComponentShown(
                    AnalyticsEvent(
                        Event.FRAGMENT_SHOWN,
                        JSONObject().apply {
                            put(FRAGMENT_NAME, currentFragmentName)
                        }
                    )
                )
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
        uiComponentShown(
            AnalyticsEvent(
                Event.ACTIVITY_SHOWN,
                JSONObject().apply {
                    put(ACTIVITY_NAME, activity.javaClass.simpleName)
                }
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