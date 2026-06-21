package com.example.proyecto_lask.canciones

data class respuestaGetCanciones(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)