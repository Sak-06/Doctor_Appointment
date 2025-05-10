package com.example.doctorappointment

data class DoctorDta(
    val id: String = "",
    val role: String = "doctor",
    val name: String = "",
    val profileUri: String = "",
    val specialty: String = "",
    val experience: Int = 0,
    val rating: Double = 0.0,
    val address: String = "",
    val email: String = "",
    val city: String = "",
    val state: String = "",
    val availability: Map<String, List<TimeSlot>> = emptyMap()
)

data class TimeSlot(
    val startTime: String = "",
    val endTime: String = ""
)
