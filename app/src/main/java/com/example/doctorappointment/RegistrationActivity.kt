package com.example.doctorappointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.doctorappointment.ui.theme.DoctorAppointmentTheme
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale



class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFFFFFF)
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "register") {
                        composable("register") {
                            RegistrationScreen(navController = navController)
                        }
                        composable("login") {
                            LoginScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionRequest(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

//    when {
//        permissionState.hasPermission -> {
//            onPermissionGranted()
//        }
//
//        permissionState.shouldShowRationale -> {
//            // Optional: Show UI explaining why camera is needed
//            Text("Camera permission is needed to upload a profile picture.")
//        }
//
//        !permissionState.permissionRequested -> {
//            // Initial state, waiting for request
//        }
//
//        else -> {
//            Text("Camera permission denied. Please enable it in settings.")
//        }
//    }
}

fun showDatePicker(context: Context, onDateSelected: (Date) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(context, { _, year, month, day ->
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, day)
        onDateSelected(selectedDate.time)
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
}

@Composable
fun ProfilePicturePicker(
    profileUri: Uri?,
    onImageSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    CameraPermissionRequest {  }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) onImageSelected(it)
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) cameraImageUri?.let(onImageSelected)
    }

    fun launchCamera() {
        val imageFile = File(
            context.getExternalFilesDir(null),
            "profile_${System.currentTimeMillis()}.jpg"
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        cameraImageUri = uri
        cameraLauncher.launch(uri)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (profileUri != null) {
            Image(
                painter = rememberAsyncImagePainter(profileUri),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
        } else {
            Icon(
                Icons.Default.Person,
                contentDescription = "Default Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Gallery")
            }
            Button(onClick = { launchCamera() }) {
                Text("Camera")
            }
        }
    }
}

fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val cal = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute ->
            val amPm = if (hour < 12) "AM" else "PM"
            val formattedHour = if (hour % 12 == 0) 12 else hour % 12
            onTimeSelected(String.format("%02d:%02d %s", formattedHour, minute, amPm))
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        false
    ).show()
}

@Composable
fun DoctorRegistrationScreen(
    navController: NavController,
    viewModel: DoctorViewModel = viewModel()
) {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var name by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }

    var addressline by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var date by remember{ mutableStateOf("") }
    var availabilityMap by remember { mutableStateOf(mutableMapOf<String, MutableList<Pair<String, String>>>()) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Doctor Registration", style = MaterialTheme.typography.headlineSmall)
        ProfilePicturePicker(profileUri = profileImageUri) {
            profileImageUri = it
        }

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = specialty, onValueChange = { specialty = it }, label = { Text("Specialty") })
        OutlinedTextField(value = experience, onValueChange = { experience = it }, label = { Text("Experience (years)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = addressline, onValueChange = { addressline = it }, label = { Text("Address Line") })
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") })
        OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") })

        Button(onClick = {
            val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()
            datePicker.show((context as AppCompatActivity).supportFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener { millis ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(millis)
                date = selectedDate
            }
        }) {
            Text("Pick Available Dates")
        }
        if(date.isNotEmpty()){
            Text("Selected Date: $date")
        }
            Row {
                Button(onClick = {
                    showTimePicker(context) { time -> startTime = time }
                }, modifier = Modifier.weight(1f)) {
                    Text(if (startTime.isEmpty()) "Start Time" else startTime)
                }

                Spacer(Modifier.width(8.dp))

                Button(onClick = {
                    showTimePicker(context) { time -> endTime = time }
                }, modifier = Modifier.weight(1f)) {
                    Text(if (endTime.isEmpty()) "End Time" else endTime)
                }
            }

            Button(onClick = {
                if (startTime.isNotEmpty() && endTime.isNotEmpty() && date.isNotEmpty()) {
                    val updatedSlots = availabilityMap[date]?.toMutableList()?: mutableListOf()
                    updatedSlots.add(Pair(startTime, endTime))
                    availabilityMap[date] = updatedSlots
                    startTime = ""
                    endTime = ""
                }
            }) {
                Text("Add Time Slot")
            }
        if (availabilityMap.containsKey(date)) {
            Text("Available Slots for $date:")
            availabilityMap[date]?.forEach { slot ->
                Text("Slot: ${slot.first} - ${slot.second}")
            }
        }

            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Transform availability map to Firestore-friendly format
                            val availabilityFirebase = availabilityMap.mapValues { entry ->
                                entry.value.map { slot ->
                                    mapOf("startTime" to slot.first, "endTime" to slot.second)
                                }
                            }

                            viewModel.registerDoctor(
                                name = name,
                                specialty = specialty,
                                experience = experience.toIntOrNull() ?: 0,
                                availability = availabilityFirebase,
                                email = email,
                                address = addressline,
                                city = city,
                                state = state,
                                profile = profileImageUri
                            )

                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                            Toast.makeText(context, "Doctor Registered", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Email and Password required", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Register Doctor")
        }
    }


@Composable
fun PatientRegistrationScreen(
    navController: NavController,
    viewModel: PatientViewModel = viewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var healthConditions by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var addressline by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Patient Registration", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(15.dp))
        ProfilePicturePicker(profileUri = profileImageUri) {
            profileImageUri = it
        }
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        GenderSelector(gender) { selectedGender ->
            gender = selectedGender
        }

        OutlinedTextField(
            value = healthConditions,
            onValueChange = { healthConditions = it },
            label = { Text("Health Conditions") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(value = addressline, onValueChange = { addressline = it }, label = { Text("Address Line") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") })
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            viewModel.registerPatient(
                                name = name,
                                age = age.toIntOrNull() ?: 0,
                                gender = gender,
                                healthConditions = healthConditions,
                                email = email,
                                address = addressline,
                                city = city,
                                state = state,
                                profile = profileImageUri
                            )
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                            Toast.makeText(context, "Patient Registered", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Register Patient")
        }
    }
}

@Composable
fun GenderSelector(selectedGender: String, onGenderSelected: (String) -> Unit) {
    val genders = listOf("Male", "Female", "Other")
    Column {
        Text("Gender")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            genders.forEach { gender ->
                OutlinedButton(
                    onClick = { onGenderSelected(gender) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (gender == selectedGender) Color.White else MaterialTheme.colorScheme.primary,
                        containerColor = if (gender == selectedGender) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Text(gender)
                }
            }
        }
    }
}

@Composable
fun RegistrationScreen(navController: NavController) {
    var selectedRole by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Role", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 50.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { selectedRole = "Doctor" },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = if (selectedRole == "Doctor") Color(0xFF629457) else Color(0xFF1779A8)

                )
            ) {
                Text("Doctor")
            }

            Button(
                onClick = { selectedRole = "Patient" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "Patient") Color(0xFF629457) else Color(0xFF1779A8)
                )
            ) {
                Text("Patient")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Conditional display of registration forms
        when (selectedRole) {
            "Doctor" -> DoctorRegistrationScreen(navController = navController)
            "Patient" -> PatientRegistrationScreen(navController = navController)
        }
    } 
}

// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview6() {
//    DoctorAppointmentTheme {
//        RegistrationScreen()
//    }
// }
