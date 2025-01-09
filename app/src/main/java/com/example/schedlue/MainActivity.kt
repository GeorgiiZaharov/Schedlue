package com.example.schedlue

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schedlue.ui.theme.SchedlueTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            val colors = if (isDarkTheme) darkColorScheme() else lightColorScheme() //TODO: add settings theme
            MaterialTheme(colorScheme = colors) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                ModalNavigationDrawer(
                    drawerState=drawerState,
                    drawerContent = {
                        SlidingPanel(Modifier.statusBarsPadding())
                    },
                    content = {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            content = { innerPadding ->
                                MainContent(modifier = Modifier.padding(innerPadding))
                            },
                            topBar = { TopBar(drawerState, scope) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun MainContent(modifier: Modifier) {
    Text(modifier = modifier, text = "Hello world")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(state: DrawerState, scope: CoroutineScope) {
    TopAppBar(
        title = { Text("Smth") },
        modifier = Modifier.fillMaxWidth(), // Используем только ширину
        navigationIcon = {
            Icon(
                modifier = Modifier.clickable {
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
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings"
            )
        }
    )
}

@Composable
fun SlidingPanel(modifier: Modifier) {
    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("First option", color = MaterialTheme.colorScheme.onSurface // Используем цвет для текста
        )
        Text("Second option", color = MaterialTheme.colorScheme.onSurface // Используем цвет для текста
        )
    }
}