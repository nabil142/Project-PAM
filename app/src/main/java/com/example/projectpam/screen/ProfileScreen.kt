package com.example.projectpam.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    name: String? = null,
    profession: String? = null,
    email: String? = null
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF91C788)
    val lightPink = Color(0xFFFFE4E1)
    val iconPink = Color(0xFFFF9E80)

    // Gunakan data dari parameter atau default
    val displayName = name ?: "John Doe"
    val displayProfession = profession ?: "User Casual"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daily Practice",
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Filled.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Restaurant, null) },
                    label = { Text("Nutrition") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Favorite, null) },
                    label = { Text("Health") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Person, null) },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.Black
                    )
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(primaryGreen)
                .verticalScroll(rememberScrollState())
        ) {

            // PROFILE SECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFA0A0A0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        null,
                        modifier = Modifier.size(70.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    displayName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    displayProfession,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // MENU ITEMS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // EDIT PROFILE
                ProfileMenuItemGreen(
                    icon = Icons.Default.Edit,
                    title = "Edit Profile",
                    onClick = {
                        Toast.makeText(context, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
                    }
                )

                // SETTINGS
                ProfileMenuItemGreen(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    onClick = {
                        Toast.makeText(context, "Settings clicked", Toast.LENGTH_SHORT).show()
                    }
                )

                // TERMS & PRIVACY
                ProfileMenuItemGreen(
                    icon = Icons.Default.Description,
                    title = "Terms & Privacy Policy",
                    onClick = {
                        Toast.makeText(context, "Terms & Privacy clicked", Toast.LENGTH_SHORT).show()
                    }
                )

                // LOG OUT
                ProfileMenuItemGreen(
                    icon = Icons.Default.Logout,
                    title = "Log Out",
                    onClick = { showLogoutDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // LOGOUT DIALOG
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        navController.navigate("register") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Yes", color = Color(0xFF5A9A5A))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileMenuItemGreen(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    val iconPink = Color(0xFFFF9E80)
    val lightPink = Color(0xFFFFE4E1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(lightPink),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconPink,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            color = Color.Black
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Arrow",
            modifier = Modifier.size(20.dp),
            tint = Color.Gray
        )
    }
}