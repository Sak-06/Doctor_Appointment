package com.example.doctorappointment

data class Patient(
    val role : String="patient",
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val address: String="",
    val city: String="",
    val email: String ="",
    val state: String="",
    val healthConditions: String = "",
    val profileUri: String = ""
)

