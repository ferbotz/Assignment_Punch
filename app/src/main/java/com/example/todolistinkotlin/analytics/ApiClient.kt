package com.example.todolistinkotlin.analytics


import android.provider.SyncStateContract
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface AnalyticsService {

    @POST(SESSION_DATA_END_POINT)
    fun postSessionAnalyticsData(@Body sessionData: AnalyticsSessionData): Call<String>

    @POST(ANALYTICS_EVENT_DATA_END_POINT)
    fun postAnalyticsEventData(@Body analyticsEvent: AnalyticsEvent): Call<String>

    @POST(CRASH_EVENT_DATA_END_POINT)
    fun postCrashEventData(@Body crashEvent: CrashEvent): Call<String>

}

object AnalyticsClient{
    val analyticsClient = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
            GsonBuilder()
                .setLenient()
                .create()))
        .baseUrl("https://dummy_base_url.com")
        .build()
        .create(AnalyticsService::class.java)
}