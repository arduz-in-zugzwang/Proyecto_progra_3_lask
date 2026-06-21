package com.example.proyecto_lask.albumes

data class respuestaGetAlbumes(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)