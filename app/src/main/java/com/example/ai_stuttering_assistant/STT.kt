package com.example.ai_stuttering_assistant

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf

class STT : ComponentActivity() {
    private var speechRecog: SpeechRecognizer
    private var recogIntent: Intent
    init {
        speechRecog = SpeechRecognizer.createSpeechRecognizer(this)
        recogIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recogIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recogIntent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            packageName
        )
    }
    fun startAudio() {
        speechRecog.startListening(recogIntent)
    }
    fun stopAudio() {
        speechRecog.stopListening()
    }
}