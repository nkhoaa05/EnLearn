package com.example.enlearn.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MainScaffoldWithBottomNav(
    screens: List<@Composable () -> Unit>,
    defaultIndex: Int = 0
) {
    var selectedIndex by remember { mutableStateOf(defaultIndex) }

    Scaffold(
        bottomBar = {
            Column {

                // 2. Thêm Divider ở trên cùng
                Divider(
                    thickness = 1.dp,
                    // Dùng màu từ theme để tự động thích ứng với Dark Mode
                    color = MaterialTheme.colorScheme.outlineVariant
                    // Hoặc dùng màu đen mờ: Color.Black.copy(alpha = 0.12f)
                )

                // 3. Đặt BottomNavigationBar của bạn ngay bên dưới
                BottomNavigationBar(
                    selectedItem = selectedIndex,
                    onItemSelected = { selectedIndex = it }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (selectedIndex in screens.indices) {
                screens[selectedIndex]()
            }
        }
    }
}
