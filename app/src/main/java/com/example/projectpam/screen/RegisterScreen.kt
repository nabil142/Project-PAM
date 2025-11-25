package com.example.projectpam.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projectpam.R
import com.example.projectpam.data.SupabaseClientProvider
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun RegisterScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf("signup") }

    var name by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val supabase = SupabaseClientProvider.client

    val primaryGreen = Color(0xFF5A9A5A)
    val lightGray = Color(0xFFE8E8E8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        Image(
            painter = painterResource(id = R.drawable.icon_logo_register),
            contentDescription = "",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // TAB SWITCH
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(lightGray, RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (selectedTab == "signup") Color.White else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedTab = "signup" },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Sign up",
                    fontWeight = if (selectedTab == "signup") FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == "signup") Color.Black else Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (selectedTab == "register") Color.White else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedTab = "register" },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Register",
                    fontWeight = if (selectedTab == "register") FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == "register") Color.Black else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (selectedTab == "signup") {
            // ========== SIGN IN FORM (LOGIN) ==========

            Text("Email address", fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Your email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = lightGray,
                    focusedBorderColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Password", fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Password") },
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = ""
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = lightGray,
                    focusedBorderColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Forgot password?",
                fontSize = 12.sp,
                color = primaryGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Toast.makeText(context, "Forgot password coming soon", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            supabase.auth.signInWith(Email) {
                                this.email = email.trim()
                                this.password = password
                            }

                            Toast.makeText(context, "Login success!", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") {
                                popUpTo("register") { inclusive = true }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Login failed: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(primaryGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign in", fontSize = 16.sp, color = Color.White)
                }
            }

        } else {
            // ========== REGISTER FORM ==========

            Text("Full name", fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Your name") },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = lightGray,
                    focusedBorderColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Profession", fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = profession,
                onValueChange = { profession = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ex: Student, Developer, Designer...") },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = lightGray,
                    focusedBorderColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Email address", fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Your email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = lightGray,
                    focusedBorderColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Password", fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Password") },
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = ""
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = lightGray,
                    focusedBorderColor = primaryGreen
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (name.isBlank() || profession.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (password.length < 6) {
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            // Register ke Supabase Auth
                            supabase.auth.signUpWith(Email) {
                                this.email = email.trim()
                                this.password = password
                            }

                            Toast.makeText(context, "Registration success!", Toast.LENGTH_SHORT).show()

                            // Encode data untuk URL safety
                            val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                            val encodedProfession = URLEncoder.encode(profession, StandardCharsets.UTF_8.toString())
                            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())

                            navController.navigate("profile/$encodedName/$encodedProfession/$encodedEmail") {
                                popUpTo("register") { inclusive = true }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Registration failed: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(primaryGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create Account", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}