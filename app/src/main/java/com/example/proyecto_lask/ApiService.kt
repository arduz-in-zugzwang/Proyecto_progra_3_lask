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
import com.example.proyecto_lask.cancion_tags.respuestaCreateCancionTag
import com.example.proyecto_lask.canciones.respuestaCreateCancion
import com.example.proyecto_lask.canciones.respuestaDeleteCancion
import com.example.proyecto_lask.canciones.respuestaGetCancion
import com.example.proyecto_lask.canciones.respuestaGetCanciones
import com.example.proyecto_lask.canciones.respuestaUpdateCancion
import com.example.proyecto_lask.comentarios.respuestaCreateComentario
import com.example.proyecto_lask.comentarios.respuestaDeleteComentario
import com.example.proyecto_lask.comentarios.respuestaGetComentarios
import com.example.proyecto_lask.letras.respuestaCreateLetra
import com.example.proyecto_lask.letras.respuestaGetAllLetras
import com.example.proyecto_lask.letras.respuestaGetLetras
import com.example.proyecto_lask.letras.respuestaUpdateLetra
import com.example.proyecto_lask.paises.respuestaGetPaises
import com.example.proyecto_lask.playlists.respuestaCreatePlaylist
import com.example.proyecto_lask.playlists.respuestaDeletePlaylist
import com.example.proyecto_lask.playlists.respuestaUpdatePlaylist
import com.example.proyecto_lask.roles.respuestagetRoles
import com.example.proyecto_lask.tags.respuestaCreateTag
import com.example.proyecto_lask.tags.respuestaDeleteTag
import com.example.proyecto_lask.tags.respuestaTagPorId
import com.example.proyecto_lask.tags.respuestaTags
import com.example.proyecto_lask.tags.respuestaUpdateTag
import com.example.proyecto_lask.users.respuestaCreateUser
import com.example.proyecto_lask.users.respuestaGetUser
import com.example.proyecto_lask.users.respuestaGetUsers
import com.example.proyecto_lask.users.respuestaLogin
import com.example.proyecto_lask.users.respuestaUpdateUser
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
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

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
        @Field("nombre_artistico") nombre_artistico: String
    ): Response<respuestaUpdateArtista>
    @DELETE("api/artistas/{id}")
    suspend fun deleteArtista(
        @Path("id") id: Int
    ): Response<respuestaDeleteArtista>


            //users
    @GET("api/users")
    suspend fun getUsers(): Response<respuestaGetUsers>
    @GET("api/users/{id}")
    suspend fun getUser(
        @Path("id") id: Int
    ): Response<respuestaGetUser>
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
    @FormUrlEncoded
    @PATCH("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Field("name") name: String,
        @Field("password") password: String,
        @Field("id_pais") idPais: Int,
        @Field("id_rol") idRol: Int,
        @Field("email") email: String = "",
        @Field("pfp") pfp: String = "",
        @Field("bio") bio: String = ""
    ): Response<respuestaUpdateUser>


    //paises
    @GET("api/paises")
    suspend fun getPaises(): Response<respuestaGetPaises>

    //canciones
    @GET(value="api/canciones")
    suspend fun getCanciones(): Response<respuestaGetCanciones>
    @GET(value="api/canciones/{id}")
    suspend fun getCancion(@Path("id") id: Int): Response<respuestaGetCancion>
    @Multipart
    @POST("api/canciones")
    suspend fun createCancion(
        @Part("id_album") id_album: RequestBody,
        @Part("id_artista") id_artista: RequestBody,
        @Part("nombre_cancion") nombre_cancion: RequestBody,
        @Part("portada_cancion") portada_cancion: RequestBody,
        @Part audio: MultipartBody.Part
    ): Response<respuestaCreateCancion>
    @FormUrlEncoded
    @PATCH("api/canciones/{id}")
    suspend fun updateCancion(
        @Path("id") id: Int,
        @Field("id_album") id_album: Int,
        @Field("nombre_cancion") nombre_cancion: String,
        @Field("portada_cancion") portada_cancion: String,
        @Field("path_link") path_link: String
    ): Response<respuestaUpdateCancion>
    @DELETE("api/canciones/{id}")
    suspend fun deleteCancion(
        @Path("id") id: Int
    ): Response<respuestaDeleteCancion>

    // algo con los tags para mi home Tags-Cancion basicamente
    @GET("api/tags/{id}/canciones")
    suspend fun getCancionesPorTag(
        @Path("id") id: Int
    ): Response<respuestaGetCanciones>
    @FormUrlEncoded
    @POST("api/cancion-tags")
    suspend fun createCancionTag(
        @Field("id_cancion") idCancion: Int,
        @Field("id_tag") idTag: Int
    ): Response<respuestaCreateCancionTag>
    //comentarios
    @GET("api/comentarios-artista")
    suspend fun getComentarios(): Response<respuestaGetComentarios>
    @FormUrlEncoded
    @POST("api/comentarios-artista")
    suspend fun createComentario(
        @Field("id_artista") id_artista: Int,
        @Field("id_usuario") id_usuario: Int,
        @Field("texto") texto: String,
    ): Response<respuestaCreateComentario>
    @DELETE("api/comentarios-artista/{id}")
    suspend fun deleteComentario(
        @Path("id") id: Int
    ): Response<respuestaDeleteComentario>

    //letras
    @FormUrlEncoded
    @POST("api/letras")
    suspend fun createLetra(
        @Field("id_cancion") id_cancion: Int,
        @Field("texto_fonetico") texto_fonetico: String?,
        @Field("letra_cancion") letra_cancion: String
    ): Response<respuestaCreateLetra>
    @FormUrlEncoded
    @PATCH("api/letras/{id}")
    suspend fun updateLetra(
        @Path("id") id: Int,
        @Field("id_cancion") id_cancion: Int,
        @Field("texto_fonetico") texto_fonetico: String?,
        @Field("letra_cancion") letra_cancion: String
    ): Response<respuestaUpdateLetra>
    @DELETE("api/letras/{id}")
    suspend fun deleteLetra(
        @Path("id") id: Int
    ): Response<respuestaDelete>
    @GET("api/letras")
    suspend fun getLetras(): Response<respuestaGetAllLetras>
    @GET("api/letras/{id}")
    suspend fun getLetra(
        @Path("id") id: Int
    ): Response<respuestaGetLetras>

    //roles
    @GET("api/roles")
    suspend fun getRoles(): Response<respuestagetRoles>

    //playlists
    @FormUrlEncoded
    @POST("api/playlists")
    suspend fun createPlaylist(
        @Field("nombre_playlist") nombre_playlist: String,
        @Field("id_usuario") id_usuario: Int,
        @Field("privacidad_playlist") privacidad_playlist: Boolean
    ): Response<respuestaCreatePlaylist>
    @DELETE("api/playlists/{id}")
    suspend fun deletePlaylist(
        @Path("id") id: Int
    ): Response<respuestaDeletePlaylist>
    @FormUrlEncoded
    @PATCH("api/playlists/{id}")
    suspend fun updatePlaylist(
        @Path("id") id: Int,
        @Field("nombre_playlist") nombre: String?,
        @Field("privacidad_playlist") privacidad: Boolean?
    ): Response<respuestaUpdatePlaylist>

    //para loguin
    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("name") name: String,
        @Field("password") password: String
    ): Response<respuestaLogin>
}
object RetrofitClient{
    fun create(): com.example.proyecto_lask.ApiService{
        val client = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
        val retrofit= Retrofit.Builder()
            //AQUI CAMBIAR EL IP ASAP
            .baseUrl("http://192.168.1.11/lask_bd/public/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(com.example.proyecto_lask.ApiService::class.java)
    }
}