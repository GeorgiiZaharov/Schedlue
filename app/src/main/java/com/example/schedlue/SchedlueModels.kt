package com.example.schedlue

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Модель для отдельного занятия
@JsonClass(generateAdapter = true)
data class Lesson(
    val date: String,
    val number: String,
    val title: String,
    val classroom: String,
    val group: String,
    @Json(name = "start_time") val startTime: String,
    @Json(name = "end_time") val endTime: String,
    val lecturer: String,
    val type: String
)

// Модель для расписания недели
@JsonClass(generateAdapter = true)
data class WeeklySchedule(
    val numerator: List<List<Lesson>>,
    val denominator: List<List<Lesson>>
)

@JsonClass(generateAdapter = true)
data class LecturersResponse(
    val lecturers: List<String>
)

// Модель для получения текущей недели
@JsonClass(generateAdapter = true)
data class WeekResponse(
    val week: String
)

// Модель для получения времени последнего обновления
@JsonClass(generateAdapter = true)
data class TimeResponse(
    val time: Long
)
