package com.example.schedlue

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SlidingPanel(modifier: Modifier, navController: NavController, state: DrawerState) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val last_schedlue = getStringFromPrefs(context, LAST_SCHEDLUE)
        // Затемнение фона, если панель активна
        if (state.isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable {
                        scope.launch {
                            if (state.isOpen) state.close() else state.open()
                        }
                    }
            )
            // Содержимое панели
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .padding(15.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    // Группы
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Группы",
                            fontSize = 24.sp,
                            color = Color.White,
                            textDecoration = TextDecoration.Underline
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val set = remember {
                            mutableStateOf(
                                getSetFromPrefs(
                                    context,
                                    GROUPS_SCEDLUE
                                ).toMutableList()
                            )
                        }
                        if (set.value.isEmpty()) {
                            Text(
                                "Нет добавленных групп",
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                        set.value.forEach { group ->
                            var color = if (group == last_schedlue) Color(0xFF9c80e7) else Color.White
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    group,
                                    fontSize = 17.sp,
                                    color = color,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            saveStingToPrefs(context, LAST_SCHEDLUE, group)
                                            navController.navigate("schedlue"){
                                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                            }
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .clickable {
                                            set.value =
                                                set.value.toMutableList().apply { remove(group) }

                                            if (last_schedlue == group){
                                                deleteVariable(context, LAST_SCHEDLUE)
                                            }

                                            saveSetToPrefs(
                                                context,
                                                GROUPS_SCEDLUE,
                                                set.value.toSet()
                                            )
                                        }
                                )
                            }
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    // Преподаватели
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Преподаватели",
                            fontSize = 24.sp,
                            color = Color.White,
                            textDecoration = TextDecoration.Underline
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val set = remember {
                            mutableStateOf(
                                getSetFromPrefs(
                                    context,
                                    LECTURERS_SCHEDLUE
                                ).toMutableList()
                            )
                        }
                        if (set.value.isEmpty()) {
                            Text(
                                "Нет добавленных преподавателей",
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                        set.value.forEach { lecturer ->
                            var color = if (lecturer == last_schedlue) Color(0xFF9c80e7) else Color.White
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    lecturer,
                                    fontSize = 17.sp,
                                    color = color,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            saveStingToPrefs(context, LAST_SCHEDLUE, lecturer)
                                            navController.navigate("schedlue") {
                                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                            }
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .clickable {
                                            set.value =
                                                set.value.toMutableList().apply { remove(lecturer) }
                                            if (last_schedlue == lecturer){
                                                deleteVariable(context, LAST_SCHEDLUE)
                                            }
                                            saveSetToPrefs(
                                                context,
                                                LECTURERS_SCHEDLUE,
                                                set.value.toSet()
                                            )
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
