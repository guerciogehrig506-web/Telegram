package com.trio.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trio.app.data.SessionManager
import com.trio.app.data.model.RegisterResponse
import com.trio.app.data.repository.ApiResult
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_TextSecondary
import com.trio.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val registerState by authViewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is ApiResult.Success -> {
                authViewModel.clearRegisterState()
                SessionManager.saveToken(state.data.token, state.data.user)
                onRegisterSuccess()
            }
            is ApiResult.Error -> { errorMessage = state.message; authViewModel.clearRegisterState() }
            is ApiResult.Loading -> {}
            null -> {}
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("创建账号", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = TG_ActionBar)
            Text("加入 Trio 团队协作", style = MaterialTheme.typography.bodyLarge, color = TG_TextSecondary, modifier = Modifier.padding(top = 8.dp))

            OutlinedTextField(username, { username = it; errorMessage = null }, label = { Text("用户名") }, modifier = Modifier.padding(top = 32.dp), singleLine = true)
            OutlinedTextField(email, { email = it; errorMessage = null }, label = { Text("邮箱") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.padding(top = 12.dp), singleLine = true)
            OutlinedTextField(password, { password = it; errorMessage = null }, label = { Text("密码") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.padding(top = 12.dp), singleLine = true)
            OutlinedTextField(confirmPassword, { confirmPassword = it; errorMessage = null }, label = { Text("确认密码") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.padding(top = 12.dp), singleLine = true)

            errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp)) }

            Button(
                onClick = {
                    when {
                        username.isBlank() || email.isBlank() || password.isBlank() -> errorMessage = "请填写所有字段"
                        password != confirmPassword -> errorMessage = "两次密码不一致"
                        password.length < 6 -> errorMessage = "密码至少需要6位"
                        else -> { errorMessage = null; authViewModel.register(username, email, password) }
                    }
                },
                modifier = Modifier.padding(top = 24.dp),
                enabled = registerState !is ApiResult.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = TG_ActionBar)
            ) {
                if (registerState is ApiResult.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("注册", modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp))
                }
            }

            TextButton(onClick = onNavigateToLogin, modifier = Modifier.padding(top = 8.dp)) {
                Text("已有账号？立即登录", color = TG_ActionBar)
            }
        }
    }
}