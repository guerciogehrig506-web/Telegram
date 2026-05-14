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
import com.trio.app.data.model.LoginResponse
import com.trio.app.data.repository.ApiResult
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_TextSecondary
import com.trio.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val loginState by authViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is ApiResult.Success -> {
                authViewModel.clearLoginState()
                SessionManager.saveToken(state.data.token, state.data.user)
                onLoginSuccess()
            }
            is ApiResult.Error -> { errorMessage = state.message; authViewModel.clearLoginState() }
            is ApiResult.Loading -> {}
            null -> {}
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("欢迎使用 Trio", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = TG_ActionBar)
            Text("登录你的账号", style = MaterialTheme.typography.bodyLarge, color = TG_TextSecondary, modifier = Modifier.padding(top = 8.dp))

            OutlinedTextField(email, { email = it; errorMessage = null }, label = { Text("邮箱") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.padding(top = 32.dp), singleLine = true)
            OutlinedTextField(password, { password = it; errorMessage = null }, label = { Text("密码") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.padding(top = 12.dp), singleLine = true)

            errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp)) }

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) errorMessage = "请输入邮箱和密码"
                    else { errorMessage = null; authViewModel.login(email, password) }
                },
                modifier = Modifier.padding(top = 24.dp),
                enabled = loginState !is ApiResult.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = TG_ActionBar)
            ) {
                if (loginState is ApiResult.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("登录", modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp))
                }
            }

            TextButton(onClick = onNavigateToRegister, modifier = Modifier.padding(top = 8.dp)) {
                Text("还没有账号？立即注册", color = TG_ActionBar)
            }
        }
    }
}