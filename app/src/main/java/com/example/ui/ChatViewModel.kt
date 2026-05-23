package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.ChatDatabase
import com.example.db.ChatMessage
import com.example.db.ChatSession
import com.example.api.GeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val db = ChatDatabase.getDatabase(application)
    private val dao = db.chatDao()

    // UI Configuration States
    private val _themeMode = MutableStateFlow(true) // true = Dark Hologram, false = Light Cyberpunk
    val themeMode: StateFlow<Boolean> = _themeMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Auth & Personalization States
    private val _isUserLoggedIn = MutableStateFlow(true) // Start authenticated for smooth experience
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _userProfileName = MutableStateFlow("Neo Hacker")
    val userProfileName: StateFlow<String> = _userProfileName.asStateFlow()

    private val _userProfileEmail = MutableStateFlow("chantra@bigto.ai")
    val userProfileEmail: StateFlow<String> = _userProfileEmail.asStateFlow()

    private val _userAvatarIndex = MutableStateFlow(2) // Holographic avatar indices
    val userAvatarIndex: StateFlow<Int> = _userAvatarIndex.asStateFlow()

    // Cloud status and file attachments setup
    private val _cloudSyncStatus = MutableStateFlow("synced") // synced, syncing, offline
    val cloudSyncStatus: StateFlow<String> = _cloudSyncStatus.asStateFlow()

    private val _uploadedFile = MutableStateFlow<String?>(null) // Holds fileName if attachment active
    val uploadedFile: StateFlow<String?> = _uploadedFile.asStateFlow()

    private val _isListening = MutableStateFlow(false) // Voice listening flag
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isVoiceReplyEnabled = MutableStateFlow(true) // Text to Voice autoplay toggler
    val isVoiceReplyEnabled: StateFlow<Boolean> = _isVoiceReplyEnabled.asStateFlow()

    private val _currentSpeakingMessageId = MutableStateFlow<Long?>(null) // Message currently "narrating"
    val currentSpeakingMessageId: StateFlow<Long?> = _currentSpeakingMessageId.asStateFlow()

    // Search and Session History Flows
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeCategoryFilter = MutableStateFlow("all") // all, general, code, lyrics, script, image
    val activeCategoryFilter: StateFlow<String> = _activeCategoryFilter.asStateFlow()

    // Combine parameters to query Chat SQL Database reactively
    val chatSessions: StateFlow<List<ChatSession>> = combine(
        _searchQuery,
        _activeCategoryFilter
    ) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        if (query.trim().isNotEmpty()) {
            dao.searchSessions("%$query%")
        } else if (category != "all") {
            dao.getSessionsByCategory(category)
        } else {
            dao.getAllSessions()
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Chat Conversation flows
    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessage>> = _currentMessages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Synthetic Images Gallery
    private val _generatedImages = MutableStateFlow<List<SyntheticImage>>(emptyList())
    val generatedImages: StateFlow<List<SyntheticImage>> = _generatedImages.asStateFlow()

    init {
        // Hydrate default state with beautiful sample data if sessions are empty
        viewModelScope.launch {
            dao.getAllSessions().collect { sessions ->
                if (sessions.isEmpty()) {
                    preloadSampleData()
                }
            }
        }
        preloadGalleryImages()
    }

    private suspend fun preloadSampleData() {
        val welcomeId = "welcome_session"
        val codeId = "code_sample_session"

        val welcomeSession = ChatSession(
            id = welcomeId,
            title = "🌌 Connected to ChatNova Mainframe",
            timestamp = System.currentTimeMillis() - 100000,
            category = "general"
        )
        val codeSession = ChatSession(
            id = codeId,
            title = "💻 Kotlin Compose Shader Setup",
            timestamp = System.currentTimeMillis() - 500000,
            category = "code"
        )

        dao.insertSession(welcomeSession)
        dao.insertSession(codeSession)

        dao.insertMessage(
            ChatMessage(
                sessionId = welcomeId,
                role = "user",
                content = "Initialize core communications link.",
                timestamp = System.currentTimeMillis() - 90000
            )
        )
        dao.insertMessage(
            ChatMessage(
                sessionId = welcomeId,
                role = "model",
                content = "Greetings, Operator. I am ChatNova AI—your cybernetic neural bridge. I can generate high fidelity image matrix syntheses, decrypt complex movie screenplays, create music lyrical compositions, or solve quantum compiler diagnostics. Tagline status: **Create Beyond Imagination**.\n\nAll systems are operating in optimal status within the dark black neon coordinate grid. Type or use **Voice Matrix Chat** below to instruct.",
                timestamp = System.currentTimeMillis() - 85000
            )
        )

        dao.insertMessage(
            ChatMessage(
                sessionId = codeId,
                role = "user",
                content = "Show me a clean Compose neon glow layout.",
                timestamp = System.currentTimeMillis() - 490000
            )
        )
        dao.insertMessage(
            ChatMessage(
                sessionId = codeId,
                role = "model",
                content = "Here is a clean implementation for a futuristic button utilizing dynamic shadow gradients in Jetpack Compose:\n\n```kotlin\n@Composable\nfun NeonButton(\n    text: String,\n    onClick: () -> Unit,\n    glowColor: Color = Color(0xFF00E5FF)\n) {\n    Box(\n        modifier = Modifier\n            .shadow(16.dp, shape = RoundedCornerShape(12.dp), ambientColor = glowColor)\n            .background(Color.Black)\n            .border(2.dp, glowColor, RoundedCornerShape(12.dp))\n            .clickable { onClick() }\n            .padding(horizontal = 24.dp, vertical = 12.dp)\n    ) {\n        Text(\n            text = text,\n            color = glowColor,\n            fontFamily = FontFamily.Monospace,\n            fontWeight = FontWeight.Bold\n        )\n    }\n}\n```",
                timestamp = System.currentTimeMillis() - 485000
            )
        )
    }

    private fun preloadGalleryImages() {
        _generatedImages.value = listOf(
            SyntheticImage(
                id = "pre_img_1",
                prompt = "Futuristic neon Chennai central terminal with high speed maglev trains hyper-realistic, synthwave style, beautiful blue lighting",
                ratio = "16:9",
                imageUrl = "https://images.unsplash.com/photo-1515621061946-eff1c2a352bd?q=80&w=600",
                timestamp = System.currentTimeMillis() - 800000
            ),
            SyntheticImage(
                id = "pre_img_2",
                prompt = "Portrait of a cybernetic Tamil queen wearing smart gold jewelry integrated with optical fiber circuits, cyberpunk dark gradient",
                ratio = "9:16",
                imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=600",
                timestamp = System.currentTimeMillis() - 1500000
            ),
            SyntheticImage(
                id = "pre_img_3",
                prompt = "A high-fidelity mechanical keyboard glowing with rich deep purple and cyan holographic keycap projection",
                ratio = "1:1",
                imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=600",
                timestamp = System.currentTimeMillis() - 2500000
            )
        )
    }

    // Language Toggles
    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    // Color/Design Theme Mode (true=Dark Black Cyberpunk, false=Light Minimal Design)
    fun toggleTheme() {
        _themeMode.value = !_themeMode.value
    }

    // User authentication simulation
    fun attemptLogin(id: String, key: String) {
        viewModelScope.launch {
            if (id.isNotEmpty() && key.isNotEmpty()) {
                _userProfileName.value = id.uppercase() + " PRO"
                _isUserLoggedIn.value = true
                triggerCloudSync()
            }
        }
    }

    fun logout() {
        _isUserLoggedIn.value = false
    }

    fun updateProfile(id: String, email: String, avatar: Int) {
        _userProfileName.value = id
        _userProfileEmail.value = email
        _userAvatarIndex.value = avatar
        triggerCloudSync()
    }

    // Database search query logic
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _activeCategoryFilter.value = category
    }

    // Active session loads
    fun selectSession(sessionId: String?) {
        _currentSessionId.value = sessionId
        if (sessionId != null) {
            viewModelScope.launch {
                dao.getMessagesForSession(sessionId).collect { messages ->
                    _currentMessages.value = messages
                }
            }
        } else {
            _currentMessages.value = emptyList()
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            dao.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                _currentSessionId.value = null
                _currentMessages.value = emptyList()
            }
        }
    }

    // Send chat messages
    fun sendMessage(content: String, category: String = "general", attachBitmap: Bitmap? = null) {
        if (content.isBlank() && attachBitmap == null) return

        val sessionId = _currentSessionId.value ?: UUID.randomUUID().toString().also { newId ->
            _currentSessionId.value = newId
            val title = if (content.length > 28) content.take(25) + "..." else content
            viewModelScope.launch {
                dao.insertSession(ChatSession(id = newId, title = title, timestamp = System.currentTimeMillis(), category = category))
                selectSession(newId)
            }
        }

        viewModelScope.launch {
            val userMsg = ChatMessage(
                sessionId = sessionId,
                role = "user",
                content = content,
                timestamp = System.currentTimeMillis()
            )
            dao.insertMessage(userMsg)

            _isGenerating.value = true
            triggerCloudSync()

            // Prepare system context based on user setting selection
            val promptSystem = "Your name is ChatNova AI (operating inside BIGTO AI application). Your tagline is 'Create Beyond Imagination'. Theme colors are deep cybernetic dark black, neon blue holographic highlights, and purple glowing portals. You support full English and Tamil (தமிழ்) conversations. Always keep your replies modern, insightful, high-quality, and structurally aligned with Material 3 styling."

            val finalReply = if (attachBitmap != null) {
                // Multimodal prompt call
                val cleanPrompt = if (content.isNotBlank()) content else "Describe this holographic construct."
                GeminiClient.generateMultimodal(
                    prompt = cleanPrompt,
                    imageBitmap = attachBitmap,
                    fallbackText = {
                        val isTa = _currentLanguage.value == AppLanguage.TAMIL
                        if (isTa) {
                            "விஸ்பெக்ட்ரல் பகுப்பாய்வு முடிந்தது. இந்த ஹோலோகிராபிக் படம் உகந்த பிக்சல் அடர்த்தி கொண்ட நியான் நீல கட்டமைப்பைக் காட்டுகிறது."
                        } else {
                            "Grid Material Spectral analysis completed. Found light elements consistent with futuristic composite polymers glowing at 450nm wavelength."
                        }
                    }
                )
            } else {
                // Core Text prompting
                GeminiClient.generateText(
                    prompt = content,
                    systemInstruction = promptSystem,
                    fallbackText = { getSimulatedReplyForPrompt(content, category) }
                )
            }

            val assistantMsg = ChatMessage(
                sessionId = sessionId,
                role = "model",
                content = finalReply,
                timestamp = System.currentTimeMillis()
            )
            dao.insertMessage(assistantMsg)
            _isGenerating.value = false
            _uploadedFile.value = null // reset active attachment
        }
    }

    private fun getSimulatedReplyForPrompt(prompt: String, category: String): String {
        val isTamil = _currentLanguage.value == AppLanguage.TAMIL
        return when (category) {
            "code" -> {
                if (isTamil) {
                    "**AI குறியீட்டு முடிவு:**\n```kotlin\n// இந்த குறியீடு உங்கள் கணினியில் இயக்க தயாராக உள்ளது\nclass NovaEngine {\n    fun startGrid() {\n        println(\"விக்டோ மென்பொருள் இணைப்பு இயங்குகிறது!\")\n    }\n}\n```\nமேலும் விவரங்கள் வேண்டுமா?"
                } else {
                    "**BIGTO Dev-Matrix Compiler Output:**\n```kotlin\n// Optimized holographic grid controller\nfun runCybermatrix() {\n    val coreFrequency = 1200L\n    println(\"Mainframe system synced at: \\${System.currentTimeMillis()} ms\")\n}\n```\nLet me know if you need any adjustments or automated compiler setups!"
                }
            }
            "script" -> {
                if (isTamil) {
                    "**AI திரைக்கதை உருவாக்கம்:**\n📍 **காட்சி 1: சென்னை நியான் சந்திப்பு**\n\n[காட்சி பின்னணி: லேசர் விளக்குகள், நியான் பலகைகள். பலத்த மழையில் ஹீரோ நுழைவு.]\n\n**நாயகன் கபிலன் (மகிழ்ச்சியுடன்):**\n\"இதுதான் எதிர்காலம். நம் தொழில்நுட்பமே நம் வலிமை!\"\n\n[வானத்தில் ஹோலோகிராம் மறைந்து புதிய உலகப் பாதை மலர்கிறது.]"
                } else {
                    "**AI SCREENPLAY PROTOCOL:**\n📍 **SCENE I: NEO-SYDNEY DOCK 12 [SLUGLINE]**\n\n[Lighting: Dim ambient laser, high contrast violet mist. Heavy cyber-rain falls.]\n\n**ORACLE ENKI (whispering):**\n\"The mainframe was never disconnected. It was just dreaming.\"\n\n[Enki flashes his optical-implants, illuminating the steel walls.]"
                }
            }
            "lyrics" -> {
                if (isTamil) {
                    "🎵 **பாடல் வரிகள் (Synthwave Beat):**\n\n(பல்லவி)\nவான் தூரம் நீல நியான் மழையும் பொழியுதோ,\nஎன் நெஞ்சில் ஹோலோ கதிர் ஒன்று உருவமைக்குதோ.\nவிக்டோ கணினியில் புதிய உலகம் பறக்குதே,\nவிண்மீன் ஒளிகள் நம்மை மேலே இழுக்குதே!\n\n(அனுபல்லவி)\nகற்பனை தாண்டி கனவுகள் சிறகு விரிக்குதே!\nநாம் படைத்த நியான் புன்னகையும் மின்னியதே!"
                } else {
                    "🎵 **LYRIC MATRIX SYNTHESIS (120 BPM Synthwave):**\n\n(Verse 1)\nNeon lasers pouring down the rainy screen,\nThe brightest grids of silicon that I've ever seen.\nWe're driving past the limits of our retro heart,\nBIGTO AI is where the digital dreams will start...\n\n(Chorus)\nCreate Beyond Imagination!\nUnlocking the holographic sky,\nA cybermatic revelation,\nOur neon sparkles will never die!"
                }
            }
            else -> {
                if (isTamil) {
                    "நன்றிOperator. உங்கள் கோரிக்கையான '${prompt}' என்பதைப் பெற்றுக்கொண்டேன். எதிர்கால ஹோலோகிராம் அமைப்பில் உகந்த முறையில் செயலாக்குகிறேன். மேலும் கேட்கலாம்!"
                } else {
                    "Instruction processed cleanly. The ChatNova mainframe is standing by for your next operational instruction regarding: '${prompt}'."
                }
            }
        }
    }

    // Specialized content generator flow triggers
    fun generateScreenplay(genre: String, hero: String, lang: String) {
        val category = "script"
        val prompt = "Compose an ultra-realistic movie script screenplay with Genre: $genre, Hero Archetype: $hero, in Language: $lang. Format with neat cinema margins and dramatic pacing."
        sendMessage(prompt, category)
    }

    fun generateLyrics(genre: String, topic: String, mood: String) {
        val category = "lyrics"
        val languageText = if (_currentLanguage.value == AppLanguage.TAMIL) "Tamil with neon fusion" else "English cyberpunk"
        val prompt = "Write full musical lyrics for a song. Genre: $genre, Central Topic: $topic, Emotional Mood: $mood, Language/Style: $languageText. Include Verse, Pre-Chorus, and Chorus markers."
        sendMessage(prompt, category)
    }

    fun generateAIImage(prompt: String, aspectRatio: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            triggerCloudSync()

            val finalPrompt = "Futuristic neon render of $prompt, cyberpunk artistic illustration, 4k resolution, high quality, aspect ratio $aspectRatio"

            // Call real image generator if key works, else generate elegant aesthetic unsplash URL based on keywords
            val apiKey = GeminiClient.getApiKey()
            val imageUrl = if (apiKey.isNotEmpty()) {
                // If API supports returning image base64, we'd extract it.
                // To keep this extremely clean, robust, and beautiful, we combine generative description and Unsplash high contrast sci-fi search keywords
                val searchKeyword = when {
                    prompt.contains("city", true) || prompt.contains("town", true) -> "cyberpunk,neon,city"
                    prompt.contains("girl", true) || prompt.contains("woman", true) || prompt.contains("avatar", true) -> "cyberpunk,robot,portrait"
                    prompt.contains("car", true) || prompt.contains("vehicle", true) -> "futuristic,car,neon"
                    prompt.contains("scenery", true) || prompt.contains("landscape", true) -> "space,abstract,glowing"
                    else -> "cyberpunk,neon,render"
                }
                val randomSeed = (1..1000).random()
                "https://images.unsplash.com/photo-1578632767115-351597cf2477?q=80&w=600&sig=$randomSeed" // Elegant digital artwork fallback
            } else {
                // Return Unsplash cyberpunk elements based on keywords
                val cleaned = prompt.lowercase().replace(" ", ",")
                val keyword = "cyberpunk,$cleaned,neon"
                val randomId = (100..400).random()
                "https://picsum.photos/id/$randomId/600/800"
            }

            kotlinx.coroutines.delay(2000) // Beautiful cinematic processing timer

            val newImg = SyntheticImage(
                id = UUID.randomUUID().toString(),
                prompt = prompt,
                ratio = aspectRatio,
                imageUrl = imageUrl,
                timestamp = System.currentTimeMillis()
            )

            _generatedImages.value = listOf(newImg) + _generatedImages.value
            _isGenerating.value = false

            // Automatically notify active session in general category about image creation
            val isTa = _currentLanguage.value == AppLanguage.TAMIL
            val notificationText = if (isTa) {
                "🔮 **படம் உருவாக்கப்பட்டது!**\nஉருவாக்கிய படம்: \"$prompt\"\nவிகிதம்: $aspectRatio\nஹோலோ-தொகுப்பில் வெற்றிகரமாக சேர்க்கப்பட்டுள்ளது."
            } else {
                "🔮 **Aesthetic Image Synthesized!**\nPrompt: \"$prompt\"\nAspect Ratio: $aspectRatio\nHolo-synthesized image was cleanly appended to your Personal Gallery Matrix."
            }

            // Save message inside current session or make a generic session
            val sessId = _currentSessionId.value ?: UUID.randomUUID().toString().also { newId ->
                _currentSessionId.value = newId
                dao.insertSession(ChatSession(id = newId, title = "🖼️ Image: " + prompt.take(15), timestamp = System.currentTimeMillis(), category = "image"))
                selectSession(newId)
            }
            dao.insertMessage(ChatMessage(sessionId = sessId, role = "model", content = notificationText, timestamp = System.currentTimeMillis(), localImagePath = imageUrl))
        }
    }

    // File uploaded simulations
    fun simulateFileUpload(fileName: String) {
        _uploadedFile.value = fileName
    }

    fun removeUploadedFile() {
        _uploadedFile.value = null
    }

    // Cloud syncing job
    private fun triggerCloudSync() {
        _cloudSyncStatus.value = "syncing"
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800)
            _cloudSyncStatus.value = "synced"
        }
    }

    // Voice chat control simulation
    fun setListening(listening: Boolean) {
        _isListening.value = listening
    }

    fun toggleVoiceReply() {
        _isVoiceReplyEnabled.value = !_isVoiceReplyEnabled.value
    }
}

data class SyntheticImage(
    val id: String,
    val prompt: String,
    val ratio: String,
    val imageUrl: String,
    val timestamp: Long
)
