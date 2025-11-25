package com.example.projectpam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectpam.screen.HomePage
import com.example.projectpam.screen.ProfileScreen
import com.example.projectpam.screen.RegisterScreen
import com.example.projectpam.screen.SplashScreen
import com.example.projectpam.ui.theme.ProjectPAMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProjectPAMTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = "splash"
        ) {
            composable("splash") {
                SplashScreen(navController)
            }

            composable("register") {
                RegisterScreen(navController)
            }

            composable("home") {
                HomePage(navController)
            }

            // Profile dengan parameter dari register
            composable("profile/{name}/{profession}/{email}") { backStackEntry ->
                ProfileScreen(
                    navController = navController,
                    name = backStackEntry.arguments?.getString("name"),
                    profession = backStackEntry.arguments?.getString("profession"),
                    email = backStackEntry.arguments?.getString("email")
                )
            }

            // Profile tanpa parameter (dari navbar)
            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    name = null,
                    profession = null,
                    email = null
                )
            }
        }
    }
}