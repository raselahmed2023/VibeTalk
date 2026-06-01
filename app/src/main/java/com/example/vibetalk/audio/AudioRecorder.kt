package com.example.vibetalk.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.*
import java.io.File

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var silenceJob: Job? = null
    private var outputFile: File? = null

    private val silenceDurationMs = 2000L
    private val minRecordingMs = 1000L

    fun startRecording(onSilenceDetected: () -> Unit): File {
        val file = File(context.cacheDir, "voice_input.m4a")
        outputFile = file

        if (file.exists()) file.delete()

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(16000)
            setAudioEncodingBitRate(128000)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }

        isRecording = true

        // Auto stop after silence
        silenceJob = CoroutineScope(Dispatchers.IO).launch {
            delay(minRecordingMs)
            var lastAmplitude = 0
            var silenceStart = 0L

            while (isRecording) {
                delay(200)
                val amplitude = mediaRecorder?.maxAmplitude ?: 0

                if (amplitude < 500) {
                    if (silenceStart == 0L) silenceStart = System.currentTimeMillis()
                    else if (System.currentTimeMillis() - silenceStart > silenceDurationMs) {
                        withContext(Dispatchers.Main) {
                            onSilenceDetected()
                        }
                        break
                    }
                } else {
                    silenceStart = 0L
                }
                lastAmplitude = amplitude
            }
        }

        return file
    }

    fun stopRecording() {
        isRecording = false
        silenceJob?.cancel()
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder = null
    }

    fun getAudioFile(): File {
        return File(context.cacheDir, "voice_input.m4a")
    }
}