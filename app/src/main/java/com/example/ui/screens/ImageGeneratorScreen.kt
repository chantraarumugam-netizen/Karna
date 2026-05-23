package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.ChatViewModel
import com.example.ui.LanguageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGeneratorScreen(viewModel: ChatViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()
    val generatedImages by viewModel.generatedImages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val context = LocalContext.current

    var testPrompt by remember { mutableStateOf("") }
    var selectedRatio by remember { mutableStateOf("9:16") }

    val ratiosList = listOf("1:1", "9:16", "16:9", "4:3")

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("image_generator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Title
        Text(
            text = LanguageHelper.getString("image_gen_title", language),
            style = MaterialTheme.typography.titleLarge,
            color = neonCyan,
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp
        )

        // Prompt compose input box
        OutlinedTextField(
            value = testPrompt,
            onValueChange = { testPrompt = it },
            placeholder = {
                Text(
                    text = LanguageHelper.getString("image_prompt_hint", language),
                    fontSize = 13.sp,
                    color = textMain.copy(alpha = 0.5f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("img_prompt_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White.copy(alpha = 0.35f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedContainerColor = Color.White.copy(alpha = 0.12f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedTextColor = textMain,
                unfocusedTextColor = textMain
            )
        )

        // Ratio Matrix selectors
        Column {
            Text(
                text = LanguageHelper.getString("ratio", language),
                style = MaterialTheme.typography.labelMedium,
                color = textMain.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ratiosList.forEach { ratio ->
                    val isActive = selectedRatio == ratio
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isActive) neonCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isActive) neonCyan else Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedRatio = ratio }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ratio,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) neonCyan else textMain.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Synthesize button with glowing gradient
        Button(
            onClick = {
                if (testPrompt.isNotBlank()) {
                    viewModel.generateAIImage(testPrompt, selectedRatio)
                    testPrompt = ""
                    Toast.makeText(context, "Contacting synthesize matrix...", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = testPrompt.isNotBlank() && !isGenerating,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("synthesize_btn")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (testPrompt.isNotBlank()) {
                            Brush.horizontalGradient(colors = listOf(neonCyan, laserPurple))
                        } else {
                            Brush.horizontalGradient(colors = listOf(Color.Gray, Color.DarkGray))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = LanguageHelper.getString("synth_btn", language),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Divider(color = textMain.copy(alpha = 0.15f), thickness = 1.dp)

        // Gallery of previous generations
        Text(
            text = "Holo-synthesized Gallery Overview",
            style = MaterialTheme.typography.labelMedium,
            color = textMain.copy(alpha = 0.6f),
            fontFamily = FontFamily.Monospace
        )

        if (generatedImages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = "empty", tint = textMain.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                    Text("No synthetic projections generated yet in session", color = textMain.copy(alpha = 0.4f), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(generatedImages) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .border(1.dp, neonCyan.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.prompt,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Prompt overlay card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = item.prompt,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Scale ${item.ratio}",
                                        color = neonCyan,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
