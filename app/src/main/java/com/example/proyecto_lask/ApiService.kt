package com.example.proyecto_lask

import com.example.proyecto_lask.model.respuestaCreateTag
import com.example.proyecto_lask.model.respuestaTags
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET(value="api/tags")
    suspend fun getTags(): Response<respuestaTags>
    @POST("api/tags")
    suspend fun createTags(@Field("nombre_tag")nombre_tag: String): Response<respuestaCreateTag>
}
object RetrofitClient{
    fun create(): com.example.proyecto_lask.ApiService{
        val retrofit= Retrofit.Builder()
            .baseUrl("http://192.168.0.10/lask_bd/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(com.example.proyecto_lask.ApiService::class.java)
    }
}