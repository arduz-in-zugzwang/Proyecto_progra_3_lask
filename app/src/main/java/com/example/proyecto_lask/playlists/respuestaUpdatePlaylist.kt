package com.example.proyecto_lask.playlists

data class respuestaUpdatePlaylist(
    val created_at: String,
    val fecha_playlist: Any,
    val id: Int,
    val id_usuario: Int,
    val nombre_playlist: String,
    val privacidad_playlist: Boolean,
    val updated_at: String
)