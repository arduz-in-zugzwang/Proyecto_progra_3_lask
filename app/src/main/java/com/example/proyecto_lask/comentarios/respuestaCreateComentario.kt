package com.example.proyecto_lask.comentarios

data class respuestaCreateComentario(
    val created_at: String,
    val id: Int,
    val id_artista: String,
    val id_usuario: String,
    val texto: String,
    val updated_at: String
)