package com.trio.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.trio.app.data.firebase.FirebaseAuthManager
import com.trio.app.data.SessionManager
import com.trio.app.ui.screen.LoginScreen
import com.trio.app.ui.screen.MainScreen
import com.trio.app.ui.screen.RegisterScreen
import com.trio.app.ui.screen.SplashScreen
import com.trio.app.ui.theme.TrioTheme

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        FirebaseAuthManager.init(application)
        SessionManager.init(application)
        enableEdgeToEdge()
        setupFcm()

        setContent {
            TrioTheme {
                TrioApp()
            }
        }
    }

    private fun setupFcm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun TrioApp() {
    val rootNavController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }
    var startDestination by remember { mutableStateOf("login") }

    if (showSplash) {
        SplashScreen(onFinished = {
            SessionManager.checkSession { loggedIn ->
                startDestination = if (loggedIn) "main" else "login"
                showSplash = false
            }
        })
    } else {
        NavHost(navController = rootNavController, startDestination = startDestination,
            enterTransition = { slideInHorizontally(tween(350)) { it / 4 } + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(tween(350)) { -it / 4 } + fadeOut(tween(200)) },
            popEnterTransition = { slideInHorizontally(tween(350)) { -it / 4 } + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(tween(350)) { it / 4 } + fadeOut(tween(200)) }
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        rootNavController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { rootNavController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = { rootNavController.popBackStack() },
                    onNavigateToLogin = { rootNavController.popBackStack() }
                )
            }
            composable("main") {
                MainScreen(
                    mainNavController = rememberNavController(),
                    onLogout = {
                        FirebaseAuthManager.logout()
                        SessionManager.logout()
                        rootNavController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}