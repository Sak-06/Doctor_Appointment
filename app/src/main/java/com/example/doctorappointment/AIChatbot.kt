package com.example.doctorappointment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.doctorappointment.ui.theme.DoctorAppointmentTheme

class AIChatbot : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointmentTheme {
                Surface {
                    AIChatbotScreen()
                }
            }
        }
    }
}

@Composable
fun AIChatbotScreen(){

}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview6() {
//    DoctorAppointmentTheme {
//        Greeting6("Android")
//    }
//}