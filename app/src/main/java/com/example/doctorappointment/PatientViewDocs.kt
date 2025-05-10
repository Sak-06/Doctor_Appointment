package com.example.doctorappointment

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.doctorappointment.ui.theme.DoctorAppointmentTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientViewDocs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointmentTheme {
                val navController = rememberNavController()
                Surface() {
                    AllDoctorsScreen(navController)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllDoctorsScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var doctors by remember { mutableStateOf<List<DoctorDta>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("doctors").get().addOnSuccessListener { result ->
            doctors = result.documents.mapNotNull { it.toObject(DoctorDta::class.java)?.copy(id = it.id) }
        }
    }

    LazyColumn {
        items(doctors) { doctor ->
            Card(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (doctor.profileUri.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(doctor.profileUri),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(doctor.name, fontWeight = FontWeight.Bold)
                            Text(doctor.specialty)
                            Text("${doctor.experience} yrs experience")
                            Text("${doctor.address}, ${doctor.city}, ${doctor.state}")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Button(onClick = { /* voice call */ }) { Text("Voice Call") }
                        Button(onClick = { /* video call */ }) { Text("Video Call") }
                        Button(onClick = { /* chat */ }) { Text("Chat") }
                        Button(onClick = {
                            navController.navigate("book_appointment/${doctor.id}")
                        }) {
                            Text("Appointment")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookAppointmentScreen(doctorId: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var doctor by remember { mutableStateOf<DoctorDta?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTimeSlot by remember { mutableStateOf<TimeSlot?>(null) }

    LaunchedEffect(doctorId) {
        db.collection("doctors").document(doctorId).get().addOnSuccessListener { snapshot ->
            snapshot.toObject(DoctorDta::class.java)?.let {
                doctor = it.copy(id = doctorId)
                selectedDate = it.availability.keys.firstOrNull() ?: ""
            }
        }
    }

    doctor?.let { doc ->
        val availableDates = doc.availability.keys.toList()
        val timeSlots = doc.availability[selectedDate] ?: emptyList()

        AlertDialog(
            onDismissRequest = { navController.popBackStack() },
            confirmButton = {
                Button(
                    onClick = {
                        val patientId = FirebaseAuth.getInstance().currentUser?.uid
                        val dateTime = getAppointmentTimestamp(selectedDate, selectedTimeSlot?.startTime)

                        if (patientId != null && selectedTimeSlot != null && dateTime != null) {
                            val appointment = hashMapOf(
                                "doctorId" to doc.id,
                                "patientId" to patientId,
                                "appointmentTime" to dateTime
                            )

                            db.collection("appointments")
                                .add(appointment)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Appointment booked!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to book: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Select a slot first", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Book")
                }
            },
            title = { Text("Book Appointment with ${doc.name}") },
            text = {
                Column {
                    Text("Select Date:")
                    availableDates.forEach { date ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDate == date,
                                onClick = { selectedDate = date }
                            )
                            Text(date)
                        }
                    }

                    Spacer(modifier = Modifier.padding(8.dp))
                    Text("Select Time Slot:")
                    timeSlots.forEach { slot ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedTimeSlot == slot,
                                onClick = { selectedTimeSlot = slot }
                            )
                            Text("${slot.startTime} - ${slot.endTime}")
                        }
                    }
                }
            }
        )
    }
}
fun getAppointmentTimestamp(dateStr: String, timeStr: String?): com.google.firebase.Timestamp? {
    return try {
        if (timeStr == null) return null
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        val date = format.parse("$dateStr $timeStr")
        date?.let { com.google.firebase.Timestamp(it) }
    } catch (e: Exception) {
        null
    }
}

// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview3() {
//    DoctorAppointmentTheme {
//        Greeting5("Android")
//    }
// }
