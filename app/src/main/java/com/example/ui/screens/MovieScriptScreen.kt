package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
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
fun MovieScriptScreen(
    viewModel: ChatViewModel,
    onNavigateToChat: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()
    val context = LocalContext.current

    var selectedGenre by remember { mutableStateOf("Sci-Fi Cyberpunk") }
    var selectedHero by remember { mutableStateOf("Neural Renegade Hacker") }
    var selectedLangName by remember { mutableStateOf("Tamil-English (Tanglish)") }

    val genres = listOf("Sci-Fi Cyberpunk", "Crime Cyber-Noir", "Retro Space Opera", "A.I. Rebellion Thriller")
    val archetypes = listOf("Neural Renegade Hacker", "Synthetic Detective", "Cyber Ronin Vigilante", "Holographic Oracle Mind")
    val languagesList = listOf("Tamil-English (Tanglish)", "English Grid", "Classical Tamil Language")

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("movie_script_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Icon and Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(laserPurple.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .border(1.dp, laserPurple.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = laserPurple)
            }
            Text(
                text = LanguageHelper.getString("script_title", language),
                style = MaterialTheme.typography.titleLarge,
                color = textMain,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Generate cinematic Hollywood/Kollywood scripts inside the cosmic chatbot matrix instantly.",
            style = MaterialTheme.typography.bodyMedium,
            color = textMain.copy(alpha = 0.6f),
            fontSize = 12.sp
        )

        Divider(color = textMain.copy(alpha = 0.12f))

        // 1. Selector Genre
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Select Genre",
                style = MaterialTheme.typography.labelMedium,
                color = neonCyan,
                fontFamily = FontFamily.Monospace
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(genres) { valGenre ->
                    val isSel = valGenre == selectedGenre
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSel) laserPurple.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(width = 1.dp, color = if (isSel) laserPurple else Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp))
                            .clickable { selectedGenre = valGenre }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(text = valGenre, color = if (isSel) laserPurple else textMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Character Archetype
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Select Hero Archetype",
                style = MaterialTheme.typography.labelMedium,
                color = neonCyan,
                fontFamily = FontFamily.Monospace
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(archetypes) { valHero ->
                    val isSel = valHero == selectedHero
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSel) neonCyan.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(width = 1.dp, color = if (isSel) neonCyan else Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp))
                            .clickable { selectedHero = valHero }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(text = valHero, color = if (isSel) neonCyan else textMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Narrative Screenplay Language
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Grid Dialogue Language",
                style = MaterialTheme.typography.labelMedium,
                color = neonCyan,
                fontFamily = FontFamily.Monospace
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(languagesList) { valLang ->
                    val isSel = valLang == selectedLangName
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSel) laserPurple.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(width = 1.dp, color = if (isSel) laserPurple else Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp))
                            .clickable { selectedLangName = valLang }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(text = valLang, color = if (isSel) laserPurple else textMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Large Screenplay Generate trigger
        Button(
            onClick = {
                viewModel.generateScreenplay(selectedGenre, selectedHero, selectedLangName)
                onNavigateToChat()
                Toast.makeText(context, "Contacting Script Decryptor core...", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_script_btn")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(colors = listOf(laserPurple, neonCyan))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AUTO SCREENPLAY GENERATION",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            }
        }
    }
}
