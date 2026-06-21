package com.example.proyecto_lask.artistas

data class respuestaCreateArtista(
    val created_at: String,
    val id: Int,
    val id_usuario: String,
    val nombre_artistico: String,
    val updated_at: String
)