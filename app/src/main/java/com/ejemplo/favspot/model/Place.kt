package com.ejemplo.favspot.model

import com.google.gson.annotations.SerializedName

data class Place (
    val id: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("notes")
    val notes: String? = "",

    @SerializedName("image_url")
    val imageUrl: String? = null
)