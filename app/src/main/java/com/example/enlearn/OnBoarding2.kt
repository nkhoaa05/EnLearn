package com.example.enlearn


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.enlearn.presentation.components.ClickableLoginText
import com.example.enlearn.presentation.components.PageIndicator
import com.example.enlearn.ui.theme.EnLearnTheme

@Composable
fun OnboardingScreen2(
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
                painter = painterResource(id = R.drawable.onboarding2),
                contentDescription = "Learning Illustration",
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1.15f),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(40.dp))

            PageIndicator(
                numberOfPages = 3,
                currentPage = 1,
                activeColor = activeIndicatorColor,
                inactiveColor = inactiveIndicatorColor,
                activeIndicatorSize = 10.dp,
                inactiveIndicatorSize = 6.dp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Take your time to learn",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                ),
                color = titleTextColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Develop a habit of learning and make it a part of your daily routine",
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryButtonColor,
                    contentColor = Color.White
                )
            ) {
                Text("Next", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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

