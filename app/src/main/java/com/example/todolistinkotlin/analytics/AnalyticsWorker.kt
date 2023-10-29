package com.example.todolistinkotlin.analytics

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AnalyticsWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.v("Vasi testing","do work called.")
        val type = inputData.getString(ANALYTICS_DATA_TYPE)
        when(type){
            SESSION_DATA_TYPE ->{
                try {
                    AnalyticsClient.analyticsClient.postSessionAnalyticsData(
                        JsonUtils.objectify(inputData.getString(ANALYTICS_DATA_KEY), AnalyticsSessionData::class.java) as AnalyticsSessionData
                    ).let {
                        it.enqueue(object : Callback<String> {
                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                Result.success()
                            }

                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Result.retry()
                            }

                        })
                    }
                }catch (e:java.lang.Exception){
                    return Result.retry()
                }
            }
            else ->{
                Result.success()
            }
        }





        return Result.success()
    }

}