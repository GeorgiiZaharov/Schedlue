package com.example.schedlue

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.ColorRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.squareup.moshi.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

val LAST_SCHEDLUE = "last_schedlue"
val GROUPS_SCEDLUE = "groups_schedlue"
val LECTURERS_SCHEDLUE = "lectures_schedlue"

fun isKeyExist(context: Context, key: String): Boolean {
    val sharedPreferences = context.getSharedPreferences("shedlue_data", Context.MODE_PRIVATE)
    return sharedPreferences.contains(key)  // Проверка, существует ли ключ
}
fun saveSetToPrefs(context: Context, key: String, set: Set<String>) {
    val sharedPreferences = context.getSharedPreferences("shedlue_data", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    editor.putStringSet(key, set) // Сохраняем набор строк
    editor.apply()
}

fun getSetFromPrefs(context: Context, key: String): Set<String> {
    val sharedPreferences = context.getSharedPreferences("shedlue_data", Context.MODE_PRIVATE)
    return sharedPreferences.getStringSet(key, emptySet()) ?: emptySet()
}

fun getStringFromPrefs(context: Context, key: String): String {
    val sharedPreferences = context.getSharedPreferences("shedlue_data", Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, "") ?: ""
}
fun saveStingToPrefs(context: Context, key: String, value: String) {
    val sharedPreferences = context.getSharedPreferences("shedlue_data", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    editor.putString(key, value)
    editor.apply()
}

@Composable
fun <T> fetchData(
    apiCall: suspend () -> Result<T>
): T? {
    var result by remember { mutableStateOf<T?>(null) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiCall()
                if (response.isSuccess) {
                    result = response.getOrNull()
                } else {
                    println("Ошибка сервера: ${response.exceptionOrNull()}")
                }
            } catch (e: Exception) {
                println("Ошибка: ${e.message}")
            }
        }
    }

    return result
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SchedlueScreen(navController: NavController){
    AppScaffold(
        navController,
        "Расписание"
    ) { paddingValue ->
        var responseRes: WeeklySchedule? by remember { mutableStateOf(null) }

        val last_schedlue = getStringFromPrefs(LocalContext.current, LAST_SCHEDLUE)

        // Если последнее расписание из списка запросов расписаний групп, значит показываем расписание группы
        val groups_schedlue_set = getSetFromPrefs(LocalContext.current, GROUPS_SCEDLUE)
        if (groups_schedlue_set.contains(last_schedlue))
        {
            responseRes = fetchData {
                val apiClient = ApiClient()
                apiClient.getWeeklySchedule(last_schedlue.split(" ").first())
            }
        }
        else {
            responseRes = fetchData {
                val apiClient = ApiClient()
                apiClient.getLecturerSchedule(last_schedlue)
            }
        }

        if (responseRes != null) {
            Box(modifier = Modifier.padding(paddingValue))
            {
                SchedlueScreenSchedlue(responseRes!!)
            }
        }
        else {
            InternetConnectionAllert()
        }
    }
}

fun get_cur_day_index(): Int {
    val currentDay: DayOfWeek = LocalDate.now().dayOfWeek
    val dayIndex = currentDay.ordinal // Порядковый номер, начиная с 0 для MONDAY
    return dayIndex
}

// WARNING!!! NASRANO!!! PEREDELAT`!!! HOTYA...
@Composable
fun InternetConnectionAllert(){
    var showDialog by remember { mutableStateOf(false) }
    // Задержка перед показом диалога
    LaunchedEffect(Unit) {
        delay(2000) // Задержка 2 секунды
        showDialog = true
    }


    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        Text(
                            text = "Ошибка",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Red
                        )

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Отстутствует подключение к интернету"
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ){
                        Button(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SchedlueScreenSchedlue(schedule_response: WeeklySchedule){
    // получаем текущую неделю (числитель/знаминатель)
    var schedlue: List<List<Lesson>>

    val responseRes = fetchData { ApiClient().getCurrentWeek() }
    when (responseRes) {
        null -> {
            InternetConnectionAllert()
            return
        }
        "numerator" -> {
            schedlue = schedule_response.numerator + schedule_response.denominator
        }
        "denominator" -> {
            schedlue = schedule_response.denominator + schedule_response.numerator
        }
        else -> error("Непонятный ответ от API!!!")
    }

    var isNumerator by remember { mutableStateOf(responseRes == "numerator") }
    var dayBias by remember { mutableStateOf(0) }
    var todayIndex = LocalDate.now().dayOfWeek.ordinal

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 50.dp),//TODO bad practice
        verticalArrangement = Arrangement.SpaceBetween
    ){
        // информация обо дне
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // Состояние недели (числитель / знаминатлеь)
            if (((todayIndex + dayBias + 14 * 10000) % 14) in 0..6) {
                if (isNumerator) Text("Числитель")
                else Text("Знаминатель")
            }
            else {
                if (!isNumerator) Text("Числитель")
                else Text("Знаминатель")
            }

            // Дата активного дня
            var currentDay = LocalDate.now().plusDays(dayBias.toLong())
            var formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale("ru", "RU"))
            var currentDayFormated = currentDay.format(formatter).split(" ")
            var currentDayName = currentDayFormated[0]
            var currentDayNumber = currentDayFormated[1] + " " + currentDayFormated[2]
            Text(currentDayName)
            Text(currentDayNumber)
        }

        // Расписание
        LazyColumn {
            // Отображаем элементы списка
            items(schedlue[(todayIndex + dayBias + 14 * 10000) % 14]) { lesson ->
                println("lesson $lesson")
                LessonCard(lesson)
            }
        }

        // Кнопки
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back in schedlue",
                modifier = Modifier.clickable {
                    dayBias --
                }
            )
            Button(
                onClick = {
                    dayBias = 0
                }
            ) {
                Text("Сегодня")
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Back in schedlue",
                modifier = Modifier.clickable {
                    dayBias ++
                }
            )
        }

    }

}

@Composable
fun LessonCard(
    lesson: Lesson
){
    var isDialogOpen by remember { mutableStateOf(false) }
    if (isDialogOpen) {
        LessonCardInfo(lesson, {isDialogOpen = false})
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LocalContentColor.current
            )
            .clickable {isDialogOpen = true},
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(lesson.number)
            Text(lesson.title)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                imageVector = Icons.Filled.Group,
                contentDescription = "Group Icon"
            )
            Text(lesson.group)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Location Icon"
            )
            Text(lesson.classroom)
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun LessonCardInfo(lesson: Lesson, onClose: ()->Unit) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Время",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${lesson.startTime} - ${lesson.endTime}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Аудитория",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = lesson.classroom,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Группа",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = lesson.group,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Преподаватель",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = lesson.lecturer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Тип занятия",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = lesson.type,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
