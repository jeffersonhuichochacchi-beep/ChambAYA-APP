package com.example.chambaya.data.model

import java.io.Serializable

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLocationShare: Boolean = false,
    val locationAddress: String? = null,
    val isPriceProposal: Boolean = false,
    val proposedPrice: Double? = null,
    val isMine: Boolean = true
) : Serializable

data class ChatConversation(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserRole: String, // "Contratante" or "Trabajador"
    var lastMessage: String,
    var lastMessageTime: Long,
    var unreadCount: Int = 0
) : Serializable
