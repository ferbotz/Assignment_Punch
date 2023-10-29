package com.example.todolistinkotlin.analytics

import org.json.JSONObject

data class AnalyticsEvent(
    val event: Event,
    val eventProperties: JSONObject
):java.io.Serializable


enum class Event{
    TASK_ADDED,
    TASK_EDITED,
    TASK_DELETED,
    APP_FIRST_OPEN,
    MAIN_ACTIVITY_SHOWN,
    TASK_ITEM_CLICKED,
    TITLE_EDIT_TEXT_CLICKED,
    DATE_EDIT_TEXT_CLICKED,
    TIME_EDIT_TEXT_CLICKED
}


