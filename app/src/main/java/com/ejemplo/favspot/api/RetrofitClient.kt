package com.ejemplo.favspot.api

object RetrofitClient {

    //aqui ponesmos la url del servidor flask, esta cambian simpre
    // no olvidar nunca o no sirve
    private const val BASE_URL = "http://192.168.0.13:5000/"

    // esta es una instancia de ApiService que se inicializa solo cuando se usa
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


}