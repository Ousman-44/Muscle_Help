
package com.example.muscle_help.data

import com.example.muscle_help.model.Exercice

data class PageExercices(
    val exercices: List<Exercice>,
    val curseurSuivant: String?
)
