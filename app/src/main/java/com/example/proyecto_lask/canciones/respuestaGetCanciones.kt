package com.example.proyecto_lask.canciones

data class respuestaGetCanciones(
    val `data`: List<DataX>,
    val links: Links,
    val meta: Meta
)