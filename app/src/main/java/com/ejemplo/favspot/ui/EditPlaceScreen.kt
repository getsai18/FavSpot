package com.ejemplo.favspot.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ejemplo.favspot.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaceScreen(
    placeId: Int,
    onUpdated: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var storedUserId by remember { mutableStateOf(0) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()


    LaunchedEffect(placeId) {
        scope.launch {
            try {
                val response = RetrofitClient.instance.getPlace(placeId)
                val place = response.body()
                if (response.isSuccessful && place != null) {
                    name = place.name
                    notes = place.notes ?: ""
                    latitude = place.latitude
                    longitude = place.longitude
                    storedUserId = place.userId
                    imageUrl = place.imageUrl
                } else {
                    error = "Lugar no encontrado"
                }
            } catch (e: Exception) {
                Log.e("EditPlace", "Error al cargar lugar", e)
                error = "Error al cargar lugar: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Editar Ubicación",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Imagen del lugar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("Sin imagen", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notas") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(15.dp))
        Text(
            "Coordenadas actuales: $latitude, $longitude",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(30.dp))


        Button(
            onClick = {
                scope.launch {
                    try {
                        val updatedPlace = Place(
                            id = placeId,
                            userId = storedUserId,
                            name = name,
                            latitude = latitude,
                            longitude = longitude,
                            notes = notes,
                            imageUrl = null
                        )
                        val response = RetrofitClient.instance.updatePlace(placeId, updatedPlace)
                        if (response.isSuccessful) {
                            onUpdated()
                        } else {
                            error = "Error al actualizar: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        error = "Excepción: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Filled.Save, contentDescription = "Guardar", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar cambios", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = RetrofitClient.instance.deletePlace(placeId)
                        if (response.isSuccessful) {
                            onUpdated()
                        } else {
                            error = "Error al eliminar"
                        }
                    } catch (e: Exception) {
                        error = "Excepción: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.9f))
        ) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Eliminar lugar", style = MaterialTheme.typography.titleMedium)
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}