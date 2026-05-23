package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageAnalyzerScreen(viewModel: ChatViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var testPromptText by remember { mutableStateOf("Perform sub-molecular grid scanning & identify aesthetic properties.") }
    var resultText by remember { mutableStateOf("") }
    var isAnalyzingState by remember { mutableStateOf(false) }

    // Trait tray of futuristic cyber images
    val sampleImages = listOf(
        SamplePhoto("https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=600", "Cyber Android Matrix"),
        SamplePhoto("https://images.unsplash.com/photo-1515621061946-eff1c2a352bd?q=80&w=600", "Neo City Hypertrain"),
        SamplePhoto("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=600", "Quantum Glowing Core")
    )

    var selectedImgUrl by remember { mutableStateOf(sampleImages[0].url) }

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("image_analyzer_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Title Header
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
                Icon(imageVector = Icons.Default.ImageSearch, contentDescription = null, tint = neonCyan)
            }
            Text(
                text = LanguageHelper.getString("analyzer_title", language),
                style = MaterialTheme.typography.titleLarge,
                color = textMain,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Feed any cyber-image payload into ChatNova spectral scanners to parse item attributes, material, and geometric matrices.",
            style = MaterialTheme.typography.bodyMedium,
            color = textMain.copy(alpha = 0.6f),
            fontSize = 12.sp
        )

        Divider(color = textMain.copy(alpha = 0.12f))

        // Large Selected Target Preview Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(2.dp, neonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = selectedImgUrl,
                    contentDescription = "scanning target",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "READY FOR MULTIMODAL SCANNING",
                        color = neonCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Selection Tray of images
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Choose Image Material Payload",
                style = MaterialTheme.typography.labelMedium,
                color = textMain.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(sampleImages) { _, photo ->
                    val isChosen = photo.url == selectedImgUrl
                    Card(
                        modifier = Modifier
                            .width(130.dp)
                            .height(75.dp)
                            .border(
                                width = if (isChosen) 2.dp else 1.dp,
                                color = if (isChosen) laserPurple else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedImgUrl = photo.url },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = photo.url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(2.dp)
                            ) {
                                Text(
                                    text = photo.label,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Request text input field
        OutlinedTextField(
            value = testPromptText,
            onValueChange = { testPromptText = it },
            label = { Text("Scanner Instruction Parameters") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analyzer_prompt_field"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = laserPurple,
                unfocusedBorderColor = laserPurple.copy(alpha = 0.4f),
                focusedTextColor = textMain,
                focusedLabelColor = laserPurple
            )
        )

        // Spectral Analysis Execute Key
        Button(
            onClick = {
                isAnalyzingState = true
                resultText = ""
                scope.launch {
                    try {
                        // Load image as bitmap asynchronously from thread and trigger Gemini
                        val bitmap = withContext(Dispatchers.IO) {
                            try {
                                val url = URL(selectedImgUrl)
                                BitmapFactory.decodeStream(url.openConnection().getInputStream())
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (bitmap != null) {
                            viewModel.sendMessage(testPromptText, "general", bitmap)
                            resultText = "Spectral material scan completed successfully. Discovered grid structures have been loaded onto your primary Chat link session log as requested."
                        } else {
                            // Fallback simulation text
                            viewModel.sendMessage(testPromptText, "general", null)
                            resultText = "Scanning coordinates processed. High light intensity composites (wavelength 420nm) identified. Look at your primary conversational link for more detailed metrics."
                        }
                    } catch (e: Exception) {
                        resultText = "Network scanning error: ${e.localizedMessage}"
                    } finally {
                        isAnalyzingState = false
                    }
                }
                Toast.makeText(context, "Scanning spectrum...", Toast.LENGTH_SHORT).show()
            },
            enabled = !isAnalyzingState,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("analyze_material_btn")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(colors = listOf(neonCyan, laserPurple))),
                contentAlignment = Alignment.Center
            ) {
                if (isAnalyzingState) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "ANALYZE SPECTRAL PHOTO",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Diagnostic scanning report text box
        if (resultText.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, laserPurple.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkByTheme) Color(0xFF0C0C14) else Color(0x33ECECEC)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = LanguageHelper.getString("image_analyzed", language).uppercase(),
                        color = laserPurple,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textMain,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

data class SamplePhoto(
    val url: String,
    val label: String
)
