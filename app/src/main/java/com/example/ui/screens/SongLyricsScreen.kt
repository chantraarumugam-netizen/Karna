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
import androidx.compose.material.icons.filled.MusicNote
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
fun SongLyricsScreen(
    viewModel: ChatViewModel,
    onNavigateToChat: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()
    val context = LocalContext.current

    var selectedGenre by remember { mutableStateOf("Synthwave Upbeat") }
    var selectedTopic by remember { mutableStateOf("Silicon Soul") }
    var selectedMood by remember { mutableStateOf("Cosmic Euphoria") }

    val genres = listOf("Synthwave Upbeat", "Future Bass Spark", "Cybermetal Overdrive", "Techno-Pop Digital")
    val topics = listOf("Silicon Soul", "Neon Rain Over Chennai", "Neural Network Chords", "Overclocked Heartbeat")
    val moods = listOf("Cosmic Euphoria", "Melancholic Glow", "Hyper-energetic Pulse")

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("song_lyrics_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Head
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
                Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = neonCyan)
            }
            Text(
                text = LanguageHelper.getString("lyrics_title", language),
                style = MaterialTheme.typography.titleLarge,
                color = textMain,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Generate chart-topping cyber-lyrics. Integrates beautifully with classical Tamil fusion, synthesized in 1K lyrics streams.",
            style = MaterialTheme.typography.bodyMedium,
            color = textMain.copy(alpha = 0.6f),
            fontSize = 12.sp
        )

        Divider(color = textMain.copy(alpha = 0.12f))

        // 1. Selector Beat Style
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Select Musical Genre",
                style = MaterialTheme.typography.labelMedium,
                color = laserPurple,
                fontFamily = FontFamily.Monospace
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(genres) { g ->
                    val isSel = g == selectedGenre
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSel) neonCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(width = 1.dp, color = if (isSel) neonCyan else Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp))
                            .clickable { selectedGenre = g }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(text = g, color = if (isSel) neonCyan else textMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Select Central Topic
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Select Lyric Topic",
                style = MaterialTheme.typography.labelMedium,
                color = laserPurple,
                fontFamily = FontFamily.Monospace
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(topics) { t ->
                    val isSel = t == selectedTopic
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSel) laserPurple.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(width = 1.dp, color = if (isSel) laserPurple else Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp))
                            .clickable { selectedTopic = t }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(text = t, color = if (isSel) laserPurple else textMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Selection Mood
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Select Emotional Mood Level",
                style = MaterialTheme.typography.labelMedium,
                color = laserPurple,
                fontFamily = FontFamily.Monospace
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(moods) { md ->
                    val isSel = md == selectedMood
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSel) neonCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(width = 1.dp, color = if (isSel) neonCyan else Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp))
                            .clickable { selectedMood = md }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(text = md, color = if (isSel) neonCyan else textMain, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Large Song Lyrics Synthesize trigger
        Button(
            onClick = {
                viewModel.generateLyrics(selectedGenre, selectedTopic, selectedMood)
                onNavigateToChat()
                Toast.makeText(context, "Assembling lyrics patterns...", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_lyrics_btn")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(colors = listOf(neonCyan, laserPurple))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SYNTHESIZE MATRIX LYRICS",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            }
        }
    }
}
