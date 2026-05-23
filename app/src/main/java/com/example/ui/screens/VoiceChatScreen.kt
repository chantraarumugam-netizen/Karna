package com.example.ui.screens

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.ChatViewModel
import com.example.ui.LanguageHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceChatScreen(viewModel: ChatViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isVoiceReplyState by viewModel.isVoiceReplyEnabled.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var userTranscript by remember { mutableStateOf("") }
    var aiReplyTranscript by remember { mutableStateOf("") }
    var voiceModeText by remember { mutableStateOf("Mainframe Idle") }

    // TextToSpeech TTS Initialization locally
    var ttsService: TextToSpeech? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        ttsService = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsService?.language = Locale.US
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsService?.stop()
            ttsService?.shutdown()
        }
    }

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    // Breathing motion scale for Voice Orb
    val infiniteTransition = rememberInfiniteTransition(label = "VoiceOrbPulse")
    val pulseScaling by infiniteTransition.animateFloat(
        initialValue = if (isListening) 0.95f else 0.98f,
        targetValue = if (isListening) 1.25f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 800 else 2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseVec"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("voice_chat_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Title Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Phone, contentDescription = "voice icon", tint = laserPurple)
            Text(
                text = LanguageHelper.getString("voice_chat", language) + " Portal",
                style = MaterialTheme.typography.titleLarge,
                color = textMain,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Main Holographic Pulsing Vocal Circle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(pulseScaling)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (isListening) laserPurple.copy(alpha = 0.35f) else neonCyan.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(neonCyan, laserPurple, neonCyan)
                        ),
                        shape = CircleShape
                    )
                    .clickable {
                        if (isListening) {
                            viewModel.setListening(false)
                            voiceModeText = "Processing Speech..."
                        } else {
                            ttsService?.stop()
                            viewModel.setListening(true)
                            voiceModeText = "Listening with neural audio grid..."
                            aiReplyTranscript = ""
                            userTranscript = ""
                            scope.launch {
                                delay(3000)
                                if (viewModel.isListening.value) {
                                    viewModel.setListening(false)
                                    voiceModeText = "Transcribing neural signals..."
                                    userTranscript = if (language == AppLanguage.TAMIL) {
                                        "ஹலோ சேட்நோவா, எனது இன்றைய தினத்தை உற்சாகப்படுத்து!"
                                    } else {
                                        "Hello ChatNova, authorize my voice links and share main status."
                                    }

                                    delay(1000)
                                    voiceModeText = "Generating vocal synthesis..."
                                    aiReplyTranscript = if (language == AppLanguage.TAMIL) {
                                        "இணைப்பு வெற்றிகரமாக உகந்தாக்கப்பட்டது. எதிர்கால உலகம் உங்களுடையது, ஆப்பரேட்டர்! உங்களின் ஆற்றல் மிகுந்த குரலை நான் ஏற்றுக்கொண்டேன்."
                                    } else {
                                        "Voice authority confirmed. Deep neural matrix synchronization is 100% complete. Ready at your direct instruction."
                                    }
                                    voiceModeText = "Speaking..."
                                    if (isVoiceReplyState) {
                                        ttsService?.speak(aiReplyTranscript, TextToSpeech.QUEUE_FLUSH, null, "voiceportal")
                                    }
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            color = if (isDarkByTheme) Color(0xFF09090E) else Color(0xFFECECEC),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = if (isListening) laserPurple else neonCyan.copy(alpha = 0.4f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                        contentDescription = "Vocal Trigger key",
                        tint = if (isListening) laserPurple else neonCyan,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = voiceModeText.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = if (isListening) laserPurple else neonCyan,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }

        // Subtitle Transcripts Box Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .border(1.dp, neonCyan.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkByTheme) Color(0xFF0F0F16).copy(alpha = 0.7f) else Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // User voice transcripts
                if (userTranscript.isNotBlank()) {
                    Text(
                        text = "You: $userTranscript",
                        color = textMain.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                }

                // AI Response transcripts
                if (aiReplyTranscript.isNotBlank()) {
                    Text(
                        text = "ChatNova: $aiReplyTranscript",
                        color = neonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 3
                    )
                }

                if (userTranscript.isBlank() && aiReplyTranscript.isBlank()) {
                    Text(
                        text = "System: Tap the large holographic sphere above and speak in English or Tamil. Dynamic vocal transcripts emit right here.",
                        color = textMain.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Toggle speaker settings button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "voice toggler", tint = laserPurple, modifier = Modifier.size(18.dp))
                Text(
                    text = "Vocal Playback Response",
                    color = textMain,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Switch(
                checked = isVoiceReplyState,
                onCheckedChange = { viewModel.toggleVoiceReply() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = neonCyan,
                    checkedTrackColor = neonCyan.copy(alpha = 0.4f)
                ),
                modifier = Modifier.testTag("voice_reply_toggle")
            )
        }
    }
}
