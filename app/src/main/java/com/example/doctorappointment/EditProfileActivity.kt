package com.example.doctorappointment

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.doctorappointment.ui.theme.DoctorAppointmentTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color= Color.White
                ) {
                    val navController= rememberNavController()
                    val viewModel: PatientViewModel =viewModel()
                    EditPatientProfileScreen(navController,viewModel)
                }
            }
        }
    }
}

@Composable
fun EditPatientProfileScreen(
    navController: NavController,
    viewModel: PatientViewModel = viewModel()
) {
    val context = LocalContext.current
    val patient = viewModel.patientData.value
    var name by remember { mutableStateOf(patient?.name ?: "") }
    var age by remember { mutableStateOf(patient?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(patient?.gender ?: "") }
    var healthConditions by remember { mutableStateOf(patient?.healthConditions ?: "") }
    var address by remember { mutableStateOf(patient?.address ?: "") }
    var city by remember { mutableStateOf(patient?.city ?: "") }
    var state by remember { mutableStateOf(patient?.state ?: "") }
    var profileUri by remember { mutableStateOf<Uri?>(patient?.profileUri?.let { Uri.parse(it) }) }

    LaunchedEffect(Unit) {
        viewModel.getPatientDetails()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Edit Profile", style = MaterialTheme.typography.headlineSmall)

        ProfilePicturePicker(profileUri) {
            profileUri = it
        }

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        GenderSelector(gender) { gender = it }
        OutlinedTextField(value = healthConditions, onValueChange = { healthConditions = it }, label = { Text("Health Conditions") })
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address Line") })
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") })
        OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") })

        Button(onClick = {
            if (name.isNotEmpty()) {
                val updatedPatient = Patient(
                    name = name,
                    age = age.toIntOrNull() ?: 0,
                    gender = gender,
                    healthConditions = healthConditions,
                    address = address,
                    city = city,
                    state = state,
                    profileUri = profileUri.toString(),
                    email = patient?.email ?: ""
                )
                viewModel.updatePatientDetails(updatedPatient)
                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Save Changes")
        }
    }
}

