package com.example.proyecto_lask.likes

data class respuestaCreateLike(
    val created_at: String,
    val id: Int,
    val id_cancion: String,
    val id_usuario: String,
    val updated_at: String
)