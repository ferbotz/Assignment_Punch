package com.example.todolistinkotlin.analytics

data class AnalyticsSessionData(
    val sessionId: String,
    val sessionStartTime: Long,
    var sessionEndTime: Long? = null,
    val sessionEvents: MutableList<AnalyticsEvent> = mutableListOf(),
    var timeSpent: Long? = null,
    val userScreenVisitFlow: MutableList<String> = mutableListOf()
):java.io.Serializable
