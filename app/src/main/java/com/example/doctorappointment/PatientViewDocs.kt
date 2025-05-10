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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.doctorappointment.ui.theme.DoctorAppointmentTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class PatientViewDocs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointmentTheme {
                Surface() {
                    AllDoctorsScreen()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllDoctorsScreen(navController: NavController){
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
                            Image(painter = rememberAsyncImagePainter(doctor.profileUri), contentDescription = null,
                                modifier = Modifier.size(64.dp))
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
    var doctor by remember { mutableStateOf<DoctorDta?>(null) }

    LaunchedEffect(doctorId) {
        db.collection("doctors").document(doctorId).get().addOnSuccessListener { snapshot ->
            doctor = snapshot.toObject(DoctorDta::class.java)?.copy(id = doctorId)
        }
    }

    doctor?.let { doc ->
        val availableDates = doc.availability.keys
        var selectedDate by remember { mutableStateOf(availableDates.firstOrNull()) }
        val slots = doc.availability[selectedDate] ?: emptyList()

        AlertDialog(
            onDismissRequest = { navController.popBackStack() },
            confirmButton = {
                Button(onClick = {
                    // Save to Firestore
                    val appointment = hashMapOf(
                        "doctorId" to doc.id,
                        "doctorName" to doc.name,
                        "patientId" to FirebaseAuth.getInstance().uid,
                        "appointmentDate" to selectedDate,
                        "appointmentTime" to slots.firstOrNull()?.get("startTime"),
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    db.collection("appointments").add(appointment)
                    scheduleReminder(appointment)
                    navController.popBackStack()
                }) {
                    Text("Book")
                }
            },
            title = { Text("Select Slot") },
            text = {
                Column {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { /* ignore */ }
                    ) {
                        availableDates.forEach { date ->
                            DropdownMenuItem(onClick = { selectedDate = date }) {
                                Text(text = date)
                            }
                        }
                    }
                    slots.forEach {
                        Text("${it["startTime"]} - ${it["endTime"]}")
                    }
                }
            }
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview3() {
//    DoctorAppointmentTheme {
//        Greeting5("Android")
//    }
//}