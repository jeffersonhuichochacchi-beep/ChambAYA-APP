package com.example.chambaya.data.model

import java.io.Serializable

data class JobOffer(
    val id: String,
    val title: String,
    val category: String,
    val district: String,
    val address: String,
    val payment: Double,
    val paymentType: String, // "por día", "por tarea", "por hora"
    val duration: String,
    val schedule: String,
    val workersNeeded: Int,
    val date: String,
    val description: String,
    val isFeatured: Boolean = false,
    val employerId: String,
    val employerName: String,
    val employerRating: Double = 4.8,
    val employerPhone: String = "966123456",
    val employerCompletedJobs: Int = 12,
    var status: String = "ABIERTA", // "ABIERTA", "EN_PROCESO", "FINALIZADA"
    var applicantsCount: Int = 0,
    var isAppliedByMe: Boolean = false,
    val distanceKm: Double = 1.2,
    val latitude: Double = -13.1631,
    val longitude: Double = -74.2236,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
