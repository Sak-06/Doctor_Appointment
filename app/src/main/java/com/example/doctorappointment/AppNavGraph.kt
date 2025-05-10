package com.example.doctorappointment

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph(navController: NavHostController){
    NavHost(
        navController= navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser != null) "patient_home" else "login"
    )
    {
        composable("login"){ LoginScreen(navController) }
        composable("patient_login") { PatientLoginScreen(navController) }
        composable("doctor_login") { DoctorLoginScreen(navController) }
        composable("register") { RegistrationScreen(navController) }
        composable("patient_home"){ PatientHome(navController)  }
    }
}