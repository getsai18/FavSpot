package com.ejemplo.favspot.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    userId: Int,
    onSaved: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                error = "Permiso de ubicación denegado"
            }
        }
    )

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            permissionLauncher.launch(permission)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "📍 Agregar Nuevo Lugar",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 15.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del lugar") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notas y Descripción") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("🖼️ Seleccionar Imagen", color = Color.Black)
        }

        if (selectedImageUri != null) {
            Spacer(modifier = Modifier.height(15.dp))
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = "Imagen seleccionada",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                scope.launch {
                    val location = LocationHelper.getCurrentLocation(context)
                    if (location != null) {
                        val (latitude, longitude) = location
                        try {
                            val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                            val latBody = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                            val longBody = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                            val notesBody = notes.toRequestBody("text/plain".toMediaTypeOrNull())

                            var imagePart: MultipartBody.Part? = null
                            if (selectedImageUri != null) {
                                val file = UriUtils.getFileFromUri(context, selectedImageUri!!)
                                if (file != null) {
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                                }
                            }

                            val response = RetrofitClient.instance.createPlace(
                                userIdBody, nameBody, latBody, longBody, notesBody, imagePart
                            )

                            if (response.isSuccessful) {
                                onSaved()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                error = "Error: ${response.code()} - $errorBody"
                            }
                        } catch (e: Exception) {
                            error = "Excepción: ${e.message}"
                        }
                    } else {
                        error = "No se pudo obtener la ubicación"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Guardar Lugar", style = MaterialTheme.typography.titleMedium)
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}