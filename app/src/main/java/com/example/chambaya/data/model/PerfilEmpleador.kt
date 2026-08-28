package com.example.chambaya.data.model

import java.io.Serializable

data class EmployerProfile(
    val id: String,
    var fullName: String,
    var phone: String,
    var district: String,
    var rating: Double = 4.8,
    var jobsPostedCount: Int = 5,
    var isVerified: Boolean = true
) : Serializable
