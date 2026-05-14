package com.example.findup.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findup.R
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

@Composable
fun LoginScreen() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    val pinkButton = Color(0xFFEFA7A9)
    val pinkLight = Color(0xFFEFA7A9)
    val grayField = Color(0xFFF2F2F2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {


        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 80.dp, y = (-120).dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(pinkLight, Color.White)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(80.dp))


            Image(
                painter = painterResource(id = R.drawable.logo_findup),
                contentDescription = "Logo FindUp",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kembali temukan yang hilang\nbersama komunitas",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "Username",
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Masukkan Email", color = Color.LightGray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = pinkButton,
                            unfocusedContainerColor = grayField,
                            focusedContainerColor = grayField
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Label Password + Lupa Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password",
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = "Lupa Password?",
                            color = pinkButton,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Masukkan Password", color = Color.LightGray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = pinkButton,
                            unfocusedContainerColor = grayField,
                            focusedContainerColor = grayField
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick =  {

                            if (email.isNotEmpty() && password.isNotEmpty()) {

                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->

                                        if (task.isSuccessful) {

                                            Toast.makeText(
                                                context,
                                                "Login Berhasil",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {

                                            Toast.makeText(
                                                context,
                                                "Login Gagal: ${task.exception?.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                            } else {

                                Toast.makeText(
                                    context,
                                    "Email dan Password wajib diisi",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = pinkButton
                        )
                    ) {
                        Text(
                            text = "Masuk Ke Akun",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Belum punya akun? ", color = Color.Gray)
                        Text(
                            text = "Daftar sekarang",
                            color = pinkButton,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }
        }
    }
}