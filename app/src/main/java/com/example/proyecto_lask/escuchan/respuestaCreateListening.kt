package com.example.proyecto_lask.escuchan

data class respuestaCreateListening(
    val created_at: String,
    val id: Int,
    val id_cancion: Int,
    val id_usuario: Int,
    val updated_at: String
)