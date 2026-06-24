package com.example.proyecto_lask.users

data class respuestaUpdateUser(
    val bio: String,
    val created_at: String,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val id_pais: Any,
    val id_rol: Any,
    val name: String,
    val password: String,
    val pfp: String,
    val remember_token: Any,
    val updated_at: String
)