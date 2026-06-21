package com.example.proyecto_lask.letras

data class respuestaCreateLetra(
    val created_at: String,
    val id: Int,
    val id_cancion: Int,
    val letra_cancion: String,
    val texto_fonetico: String,
    val updated_at: String
)