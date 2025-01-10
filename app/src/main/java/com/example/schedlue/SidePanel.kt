package com.example.schedlue

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun SlidingPanel(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f))
        {
            Text("Группы", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        Box(modifier = Modifier.weight(1f))
        {
            Text("Преподаватели", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}