package com.example.vibetalk.api

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val time: String = ""
)