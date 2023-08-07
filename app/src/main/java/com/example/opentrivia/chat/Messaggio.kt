package com.example.opentrivia.chat

data class Messaggio(
    val messageId: String? = null,
    val senderId: String? = null,
    val content: String? = null,
    val timestamp: Long? = null
)
