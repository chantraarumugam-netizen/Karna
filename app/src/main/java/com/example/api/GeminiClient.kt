package com.example.api

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    /**
     * Helper to retrieve the API key from BuildConfig.
     */
    fun getApiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key == "MY_GEMINI_API_KEY" || key.isBlank()) "" else key
    }

    /**
     * Base method to call Gemini API.
     * Safely falls back to simulation mode if API key is not configured or call fails.
     */
    suspend fun generateText(
        prompt: String,
        model: String = "gemini-3.5-flash",
        systemInstruction: String? = null,
        fallbackText: () -> String
    ): String {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            Log.w(TAG, "Gemini API key is not configured, running in simulation mode.")
            return simulateResponseDelay(fallbackText())
        }

        val contents = listOf(Content(parts = listOf(Part(text = prompt))))
        val systemInstructionContent = systemInstruction?.let {
            Content(parts = listOf(Part(text = it)))
        }

        val request = GenerateContentRequest(
            contents = contents,
            systemInstruction = systemInstructionContent
        )

        return try {
            val response = apiService.generateContent(
                model = model,
                apiKey = apiKey,
                request = request
            )
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("No response parts received from model")
        } catch (e: Exception) {
            Log.e(TAG, "Error during Gemini API call: ${e.message}. Falling back to simulation.", e)
            simulateResponseDelay(fallbackText())
        }
    }

    /**
     * Generate content with an uploaded image asset (Multimodal).
     */
    suspend fun generateMultimodal(
        prompt: String,
        imageBitmap: Bitmap,
        mimeType: String = "image/jpeg",
        fallbackText: () -> String
    ): String {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return simulateResponseDelay(fallbackText())
        }

        return try {
            val base64Data = convertBitmapToBase64(imageBitmap)
            val parts = listOf(
                Part(text = prompt),
                Part(inlineData = InlineData(mimeType = mimeType, data = base64Data))
            )
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = parts))
            )
            val response = apiService.generateContent(
                model = "gemini-3.5-flash",
                apiKey = apiKey,
                request = request
            )
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("No content returned")
        } catch (e: Exception) {
            Log.e(TAG, "Multimodal generation failed: ${e.message}", e)
            simulateResponseDelay(fallbackText())
        }
    }

    private suspend fun simulateResponseDelay(text: String): String {
        kotlinx.coroutines.delay(1500)
        return text
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
