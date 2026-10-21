
package com.example.muscle_help.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscle_help.data.ExerciceRepository
import com.example.muscle_help.model.Exercice
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PPLViewModel(
    private val repository: ExerciceRepository
) : ViewModel() {

    private val _etat = MutableStateFlow(EtatExercices())

    val etat: StateFlow<EtatExercices> = _etat

    private var rechercheEnCours: Job? = null

    private val programmes = mapOf(
        "Push" to listOf(
            "pectorals", "delts", "triceps"
        ),
        "Pull" to listOf(
            "lats", "biceps"
        ),
        "Legs" to listOf(
            "quads", "hamstrings", "glutes"
        )
    )

    init {
        recupererProgramme("Push")
    }

    fun recupererProgramme(programme: String) {

        val muscles = programmes[programme] ?: return

        rechercheEnCours?.cancel()

        rechercheEnCours = viewModelScope.launch {

            _etat.value = EtatExercices(
                chargement = true
            )

            val exercices = mutableListOf<Exercice>()
            var erreurRencontree = false

            try {
                for (muscle in muscles) {

                    try {
                        val resultat = repository
                            .recupererExercicesParMuscle(muscle)

                        exercices.addAll(resultat.take(2))

                    } catch (e: CancellationException) {
                        throw e

                    } catch (e: Exception) {
                        erreurRencontree = true

                        Log.e(
                            "PPL_VIEWMODEL",
                            "Erreur pour le muscle : $muscle",
                            e
                        )
                    }
                }

                val seance = exercices.distinctBy { it.id }

                _etat.value = EtatExercices(
                    exercices = seance,
                    erreur = if (
                        erreurRencontree && seance.isEmpty()
                    ) {
                        "Impossible de charger le programme."
                    } else {
                        null
                    }
                )

            } catch (e: CancellationException) {
                throw e
            }
        }
    }
}
