package com.example.proyecto_lask.users

data class respuestaGetUsers(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)