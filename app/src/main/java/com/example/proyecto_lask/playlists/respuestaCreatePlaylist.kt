package com.example.proyecto_lask.playlists

data class respuestaCreatePlaylist(
    val created_at: String,
    val id: Int,
    val id_usuario: String,
    val nombre_playlist: String,
    val updated_at: String
)