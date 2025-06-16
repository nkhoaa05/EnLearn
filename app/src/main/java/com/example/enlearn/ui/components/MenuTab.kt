package com.example.enlearn.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun MainScaffoldWithBottomNav(
    screens: List<@Composable () -> Unit>,
    defaultIndex: Int = 0
) {
    var selectedIndex by remember { mutableStateOf(defaultIndex) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedIndex,
                onItemSelected = { selectedIndex = it }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (selectedIndex in screens.indices) {
                screens[selectedIndex]()
            }
        }
    }
}
