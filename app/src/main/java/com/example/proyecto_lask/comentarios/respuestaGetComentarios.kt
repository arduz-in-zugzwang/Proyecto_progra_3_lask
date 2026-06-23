package com.example.proyecto_lask.comentarios

data class respuestaGetComentarios(
    val `data`: List<DataComentario>,
    val links: Links,
    val meta: Meta
)