package com.example.vibetalk.audio

import android.media.MediaPlayer
import java.io.File

class AudioPlayer {

    private var player: MediaPlayer? = null

    fun playFile(file: File, onFinished: () -> Unit = {}) {
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            start()
            setOnCompletionListener {
                onFinished()
                release()
            }
        }
    }

    fun stop() {
        player?.release()
        player = null
    }
}