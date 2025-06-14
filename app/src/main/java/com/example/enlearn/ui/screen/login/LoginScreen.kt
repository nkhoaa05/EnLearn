package com.example.enlearn.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.enlearn.R
import com.example.enlearn.ui.components.AppButton


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
//    onResgister: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel()
    val user by viewModel.user.observeAsState()
    val error by viewModel.error.observeAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (user != null) {
        onLoginSuccess()
        return
    }

    val PrimaryColor = Color(0xFF410FA3)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(PrimaryColor)
                .offset(y = 10.dp)
        ) {
            Text(
                "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(200.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_login),
                contentDescription = "Login Icon",
                modifier = Modifier.size(170.dp)
            )

            Text(
                "For free, join now and start learning",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                InputField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Nhập email của bạn"
                )
                InputField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Nhập mật khẩu",
                    isPassword = true
                )
            }
            AppButton(
                onClick = {
                    viewModel.login(email, password)
                }, "Đăng nhập",
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Hoặc",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Tạo tài khoản",
                    fontSize = 18.sp,
                    color = Color.Blue,
                    modifier = Modifier
                        .clickable {
                            // Xử lý sự kiện click chuyển sang màn tạo tài khoản
                        }
                        .padding(end = 16.dp)
                )
                LoginBtnThird(
                    onClick = { /* Xử lý đăng nhập Google */ },
                    logo = R.drawable.g_logo,
                )
            }
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Text(
        text = label,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 25.dp)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .width(320.dp)
                .height(56.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.LightGray
            ),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }

    Spacer(modifier = Modifier.height(20.dp))
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
            .height(55.dp)
            .width(100.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = logo),
                contentDescription = "icon",
            )
        }
    }
}
