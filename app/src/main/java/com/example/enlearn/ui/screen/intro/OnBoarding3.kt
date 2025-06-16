package com.example.enlearn.ui.screen.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.enlearn.R
import com.example.enlearn.ui.components.ClickableLoginText
import com.example.enlearn.ui.components.PageIndicator

@Composable
fun OnboardingScreen3(
    onNextClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    navController: NavController
) {
    val primaryButtonColor = Color(0xFF6A77EE)
    val activeIndicatorColor = Color(0xFFFFA500)
    val inactiveIndicatorColor = Color(0xFFE0E0E0)
    val linkTextColor = primaryButtonColor
    val titleTextColor = Color(0xFF1D1D1F)
    val subtitleTextColor = Color(0xFF8A8A8F)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 80.dp)
                .padding(top = 115.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Image(
                painter = painterResource(id = R.drawable.onboarding3),
                contentDescription = "Language Learning Illustration",
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1.15f),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(40.dp))

            PageIndicator(
                numberOfPages = 3,
                currentPage = 2,
                activeColor = activeIndicatorColor,
                inactiveColor = inactiveIndicatorColor,
                activeIndicatorSize = 10.dp,
                inactiveIndicatorSize = 6.dp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "The lessons you need to learn",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                ),
                color = titleTextColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Using a variety of learning styles to learn and retain",
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                color = subtitleTextColor
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNextClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryButtonColor,
                    contentColor = Color.White
                )
            ) {
                Text("Get started", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            ClickableLoginText(
                onLoginClicked = onLoginClicked,
                defaultTextColor = subtitleTextColor,
                linkColor = linkTextColor
            )
        }
    }
}

