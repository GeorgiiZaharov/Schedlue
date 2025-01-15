package com.example.schedlue

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

@Composable
fun AppScaffold(
    navController: NavController,
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    // Добавляем флаг для определения, открыта панель или нет
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SlidingPanel(
                modifier = Modifier.statusBarsPadding(),
                navController = navController,
                drawerState
            )
        },
        content = {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary),
                content = {paddingValue ->
                    content(paddingValue)
                },
                topBar = { TopBar(title, drawerState, scope, navController) }
            )
        }
    )
}

@Composable
fun Close_button(showDialog: MutableState<Boolean>){
    Button(
        onClick = { showDialog.value = false },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary, // Цвет заливки кнопки
            contentColor = MaterialTheme.colorScheme.onTertiary // Цвет текста внутри кнопки
        ),
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.background, // Цвет рамки
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.tertiary, // Цвет фона кнопки
                shape = RoundedCornerShape(20.dp)
            )

    ) {
        Text(
            "Закрыть",
            color = MaterialTheme.colorScheme.surface
        )

    }
}

@Composable
fun Save_button(
    showDialog: MutableState<Boolean>,
    context: Context,
    navController: NavController,
    scheduleFilter: String,
    func: (String, Context) -> Unit
){
    Button(onClick = {
        showDialog.value = false
        func(scheduleFilter, context)
        navController.navigate("schedlue")
    },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary, // Цвет заливки кнопки
            contentColor = MaterialTheme.colorScheme.onTertiary // Цвет текста внутри кнопки
        ),
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.background, // Цвет рамки
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.tertiary, // Цвет фона кнопки
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Text(
            "Сохранить",
            color = MaterialTheme.colorScheme.surface
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, state: DrawerState, scope: CoroutineScope, navController: NavController) {
    val showDialog = remember { mutableStateOf(false) }

    Column  {
        TopAppBar(
            title = { Text(title) },
            modifier = Modifier
                .fillMaxWidth(),
            navigationIcon = {
                Icon(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(start = 10.dp, end = 10.dp)
                        .clickable {
                            scope.launch {
                                if (state.isOpen) state.close() else state.open()
                            }
                        },
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            },
            actions = {
                Icon(
                    modifier = Modifier
                        .clickable {
                            showDialog.value = true
                        }
                        .size(50.dp)
                        .padding(end = 10.dp),
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add schedule",
                    tint = MaterialTheme.colorScheme.background
                )
                Icon(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            navController.navigate("settings")
                        },
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.surface
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary, // Фон AppBar
                titleContentColor = MaterialTheme.colorScheme.surface, // Цвет текста
                navigationIconContentColor = MaterialTheme.colorScheme.surface // Цвет иконок
            )
        )
    }

    if (showDialog.value) {
        ShowNewScheduleDialog(showDialog, navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowNewScheduleDialog(
    showDialog: MutableState<Boolean>,
    navController: NavController
) {
    Dialog(
        onDismissRequest = { showDialog.value = false }
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.tertiary,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Добавить расписание",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.background
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.background, // Цвет рамки в фокусе
                            unfocusedBorderColor = MaterialTheme.colorScheme.surface, // Цвет рамки в неактивном состоянии
                            focusedLabelColor = MaterialTheme.colorScheme.background, // Цвет метки в фокусе
                            unfocusedLabelColor = MaterialTheme.colorScheme.surface, // Цвет метки в неактивном состоянии
                            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), // Цвет текста при отключённом состоянии
                        )
                    )
                    DropdownMenu(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.tertiary
                            ),

                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                colors = MenuItemColors(
                                    textColor = MaterialTheme.colorScheme.surface, // Цвет текста
                                    leadingIconColor = MaterialTheme.colorScheme.primary, // Цвет иконки слева
                                    trailingIconColor = MaterialTheme.colorScheme.secondary, // Цвет иконки справа
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), // Цвет текста при отключённом состоянии
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), // Цвет иконки слева при отключённом состоянии
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Цвет иконки справа при отключённом состоянии
                                ),
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


                if (selectedOption == "Расписание группы") {
                    var responseRes: List<Group>? by remember { mutableStateOf(null) }
                    responseRes = fetchData { ApiClient().getGroups() }

                    if (responseRes != null) {
                        var scheduleFilter by remember { mutableStateOf("") }
                        var filteredGroups by remember { mutableStateOf(listOf<Group>()) }

                        OutlinedTextField(
                            value = scheduleFilter,
                            onValueChange = { input ->
                                scheduleFilter = input
                                filteredGroups = responseRes!!.filter {
                                    (it.name.contains(input, ignoreCase = true) || it.code.contains(input, ignoreCase = true)) && input != ""
                                }
                            },
                            label = { Text("Введите номер группы") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.background, // Цвет границы при фокусе
                                unfocusedBorderColor = MaterialTheme.colorScheme.surface, // Цвет границы без фокуса
                                focusedLabelColor = MaterialTheme.colorScheme.background, // Цвет метки при фокусе
                                unfocusedLabelColor = MaterialTheme.colorScheme.surface, // Цвет метки без фокуса
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) // Цвет фона (слабая прозрачность)
                            )

                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            items(filteredGroups) { group ->
                                Text(
                                    text = "${group.code} ${group.name}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            scheduleFilter = "${group.code} ${group.name}"
                                            filteredGroups = emptyList()
                                        }
                                        .padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Close_button(showDialog)
                            Spacer(modifier = Modifier.width(8.dp))

                            if (responseRes!!.filter { group -> "${group.code} ${group.name}" == scheduleFilter && "${group.code} ${group.name}" != "" }.size != 0){
                                val context = LocalContext.current
                                Save_button(showDialog, context, navController, scheduleFilter, ::saveGroup)
                            }
                        }
                    } else {
                        Text(
                            "Нет интернет соединения")
                    }

                }

                if (selectedOption == "Расписание преподавателя") {
                    var responseRes: List<Lecturer>? by remember { mutableStateOf(null) }
                    responseRes = fetchData { ApiClient().getLecturers() }

                    if (responseRes != null) {
                        var scheduleFilter by remember { mutableStateOf("") }
                        var filteredLecturers by remember { mutableStateOf(listOf<Lecturer>()) }

                        OutlinedTextField(
                            value = scheduleFilter,
                            onValueChange = { input ->
                                scheduleFilter = input
                                filteredLecturers = responseRes!!.filter {
                                    it.name.contains(input, ignoreCase = true) && input != ""
                                }
                            },
                            label = { Text("Введите имя преподавателя") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.background, // Цвет границы при фокусе
                                unfocusedBorderColor = MaterialTheme.colorScheme.surface, // Цвет границы без фокуса
                                focusedLabelColor = MaterialTheme.colorScheme.background, // Цвет метки при фокусе
                                unfocusedLabelColor = MaterialTheme.colorScheme.surface, // Цвет метки без фокуса
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) // Цвет фона (слабая прозрачность)
                            )
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
                                            scheduleFilter = lecturer.name
                                            filteredLecturers = emptyList()
                                        }
                                        .padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Close_button(showDialog)
                            Spacer(modifier = Modifier.width(8.dp))

                            if (responseRes!!.filter { lecturer -> lecturer.name == scheduleFilter && lecturer.name != "" }.size != 0){
                                val context = LocalContext.current
                                Save_button(showDialog, context, navController, scheduleFilter, ::saveLecturer)
                            }
                        }
                    } else {
                        Text(
                            "Нет интернет соединения",
                            color = MaterialTheme.colorScheme.background
                        )
                    }

                }
            }
        }
    }
}

fun saveLecturer(name: String, context: Context){
    var set = getSetFromPrefs(context, LECTURERS_SCHEDLUE)
    set = set.plus(name)
    saveSetToPrefs(context, LECTURERS_SCHEDLUE, set)
    saveStingToPrefs(context, LAST_SCHEDLUE, name)
}
fun saveGroup(groupName: String, context: Context){
    var set = getSetFromPrefs(context, GROUPS_SCEDLUE)
    set = set.plus(groupName)
    saveSetToPrefs(context, GROUPS_SCEDLUE, set)
    saveStingToPrefs(context, LAST_SCHEDLUE, groupName)
}
