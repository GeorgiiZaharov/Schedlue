package com.example.schedlue

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavController,
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val showDialog = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SlidingPanel(Modifier.statusBarsPadding())
        },
        content = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = content,
                topBar = { TopBar(title, drawerState, scope, navController) },
                floatingActionButton = {
                    Button(onClick = {
                        showDialog.value = true // Открыть диалог
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add schedule"
                        )
                    }
                }
            )
        }
    )

    if (showDialog.value) {
        ShowNewScheduleDialog(showDialog)
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(InternalCoroutinesApi::class)
@Composable
fun ShowNewScheduleDialog(showDialog: MutableState<Boolean>){
    Dialog(
        onDismissRequest = { showDialog.value = false }
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
                Text(
                    text = "Добавить расписание",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                var expanded by remember { mutableStateOf(false) }
                var selectedOption by remember { mutableStateOf("Выберите опцию") }
                val options = listOf("Расписание группы", "Расписание преподавателя")

                Box {
                    OutlinedTextField(
                        value = selectedOption,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Список") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                contentDescription = null,
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                var scheduleFilter by remember { mutableStateOf("") }

//                        var label = if (selectedOption == "Расписание преподавателя") "Введите имя преподавателя" else "Введите номер группы"
                if (selectedOption == "Расписание группы") {
                    OutlinedTextField(
                        value = scheduleFilter,
                        onValueChange = { scheduleFilter = it },
                        label = { Text("Введите номер группы") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                if (selectedOption == "Расписание преподавателя") {
                    var responseRes: List<Lecturer>? by remember { mutableStateOf(null) }

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val apiService = ApiClient()
                            val response = apiService.getLecturers()
                            if (response.isSuccess) {
                                responseRes = response.getOrNull()
                            } else {
                                println("Ошибка сервера: ${response.exceptionOrNull()}")
                            }
                        } catch (e: Exception) {
                            println("Ошибка: ${e.message}")
                        }
                    }

                    if (responseRes != null) {
                        var scheduleFilter by remember { mutableStateOf("") }
                        var filteredLecturers by remember { mutableStateOf(listOf<Lecturer>()) }

                        OutlinedTextField(
                            value = scheduleFilter,
                            onValueChange = { input ->
                                scheduleFilter = input
                                filteredLecturers = responseRes!!.filter {
                                    it.name.contains(input, ignoreCase = true)
                                }
                            },
                            label = { Text("Введите имя преподавателя") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            items(filteredLecturers) { lecturer ->
                                Text(
                                    text = lecturer.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            filteredLecturers = emptyList()
                                        }
                                        .padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                    }
                    else {
                        // TODO NO INTERNET
                    }





                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Закрыть")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        // Обработка сохранения
                        println("Выбранная опция: $selectedOption")
                        showDialog.value = false
                    }) {
                        Text("Сохранить")
                    }
                }
            }
        }
    }
}


}

