package com.example.appteca3

data class App(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    var esFavorita: Boolean = false
)