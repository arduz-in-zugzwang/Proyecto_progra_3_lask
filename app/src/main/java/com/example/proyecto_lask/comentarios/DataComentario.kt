package com.example.proyecto_lask.comentarios

data class DataComentario(
    val id: Int,
    val id_artista: String,
    val id_usuario: String,
    val texto: String,
    val created_at: String,
    val updated_at: String
)