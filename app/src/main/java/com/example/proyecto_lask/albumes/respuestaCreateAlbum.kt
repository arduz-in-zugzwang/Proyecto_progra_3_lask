package com.example.proyecto_lask.albumes

data class respuestaCreateAlbum(
    val created_at: String,
    val descripcion_album: String,
    val id: Int,
    val id_artista: String,
    val nombre_album: String,
    val portada_album: String,
    val updated_at: String
)