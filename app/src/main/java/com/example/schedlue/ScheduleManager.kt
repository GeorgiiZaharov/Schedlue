package com.example.schedlue

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.ColorRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

fun deleteVariable(context: Context, key: String){
    val sharedPreferences = context.getSharedPreferences("shedlue_data", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove(key) // Удаляем ключ key
    editor.apply() // Применяем изменения
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
    if (!isKeyExist(LocalContext.current, LAST_SCHEDLUE)){
        AppScaffold(
            navController,
            "Расписание"
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(10.dp, 0.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "У Вас пока нет расписаний (Нажмите \"+\" чтобы добавить расписание)"
                )
            }
        }
        return
    }
    AppScaffold(
        navController,
        "Расписание"
    ) { paddingValue ->
        var responseRes: WeeklySchedule? by remember { mutableStateOf(null) }

        val last_schedlue = getStringFromPrefs(LocalContext.current, LAST_SCHEDLUE)

        // Если последнее расписание из списка запросов расписаний групп, значит показываем расписание группы
        val groups_schedlue_set = getSetFromPrefs(LocalContext.current, GROUPS_SCEDLUE)
        val lecturers_schedlue_set = getSetFromPrefs(LocalContext.current, LECTURERS_SCHEDLUE)

        if (groups_schedlue_set.contains(last_schedlue))
        {
            responseRes = fetchData {
                val apiClient = ApiClient()
                apiClient.getWeeklySchedule(last_schedlue.split(" ").first())
            }
        }
        else if (lecturers_schedlue_set.contains(last_schedlue)) {
            responseRes = fetchData {
                val apiClient = ApiClient()
                apiClient.getLecturerSchedule(last_schedlue)
            }
        }
        else {
            deleteVariable(LocalContext.current, LAST_SCHEDLUE)
            navController.navigate("schedlue") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            return@AppScaffold
        }

        if (responseRes != null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue))
            SchedlueScreenSchedlue(responseRes!!, paddingValue)
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
        delay(3000) // Задержка 3 секунды
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

fun getCurrentWeekDays(shift: Int): List<String> {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY).plusWeeks(shift.toLong()) // Сдвигаем начало недели на shift недель
    val daysOfWeek = mutableListOf<String>()
    val formatter = DateTimeFormatter.ofPattern("d")

    for (i in 0..6) {
        daysOfWeek.add(startOfWeek.plusDays(i.toLong()).format(formatter))
    }
    return daysOfWeek
}

@Composable
fun SchedlueScreenSchedlue(
    schedule_response: WeeklySchedule,
    paddingValue: PaddingValues
){
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

    schedlue = schedlue.map { dailyLessons ->
        dailyLessons.sortedBy { lesson -> lesson.number.toIntOrNull() ?: Int.MAX_VALUE }
    }


    var isNumerator by remember { mutableStateOf(responseRes == "numerator") }
    var dayBias by remember { mutableStateOf(0) }
    var activeDayIndex by remember { mutableStateOf(0) }
    var todayIndex = LocalDate.now().dayOfWeek.ordinal
    var currentIndexOfDay by remember { mutableStateOf(todayIndex) }

    println("$currentIndexOfDay NIGGA")

    val daysNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    var shift by remember { mutableStateOf(0) }
    var daysNumbers = getCurrentWeekDays(shift)

    // Дата активного дня
    var currentDay = LocalDate.now().plusDays(dayBias.toLong())
    var formatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale("ru", "RU"))
    var currentDayFormated = currentDay.format(formatter).split(" ")
    var currentDayName = currentDayFormated[0]
    var currentDayNumber = currentDayFormated[1] + " " + currentDayFormated[2]
    var currentDayNumberOnly = currentDayFormated[1]

    // Определяем размер экрана
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val isSmallScreen = screenWidth <= 360 // Условие для маленьких экранов

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValue)
            .padding(20.dp, 0.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        // информация о дне
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(0.dp, 10.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val buttonModifier = Modifier
                    .weight(1f)
                    .padding(5.dp)

                val buttonColors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                )

                val buttonCurrentColors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.Gray
                )

                // Устанавливаем стили текста с адаптацией
                val buttonTextDayNameStyle = TextStyle(
                    fontSize = if (isSmallScreen) 12.sp else 16.sp,
                    textAlign = TextAlign.Center
                )

                val buttonTextDayNameCurrentStyle = TextStyle(
                    fontSize = if (isSmallScreen) 12.sp else 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                val buttonTextDayNumberStyle = TextStyle(
                    fontSize = if (isSmallScreen) 16.sp else 20.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                val buttonTextDayNumberCurrentStyle = TextStyle(
                    fontSize = if (isSmallScreen) 16.sp else 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                for (i in 0..6) {
                    val buttonColor = if (i == currentIndexOfDay) buttonCurrentColors else buttonColors
                    val textNameColor = if (i == currentIndexOfDay) buttonTextDayNameCurrentStyle else buttonTextDayNameStyle
                    val textDayColor = if (i == currentIndexOfDay) buttonTextDayNumberCurrentStyle else buttonTextDayNumberStyle


                    Button(
                        onClick = {
                            dayBias = dayBias + (i - currentIndexOfDay)
                            currentIndexOfDay = i
                        },
                        modifier = buttonModifier,
                        colors = buttonColor,
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        // Используем BoxWithConstraints для адаптации текста
                        BoxWithConstraints(
                            contentAlignment = Alignment.Center
                        ) {
                            val maxFontSize = with(LocalDensity.current) {
                                maxWidth.value.coerceAtMost(maxHeight.value) * 0.5 // Пропорциональный размер
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = daysNames[i],
                                    style = TextStyle(
                                        fontSize = maxFontSize.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = daysNumbers[i],
                                    style = TextStyle(
                                        fontSize = (maxFontSize * 1.2).sp, // Немного больше для чисел
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }



            // Состояние недели (числитель / знаминатлеь)
            if (((todayIndex + dayBias + 14 * 10000) % 14) in 0..6) {
                if (isNumerator) Text("Числитель")
                else Text("Знаменатель")
            }
            else {
                if (!isNumerator) Text("Числитель")
                else Text("Знаменатель")
            }

            Text(currentDayNumber)
        }

        var schedule_weight = 4f
        if (isSmallScreen) {
            schedule_weight = 2.5f
        }
        // Расписание
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(schedule_weight)
        ){
            // есть пары
            if (!schedlue[(todayIndex + dayBias + 14 * 10000) % 14].isEmpty()){
                LazyColumn {
                    // Отображаем элементы списка
                    items(schedlue[(todayIndex + dayBias + 14 * 10000) % 14]) { lesson ->
                        println("lesson $lesson")
                        LessonCard(lesson)
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
            else {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        "В этот день нет пар \uD83E\uDD73",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        var buttons_weight = 0.4f
        if (isSmallScreen){
            buttons_weight = 0.35f
        }
        // Кнопки
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(buttons_weight)
                .padding(0.dp, 10.dp, 0.dp, 0.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back in schedlue",
                modifier = Modifier.clickable {
                    dayBias --
                    currentIndexOfDay--
                    if (currentIndexOfDay < 0) {
                        currentIndexOfDay = 6
                        shift--
                    }
                }
            )

            Button(
                onClick = {
                    dayBias = 0
                    currentIndexOfDay = todayIndex
                }
            ) {
                Text(
                    "Сегодня"
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Back in schedlue",
                modifier = Modifier.clickable {
                    dayBias ++
                    currentIndexOfDay++
                    if (currentIndexOfDay > 6) {
                        currentIndexOfDay = 0
                        shift++
                    }
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = LocalContentColor.current
            )
            .clickable { isDialogOpen = !isDialogOpen },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // номер лекции
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = LocalContentColor.current
                )
                .align(Alignment.Top),
            contentAlignment = Alignment.Center
        ){
            Text(lesson.number)
        }
        // информация о лекции
        Column (
            modifier = Modifier
                .fillMaxSize()
                .weight(9f)
                .padding(10.dp, 0.dp)
        ){
            Spacer(modifier = Modifier.height(7.dp))

            Text(lesson.title)
            Spacer(modifier = Modifier.height(14.dp))

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
            Spacer(modifier = Modifier.height(14.dp))

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
            Spacer(modifier = Modifier.height(14.dp))
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
            Spacer(modifier = Modifier.height(14.dp))
            AnimatedVisibility(isDialogOpen) {
                LessonCardAddInfo(lesson)
            }

        }
    }
}

@Composable
fun LessonCardAddInfo(lesson: Lesson) {
    Column() {
        Row() {
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

        Spacer(modifier = Modifier.height(14.dp))

        Row() {
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
        Spacer(modifier = Modifier.height(14.dp))

    }

}
