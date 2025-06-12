package com.example.enlearn.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enlearn.R


@Preview(showBackground = true)
@Composable
fun LoginScreen() {

    val PrimaryColor = Color(0xFF410FA3)
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(PrimaryColor)
                .offset(y = (10.dp))

        ) {
            Image(
                painter = painterResource(R.drawable.back_icon),
                contentDescription = "Back icon",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = 20.dp)
                    .clickable { }
            )
            Text(
                "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
            )

        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(200.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_login),
                contentDescription = "Login Icon",
                modifier = Modifier
                    .size(200.dp)
            )

            Text(
                "For free, join now and start learning",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text("login",
            color = Color.Blue)
    }
}