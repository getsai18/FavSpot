package com.ejemplo.favspot.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ejemplo.favspot.api.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp, vertical = 50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Crea tu Cuenta",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            "Empieza a guardar tus lugares favoritos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Define tu Usuario") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Define tu Contraseña") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = RetrofitClient.instance.register(
                            mapOf(
                                "username" to username,
                                "password" to password
                            )
                        )

                        if (response.isSuccessful) {
                            message = "Usuario creado correctamente"
                            onRegisterSuccess()
                        } else {
                            message = "El usuario ya existe"
                        }
                    } catch (e: Exception) {
                        message = "Error creando usuario: ${e.message}"
                        Log.e("Registro", "Excepción: ", e)

                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Registrar", style = MaterialTheme.typography.titleMedium)
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(15.dp))
            val textColor = if (message.contains("correctamente")) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
            Text(message, color = textColor)
        }
    }
}