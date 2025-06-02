package com.example.enlearn.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box

@Composable
fun PageIndicator(
    numberOfPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    activeIndicatorSize: Dp = 8.dp,
    inactiveIndicatorSize: Dp = 8.dp,
    spacing: Dp = 8.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(numberOfPages) { index ->
            val isCurrentPage = index == currentPage
            Box(
                modifier = Modifier
                    .size(if (isCurrentPage) activeIndicatorSize else inactiveIndicatorSize)
                    .background(
                        color = if (isCurrentPage) activeColor else inactiveColor,
                        shape = CircleShape
                    )
            )
        }
    }
}
