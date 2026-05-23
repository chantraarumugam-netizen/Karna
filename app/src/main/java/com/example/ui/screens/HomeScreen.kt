package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.ChatViewModel
import com.example.ui.LanguageHelper
import kotlinx.coroutines.delay

class Particle(
    var x: Float,
    var y: Float,
    val radius: Float,
    val dx: Float,
    val dy: Float
)

@Composable
fun FloatingParticlesCanvas(color: Color) {
    val particles = remember {
        List(25) {
            Particle(
                x = (5..95).random().toFloat() / 100f,
                y = (5..95).random().toFloat() / 100f,
                radius = (3..10).random().toFloat(),
                dx = ((-5..5).random().toFloat() / 4000f),
                dy = ((-5..5).random().toFloat() / 4000f)
            )
        }
    }

    var tick by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(32)
            particles.forEach { p ->
                p.x += p.dx
                p.y += p.dy
                if (p.x < 0f || p.x > 1f) p.x = (p.x + 1f) % 1f
                if (p.y < 0f || p.y > 1f) p.y = (p.y + 1f) % 1f
            }
            tick++
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            drawCircle(
                color = color.copy(alpha = 0.2f),
                radius = p.radius,
                center = Offset(p.x * size.width, p.y * size.height)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ChatViewModel,
    onNavigateToCategory: (String) -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()
    val focusManager = LocalFocusManager.current

    var searchInput by remember { mutableStateOf("") }

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    // Breathing motion scale for Logo Glow Accent
    val infiniteTransition = rememberInfiniteTransition(label = "HoloLogo")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    val trendingPrompts = remember(language) {
        if (language == AppLanguage.TAMIL) {
            listOf(
                "நாளை சென்னை காலநிலை எப்படி?",
                "AI தொழில்நுட்பத்தின் எதிர்காலக் கட்டுரை",
                "அறிவியல் கவிதை ஒன்றை உருவாக்கு",
                "ஆண்ட்ராய்டு கம்போஸ் நியாண் வழிகாட்டி"
            )
        } else {
            listOf(
                "Is artificial general intelligence near?",
                "Draft a cosmic tech screenplay",
                "Write deep synthwave chords instructions",
                "Explain quantum compilers to me"
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        // Starfield Particles Backdrop
        FloatingParticlesCanvas(color = neonCyan)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // BIGTO AI Large Pulsing Hologram Emblem
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(pulseScale)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(laserPurple.copy(alpha = 0.25f), Color.Transparent)
                            ),
                            radius = size.minDimension * 0.9f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Layered glossy neon rings
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(colors = listOf(neonCyan, laserPurple)),
                            shape = CircleShape
                        )
                        .background(
                            color = if (isDarkByTheme) Color(0xFF090911).copy(alpha = 0.8f) else Color.White,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Holo Mind Logo",
                        tint = neonCyan,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // App Name & Tagline Heading
            Text(
                text = LanguageHelper.getString("app_name", language),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp
                ),
                color = textMain
            )
            Text(
                text = "“" + LanguageHelper.getString("tagline", language) + "”",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                ),
                color = laserPurple,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Ask Anything input box
            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                placeholder = {
                    Text(
                        LanguageHelper.getString("ask_anything", language),
                        color = textMain.copy(alpha = 0.4f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ask_anything_box"),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (searchInput.isNotBlank()) {
                                viewModel.sendMessage(searchInput, "general")
                                searchInput = ""
                                onNavigateToCategory("general")
                            }
                        },
                        modifier = Modifier.testTag("send_prompt_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = neonCyan
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (searchInput.isNotBlank()) {
                        viewModel.sendMessage(searchInput, "general")
                        searchInput = ""
                        focusManager.clearFocus()
                        onNavigateToCategory("general")
                    }
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.35f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedContainerColor = Color.White.copy(alpha = 0.12f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedTextColor = textMain,
                    unfocusedTextColor = textMain
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Carousel: Trending Prompts
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = LanguageHelper.getString("trending", language),
                    style = MaterialTheme.typography.labelMedium,
                    color = textMain.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(end = 12.dp)
                ) {
                    items(trendingPrompts) { tag ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isDarkByTheme) Color(0xFF141421).copy(alpha = 0.8f) else Color(0xFFECECEC),
                                    shape = RoundedCornerShape(10.dp)
                                        )
                                .border(
                                    width = 1.dp,
                                    color = neonCyan.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.sendMessage(tag, "general")
                                    onNavigateToCategory("general")
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "trend icon",
                                    tint = laserPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = textMain,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Access Card Grid Selector
            Text(
                text = LanguageHelper.getString("quick_access", language),
                style = MaterialTheme.typography.labelMedium,
                color = textMain.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAccessCard(
                        title = LanguageHelper.getString("gen_image", language),
                        icon = Icons.Default.PlayArrow,
                        color = neonCyan,
                        desc = "Image Matrix Synthesizer",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCategory("image") }
                    )
                    QuickAccessCard(
                        title = LanguageHelper.getString("create_song", language),
                        icon = Icons.Default.MusicNote,
                        color = laserPurple,
                        desc = "AI Lyrical Composer",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCategory("lyrics") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAccessCard(
                        title = LanguageHelper.getString("write_script", language),
                        icon = Icons.Default.List,
                        color = laserPurple,
                        desc = "Cinema Decryptor Module",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCategory("script") }
                    )
                    QuickAccessCard(
                        title = LanguageHelper.getString("ai_coding", language),
                        icon = Icons.Default.Code,
                        color = neonCyan,
                        desc = "Diagnostic Compiler Stack",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCategory("code") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAccessCard(
                        title = LanguageHelper.getString("voice_chat", language),
                        icon = Icons.Default.Phone,
                        color = laserPurple,
                        desc = "Neural Vocal Interface",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCategory("voice_chat") }
                    )
                    QuickAccessCard(
                        title = LanguageHelper.getString("analyzer_title", language),
                        icon = Icons.Default.ThumbUp,
                        color = neonCyan,
                        desc = "Quantum Image Scanning",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCategory("analyzer") }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickAccessCard(
    title: String,
    icon: ImageVector,
    color: Color,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassBg = Color.White.copy(alpha = 0.07f)
    val glassBorder = Color.White.copy(alpha = 0.15f)
    Card(
        modifier = modifier
            .height(85.dp)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(glassBorder, glassBorder.copy(alpha = 0.02f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = glassBg
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, color.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TechWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CyberGray,
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }
        }
    }
}
