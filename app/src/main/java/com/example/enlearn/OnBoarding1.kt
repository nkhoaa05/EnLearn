package com.example.enlearn

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val onboardingImageResource = R.drawable.logo
//@Preview(showBackground = true)
@Composable
fun OnboardingScreen1(
    onNextClicked: () -> Unit,
    onLoginClicked: () -> Unit
) {
    val primaryButtonColor = Color(0xFF6A77EE)
    val activeIndicatorColor = Color(0xFFFFA500) // Màu cam cho chấm active (từ Figma)
    val inactiveIndicatorColor = Color(0xFFE0E0E0) // Màu xám nhạt hơn cho chấm inactive (điều chỉnh từ D3D3D3)
    val linkTextColor = primaryButtonColor
    val titleTextColor = Color(0xFF1D1D1F) // Màu đen/xám đậm cho tiêu đề (giống iOS)
    val subtitleTextColor = Color(0xFF8A8A8F) // Màu xám cho phụ đề (giống iOS)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // Nền trắng tinh như Figma
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp) // Chỉ padding ngang, vertical sẽ dùng Spacer
                .padding(bottom = 80.dp)
                .padding(top = 115.dp), // Padding dưới cùng cho Column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer lớn ở trên cùng để đẩy hình ảnh xuống
            Spacer(modifier = Modifier.height(80.dp)) // Điều chỉnh để khớp với Figma

            // 1. Hình ảnh
            Image(
                painter = painterResource(id = onboardingImageResource), // << THAY THẾ ID RESOURCE Ở ĐÂY
                contentDescription = "Onboarding Illustration",
                modifier = Modifier
                    .fillMaxWidth(0.85f) // Chiếm 85% chiều rộng, để có khoảng trống hai bên
                    .aspectRatio(1.15f) // Tỷ lệ W/H ~295/255 từ Figma (điều chỉnh nếu cần)
                // .padding(top = 112.5.dp), // Bỏ padding top ở đây, dùng Spacer ở trên Column
                ,
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(40.dp)) // Khoảng cách từ ảnh đến indicators

            // 2. Chỉ báo trang (Page Indicators)
            PageIndicator(
                numberOfPages = 3,
                currentPage = 0,
                activeColor = activeIndicatorColor,
                inactiveColor = inactiveIndicatorColor,
                activeIndicatorSize = 10.dp, // Kích thước lớn hơn cho active
                inactiveIndicatorSize = 6.dp // Kích thước nhỏ hơn cho inactive
            )

            Spacer(modifier = Modifier.height(40.dp)) // Khoảng cách từ indicators đến tiêu đề

            // 3. Tiêu đề chính
            Text(
                text = "Confidence in your words",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp // Đảm bảo kích thước
                ),
                color = titleTextColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Tiêu đề phụ
            Text(
                text = "With conversation-based learning, you'll be talking from lesson one",
                style = MaterialTheme.typography.bodyLarge.copy( // bodyLarge (16sp) thay vì bodyMedium (14sp)
                    textAlign = TextAlign.Justify,
                    fontSize = 16.sp, // Cụ thể hóa kích thước
                    lineHeight = 24.sp // Tăng lineHeight cho dễ đọc
                ),
                color = subtitleTextColor
            )

            // Spacer để đẩy các nút xuống dưới
            Spacer(modifier = Modifier.weight(1f))

            // 5. Nút "Next"
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
                Text("Next", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) // Font to hơn, đậm vừa
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Văn bản "Already an account? Log in"
            ClickableLoginText(
                onLoginClicked = onLoginClicked,
                defaultTextColor = subtitleTextColor, // Dùng màu xám của phụ đề
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
    activeIndicatorSize: Dp = 8.dp, // Kích thước cho chấm active
    inactiveIndicatorSize: Dp = 8.dp, // Kích thước cho chấm inactive
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
                    .size(if (isCurrentPage) activeIndicatorSize else inactiveIndicatorSize) // Kích thước động
                    .clip(CircleShape)
                    .background(if (isCurrentPage) activeColor else inactiveColor)
            )
        }
    }
}

@Composable
fun ClickableLoginText(
    onLoginClicked: () -> Unit,
    defaultTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant, // Màu mặc định cho phần text thường
    linkColor: Color = MaterialTheme.colorScheme.primary
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = defaultTextColor, fontSize = 14.sp)) { // Áp dụng màu và size cho phần text thường
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
        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center), // Style cơ bản, size sẽ được override bởi SpanStyle
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "LOGIN", start = offset, end = offset)
                .firstOrNull()?.let {
                    onLoginClicked()
                }
        }

    )
}


// Giả sử bạn có một Theme tên là EnLearnTheme
// Nếu không, bạn có thể thay thế bằng MaterialTheme {}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreen1Preview() {
    // Thay EnLearnTheme bằng theme của bạn nếu có
    MaterialTheme { // Hoặc EnLearnTheme {
        OnboardingScreen1(
            onNextClicked = { println("Next clicked") },
            onLoginClicked = { println("Login clicked") }
        )
    }
}