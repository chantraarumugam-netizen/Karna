package com.example.ui

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    TAMIL("ta", "தமிழ்")
}

object LanguageHelper {
    private val enMap = mapOf(
        "app_name" to "BIGTO AI",
        "tagline" to "Create Beyond Imagination",
        "ask_anything" to "Ask Anything...",
        "trending" to "Trending Prompts",
        "quick_access" to "Quick Access Tools",
        "gen_image" to "Generate Image",
        "create_song" to "Create Song",
        "write_script" to "Write Script",
        "ai_coding" to "AI Coding",
        "voice_chat" to "Voice Chat",
        "chat_history" to "Chat History",
        "search" to "Search History...",
        "profile" to "Holo-Profile",
        "settings" to "System Matrix",
        "toggle_theme" to "Retro Theme Toggle",
        "suggest_intro" to "Need inspiration?",
        "suggest_1" to "Explain quantum physics to a futuristic kid",
        "suggest_2" to "Write a cyberpunk neon visual scene description",
        "suggest_3" to "Tamil traditional poetry meets sci-fi technology",
        "language" to "Grid Language",
        "login" to "Initialize Profile",
        "username" to "Holo-ID (Username)",
        "password" to "Security Key (Password)",
        "sign_in" to "Sign In",
        "sign_up" to "Sign Up",
        "image_gen_title" to "Holo-Image Synthesizer",
        "image_prompt_hint" to "Describe the future (e.g., Cyberpunk Tamil temple)...",
        "ratio" to "Aspect Grid Ratio",
        "synth_btn" to "Synthesize Image",
        "script_title" to "Movie Script Decryptor",
        "lyrics_title" to "Holographic Lyric Synthesizer",
        "coding_title" to "Neural Coding Chamber",
        "analyzer_title" to "Multimodal Spectral Analyzer",
        "analyzer_hint" to "Select cyber-image & analyze material properties...",
        "cloud_sync" to "Mainframe Cloud Sync",
        "sync_active" to "Syncing to mainframe...",
        "synced" to "Fully Synced",
        "voice_input" to "Tap to speak",
        "voice_listening" to "Neural link listening...",
        "upload_file" to "Inject Data File",
        "image_analyzed" to "Image Material Discovered"
    )

    private val taMap = mapOf(
        "app_name" to "விக்டோ AI (BIGTO)",
        "tagline" to "கற்பனைக்கு அப்பாற்பட்ட உருவாக்கம்",
        "ask_anything" to "எதையும் கேளுங்கள்...",
        "trending" to "பிரபலமானவை",
        "quick_access" to "விரைவான கருவிகள்",
        "gen_image" to "படம் உருவாக்கு",
        "create_song" to "பாடல் வரிகள்",
        "write_script" to "திரைக்கதை எழுது",
        "ai_coding" to "AI குறியீடு",
        "voice_chat" to "குரல் அரட்டை",
        "chat_history" to "அரட்டை வரலாறு",
        "search" to "வரலாற்றைத் தேடு...",
        "profile" to "சுயவிவரம்",
        "settings" to "கணினி அமைப்பு",
        "toggle_theme" to "நிற முறை மாற்றி",
        "suggest_intro" to "தூண்டுதல் தேவையா?",
        "suggest_1" to "விண்வெளி அறிவியல் பற்றி எளிய தமிழில் விளக்கு",
        "suggest_2" to "எதிர்கால சென்னை நகரம் எப்படி இருக்கும்?",
        "suggest_3" to "தமிழ் இலக்கியமும் அறிவியல் தொழில்நுட்பமும்",
        "language" to "மொழியைத் தேர்ந்தெடு",
        "login" to "சுயவிவர உள்நுழைவு",
        "username" to "பயனர் பெயர் (Holo-ID)",
        "password" to "பாதுகாப்பு விசை (கடவுச்சொல்)",
        "sign_in" to "உள்நுழைக",
        "sign_up" to "பதிவு செய்க",
        "image_gen_title" to "ஹோலோ-படம் தொகுப்பாளர்",
        "image_prompt_hint" to "எதிர்கால உலகத்தை விவரிக்கவும்...",
        "ratio" to "விகிதம் உருவமைப்பு",
        "synth_btn" to "படத்தை உருவாக்கு",
        "script_title" to "திரைக்கதை உருவாக்கி",
        "lyrics_title" to "பாடல் வரிகள் உருவாக்கி",
        "coding_title" to "AI குறியீட்டு அறை",
        "analyzer_title" to "முப்பரிமாண பட பகுப்பாய்வி",
        "analyzer_hint" to "படத்தைத் தேர்ந்தெடுத்து பகுப்பாய்வு செய்க...",
        "cloud_sync" to "மேகக்கணி ஒத்திசைவு",
        "sync_active" to "ஒத்திசைவு செயலில் உள்ளது...",
        "synced" to "ஒத்திசைக்கப்பட்டது",
        "voice_input" to "பேச தட்டவும்",
        "voice_listening" to "குரல் இணைப்பு கேட்கிறது...",
        "upload_file" to "தரவுக் கோப்பை ஏற்று",
        "image_analyzed" to "படம் பகுப்பாய்வு முடிந்தது"
    )

    fun getString(key: String, lang: AppLanguage): String {
        return if (lang == AppLanguage.TAMIL) {
            taMap[key] ?: enMap[key] ?: key
        } else {
            enMap[key] ?: key
        }
    }
}
