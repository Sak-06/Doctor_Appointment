package com.example.doctorappointment

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DoctorViewModel  : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun registerDoctor(
        name: String,
        specialty: String,
        experience: Int,
        availability: Map<String, List<Pair<String, String>>>,
        email: String,
        profile: Uri?,
        address: String,
        city : String,
        state : String
    ) {
        val docid = auth.currentUser?.uid ?: return
        val docAvailability = availability.mapValues { entry ->
            entry.value.map { TimeSlot(it.first, it.second) }
        }
        val profileUrl =profile?.toString()?:""

        val doctor = DoctorDta(
            role = "doctor",
            name = name,
            specialty = specialty,
            experience = experience,
            profileUri = profileUrl,
            availability = docAvailability,
            email = email,
            address = address,
            city = city,
            state = state
        )

        firestore.collection("doctors").document(docid)
            .set(doctor)
            .addOnSuccessListener { Log.d("Doctor", "Registered") }
            .addOnFailureListener { Log.e("Doctor", "Error", it) }
    }
}