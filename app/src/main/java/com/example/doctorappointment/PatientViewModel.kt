package com.example.doctorappointment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun registerPatient(
        name: String,
        age: Int,
        gender: String,
        healthConditions: String,
        email: String,
        address: String,
        city : String,
        state: String) {
        val patientid = auth.currentUser?.uid ?: return

        val patient = Patient(
            name = name,
            age = age,
            gender = gender,
            healthConditions = healthConditions,
            photoUrl = "",
            address = address,
            city = city,
            state = state // Placeholder — add upload logic



        )

        firestore.collection("patients").document(patientid)
            .set(patient)
            .addOnSuccessListener { Log.d("Patient", "Registered") }
            .addOnFailureListener { Log.e("Patient", "Error", it) }
    }
}