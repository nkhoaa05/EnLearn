package com.example.enlearn.ui.screen.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.enlearn.R
import com.example.enlearn.ui.components.AppButton
import com.example.enlearn.ui.components.InputField
import com.example.enlearn.ui.components.LoginBtnThird
import com.example.enlearn.ui.components.NotiLoginEr
import com.example.enlearn.ui.viewModel.LoginViewModel
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    navController: NavController
) {
    val viewModel: LoginViewModel = viewModel()
    val isLoginSuccessful by viewModel.loginSuccess.observeAsState(initial = false)
    val errorMessage by viewModel.error.observeAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hasNavigated by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf("") }
    var loginSuccessMsg by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isLoginSuccessful, errorMessage) {
        if (isLoginSuccessful) {
            scope.launch {
                snackbarHostState.showSnackbar("Đăng nhập thành công!")
            }
            onLoginSuccess()
        }

        errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
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
                "Đăng nhập",
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
                "Tham gia miễn phí ngay và bắt đầu hành trình học tập của bạn!",
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
                    onValueChange = { password = it},
                    placeholder = "Nhập mật khẩu",
                    isPassword = true
                )
                if (email.isNotEmpty() && password.isNotEmpty()) msg = " " // Xóa thông báo
            }
            val displayMsg = when {
                msg.isNotBlank() -> msg
                !loginSuccessMsg.isNullOrBlank() -> loginSuccessMsg
                !errorMessage.isNullOrBlank() -> errorMessage!!
                else -> ""
            }
            NotiLoginEr(displayMsg)
            AppButton(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) viewModel.login(
                        email,
                        password
                    )
                    else {
                        msg = "Email hoặc mật khẩu không được để trống"
                    }
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

            Text(
                "Tạo tài khoản",
                fontSize = 18.sp,
                color = Color.Blue,
                modifier = Modifier
                    .clickable {
                        navController.navigate("signup")
                    }
            )
            Spacer(modifier = Modifier.height(20.dp))
            val context = LocalContext.current
            val startGoogleSignIn by viewModel.startGoogleSignIn.observeAsState()

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    viewModel.loginWithGoogle(result.data)
                }
            }
            LoginBtnThird(
                onClick = {
                    val signInIntent = viewModel.getGoogleSignInIntent(context)
                    launcher.launch(signInIntent)
                },
                logo = R.drawable.g_logo,
            )

        }
    }
}
