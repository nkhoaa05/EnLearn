package com.example.enlearn.ui.screen.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.enlearn.R


val onboardingImageResource = R.drawable.logo
//@Preview(showBackground = true)
@Composable
fun OnboardingScreen1(navController: NavController) {

    val onNextClicked = {
        navController.navigate("onboarding2")
    }

    val onLoginClicked = {
        navController.navigate("home")
    }

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
                .padding(top = 115.dp), // Padding dưới cùng cho Column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // 1. Hình ảnh
            Image(
                painter = painterResource(id = onboardingImageResource),
                contentDescription = "Onboarding Illustration",
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1.15f)
                // .padding(top = 112.5.dp),
                ,
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 2. Chỉ báo trang (Page Indicators)
            PageIndicator(
                numberOfPages = 3,
                currentPage = 0,
                activeColor = activeIndicatorColor,
                inactiveColor = inactiveIndicatorColor,
                activeIndicatorSize = 10.dp,
                inactiveIndicatorSize = 6.dp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Tiêu đề chính
            Text(
                text = "Confidence in your words",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                ),
                color = titleTextColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Tiêu đề phụ
            Text(
                text = "With conversation-based learning, you'll be talking from lesson one",
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
                    .clip(CircleShape)
                    .background(if (isCurrentPage) activeColor else inactiveColor)
            )
        }
    }
}

@Composable
fun ClickableLoginText(
    onLoginClicked: () -> Unit,
    defaultTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    linkColor: Color = MaterialTheme.colorScheme.primary
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = defaultTextColor, fontSize = 14.sp)) {
            append("Already an account? ")
        }
        pushStringAnnotation(tag = "LOGIN", annotation = "login")
        withStyle(style = SpanStyle(color = linkColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
            append("Log in")
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "LOGIN", start = offset, end = offset)
                .firstOrNull()?.let {
                    onLoginClicked()
                }
        }

    )
}

