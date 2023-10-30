package com.example.todolistinkotlin.analytics

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AnalyticsWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO){
            val type = inputData.getString(ANALYTICS_DATA_TYPE)
            when(type){
                SESSION_DATA_TYPE ->{
                    try {
                        Log.v("Vasi testing","data...${JsonUtils.jsonify(inputData.getString(ANALYTICS_DATA_KEY))}")
                        AnalyticsClient.analyticsClient.postSessionAnalyticsData(
                            JsonUtils.objectify(inputData.getString(ANALYTICS_DATA_KEY), AnalyticsSessionData::class.java) as AnalyticsSessionData
                        ).let {
                            it.execute().let { response ->
                                if (response.isSuccessful){
                                    Result.success()
                                }
                                else{
                                    Result.retry()
                                }
                            }
                        }
                    }catch (e:java.lang.Exception){
                        Result.retry()
                    }
                }
                CRASH_DATA_TYPE ->{
                    try {
                        Log.v("Vasi testing","data...${JsonUtils.jsonify(inputData.getString(ANALYTICS_DATA_KEY))}")
                        AnalyticsClient.analyticsClient.postCrashEventData(
                            JsonUtils.objectify(inputData.getString(ANALYTICS_DATA_KEY), CrashEvent::class.java) as CrashEvent
                        ).let {
                            it.execute().let { response ->
                                if (response.isSuccessful){
                                    Result.success()
                                }
                                else{
                                    Result.retry()
                                }
                            }
                        }
                    }catch (e:java.lang.Exception){
                        Result.retry()
                    }
                }
                else ->{
                    Result.success()
                }
            }
        }
    }
}