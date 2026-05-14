package com.trio.app.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import com.trio.app.data.SharedData
import com.trio.app.ui.components.ContactDetailSheet
import com.trio.app.ui.components.LiquidGlassBottomBar

@Composable
fun MainScreen(
    mainNavController: NavHostController,
    onLogout: () -> Unit
) {
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("chats", "contacts", "moments", "profile")
    var showContactDetail by remember { mutableStateOf<String?>(null) }
    val contactToShow = showContactDetail?.let { SharedData.getContact(it) }

    androidx.compose.material3.Scaffold(
        bottomBar = {
            if (showBottomBar) {
                LiquidGlassBottomBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        mainNavController.navigate(route) {
                            popUpTo("chats") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = "chats",
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideInHorizontally(tween(350)) { it / 4 } + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(tween(350)) { -it / 4 } + fadeOut(tween(200)) },
            popEnterTransition = { slideInHorizontally(tween(350)) { -it / 4 } + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(tween(350)) { it / 4 } + fadeOut(tween(200)) }
        ) {
            composable("chats") {
                ChatListScreen(onChatClick = { userId -> mainNavController.navigate("chat/$userId") })
            }

            composable("contacts") {
                ContactsScreen(onContactClick = { userId -> showContactDetail = userId })
            }

            composable("moments") {
                MomentsScreen()
            }

            composable("profile") {
                SettingsScreen(onLogout = onLogout, onEditProfile = { mainNavController.navigate("profile/edit") })
            }

            composable("profile/edit") {
                ProfileEditScreen(onBack = { mainNavController.popBackStack() })
            }

            composable("group/create") {
                CreateGroupScreen(
                    onBack = { mainNavController.popBackStack() },
                    onGroupCreated = { groupId ->
                        mainNavController.popBackStack()
                        mainNavController.navigate("group/$groupId")
                    }
                )
            }

            composable("group/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                GroupChatScreen(groupId = groupId, onBack = { mainNavController.popBackStack() })
            }

            composable("chat/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ChatScreen(userId = userId, onBack = { mainNavController.popBackStack() })
            }
        }
    }

    if (contactToShow != null) {
        ContactDetailSheet(
            contact = contactToShow,
            onDismiss = { showContactDetail = null },
            onStartChat = {
                showContactDetail?.let { userId ->
                    showContactDetail = null
                    mainNavController.navigate("chat/$userId") {
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}
