package com.example.schedlue

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SchedlueScreen(navController: NavController){
//    AppScaffold(
//        navController,
//        "Расписание"
//    ) { paddingValue ->
//        val responseRes by remember { mutableStateOf() }
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val apiService = ApiClient()
//                val response = apiService.getGroups()
//                if (response.isSuccess) {
//                    responseRes = response.getOrNull()
//                } else {
//                    println("Ошибка сервера: ${response.exceptionOrNull()}")
//                }
//            } catch (e: Exception) {
//                println("Ошибка: ${e.message}")
//            }
//        }
//
//    }
}