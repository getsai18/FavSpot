package com.ejemplo.favspot.api

interface ApiService {

    // registo de un usuarioo
    @POST("register")
    suspend fun register(@Body user: Map<String, String>): Response<Unit>

    // login
    @POST("login")
    suspend fun login(@Body user: Map<String, String>): Map<String, Int> // Retorna user_id

    // con esto obtenemos todos los lugares de un user
    @GET("places/{userId}")
    suspend fun getPlaces(@Path("userId") userId: Int): List<Place>

    // con estte solo obtenemos un lugar a diferencia de lo anterior
    @GET("place/{id}")
    suspend fun getPlace(@Path("id") id: Int): Response<Place>

    // crear un lugar
    @Multipart
    @POST("places")
    suspend fun createPlace(
        @Part("user_id") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("notes") notes: RequestBody,
        @Part image: MultipartBody.Part? // puede quedar nulo si no selecciona foto, no es obligatoria
    ): Response<Unit> // multipart nos permite enviar acgivos como imagenes, etc dentro d euna peticion http

    // paraa catualizar un place
    @PUT("places/{id}")
    suspend fun updatePlace(@Path("id") id: Int, @Body place: Place): Response<Unit>

    // eliminar place
    @DELETE("places/{id}")
    suspend fun deletePlace(@Path("id") id: Int): Response<Unit>
}


}