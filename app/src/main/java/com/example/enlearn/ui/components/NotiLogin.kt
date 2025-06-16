package com.example.enlearn.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotiLoginEr(msg: String?) {
    if (!msg.isNullOrBlank()) {
        Text(
            text = msg,
            fontSize = 18.sp,
            color = if (msg.contains("thành công", ignoreCase = true)) Color(0xFF1B5E20) else Color.Red
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}
