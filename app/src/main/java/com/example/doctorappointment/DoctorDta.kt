package com.example.doctorappointment

data class DoctorDta(

val name: String = "",
val specialty: String = "",
val experience: Int = 0,
val photoUrl: String = "",
val address: String ="",
val email: String ="",
val city: String="",
val state: String="",
val availability: Map<String, List<TimeSlot>> = emptyMap()
)

data class TimeSlot(
    val startTime: String = "",
    val endTime: String = ""
)
