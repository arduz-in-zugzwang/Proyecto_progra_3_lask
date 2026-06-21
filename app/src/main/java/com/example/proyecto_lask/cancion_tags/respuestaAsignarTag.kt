package com.example.proyecto_lask.cancion_tags

data class respuestaAsignarTag(
    val created_at: String,
    val id: Int,
    val id_cancion: String,
    val id_tag: String,
    val updated_at: String
)