package com.example.proyecto_lask.playlists

data class respuestaGetPlaylists(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)