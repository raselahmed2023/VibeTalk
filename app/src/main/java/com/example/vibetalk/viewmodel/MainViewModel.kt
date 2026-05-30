package com.example.vibetalk.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import com.example.vibetalk.api.ChatMessage
import com.example.vibetalk.api.GroqApiService
import com.example.vibetalk.audio.AudioRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val recorder = AudioRecorder(application)

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _statusText = MutableStateFlow("Tap mic to speak")
    val statusText: StateFlow<String> = _statusText

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    // Settings
    private val _autoSpeak = MutableStateFlow(true)
    val autoSpeak: StateFlow<Boolean> = _autoSpeak

    private val _voiceSpeed = MutableStateFlow("Normal")
    val voiceSpeed: StateFlow<String> = _voiceSpeed

    private var audioFile: File? = null
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(1.0f)
            }
        }
    }

    private fun currentTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    fun setAutoSpeak(value: Boolean) {
        _autoSpeak.value = value
    }

    fun setVoiceSpeed(speed: String) {
        _voiceSpeed.value = speed
        val rate = when (speed) {
            "Slow" -> 0.6f
            "Fast" -> 1.4f
            else -> 1.0f
        }
        tts?.setSpeechRate(rate)
    }

    fun startRecording() {
        audioFile = recorder.startRecording()
        _isRecording.value = true
        _statusText.value = "Listening..."
    }

    fun stopRecording() {
        recorder.stopRecording()
        _isRecording.value = false
        _statusText.value = "Thinking..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Step 1 — transcribe voice
                val transcribed = audioFile?.let {
                    GroqApiService.transcribeAudio(it)
                } ?: return@launch

                // Add user message to chat
                withContext(Dispatchers.Main) {
                    addMessage(ChatMessage(transcribed, isUser = true, currentTime()))
                    _statusText.value = "AI is thinking..."
                }

                // Step 2 — get AI reply
                val reply = GroqApiService.getAIReply(transcribed)

                withContext(Dispatchers.Main) {
                    addMessage(ChatMessage(reply, isUser = false, currentTime()))
                    _statusText.value = "Tap mic to speak"
                    if (_autoSpeak.value) {
                        tts?.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _statusText.value = "Error: ${e.message}"
                }
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
    }
}