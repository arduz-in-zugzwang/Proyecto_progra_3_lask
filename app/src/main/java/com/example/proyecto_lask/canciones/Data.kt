package com.example.proyecto_lask.canciones

data class Data(
    val created_at: String,
    val id: Int,
    val id_album: Int,
    val id_artista: Int,
    val nombre_cancion: String,
    val numero_pista: Any,
    val path_link: String,
    val portada_cancion: String,
    val updated_at: String
)