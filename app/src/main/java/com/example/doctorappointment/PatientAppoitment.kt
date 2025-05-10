package com.example.doctorappointment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.doctorappointment.ui.theme.DoctorAppointmentTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PatientAppoitment : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointmentTheme {
                PatientAppointmentsScreen()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentsScreen() {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().uid
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }

    LaunchedEffect(uid) {
        db.collection("appointments").whereEqualTo("patientId", uid).get()
            .addOnSuccessListener { result ->
                appointments = result.documents.mapNotNull { it.toObject(Appointment::class.java) }
            }
    }

    LazyColumn {
        items(appointments) { appt ->
            Card(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Doctor: ${appt.doctorName}", fontWeight = FontWeight.Bold)
                    Text("Date: ${appt.appointmentDate}")
                    Text("Time: ${appt.appointmentTime}")
                }
            }
        }
    }
}

@Composable
fun AppointmentItem(appointment: Appointment) {
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Doctor: ${appointment.doctorName}", fontWeight = FontWeight.Bold)
            Text("Date: ${appointment.appointmentDate}")
            Text("Schedule: ${appointment.schedule}")
        }
    }
}
data class Appointment(
    val doctorName: String,
    val appointmentDate: String,
    val schedule: String
)
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview2() {
//    DoctorAppointmentTheme {
//        Greeting4("Android")
//    }
//}