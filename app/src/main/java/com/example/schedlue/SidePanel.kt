package com.example.schedlue

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SlidingPanel(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f))
        {
            Column {
                Text("Группы", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                val context = LocalContext.current
                val set = getSetFromPrefs(context, GROUPS_SCEDLUE)
                set.forEach { lecturer ->
                    Row (modifier = Modifier.fillMaxWidth()) {
                        Text(
                            lecturer,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f))
        {
            Column {
                Text("Преподаватели", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)

                Spacer(modifier = Modifier.height(16.dp))

                val context = LocalContext.current
                val set = getSetFromPrefs(context, LECTURERS_SCHEDLUE)
                set.forEach { lecturer ->
                    Text(lecturer, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

        }
    }

}