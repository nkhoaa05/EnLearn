package com.example.enlearn.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.enlearn.ui.viewModel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    navController: NavController
) {

    val viewModel: LoginViewModel = viewModel()
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val errorMessage by viewModel.error.observeAsState()
    val isSignUpSuccessful by viewModel.loginSuccess.observeAsState(initial = false)


    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    var hasAttemptedSignUp by remember { mutableStateOf(false) }


    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isSignUpSuccessful, errorMessage) {
        if (isSignUpSuccessful) {
            scope.launch {
                snackbarHostState.showSnackbar("Đăng ký thành công! Đang chuyển đến trang đăng nhập...")
            }
            kotlinx.coroutines.delay(1500)
            onSignUpSuccess()
        }

        errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                // Cần có cơ chế reset lỗi trong ViewModel để không hiển thị lại
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF410FA3)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Đăng ký", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(25.dp))

                // Các ô nhập liệu
                Column(modifier = Modifier.padding(horizontal = 32.dp)) {
                    InputField(label = "First Name", value = firstname, onValueChange = { firstname = it }, placeholder = "Nhập tên của bạn")
                    InputField(label = "Last Name", value = lastname, onValueChange = { lastname = it }, placeholder = "Nhập họ của bạn")
                    InputField(label = "Email", value = email, onValueChange = { email = it }, placeholder = "Nhập email của bạn")
                    InputField(label = "Password", value = password, onValueChange = { password = it }, placeholder = "Nhập mật khẩu", isPassword = true)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nút Tạo tài khoản
                AppButton(
                    onClick = {
                        hasAttemptedSignUp = true
                        if (firstname.isNotBlank() && lastname.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                            viewModel.register(firstname, lastname, email, password)
                        }
                    },
                    text = "Tạo tài khoản",
                    modifier = Modifier.width(200.dp).height(50.dp),
                    enabled = !isLoading
                )

                // Hiển thị thông báo yêu cầu nhập nếu cần
                if (hasAttemptedSignUp && (firstname.isBlank() || lastname.isBlank() || email.isBlank() || password.isBlank())) {
                    Text(
                        text = "Vui lòng nhập đầy đủ thông tin",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nút chuyển sang Đăng nhập
                Text(
                    "Đã có tài khoản? Đăng nhập",
                    fontSize = 16.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable { navController.navigate("login") },
                    textAlign = TextAlign.Center
                )
            }

            // Hiển thị vòng xoay loading
            if (isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}