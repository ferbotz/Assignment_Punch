package com.example.todolistinkotlin

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.todolistinkotlin.analytics.*

class ToDoApplication(): Application() {

    val analyticsManager by lazy { AnalyticsManager(this) }

    private val processLifecycleOwner = ProcessLifecycleOwner.get()
    private var lifecycle:Lifecycle? = Lifecycle(){
        analyticsManager.enqueueAndroidEvent(it)
    }


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

        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler(){
            analyticsManager.pushCrashEvent(it)
        })


    }

}

fun Application.enqueueUserEvent(event:AnalyticsEvent){
    event.eventProperties.apply {
        put(TIME_STAMP, getDateTime())
        put(DEVICE_ID, getDateTime())
        put(VERSION_CODE, BuildConfig.VERSION_CODE)
        put(VERSION_NAME, BuildConfig.VERSION_NAME)
        put(DEVICE_MODEL, Build.MODEL)
        put(DEVICE_BRAND, Build.BRAND)
    }
    (this as ToDoApplication).analyticsManager.enqueueUserEvent(event)
}

fun Application.enqueueAndroidEvent(event:AnalyticsEvent){
    event.eventProperties.apply {
        put(TIME_STAMP, getDateTime())
        put(DEVICE_ID, getDateTime())
        put(VERSION_CODE, BuildConfig.VERSION_CODE)
        put(VERSION_NAME, BuildConfig.VERSION_NAME)
        put(DEVICE_MODEL, Build.MODEL)
        put(DEVICE_BRAND, Build.BRAND)
    }
    (this as ToDoApplication).analyticsManager.enqueueAndroidEvent(event)
}