package com.example.enlearn.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enlearn.R
import com.example.enlearn.ui.components.BottomNavigationBar
import com.example.enlearn.ui.theme.EnLearnTheme


@Composable
fun ProfileScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = 60.dp) // để tránh trùng thanh điều hướng
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6A2DEE))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Profile",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar + Name
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar_placeholder),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Do",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Name Field
        ProfileTextField(label = "Name", value = "Do Long")

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        ProfileTextField(label = "Email", value = "dogialong@gmail.com")
    }
}

@Composable
fun ProfileTextField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = Color.White,
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun ProfileScreen() {
    var selectedIndex by remember { mutableStateOf(2) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedItem = selectedIndex, onItemSelected = {
                selectedIndex = it
            })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedIndex) {
                0 -> Text("Home Screen")
                1 -> Text("Lesson Screen")
                2 -> ProfileScreenContent()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    EnLearnTheme {
        ProfileScreen()
    }
}
