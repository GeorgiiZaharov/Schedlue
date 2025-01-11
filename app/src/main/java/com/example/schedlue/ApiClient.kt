package com.example.schedlue

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request

class ApiClient {
    private val baseUrl: String = "https://petrsu.egipti.com/"
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun getWeeklySchedule(groupNumber: String): Result<WeeklySchedule> {
        val adapter = moshi.adapter(WeeklySchedule::class.java)

        val request = Request.Builder()
            .url("$baseUrl/api/v2/schedule/$groupNumber")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(Exception("Ошибка при выполнении запроса: ${response.code}"))
                }

                val body = response.body?.string()
                    ?: return Result.failure(Exception("Пустой ответ от сервера"))

                val schedule = adapter.fromJson(body)
                    ?: return Result.failure(Exception("Ошибка парсинга JSON"))

                Result.success(schedule)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getLecturers(): Result<List<Lecturer>> {
        val request = Request.Builder()
            .url("$baseUrl/api/v2/lecturers")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(Exception("Ошибка при выполнении запроса: ${response.code}"))
                }

                val body = response.body?.string()
                    ?: return Result.failure(Exception("Пустой ответ от сервера"))

                // Парсим JSON как список строк
                val lecturers = moshi.adapter(List::class.java).fromJson(body) as? List<String>
                    ?: return Result.failure(Exception("Ошибка парсинга JSON"))

                // Преобразуем список строк в список объектов Lecturer
                val lecturerObjects = lecturers.map { Lecturer(name = it) }

                Result.success(lecturerObjects)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getLecturerSchedule(lecturerName: String): Result<WeeklySchedule> {
        val adapter = moshi.adapter(WeeklySchedule::class.java)

        val request = Request.Builder()
            .url("$baseUrl/api/v2/lecturer?name=$lecturerName")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(Exception("Ошибка при выполнении запроса: ${response.code}"))
                }

                val body = response.body?.string()
                    ?: return Result.failure(Exception("Пустой ответ от сервера"))

                val schedule = adapter.fromJson(body)
                    ?: return Result.failure(Exception("Ошибка парсинга JSON"))

                Result.success(schedule)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentWeek(): Result<String> {
        val request = Request.Builder()
            .url("$baseUrl/api/v2/week")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(Exception("Ошибка при выполнении запроса: ${response.code}"))
                }

                val body = response.body?.string()
                    ?: return Result.failure(Exception("Пустой ответ от сервера"))

                val weekResponse = moshi.adapter(WeekResponse::class.java).fromJson(body)
                    ?: return Result.failure(Exception("Ошибка парсинга JSON"))

                Result.success(weekResponse.week)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun getLastUpdateTime(): Result<Long> {
        val request = Request.Builder()
            .url("$baseUrl/api/v2/time")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(Exception("Ошибка при выполнении запроса: ${response.code}"))
                }

                val body = response.body?.string()
                    ?: return Result.failure(Exception("Пустой ответ от сервера"))

                val timeResponse = moshi.adapter(TimeResponse::class.java).fromJson(body)
                    ?: return Result.failure(Exception("Ошибка парсинга JSON"))

                Result.success(timeResponse.time)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun getGroups(): Result<List<Group>> {
        val request = Request.Builder()
            .url("$baseUrl/api/v2/groups")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(Exception("Ошибка при выполнении запроса: ${response.code}"))
                }

                val body = response.body?.string() ?: return Result.failure(Exception("Пустой ответ от сервера"))

                // Создаем адаптер для десериализации Map<String, String>
                val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
                val adapter = moshi.adapter<Map<String, String>>(type)
                val groupsMap = adapter.fromJson(body)

                if (groupsMap != null) {
                    // Преобразуем Map в List<Group>
                    val groupsList = groupsMap.map { entry ->
                        Group(entry.key, entry.value) // Преобразуем каждый элемент Map в Group
                    }
                    Result.success(groupsList) // Возвращаем результат как список объектов Group
                } else {
                    Result.failure(Exception("Ошибка парсинга JSON"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun main() {
    // Инициализация клиента API
    val apiClient = ApiClient()

    // Тестирование получения расписания
    println("Тестирование получения расписания:")
    val weeklyScheduleResult = apiClient.getWeeklySchedule("22207")
    weeklyScheduleResult.onSuccess { schedule ->
        println("Получено расписание успешно!")
        println("Numerator:")
        schedule.numerator.forEachIndexed { index, day ->
            println("День $index:")
            day.forEach { lesson ->
                println("  - ${lesson.title} (${lesson.startTime} - ${lesson.endTime})")
            }
        }
        println("Denominator:")
        schedule.denominator.forEachIndexed { index, day ->
            println("День $index:")
            day.forEach { lesson ->
                println("  - ${lesson.title} (${lesson.startTime} - ${lesson.endTime})")
            }
        }
    }.onFailure { error ->
        println("Ошибка при получении расписания: ${error.message}")
    }

    // Тестирование получения списка преподавателей
    println("\nТестирование получения списка преподавателей:")
    val lecturersResult = apiClient.getLecturers()
    lecturersResult.onSuccess { lecturers ->
        println("Список преподавателей:")
        lecturers.forEach { lecturer ->
            println("  - $lecturer")
        }
    }.onFailure { error ->
        println("Ошибка при получении списка преподавателей: ${error.message}")
    }

    // Тестирование получения расписания преподавателя
    println("\nТестирование получения расписания преподавателя:")
    val lecturerScheduleResult = apiClient.getLecturerSchedule("Димитров Вячеслав Михайлович")
    lecturerScheduleResult.onSuccess { schedule ->
        println("Получено расписание преподавателя успешно!")
        println("Numerator:")
        schedule.numerator.forEachIndexed { index, day ->
            println("День $index:")
            day.forEach { lesson ->
                println("  - ${lesson})")
            }
        }
        println("Denominator:")
        schedule.denominator.forEachIndexed { index, day ->
            println("День $index:")
            day.forEach { lesson ->
                println("  - ${lesson})")
            }
        }
    }.onFailure { error ->
        println("Ошибка при получении расписания преподавателя: ${error.message}")
    }

    // Тестирование получения текущей недели
    println("\nТестирование получения текущей недели:")
    val currentWeekResult = apiClient.getCurrentWeek()
    currentWeekResult.onSuccess { week ->
        println("Текущая неделя: $week")
    }.onFailure { error ->
        println("Ошибка при получении текущей недели: ${error.message}")
    }

    // Тестирование получения времени последнего обновления
    println("\nТестирование получения времени последнего обновления:")
    val lastUpdateTimeResult = apiClient.getLastUpdateTime()
    lastUpdateTimeResult.onSuccess { time ->
        println("Время последнего обновления: $time")
    }.onFailure { error ->
        println("Ошибка при получении времени последнего обновления: ${error.message}")
    }
    // Тестирование получения списка групп
    println("\nТестирование получения списка групп:")
    val groupsResult = apiClient.getGroups() // Вызов метода для получения групп
    groupsResult.onSuccess { groups ->
        println("Список групп:")
        groups.forEach { group ->
            println("  - ${group.name} (${group.code})")
        }
    }.onFailure { error ->
        println("Ошибка при получении списка групп: ${error.message}")
    }

}
