package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.db.ChatMessage
import com.example.ui.AppLanguage
import com.example.ui.ChatViewModel
import com.example.ui.LanguageHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    category: String = "general"
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    val language by viewModel.currentLanguage.collectAsState()
    val isDarkByTheme by viewModel.themeMode.collectAsState()
    val messages by viewModel.currentMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val uploadedFile by viewModel.uploadedFile.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val speakingMessageId by viewModel.currentSpeakingMessageId.collectAsState()

    var textInput by remember { mutableStateOf("") }

    // TextToSpeech TTS Initialization
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val neonCyan = if (isDarkByTheme) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (isDarkByTheme) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val textMain = if (isDarkByTheme) Color(0xFFF0F2FA) else Color(0xFF1C1B1F)

    // Scroll to the latest message automatically
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("chat_screen")
    ) {
        // Conversation List
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1.5f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(messages) { _, msg ->
                ChatMessageBubble(
                    msg = msg,
                    isDark = isDarkByTheme,
                    neonCyan = neonCyan,
                    laserPurple = laserPurple,
                    textColor = textMain,
                    isSpeaking = speakingMessageId == msg.id,
                    onVoiceToggle = {
                        if (speakingMessageId == msg.id) {
                            tts?.stop()
                            // Update speaking state
                        } else {
                            tts?.stop()
                            // Clean tags for neat speech synthesis
                            val cleanText = msg.content
                                .replace(Regex("```[a-zA-Z]*\\n[\\s\\S]*?\\n```"), "[Code Segment]")
                                .replace("*", "")
                            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "novaspeech")
                        }
                    },
                    onCopy = {
                        clipboard.setText(AnnotatedString(msg.content))
                        Toast.makeText(context, "Holo-Text Copied to Clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onShare = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, msg.content)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Cyber-Note"))
                    }
                )
            }

            // Real-time typing dot indicators
            if (isGenerating) {
                item {
                    HolographicTypingWave(neonCyan = neonCyan)
                }
            }
        }

        // Upload attachment banner
        AnimatedVisibility(visible = uploadedFile != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(neonCyan.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Attachment, contentDescription = "attachment", tint = neonCyan)
                    Column {
                        Text(
                            text = uploadedFile ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textMain,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Data Matrix Ready for Injection",
                            style = MaterialTheme.typography.bodyMedium,
                            color = neonCyan,
                            fontSize = 9.sp
                        )
                    }
                }
                IconButton(onClick = { viewModel.removeUploadedFile() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "clear attachment", tint = Color.Red)
                }
            }
        }

        // Interactive Input Matrix Controller
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .background(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                // Voice Listening Animation overlay
                if (isListening) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = LanguageHelper.getString("voice_listening", language),
                            color = laserPurple,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        HolographicVoiceLines(laserPurple = laserPurple)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick Attachment Key
                    IconButton(
                        onClick = {
                            val simulations = listOf("cyber_kernel_dump.log", "neural_matrix_manifest.csv", "synth_spectrum_graph.pdf")
                            viewModel.simulateFileUpload(simulations.random())
                            Toast.makeText(context, "Secure file payload injected", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("attach_file_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Attachment,
                            contentDescription = "Attach File",
                            tint = neonCyan
                        )
                    }

                    // Main chat text input
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = {
                            Text(
                                text = LanguageHelper.getString("ask_anything", language),
                                fontSize = 14.sp,
                                color = textMain.copy(alpha = 0.5f)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("prompt_text_field"),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.35f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedContainerColor = Color.White.copy(alpha = 0.12f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                            focusedTextColor = textMain,
                            unfocusedTextColor = textMain
                        )
                    )

                    // Unified audio dictation, long-tapping simulated voice link
                    IconButton(
                        onClick = {
                            if (isListening) {
                                viewModel.setListening(false)
                                textInput = if (language == AppLanguage.TAMIL) {
                                    "மெஷின் லேர்னிங் சிறந்த நுட்பங்கள்"
                                } else {
                                    "Deploy a secure multi-layer neural firewall"
                                }
                            } else {
                                viewModel.setListening(true)
                                scope.launch {
                                    delay(2000)
                                    if (viewModel.isListening.value) {
                                        viewModel.setListening(false)
                                        textInput = if (language == AppLanguage.TAMIL) {
                                            "மெஷின் லேர்னிங் சிறந்த நுட்பங்கள்"
                                        } else {
                                            "Deploy a secure multi-layer neural firewall"
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .background(
                                if (isListening) laserPurple.copy(alpha = 0.25f) else Color.Transparent,
                                CircleShape
                            )
                            .border(1.dp, if (isListening) laserPurple else Color.Transparent, CircleShape)
                            .testTag("voice_dictation_btn")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = "Voice Matrix Input",
                            tint = if (isListening) laserPurple else neonCyan
                        )
                    }

                    // Dynamic Synthesized Action Key
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessage(textInput, category)
                                textInput = ""
                            }
                        },
                        enabled = textInput.isNotBlank(),
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(colors = listOf(neonCyan, laserPurple)),
                                CircleShape
                            )
                            .size(42.dp)
                            .testTag("submit_message_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Submit message",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    msg: ChatMessage,
    isDark: Boolean,
    neonCyan: Color,
    laserPurple: Color,
    textColor: Color,
    isSpeaking: Boolean,
    onVoiceToggle: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val isUser = msg.role == "user"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            // Identity Tag
            Text(
                text = if (isUser) "Operator" else "ChatNova AI",
                style = MaterialTheme.typography.labelMedium,
                color = if (isUser) laserPurple else neonCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 4.dp, start = 8.dp, end = 8.dp)
            )

            // Content Bubble
            Box(
                modifier = Modifier
                    .background(
                        color = if (isUser) laserPurple.copy(alpha = 0.15f) else neonCyan.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = if (isUser) laserPurple.copy(alpha = 0.35f) else neonCyan.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Render image attachment if included
                    if (msg.localImagePath != null) {
                        AsyncImage(
                            model = msg.localImagePath,
                            contentDescription = "hologram graphic",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, neonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Markdown Parser Implementation
                    MarkdownFormattedText(msg.content, textColor = textColor, isDark = isDark)
                }
            }

            // Quick interaction accessories
            if (!isUser) {
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onVoiceToggle, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Read Aloud Voice Button",
                            tint = neonCyan.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy text",
                            tint = textColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    IconButton(onClick = onShare, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share text",
                            tint = textColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    if (isSpeaking) {
                        Spacer(modifier = Modifier.width(4.dp))
                        VocalWavesIndicator(neonCyan = neonCyan)
                    }
                }
            }
        }
    }
}

@Composable
fun MarkdownFormattedText(content: String, textColor: Color, isDark: Boolean) {
    // Basic Markdown Parser (Code snippets, bold parameters)
    val neonCyan = if (isDark) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val segments = content.split("```")

    segments.forEachIndexed { index, seg ->
        if (index % 2 == 1) {
            // Code block segment
            val lines = seg.trim().split("\n")
            val lang = lines.firstOrNull() ?: ""
            val codeBody = if (lines.size > 1) lines.drop(1).joinToString("\n") else seg

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF030304) else Color(0xFFD6D6D6)
                )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    if (lang.isNotBlank() && lang.length < 15) {
                        Text(
                            text = lang.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = neonCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = codeBody,
                        color = if (isDark) Color(0xFF90F7FF) else Color(0xFF004050),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        } else {
            // Standard conversational block (highlighting asterisks dynamically)
            val styledText = buildAnnotatedString {
                var pos = 0
                val pattern = Regex("\\*\\*([\\s\\S]*?)\\*\\*")
                val matches = pattern.findAll(seg)

                for (match in matches) {
                    val start = match.range.first
                    val end = match.range.last + 1
                    val contentText = match.groupValues[1]

                    if (start > pos) {
                        append(seg.substring(pos, start))
                    }
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = neonCyan))
                    append(contentText)
                    pop()
                    pos = end
                }
                if (pos < seg.length) {
                    append(seg.substring(pos))
                }
            }

            Text(
                text = styledText,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun HolographicTypingWave(neonCyan: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotCount = 3
    val dots = List(dotCount) { i ->
        infiniteTransition.animateFloat(
            initialValue = 2f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, delayMillis = i * 200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_$i"
        )
    }

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .background(Color.Transparent)
            .border(1.dp, neonCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Thinking", color = neonCyan.copy(alpha = 0.6f), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        dots.forEach { offset ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(y = (-offset.value).dp)
                    .background(neonCyan, CircleShape)
            )
        }
    }
}

@Composable
fun VocalWavesIndicator(neonCyan: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_indicator")
    val lines = 4
    val heights = List(lines) { i ->
        infiniteTransition.animateFloat(
            initialValue = 2f,
            targetValue = 18f,
            animationSpec = infiniteRepeatable(
                animation = tween((400..800).random(), easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "line_$i"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(20.dp)
    ) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(h.value.dp)
                    .background(neonCyan, CircleShape)
            )
        }
    }
}

@Composable
fun HolographicVoiceLines(laserPurple: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "vocal_waves")
    val count = 8
    val scaling = List(count) { i ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(500 + i * 100, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "wav_$i"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(40.dp)
    ) {
        scaling.forEach { s ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((30 * s.value).dp)
                    .background(laserPurple, RoundedCornerShape(2.dp))
            )
        }
    }
}
