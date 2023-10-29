package com.example.todolistinkotlin.analytics

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.todolistinkotlin.MainActivity
import java.util.UUID
import java.util.concurrent.TimeUnit

class AnalyticsManager(private val context: Context) {

    var sessionData: AnalyticsSessionData = AnalyticsSessionData(
        sessionId = UUID.randomUUID().toString(),
        sessionStartTime = System.currentTimeMillis()
    )

    var workManager: WorkManager = WorkManager.getInstance(context)

    init {
        startWorker()
    }

    fun enqueueUserEvent(event: AnalyticsEvent){
        sessionData.sessionEvents.add(event)
        updateWorker()
        Log.v("Vasi testing", "new event:${JsonUtils.jsonify(event)}")
    }

    fun enqueueAndroidEvent(event: AnalyticsEvent){
        sessionData.sessionEvents.add(event)
        if (event.event == Event.MAIN_ACTIVITY_SHOWN){
            sessionData.userScreenVisitFlow.add(MainActivity::class.java.simpleName)
        }
        updateWorker()
        Log.v("Vasi testing", "new event:${JsonUtils.jsonify(event)}")
    }

    fun startWorker(){
        sessionData.sessionEndTime = System.currentTimeMillis()
        sessionData.timeSpent = (System.currentTimeMillis() - sessionData.sessionStartTime)/1000
        val flushEventsWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<AnalyticsWorker>()
            .setInitialDelay(30, TimeUnit.MINUTES)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .setInputData(
                Data.Builder().apply {
                    putString(ANALYTICS_DATA_KEY, JsonUtils.jsonify(sessionData))
                    putString(ANALYTICS_DATA_TYPE, SESSION_DATA_TYPE)
                }.build()
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(ANALYTICS_WORKER_TAG)
            .build()
        workManager.enqueue(flushEventsWorkRequest)
    }

    fun updateWorker(){
        workManager.cancelAllWorkByTag(ANALYTICS_WORKER_TAG)
        startWorker()
    }

    fun flush(){
        val flushEventsWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<AnalyticsWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(flushEventsWorkRequest)
    }


}