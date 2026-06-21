package com.example.proyecto_lask.playlistcanciones

data class respuestaGetPlaylistCanciones(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)