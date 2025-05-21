package com.example.enlearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enlearn.ui.theme.EnLearnTheme

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Nội dung sẽ tràn ra các cạnh
        setContent {
            EnLearnTheme {
                // Màu sắc cho gradient (bạn có thể tùy chỉnh các mã màu HEX này)
                val topColor = Color(0xFF410FA3)
                val bottomColor = Color(0xFF18063D)

                Surface( // Thay thế Scaffold bằng Surface
                    modifier = Modifier
                        .fillMaxSize() // Surface chiếm toàn bộ không gian
                        .background( // Áp dụng gradient cho nền của Surface
                            brush = Brush.verticalGradient(
                                colors = listOf(topColor, bottomColor)
                            )
                        )
                ) {
                    // Box này dùng để chứa nội dung và áp dụng padding cho system insets
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding(),
                        contentAlignment = Alignment.Center
                    ) {
                        EnLearnSplashScreenContent()
                    }
                }
            }
        }
    }
}

@Composable
fun EnLearnSplashScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = "EnLearn App Logo",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "EnLearn",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = false, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    EnLearnTheme {
        val topColor = Color(0xFF410FA3)
        val bottomColor = Color(0xFF18063D)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(topColor, bottomColor)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            EnLearnSplashScreenContent()
        }
    }
}