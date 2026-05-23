package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import android.widget.Toast
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.ui.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Construct viewModel cleanly via ViewModelProvider
        val viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        setContent {
            val isDarkTheme by viewModel.themeMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppMainLayout(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainLayout(viewModel: ChatViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentLang by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val username by viewModel.userProfileName.collectAsState()
    val activeAvatarIdx by viewModel.userAvatarIndex.collectAsState()

    var activeScreenRoute by remember { mutableStateOf("home") }

    val neonCyan = if (themeMode) Color(0xFF00E5FF) else Color(0xFF0057B7)
    val laserPurple = if (themeMode) Color(0xFFBF5AF2) else Color(0xFFFF5722)
    val cardBg = if (themeMode) Color(0xFF0B0B11) else Color(0xFFECECEC)
    val headerTextColor = if (themeMode) Color.White else Color.Black

    val avatarsEmojis = listOf("🤖", "🛸", "👾", "⚡", "💠")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = if (themeMode) Color(0xBE07070E) else Color(0xBEF3F2F0),
                drawerContentColor = contentColorFor(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Brand Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(bottom = 20.dp, top = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        Brush.linearGradient(colors = listOf(neonCyan, laserPurple)),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(avatarsEmojis.getOrElse(activeAvatarIdx) { "🤖" }, fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    text = LanguageHelper.getString("app_name", currentLang),
                                    color = headerTextColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Active ID: $username",
                                    color = neonCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Divider(color = neonCyan.copy(alpha = 0.2f), modifier = Modifier.padding(bottom = 16.dp))

                        // Sidebar link categories
                        SidebarLink(
                            title = "Home Matrix",
                            icon = Icons.Default.Home,
                            color = neonCyan,
                            isSelected = activeScreenRoute == "home",
                            onClick = {
                                activeScreenRoute = "home"
                                scope.launch { drawerState.close() }
                            }
                        )

                        SidebarLink(
                            title = "AI ChatNova link",
                            icon = Icons.Default.Chat,
                            color = laserPurple,
                            isSelected = activeScreenRoute == "general",
                            onClick = {
                                activeScreenRoute = "general"
                                viewModel.selectSession(null) // connect new draft session
                                scope.launch { drawerState.close() }
                            }
                        )

                        SidebarLink(
                            title = "Image Synthesizer",
                            icon = Icons.Default.Image,
                            color = neonCyan,
                            isSelected = activeScreenRoute == "image",
                            onClick = {
                                activeScreenRoute = "image"
                                scope.launch { drawerState.close() }
                            }
                        )

                        SidebarLink(
                            title = "Cinema Screenplay",
                            icon = Icons.Default.Movie,
                            color = laserPurple,
                            isSelected = activeScreenRoute == "script",
                            onClick = {
                                activeScreenRoute = "script"
                                scope.launch { drawerState.close() }
                            }
                        )

                        SidebarLink(
                            title = "Lyrical Synthesizer",
                            icon = Icons.Default.MusicNote,
                            color = neonCyan,
                            isSelected = activeScreenRoute == "lyrics",
                            onClick = {
                                activeScreenRoute = "lyrics"
                                scope.launch { drawerState.close() }
                            }
                        )

                        SidebarLink(
                            title = "Coding Chamber",
                            icon = Icons.Default.Code,
                            color = laserPurple,
                            isSelected = activeScreenRoute == "code",
                            onClick = {
                                activeScreenRoute = "code"
                                scope.launch { drawerState.close() }
                            }
                        )

                        SidebarLink(
                            title = "Vocal Interface",
                            icon = Icons.Default.Mic,
                            color = neonCyan,
                            isSelected = activeScreenRoute == "voice_chat",
                            onClick = {
                                activeScreenRoute = "voice_chat"
                                scope.launch { drawerState.close() }
                            }
                        )

                        SidebarLink(
                            title = "Spectral Analyzer",
                            icon = Icons.Default.ImageSearch,
                            color = laserPurple,
                            isSelected = activeScreenRoute == "analyzer",
                            onClick = {
                                activeScreenRoute = "analyzer"
                                scope.launch { drawerState.close() }
                            }
                        )

                        SidebarLink(
                            title = "Holo-Registry Settings",
                            icon = Icons.Default.Settings,
                            color = neonCyan,
                            isSelected = activeScreenRoute == "profile",
                            onClick = {
                                activeScreenRoute = "profile"
                                scope.launch { drawerState.close() }
                            }
                        )
                    }

                    // Lower logout key / info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BIGTO AI v1.0.4",
                            color = neonCyan.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        IconButton(
                            onClick = {
                                viewModel.toggleTheme()
                                Toast.makeText(context, "Retro Grid Toggled", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = if (themeMode) Icons.Default.Brightness5 else Icons.Default.Brightness2,
                                contentDescription = "toggle theme tint",
                                tint = laserPurple
                            )
                        }
                    }
                }
            }
        }
    ) {
        // App Core Layout
        FrostedGlassEffectBackground(
            themeMode = themeMode,
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (activeScreenRoute) {
                                "home" -> LanguageHelper.getString("app_name", currentLang)
                                "general" -> "CHATNOVA MATRIX"
                                "image" -> "IMAGE CORE"
                                "script" -> "SCRIPT CORE"
                                "lyrics" -> "LYRICS CORE"
                                "code" -> "CODING CORE"
                                "voice_chat" -> "VOCAL PORTAL"
                                "analyzer" -> "SPECTRAL ANALYZER"
                                else -> "REGISTRY OPTIONS"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = headerTextColor,
                            fontSize = 17.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu drawer",
                                tint = neonCyan
                            )
                        }
                    },
                    actions = {
                        // Quick dynamic English <-> Tamil toggle key
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .background(laserPurple.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, laserPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    val nextLang = if (currentLang == AppLanguage.ENGLISH) AppLanguage.TAMIL else AppLanguage.ENGLISH
                                    viewModel.setLanguage(nextLang)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (currentLang == AppLanguage.ENGLISH) "En 🌐 Ta" else "Ta 🌐 En",
                                color = laserPurple,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = if (themeMode) Color(0x33020205) else Color(0x33F3F4F6)
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Route mapping navigation switcher
                when (activeScreenRoute) {
                    "home" -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToCategory = { route ->
                                activeScreenRoute = route
                            }
                        )
                    }
                    "general" -> {
                        ChatScreen(
                            viewModel = viewModel,
                            category = "general"
                        )
                    }
                    "image" -> {
                        ImageGeneratorScreen(viewModel = viewModel)
                    }
                    "script" -> {
                        MovieScriptScreen(
                            viewModel = viewModel,
                            onNavigateToChat = {
                                activeScreenRoute = "general"
                            }
                        )
                    }
                    "lyrics" -> {
                        SongLyricsScreen(
                            viewModel = viewModel,
                            onNavigateToChat = {
                                activeScreenRoute = "general"
                            }
                        )
                    }
                    "code" -> {
                        CodingAssistantScreen(
                            viewModel = viewModel,
                            onNavigateToChat = {
                                activeScreenRoute = "general"
                            }
                        )
                    }
                    "voice_chat" -> {
                        VoiceChatScreen(viewModel = viewModel)
                    }
                    "analyzer" -> {
                        ImageAnalyzerScreen(viewModel = viewModel)
                    }
                    "profile" -> {
                        ProfileCustomizerScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
}

@Composable
fun SidebarLink(
    title: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = if (isSelected) color.copy(alpha = 0.12f) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) color.copy(alpha = 0.3f) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) color else Color.Gray,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun FrostedGlassEffectBackground(
    themeMode: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (themeMode) Color(0xFF020205) else Color(0xFFF3F4F6))
            .drawBehind {
                if (themeMode) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x2E8B5CF6), Color.Transparent),
                            center = Offset(size.width * -0.1f, size.height * -0.1f),
                            radius = size.maxDimension * 0.7f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x241E40AF), Color.Transparent),
                            center = Offset(size.width * 1.1f, size.height * 1.1f),
                            radius = size.maxDimension * 0.7f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x1F06B6D4), Color.Transparent),
                            center = Offset(size.width * 0.15f, size.height * 0.35f),
                            radius = size.maxDimension * 0.45f
                        )
                    )
                } else {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x1FEC4899), Color.Transparent),
                            center = Offset(size.width * 0.1f, size.height * 0.2f),
                            radius = size.maxDimension * 0.6f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x153B82F6), Color.Transparent),
                            center = Offset(size.width * 0.9f, size.height * 0.8f),
                            radius = size.maxDimension * 0.6f
                        )
                    )
                }
            }
    ) {
        content()
    }
}

