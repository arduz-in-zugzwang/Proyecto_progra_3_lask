package com.example.proyecto_lask.artistas

data class respuestaGetArtistas(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)