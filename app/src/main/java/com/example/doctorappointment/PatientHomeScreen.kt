package com.example.doctorappointment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.doctorappointment.ui.theme.DoctorAppointmentTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class PatientHomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointmentTheme {
                val navController= rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFFFFFF)
                ){
                    PatientHome(navController)
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHome(navController: NavController,){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                onItemClick = { label ->
                    when (label) {
                        "Edit Profile" -> { /* Navigate to Edit Profile */ }
                        "Settings" -> { /* Navigate to Settings */ }
                        "Feedback" -> { /* Navigate to Feedback */ }
                        "Logout" -> {
                            Firebase.auth.signOut()
                            navController.navigate("login") {
                                popUpTo("patient_home") { inclusive = true }
                            }
                        }
                    }
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController)
            },
            topBar = {
                TopAppBar(
                    title = { Text("Doctor Appointment") },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "patient_home",
                modifier = Modifier.padding(padding)
            ) {
                composable("home") { DoctorHomeScreen() }
                composable("appointments") { AppointmentsScreen() }
                composable("doctors") { AllDoctorsScreen() }
                composable("chatbot") { AIChatbotScreen() }
            }
        }
    }
}
//    val firestore :FirebaseFirestore= Firebase.firestore
//    var doctors by remember { mutableStateOf(listOf<DoctorDta>()) }
//    var selectedSpeciality by remember { mutableStateOf("All") }
//    var sortOption by remember { mutableStateOf("None") }
//
//    LaunchedEffect(Unit) {
//        firestore.collection("doctors")
//            .get()
//            .addOnSuccessListener { snapshot ->
//                val fetchedDoctors = snapshot.documents.mapNotNull {
//                    it.toObject(DoctorDta::class.java)
//                }
//                doctors = fetchedDoctors
//            }
//    }
//
//    val filteredDoctors = doctors.filter {
//        selectedSpeciality == "All" || it.specialty == selectedSpeciality
//    }.let {
//        when (sortOption) {
//            "Experience" -> it.sortedByDescending { doc -> doc.experience }
//            "Rating" -> it.sortedByDescending { doc -> doc.rating }
//            else -> it
//        }
//    }
//
//    Column(Modifier.padding(16.dp)) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            DropdownMenuBox(
//                label = "Speciality",
//                options = listOf("All", "Cardiologist", "Dermatologist", "Orthopedic", "Dentist"),
//                selectedOption = selectedSpeciality,
//                onOptionSelected = { selectedSpeciality = it }
//            )
//
//            DropdownMenuBox(
//                label = "Sort By",
//                options = listOf("None", "Experience", "Rating"),
//                selectedOption = sortOption,
//                onOptionSelected = { sortOption = it }
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        LazyColumn {
//            items(filteredDoctors) { doctor ->
//                DoctorCard(doctor)
//                Spacer(modifier = Modifier.height(12.dp))
//            }
//        }
//    }

@Composable
fun DropdownMenuBox(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontWeight = FontWeight.SemiBold)
        Box {
            TextButton(onClick = { expanded = true }) {
                Text(selectedOption)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            onOptionSelected(it)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun DoctorCard(doctor: DoctorDta) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = doctor.profileUri,
                contentDescription = "Doctor Photo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(doctor.specialty, color = Color.Gray)
                Text("Exp: ${doctor.experience} yrs | Rating: ${doctor.rating}")
                Text("Location: ${doctor.address}+${doctor.city},+${doctor.state}", fontSize = 12.sp)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    DoctorAppointmentTheme {
//        Greeting2("Android")
//    }
//}