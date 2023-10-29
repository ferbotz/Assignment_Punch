package com.example.todolistinkotlin.analytics

import android.content.Context
import android.provider.Settings
import com.google.gson.Gson
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

fun getDateTime():String{
    Calendar.getInstance().let {
        val dateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        val date = dateFormat.format(it.time)
        return date
    }
}

fun getDeviceId(context: Context):String{
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

}

object JsonUtils{

    private val mGson: Gson = Gson()

    fun jsonify(`object`: Any?): String? {
        return mGson.toJson(`object`)
    }

    fun objectify(jsonString: String?, T: Class<*>?): Any? {
        return mGson.fromJson(jsonString, T)
    }

    fun <T> arrayObjectify(jsonString: String?, listType: Type?): Any? {
        return mGson.fromJson<Any>(jsonString, listType)
    }


}