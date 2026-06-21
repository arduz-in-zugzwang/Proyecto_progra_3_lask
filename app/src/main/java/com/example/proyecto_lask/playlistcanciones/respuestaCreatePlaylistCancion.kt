package com.example.proyecto_lask.playlistcanciones

data class respuestaCreatePlaylistCancion(
    val created_at: String,
    val id: Int,
    val id_cancion: String,
    val id_playlist: String,
    val updated_at: String
)