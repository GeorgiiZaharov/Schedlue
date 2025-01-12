package com.example.schedlue

import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Lesson(
    val number: String,
    val title: String,
    val classroom: String,
    val group: String,
    @Json(name = "start_time") val startTime: String,
    @Json(name = "end_time") val endTime: String,
    val lecturer: String,
    val type: String
)

@JsonClass(generateAdapter = true)
data class WeeklySchedule(
    val numerator: List<List<Lesson>>,
    val denominator: List<List<Lesson>>
)

@JsonClass(generateAdapter = true)
data class Lecturer(
    val name: String
)

@JsonClass(generateAdapter = true)
data class LecturersResponse(
    val lecturers: List<String> // JSON приходит как список строк
)

@JsonClass(generateAdapter = true)
data class Group(
    val code: String,
    val name: String
)

@JsonClass(generateAdapter = true)
data class WeekResponse(
    val week: String
)

@JsonClass(generateAdapter = true)
data class TimeResponse(
    val time: Long
)

