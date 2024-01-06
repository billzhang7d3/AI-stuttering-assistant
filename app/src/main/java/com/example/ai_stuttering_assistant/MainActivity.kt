package com.example.ai_stuttering_assistant

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai_stuttering_assistant.ui.theme.AIStutteringAssistantTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

lateinit var virtualAssistant: VirtualAssistant
lateinit var speechRecog: SpeechRecognizer
lateinit var recogIntent: Intent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val apiInstance: APIKeys = APIKeys()
        val apiKey: String = apiInstance.openAI_Key()
        Log.d("APIKey", apiKey)
        virtualAssistant = VirtualAssistant(apiKey)
        setContent {
            Interface()
        }
    }
}

@Composable
fun ButtonExample(onClick: () -> Unit) {
    var selected by remember { mutableStateOf(false) }
    val color = if (selected) Color.Gray else Color.White
    ElevatedButton(
        onClick = {
            Log.d("checkpoint", "checkpoint")
            if (selected) {
                speechRecog.startListening(recogIntent)
                selected = false
            } else {
                speechRecog.stopListening()
                GlobalScope.launch(Dispatchers.IO) {
                    val response: String = virtualAssistant.process("what is kotlin", "gpt-3.5-turbo")
                    Log.d("response", response)
                    val audio = virtualAssistant.generateSpeech(response, "tts-1")
                    Log.d("audio bytearray", audio.toString())
                }
                selected = true
            }
        },
        colors = ButtonDefaults.buttonColors(color),
        modifier = Modifier.size(width = 240.dp, height = 80.dp),
    ) {
        Text("Press to Talk", fontSize = 30.sp, color = Color.Magenta)
    }
}

@Composable
fun Interface() {
    Column(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.misato),
            contentDescription = "Misato Katsuragi",
            modifier = Modifier
                .size(500.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Misato Katsuragi", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(100.dp))
        ButtonExample(
            onClick = { Log.d("talk-button", "BUTTON CLICKED") }
        )
    }
}