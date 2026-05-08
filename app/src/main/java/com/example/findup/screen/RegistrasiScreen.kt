package com.example.findup.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import com.example.findup.R

@Composable
fun RegistrasiScreen() {
    val pinkButton = Color(0xFFEFA7A9)
    val pinkLight = Color(0xFFEFA7A9)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var konfirmasiPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .size(350.dp)
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

            Text(
                text = "Bantu sesama menemukan barang yang\nhilang dengan mudah dan cepat.",
                color = Color(0xFF888888),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    // Username
                    Text(text = "Username", fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("Buat Username", color = Color.LightGray) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Person, contentDescription = null, tint = Color.LightGray)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = pinkButton,
                            unfocusedContainerColor = Color(0xFFF2F2F2),
                            focusedContainerColor = Color(0xFFF2F2F2)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    Text(text = "Password", fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Buat Password", color = Color.LightGray) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = Color.LightGray)
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = pinkButton,
                            unfocusedContainerColor = Color(0xFFF2F2F2),
                            focusedContainerColor = Color(0xFFF2F2F2)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Konfirmasi Password
                    Text(text = "Konfirmasi Password", fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = konfirmasiPassword,
                        onValueChange = { konfirmasiPassword = it },
                        placeholder = { Text("Konfirmasi Password", color = Color.LightGray) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = Color.LightGray)
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = if (konfirmasiPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.clickable { konfirmasiPasswordVisible = !konfirmasiPasswordVisible }
                            )
                        },
                        visualTransformation = if (konfirmasiPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = pinkButton,
                            unfocusedContainerColor = Color(0xFFF2F2F2),
                            focusedContainerColor = Color(0xFFF2F2F2)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Syarat & Ketentuan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Saya menyetujui ", color = Color.Gray, fontSize = 12.sp)
                        Text(text = "Syarat & Ketentuan", color = pinkButton, fontSize = 12.sp, modifier = Modifier.clickable { })
                        Text(text = " serta", color = Color.Gray, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Kebijakan Privasi", color = pinkButton, fontSize = 12.sp, modifier = Modifier.clickable { })
                        Text(text = ".", color = Color.Gray, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tombol Daftar
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = pinkButton)
                    ) {
                        Text(text = "Daftar Sekarang", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sudah punya akun
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Sudah punya akun? ", color = Color.Gray)
                        Text(
                            text = "Masuk di sini",
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