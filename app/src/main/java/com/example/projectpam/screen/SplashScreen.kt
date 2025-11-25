package com.example.projectpam.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectpam.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val primaryGreen = Color(0xFF8FBC8F)

    // ⏳ Setelah 2 detik → LANGSUNG ke register
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("register") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryGreen),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = painterResource(id = R.drawable.icon_logo_splash),
                contentDescription = "Splash Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(8.dp))
        }

        Text(
            text = "Copyright ©2025",
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
