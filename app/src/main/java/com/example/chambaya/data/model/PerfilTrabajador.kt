package com.example.chambaya.data.model

import java.io.Serializable

data class WorkerProfile(
    val id: String,
    var fullName: String,
    var dni: String,
    var phone: String,
    var age: Int,
    var district: String,
    var specialties: List<String>,
    var experienceYears: Int,
    var bio: String,
    var rating: Double = 4.9,
    var reviewsCount: Int = 18,
    var completedJobsCount: Int = 24,
    var isDniVerified: Boolean = true,
    var isPhoneVerified: Boolean = true,
    var hourlyRateSuggested: Double = 35.0,
    var walletBalance: Double = 25.0
) : Serializable
