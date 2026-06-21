package com.example.proyecto_lask

import com.example.proyecto_lask.albumes.respuestaCreateAlbum
import com.example.proyecto_lask.albumes.respuestaDeleteAlbum
import com.example.proyecto_lask.albumes.respuestaGetAlbum
import com.example.proyecto_lask.albumes.respuestaGetAlbumes
import com.example.proyecto_lask.albumes.respuestaUpdateAlbum
import com.example.proyecto_lask.artistas.respuestaCreateArtista
import com.example.proyecto_lask.artistas.respuestaDeleteArtista
import com.example.proyecto_lask.artistas.respuestaGetArtista
import com.example.proyecto_lask.artistas.respuestaGetArtistas
import com.example.proyecto_lask.artistas.respuestaUpdateArtista
import com.example.proyecto_lask.paises.respuestaGetPaises
import com.example.proyecto_lask.tags.respuestaCreateTag
import com.example.proyecto_lask.tags.respuestaDeleteTag
import com.example.proyecto_lask.tags.respuestaTagPorId
import com.example.proyecto_lask.tags.respuestaTags
import com.example.proyecto_lask.tags.respuestaUpdateTag
import com.example.proyecto_lask.users.respuestaCreateUser
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

    //tags
    @GET(value="api/tags")
    suspend fun getTags(): Response<respuestaTags>
    @GET(value="api/tags/{id}")
    suspend fun getTag(@Path("id") id: Int): Response<respuestaTagPorId>
    @FormUrlEncoded
    @POST("api/tags")
    suspend fun createTags(@Field("nombre_tag")nombre_tag: String,@Field("descripcion_tag") descripcion_tag: String): Response<respuestaCreateTag>
    @FormUrlEncoded
    @PATCH("api/tags/{id}")
    suspend fun actualizarTag(@Path("id") id: Int, @Field("nombre_tag")nombre_tag: String,@Field("descripcion_tag") descripcion_tag: String): Response<respuestaUpdateTag>
    @DELETE("api/tags/{id}")
    suspend fun deleteTag(@Path("id") id: Int): Response<respuestaDeleteTag>


// albumes

    @GET("api/albumes")
    suspend fun getAlbumes(): Response<respuestaGetAlbumes>

    @GET("api/albumes/{id}")
    suspend fun getAlbum(
        @Path("id") id: Int
    ): Response<respuestaGetAlbum>

    @FormUrlEncoded
    @POST("api/albumes")
    suspend fun createAlbum(
        @Field("nombre_album") nombre_album: String,
        @Field("descripcion_album") descripcion_album: String,
        @Field("portada_album") portada_album: String,
        @Field("id_artista") id_artista: Int
    ): Response<respuestaCreateAlbum>

    @FormUrlEncoded
    @PATCH("api/albumes/{id}")
    suspend fun actualizarAlbum(
        @Path("id") id: Int,
        @Field("nombre_album") nombre_album: String,
        @Field("descripcion_album") descripcion_album: String,
        @Field("portada_album") portada_album: String
    ): Response<respuestaUpdateAlbum>

    @DELETE("api/albumes/{id}")
    suspend fun deleteAlbum(
        @Path("id") id: Int
    ): Response<respuestaDeleteAlbum>


    //artistas
    @GET("api/artistas")
    suspend fun getArtistas(): Response<respuestaGetArtistas>

    @GET("api/artistas/{id}")
    suspend fun getArtista(
        @Path("id") id: Int
    ): Response<respuestaGetArtista>

    @FormUrlEncoded
    @POST("api/artistas")
    suspend fun createArtista(
        @Field("id_usuario") id_usuario: Int,
        @Field("nombre_artistico") nombre_artistico: String

    ): Response<respuestaCreateArtista>

    @FormUrlEncoded
    @PATCH("api/artistas/{id}")
    suspend fun actualizarArtista(
        @Path("id") id: Int,
        @Field("nombre_artistico") nombre_artistico: String,
    ): Response<respuestaUpdateArtista>

    @DELETE("api/artistas/{id}")
    suspend fun deleteArtista(
        @Path("id") id: Int
    ): Response<respuestaDeleteArtista>


            //users
    @FormUrlEncoded
    @POST("api/users")
    suspend fun createUser(
                @Field("name") name: String,
                @Field("password") password: String,
                @Field("id_pais") idPais: Int,
                @Field("id_rol") idRol: Int,
                @Field("email") email: String = "",
                @Field("pfp") pfp: String = "",
                @Field("bio") bio: String = ""
    ): Response<respuestaCreateUser>

    //paises
    @GET("api/paises")
    suspend fun getPaises(): Response<respuestaGetPaises>

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