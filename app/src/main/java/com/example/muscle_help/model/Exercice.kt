package com.example.muscle_help.model

data class Exercice(
    val id: String,
    val nom: String,
    val exercicesCibles: List<String>,
    val equipements: List<String>,
    val instructions: List<String>,
    val gifUrl: String = ""
    )
