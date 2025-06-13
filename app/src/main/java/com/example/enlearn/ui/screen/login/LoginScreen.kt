package com.example.enlearn.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
                .height(120.dp)
                .background(PrimaryColor)
                .offset(y = (10.dp))

        ) {
            Text(
                "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
            )

        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(200.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_login),
                contentDescription = "Login Icon",
                modifier = Modifier
                    .size(170.dp)
            )

            Text(
                "For free, join now and start learning",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    "Đăng nhập nhanh với Google",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(15.dp))
                LoginBtnThird(onClick = {}, logo = R.drawable.g_logo)
                Spacer(modifier = Modifier.height(50.dp))
            }

        }


    }
}


@Composable
fun LoginBtnThird(onClick: () -> Unit, logo: Int) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD5EDFF),
            contentColor = Color(0xFF130160)
        ),
        modifier = Modifier
            .height(80.dp)
            .width(320.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = logo),
                contentDescription = "icon",

                )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                "Đăng nhập bằng Google",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}