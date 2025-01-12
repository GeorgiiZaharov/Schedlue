package com.example.schedlue

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Ключ для хранения в SharedPreferences
const val THEME_KEY = "theme_preference"
const val DARK_THEME = "dark"
const val LIGHT_THEME = "light"
const val DEFAULT_THEME = "default"

@Composable
fun getColorScheme(): ColorScheme {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    // Проверяем, установлена ли тема в SharedPreferences
    return if (!isThemeSet(context) || getThemePreference(context) == DEFAULT_THEME) {
        // Если тема не установлена, выбираем систему: светлая или темная
        if (isDarkTheme) darkColorScheme()
        else return lightColorScheme()
    } else {
        // Если тема установлена, выбираем из SharedPreferences
        if (getThemePreference(context) == DARK_THEME) return darkColorScheme()
        return lightColorScheme()

    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = { SettingsScreenTopBar(navController) },
        content = { paddingValue ->
            SettingsScreenContent(LocalContext.current, Modifier.padding(paddingValue))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Настройки") },
        modifier = Modifier.fillMaxWidth(), // Используем только ширину
        navigationIcon = {
            Icon(
                modifier = Modifier.clickable {
                    navController.popBackStack()
                },
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back button"
            )
        }
    )
}

fun isThemeSet(context: Context): Boolean {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.contains(THEME_KEY)
}

// Функция для получения предпочтений темы из SharedPreferences
fun getThemePreference(context: Context): String {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString(THEME_KEY, DEFAULT_THEME) ?: DEFAULT_THEME
}

// Функция для сохранения выбранной темы в SharedPreferences
fun setThemePreference(context: Context, theme: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString(THEME_KEY, theme)
        apply()
    }
}

@Composable
fun SettingsScreenContent(context: Context, modifier: Modifier) {
    // Если тема не установлена, установим по умолчанию
    if (!isThemeSet(context)) {
        setThemePreference(context, DEFAULT_THEME)
    }
    // Получаем текущие предпочтения по теме, используя функцию getThemePreference
    val savedTheme = getThemePreference(context)

    // Состояние для выбора темы с проверкой наличия значения в списке опций
    var selectedTheme by remember { mutableStateOf(savedTheme) }

    // Состояние для отображения/скрытия выпадающего списка
    var expanded by remember { mutableStateOf(false) }

    // Опции для выбора темы
    val themeOptions = mapOf(
        "По умолчанию" to DEFAULT_THEME,
        "Светлая тема" to LIGHT_THEME,
        "Темная тема" to DARK_THEME
    )

    // Функция для изменения темы
    val onThemeChange: (String) -> Unit = { theme ->
        selectedTheme = theme // Устанавливаем выбранную тему
        setThemePreference(context, theme) // Сохраняем тему в SharedPreferences
        expanded = false // Закрыть выпадающий список
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Отображение выпадающего списка и текста на экране
        Row(
            modifier = modifier
                .padding(16.dp,0.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Цветовая тема:")

            // Кнопка для открытия выпадающего списка
            TextButton(onClick = { expanded = !expanded }) {
                // Отображаем выбранную тему (ключ, который связан с выбранной темой)
                Text(text = themeOptions.entries.find { it.value == selectedTheme }?.key ?: "По умолчанию")
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown arrow")
            }

            // Выпадающий список
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                themeOptions.forEach { (key, value) ->
                    DropdownMenuItem(
                        onClick = { onThemeChange(value) },
                        text = { Text(key) }
                    )
                }
            }
            Text(
                text = "Перезапуск для применения",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

        }
        SettingsScreenAbout()
    }
}

@Composable
fun SettingsScreenAbout() {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Разработано: ",
            style = MaterialTheme.typography.bodyMedium
        )

        // Создаем аннотированную строку для кликабельных ссылок
        val annotatedString = buildAnnotatedString {
            append("danelloptz (GitHub: ")
            // Добавляем ссылку
            pushStringAnnotation(tag = "github1", annotation = "https://github.com/danelloptz")
            withStyle(style = MaterialTheme.typography.bodySmall.toSpanStyle().copy(color = Color.Blue)) {
                append("danelloptz")
            }
            pop()

            append(") и GeorgiiZaharov (GitHub: ")
            // Добавляем вторую ссылку
            pushStringAnnotation(tag = "github2", annotation = "https://github.com/GeorgiiZaharov")
            withStyle(style = MaterialTheme.typography.bodySmall.toSpanStyle().copy(color = Color.Blue)) {
                append("GeorgiiZaharov")
            }
            pop()
            append(")")
        }

        // Используем ClickableText для отображения аннотированного текста
        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let { annotation ->
                    val url = annotation.item
                    // Открытие ссылки через Intent
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}
