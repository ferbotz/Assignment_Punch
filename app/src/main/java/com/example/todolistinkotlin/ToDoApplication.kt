package com.example.todolistinkotlin

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.todolistinkotlin.analytics.*

class ToDoApplication(): Application() {

    val analyticsManager by lazy { AnalyticsManager(this) }

    private val processLifecycleOwner = ProcessLifecycleOwner.get()
    private var lifecycle:Lifecycle? = Lifecycle()


    override fun onCreate() {
        super.onCreate()

        processLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                Log.e("alarm123" , "start")
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                Log.e("alarm123" , "end")
            }
        })

        registerActivityLifecycleCallbacks(lifecycle)


    }

}

fun Application.enqueueUserEvent(event:AnalyticsEvent){
    event.eventProperties.apply {
        put(TIME_STAMP, getDateTime())
        put(DEVICE_ID, getDateTime())
        put(VERSION_CODE, BuildConfig.VERSION_CODE)
        put(VERSION_NAME, BuildConfig.VERSION_NAME)
    }
    (this as ToDoApplication).analyticsManager.enqueueUserEvent(event)
}

fun Application.enqueueAndroidEvent(event:AnalyticsEvent){
    event.eventProperties.apply {
        put(TIME_STAMP, getDateTime())
        put(DEVICE_ID, getDateTime())
        put(VERSION_CODE, BuildConfig.VERSION_CODE)
        put(VERSION_NAME, BuildConfig.VERSION_NAME)
    }
    (this as ToDoApplication).analyticsManager.enqueueAndroidEvent(event)
}

fun Application.flush(){
    (this as ToDoApplication).analyticsManager.flush()
}