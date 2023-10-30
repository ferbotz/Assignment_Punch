package com.example.todolistinkotlin.analytics

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.todolistinkotlin.MainActivity
import java.util.UUID
import java.util.concurrent.TimeUnit



/**
 * The Analytics Manager acts as a single source of truth for the events trigger by the user aswell as pre defined events.
 * it uses application level scope making it a single reference for the entire application.
 *
 * The Analytics Manager uses Work Manager to post all the events to the backend because, with the constraints added
 * the Work Manager waits even for the internet connection and make sure that the given events is succesfully posted to the backend.
 * */
class AnalyticsManager(private val context: Context) {

    var sessionData: AnalyticsSessionData = AnalyticsSessionData(
        sessionId = UUID.randomUUID().toString(),
        sessionStartTime = System.currentTimeMillis()
    )

    var workManager: WorkManager = WorkManager.getInstance(context)

    init {
        startWorker()
    }

    /**
     * adds the user triggered event to the session events data.
     * */
    fun enqueueUserEvent(event: AnalyticsEvent){
        sessionData.sessionEvents.add(event)
        updateWorker()
        Log.v("Vasi testing", "new event:${JsonUtils.jsonify(event)}")
    }


    /**
     * adds the android event to the session events data.
     * */
    fun enqueueAndroidEvent(event: AnalyticsEvent){
        sessionData.sessionEvents.add(event)
        if (event.event == Event.ACTIVITY_SHOWN){
            sessionData.userScreenVisitFlow.add(event.eventProperties.getString(ACTIVITY_NAME))
        }
        else if (event.event == Event.FRAGMENT_SHOWN){
            sessionData.userScreenVisitFlow.add(event.eventProperties.getString(FRAGMENT_NAME))
        }
        updateWorker()
        Log.v("Vasi testing", "new event:${JsonUtils.jsonify(event)}")
    }

    /**
     * start new scheduled task with 30 minutes initial delay to post the current session data.
     * */
    private fun startWorker(){
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

    /**
     * updateWorker function cancels the ongoing scheduled task and start new scheduled task with 30 minutes
     * initial delay.
     * */
    private fun updateWorker(){
        workManager.cancelAllWorkByTag(ANALYTICS_WORKER_TAG)
        startWorker()
    }

    /**
     * pushCrashEvent function is used to post the crash issues to the backend for analysis.
     * */
    fun pushCrashEvent(crashEvent: CrashEvent){
        val crashEventWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<AnalyticsWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .setInputData(
                Data.Builder().apply {
                    putString(ANALYTICS_DATA_KEY, JsonUtils.jsonify(crashEvent))
                    putString(ANALYTICS_DATA_TYPE, CRASH_DATA_TYPE)
                }.build()
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        workManager.enqueue(crashEventWorkRequest)

    }

}