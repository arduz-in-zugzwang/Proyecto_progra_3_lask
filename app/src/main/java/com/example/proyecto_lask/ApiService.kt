package com.example.proyecto_lask

import com.example.proyecto_lask.model.respuestaCreateTag
import com.example.proyecto_lask.model.respuestaDeleteTag
import com.example.proyecto_lask.model.respuestaTagPorId
import com.example.proyecto_lask.model.respuestaTags
import com.example.proyecto_lask.model.respuestaUpdateTag
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET(value="api/tags")
    suspend fun getTags(): Response<respuestaTags>
    @GET(value="api/tags/{id}")
    suspend fun getTag(@Path("id") id: Int): Response<respuestaTagPorId>
    @FormUrlEncoded
    @POST("api/tags")
    suspend fun createTags(@Field("nombre_tag")nombre_tag: String,@Field("descripcion_tag") descripcion_tag: String): Response<respuestaCreateTag>
    @FormUrlEncoded
    @PATCH("api/tags/{id}")
    suspend fun actualizarTag(@Path("id") id: Int, @Field("nombre_tag")nombre_tag: String): Response<respuestaUpdateTag>
    @DELETE("api/tags/{id}")
    suspend fun deleteTag(@Path("id") id: Int): Response<respuestaDeleteTag>
}
object RetrofitClient{
    fun create(): com.example.proyecto_lask.ApiService{
        val retrofit= Retrofit.Builder()
            //AQUI CAMBIAR EL IP ASAP
            .baseUrl("http://192.168.1.8/lask_bd/public/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(com.example.proyecto_lask.ApiService::class.java)
    }
}