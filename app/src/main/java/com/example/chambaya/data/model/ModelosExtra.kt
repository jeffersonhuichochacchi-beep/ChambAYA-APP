package com.example.chambaya.data.model

import java.io.Serializable

data class Review(
    val id: String,
    val targetUserId: String,
    val reviewerName: String,
    val reviewerRole: String, // "Contratante" o "Trabajador"
    val rating: Float, // 1.0 to 5.0
    val comment: String,
    val jobTitle: String,
    val date: String
) : Serializable

data class BusinessAd(
    val id: String,
    val name: String,
    val category: String,
    val district: String,
    val tagline: String,
    val phone: String,
    val address: String,
    val promoBadge: String = "15% DSCTO"
) : Serializable

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: String, // "JOB_ALERT", "CHAT", "RATING", "SYSTEM"
    val isRead: Boolean = false
) : Serializable
