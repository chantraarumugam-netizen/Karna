package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.ChatViewModel
import com.example.ui.LanguageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: ChatViewModel,
    onLoginSuccess: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }

    val gradientPrimary = if (isDarkByTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF030305),
                Color(0xFF0F0F16)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF0F2FA),
                Color(0xFFFAF9F6)
            )
        )
    }

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientPrimary),
        contentAlignment = Alignment.Center
    ) {
        // Futuristic Decorative Background Orbs
        if (isDarkByTheme) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .offset(x = (-80).dp, y = (-200).dp)
                    .background(laserPurple.copy(alpha = 0.12f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = 100.dp, y = 250.dp)
                    .background(neonCyan.copy(alpha = 0.08f), CircleShape)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .border(1.dp, neonCyan.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .testTag("auth_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkByTheme) Color(0xFF08080C).copy(alpha = 0.85f) else Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Glow Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.linearGradient(colors = listOf(neonCyan, laserPurple)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Logo",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = LanguageHelper.getString("app_name", language),
                    style = MaterialTheme.typography.headlineLarge,
                    color = textMain,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = LanguageHelper.getString("tagline", language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = neonCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input Holo-ID
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(LanguageHelper.getString("username", language)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User", tint = neonCyan) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = neonCyan,
                        unfocusedBorderColor = neonCyan.copy(alpha = 0.4f),
                        focusedLabelColor = neonCyan,
                        focusedTextColor = textMain,
                        unfocusedTextColor = textMain
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Password Key
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(LanguageHelper.getString("password", language)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Key", tint = laserPurple) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = laserPurple,
                        unfocusedBorderColor = laserPurple.copy(alpha = 0.4f),
                        focusedLabelColor = laserPurple,
                        focusedTextColor = textMain,
                        unfocusedTextColor = textMain
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Holographic Portal Sign In Button
                Button(
                    onClick = {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            viewModel.attemptLogin(username, password)
                            onLoginSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(colors = listOf(neonCyan, laserPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSignUpMode) {
                                LanguageHelper.getString("sign_up", language)
                            } else {
                                LanguageHelper.getString("sign_in", language)
                            },
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Register Link Toggle
                Text(
                    text = if (isSignUpMode) {
                        if (language == AppLanguage.TAMIL) "ஏற்கனவே கணக்கு உள்ளதா? உள்நுழைக" else "Already have a Holo-ID? Sign In"
                    } else {
                        if (language == AppLanguage.TAMIL) "புதிய கணக்கு இல்லையா? இப்போது உருவாக்கு" else "Don't have a Holo-ID? Create One"
                    },
                    modifier = Modifier
                        .clickable { isSignUpMode = !isSignUpMode }
                        .padding(8.dp)
                        .testTag("toggle_signup"),
                    color = neonCyan.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }
    }
}
