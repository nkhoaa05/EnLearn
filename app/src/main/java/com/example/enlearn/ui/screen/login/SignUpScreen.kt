package com.example.enlearn.ui.screen.login

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.enlearn.ui.components.AppButton
import com.example.enlearn.ui.components.InputField
import com.example.enlearn.ui.components.NotiLoginEr
import com.example.enlearn.ui.viewModel.LoginViewModel


@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    navController: NavController
) {
    val viewModel: LoginViewModel = viewModel()
    val user by viewModel.user.observeAsState()
    val error by viewModel.error.observeAsState()
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    var signupSuccessMsg by remember { mutableStateOf("") }
    var hasNavigated by remember { mutableStateOf(false) }

    // Khi user thay đổi (đăng ký thành công)
    LaunchedEffect(user) {
        if (user != null && !hasNavigated) {
            signupSuccessMsg = "Đăng ký thành công"
            hasNavigated = true
            // Delay 2 giây để hiển thị thông báo
            kotlinx.coroutines.delay(2000)
            onSignUpSuccess()
        }
    }

    val primaryColor = Color(0xFF410FA3)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(primaryColor)
                .offset(y = 10.dp)
        ) {
            Text(
                "Đăng ký",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(260.dp)
        ) {
            Text(
                "Tạo tài khoản để bắt đầu hành trình học tập của bạn!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            InputField(
                label = "First Name",
                value = firstname,
                onValueChange = { firstname = it },
                placeholder = "Nhập tên của bạn"
            )
            InputField(
                label = "Last Name",
                value = lastname,
                onValueChange = { lastname = it },
                placeholder = "Nhập họ của bạn"
            )
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
            if (firstname.isNotEmpty() && lastname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) msg =
                " "

            Spacer(modifier = Modifier.height(10.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hiển thị thông báo lỗi hoặc thành công
            val displayMsg = when {
                msg.isNotBlank() -> msg
                signupSuccessMsg.isNotBlank() -> signupSuccessMsg
                !error.isNullOrBlank() -> error!!
                else -> ""
            }
            NotiLoginEr(displayMsg)

            AppButton(
                onClick = {
                    if (firstname.isBlank() || lastname.isBlank() || email.isBlank() || password.isBlank()) {
                        msg = "Vui lòng nhập đầy đủ thông tin"
                    } else {
                        msg = ""
                        viewModel.register(firstname, lastname, email, password)
                    }
                },
                text = "Tạo tài khoản",
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Đã có tài khoản? Đăng nhập",
                fontSize = 20.sp,
                color = Color.Blue,
                modifier = Modifier.clickable {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                textAlign = TextAlign.Center
            )
        }
    }

}





