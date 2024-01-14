package com.example.ai_stuttering_assistant

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
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
import java.io.File
import java.io.FileOutputStream

lateinit var virtualAssistant: VirtualAssistant
lateinit var speechRecog: SpeechRecognizer
lateinit var recogIntent: Intent
lateinit var mediaPlayer: MediaPlayer

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
        mediaPlayer = MediaPlayer()

        enableEdgeToEdge()

        if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P ) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        setContent {
            AIStutteringAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Interface( this )
                }
            }
        }
    }
}

@Composable
fun ButtonExample(context: Context, onClick: () -> Unit) {
    var selected by remember { mutableStateOf(false) }
    val color = if (selected) Color.Gray else Color.White
    ElevatedButton(
        onClick = {
            Log.d("checkpoint", "checkpoint button unpress")
            if (selected) {
                //start recording
                mediaPlayer.stop()
                speechRecog.startListening(recogIntent)
                selected = false
            } else {
                //if recording, then we stop recording and process the info
                speechRecog.stopListening()
                GlobalScope.launch(Dispatchers.IO) {
                    val response: String = virtualAssistant.process("explain kotlin in less than 10 seconds", "gpt-3.5-turbo")
                    Log.d("response", response)
                    val audio: ByteArray = virtualAssistant.generateSpeech(response, "tts-1")
                    Log.d("audio bytearray", audio.toString())
                    //play audio
                    val tempFile: File = File.createTempFile("temp_audio", "mp3", context.cacheDir)
                    val fileOutputStream = FileOutputStream(tempFile)
                    fileOutputStream.write(audio)
                    fileOutputStream.close()
                    Log.d("checkpoint", "checkpoint button press")
                    try {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(context, Uri.fromFile(tempFile))
                        mediaPlayer.prepareAsync()
                        mediaPlayer.setOnPreparedListener {
                            mediaPlayer.start()
                        }
                    } catch (e: Exception) {
                        Log.d("Audio Player Error", e.toString())
                    }
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
fun Interface(context: Context) {
    LazyColumn(
        contentPadding = WindowInsets.systemBars.asPaddingValues(),
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
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
                context = context,
                onClick = { Log.d("talk-button", "BUTTON CLICKED") }
            )
        }
    }
}

