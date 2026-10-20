
package com.example.muscle_help.viewmodel

import com.example.muscle_help.model.Exercice

data class EtatExercices(
    val exercices: List<Exercice> = emptyList(),
    val chargement: Boolean = false,
    val erreur: String? = null
)
