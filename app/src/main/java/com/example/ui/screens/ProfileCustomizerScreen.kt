package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.ChatViewModel
import com.example.ui.LanguageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCustomizerScreen(viewModel: ChatViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()
    val username by viewModel.userProfileName.collectAsState()
    val email by viewModel.userProfileEmail.collectAsState()
    val activeAvatarIdx by viewModel.userAvatarIndex.collectAsState()
    val cloudSyncState by viewModel.cloudSyncStatus.collectAsState()

    val context = LocalContext.current

    var editUsername by remember { mutableStateOf(username) }
    var editEmail by remember { mutableStateOf(email) }
    var selectedAvatar by remember { mutableStateOf(activeAvatarIdx) }

    // Cyberpunk Avatars lists
    val avatarsList = listOf(
        AvatarItem(0, "🤖", "Neural Drone"),
        AvatarItem(1, "🛸", "Cosmic Core"),
        AvatarItem(2, "👾", "Glitch Matrix"),
        AvatarItem(3, "⚡", "Tesla Overdrive"),
        AvatarItem(4, "💠", "Holo Polygon")
    )

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Title
        Text(
            text = LanguageHelper.getString("profile", language),
            style = MaterialTheme.typography.titleLarge,
            color = neonCyan,
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        // Large Accent Glowing Avatar representation
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = Brush.radialGradient(colors = listOf(laserPurple.copy(alpha = 0.2f), Color.Transparent)),
                        shape = CircleShape
                    )
                    .border(2.dp, neonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarsList.find { it.id == selectedAvatar }?.emoji ?: "🤖",
                    fontSize = 42.sp
                )
            }
        }

        Divider(color = textMain.copy(alpha = 0.12f))

        // Avatar selector lists
        Column {
            Text(
                text = "Choose Cyber-Avatar",
                style = MaterialTheme.typography.labelMedium,
                color = textMain.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                avatarsList.forEach { av ->
                    val isSel = av.id == selectedAvatar
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                color = if (isSel) neonCyan.copy(alpha = 0.15f) else (if (isDarkByTheme) Color(0xFF101018) else Color(0xFFECECEC)),
                                shape = CircleShape
                            )
                            .border(width = 1.dp, color = if (isSel) neonCyan else Color.Transparent, shape = CircleShape)
                            .clickable { selectedAvatar = av.id },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = av.emoji, fontSize = 24.sp)
                    }
                }
            }
        }

        // Form fields inputs
        OutlinedTextField(
            value = editUsername,
            onValueChange = { editUsername = it },
            label = { Text("Profile Call-sign (Holo ID)") },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = neonCyan) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_username_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = neonCyan,
                focusedTextColor = textMain,
                focusedLabelColor = neonCyan
            )
        )

        OutlinedTextField(
            value = editEmail,
            onValueChange = { editEmail = it },
            label = { Text("Mainframe Cloud Registry") },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = laserPurple) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_email_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = laserPurple,
                focusedTextColor = textMain,
                focusedLabelColor = laserPurple
            )
        )

        // Toggles: Dark / Light Mode Config
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, neonCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .clickable { viewModel.toggleTheme() }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(
                    imageVector = if (isDarkByTheme) Icons.Default.Brightness2 else Icons.Default.Brightness5,
                    contentDescription = null,
                    tint = neonCyan
                )
                Text(
                    text = LanguageHelper.getString("toggle_theme", language),
                    color = textMain,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Switch(
                checked = isDarkByTheme,
                onCheckedChange = { viewModel.toggleTheme() },
                colors = SwitchDefaults.colors(checkedThumbColor = neonCyan, checkedTrackColor = neonCyan.copy(alpha = 0.4f)),
                modifier = Modifier.testTag("dark_mode_toggle")
            )
        }

        // Mainframe Cloud Sync status indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, laserPurple.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(
                    imageVector = if (cloudSyncState == "syncing") Icons.Default.Sync else Icons.Default.CheckCircle,
                    contentDescription = "cloud sync",
                    tint = if (cloudSyncState == "syncing") laserPurple else Color.Green,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = LanguageHelper.getString("cloud_sync", language),
                        color = textMain,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (cloudSyncState == "syncing") "Automating data compilation..." else "All credentials synchronized securely",
                        color = textMain.copy(alpha = 0.5f),
                        fontSize = 9.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        if (cloudSyncState == "syncing") laserPurple.copy(alpha = 0.15f) else Color.Green.copy(alpha = 0.15f),
                        RoundedCornerShape(8.dp)
                    )
                    .border(width = 1.dp, color = if (cloudSyncState == "syncing") laserPurple else Color.Green, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (cloudSyncState == "syncing") "SYNCING" else "SECURED",
                    color = if (cloudSyncState == "syncing") laserPurple else Color.Green,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save modification changes button
        Button(
            onClick = {
                viewModel.updateProfile(editUsername, editEmail, selectedAvatar)
                Toast.makeText(context, "Holo-Registry Updates Completed", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("apply_profile_btn")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(colors = listOf(neonCyan, laserPurple))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "APPLY MAINREGISTRY UPDATES",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }
    }
}

data class AvatarItem(
    val id: Int,
    val emoji: String,
    val name: String
)
