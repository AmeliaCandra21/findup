package com.example.findup.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen() {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val pinkButton = Color(0xFFEFA7A9)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(120.dp))

            Text(
                text = "FindUp",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = pinkButton
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kembali temukan yang hilang\nbersama komunitas.",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(text = "Username")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                        },
                        placeholder = {
                            Text("Masukkan Username")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(text = "Password")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                        },
                        placeholder = {
                            Text("Masukkan Password")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = pinkButton
                        )
                    ) {

                        Text(
                            text = "Masuk Ke Akun"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Text(text = "Belum punya akun? ")

                        Text(
                            text = "Daftar sekarang",
                            color = pinkButton,
                            modifier = Modifier.clickable {

                            }
                        )
                    }
                }
            }
        }
    }
}