package com.example.findup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.findup.navigasi.AppNav
import com.example.findup.screen.LoginScreen
import com.example.findup.screen.RegistrasiScreen
import com.example.findup.ui.theme.FindUpTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindUpTheme {
                val navController = rememberNavController()
                val user = FirebaseAuth.getInstance().currentUser
                val startDestination = if (user != null) "main" else "login"

                NavHost(
                    navController    = navController,
                    startDestination = startDestination
                ) {
                    composable("register") { RegistrasiScreen(navController) }
                    composable("login")    { LoginScreen(navController) }
                    composable("main") { AppNav(rootNavController = navController) }
                }
            }
        }
    }
}