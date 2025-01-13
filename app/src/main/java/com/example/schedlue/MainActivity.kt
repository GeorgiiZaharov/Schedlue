package com.example.schedlue

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val colors = getColorScheme()
            MaterialTheme(colorScheme = colors) {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    if (isKeyExist(LocalContext.current, LAST_SCHEDLUE)) {
        NavHost(
            navController = navController,
            startDestination = "schedlue" // Начальный экран
        ) {
            composable("home") { HomeScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("schedlue") { SchedlueScreen(navController) }
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = "home" // Начальный экран
        ) {
            composable("home") { HomeScreen(navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("schedlue") { SchedlueScreen(navController) }
        }
    }
}


@Composable
fun HomeScreen(navController: NavController) {
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, state: DrawerState, scope: CoroutineScope, navController: NavController) {
    val showDialog = remember { mutableStateOf(false) }

    Column {
        TopAppBar(
            title = { Text(title) },
            modifier = Modifier.fillMaxWidth(), // Используем только ширину
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
                    tint = MaterialTheme.colorScheme.primary
                )
                Icon(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            navController.navigate("settings")
                        },
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        )
        // Горизонтальная полоса
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface,
            thickness = 1.dp // Толщина линии
        )
    }

    if (showDialog.value) {
        ShowNewScheduleDialog(showDialog, navController)
    }
}

