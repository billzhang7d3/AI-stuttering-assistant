package com.example.ai_stuttering_assistant

import android.util.Log
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.Voice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import kotlin.time.Duration.Companion.seconds

class VirtualAssistant(apiKey: String) {
    private var openai: OpenAI
    init {
        openai = OpenAI(
            OpenAIConfig(
                token = apiKey,
                timeout = Timeout(socket = 60.seconds),
            )
        )
    }
    suspend fun process(message: String, model: String): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = message
                )
            )
        )
        val completion = openai.chatCompletion(chatCompletionRequest)
        return completion.choices[0].message.content.toString()
    }
    suspend fun generateSpeech(message: String, model: String): ByteArray {
        val rawAudio = openai.speech(
            request = SpeechRequest(
                model = ModelId(model),
                input = message,
                voice = Voice.Nova
            )
        )
        return rawAudio
    }
}