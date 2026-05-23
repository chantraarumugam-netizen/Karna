package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatViewModel
import com.example.ui.LanguageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodingAssistantScreen(
    viewModel: ChatViewModel,
    onNavigateToChat: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()
    val context = LocalContext.current

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    // Preloaded development helpers
    val templatePrompts = listOf(
        CodeTemplate(
            title = "Kotlin Coroutine Channel",
            desc = "Synchronize twin async background buffers safe from deadlocks",
            promptText = "Draft a robust Kotlin flow sample that uses a high capacity Channel backoff mechanism to pipe network events into Room."
        ),
        CodeTemplate(
            title = "Jetpack Compose Neon Shader",
            desc = "Custom hardware drawing block with infinite pulsing loop",
            promptText = "Create a custom Jetpack Compose Canvas modifier implementing a shimmering radial laser neon border stroke."
        ),
        CodeTemplate(
            title = "Cybernetic Python Web Socket",
            desc = "Fully multiplexed terminal channel using asyncio",
            promptText = "Provide a clean asyncio Python client program that reads continuous streams from an AI REST server."
        ),
        CodeTemplate(
            title = "Rust Kernel Ring Buffer",
            desc = "Zero-copy memory safety ring block with static references",
            promptText = "Show me a static Rust heap-less circular ring buffer implementation utilizing pointer offsets."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("coding_assistant_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title banner
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(neonCyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .border(1.dp, neonCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = neonCyan)
            }
            Text(
                text = LanguageHelper.getString("coding_title", language),
                style = MaterialTheme.typography.titleLarge,
                color = textMain,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Select from specialized pre-compiled developer macros to inject diagnostic inquiries directly into Gemini compiler mainframe.",
            style = MaterialTheme.typography.bodyMedium,
            color = textMain.copy(alpha = 0.6f),
            fontSize = 12.sp
        )

        Divider(color = textMain.copy(alpha = 0.12f))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(templatePrompts) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Brush.horizontalGradient(colors = listOf(neonCyan.copy(alpha = 0.3f), Color.Transparent)),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            viewModel.sendMessage(item.promptText, "code")
                            onNavigateToChat()
                            Toast.makeText(context, "Coding Matrix parameters loaded", Toast.LENGTH_SHORT).show()
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkByTheme) Color(0xFF0C0C12).copy(alpha = 0.85f) else Color(0x66ECECEC)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(laserPurple.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.DeveloperMode, contentDescription = null, tint = laserPurple, modifier = Modifier.size(20.dp))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = textMain,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = textMain.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Inject",
                            tint = neonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

data class CodeTemplate(
    val title: String,
    val desc: String,
    val promptText: String
)
