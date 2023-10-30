package com.example.todolistinkotlin.analytics

import android.util.Log
import kotlin.system.exitProcess


/**
 * MyUncaughtExceptionHandler captures the app crash events and trigger the push crash event function in analytics manager
 * which triggers a worker to send the report to the backend for analytics.
 * */

class MyUncaughtExceptionHandler(val onException:(CrashEvent)-> Unit) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        onException(
            CrashEvent(
                exception = throwable.toString(),
                localizedMessage = throwable.localizedMessage
            ).also {
                Log.v("Vasi testing","crash...${JsonUtils.jsonify(it)}")
            }


        )
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(1)
    }
}