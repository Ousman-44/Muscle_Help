
package com.example.muscle_help.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscle_help.data.ExerciceRepository
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

            try {
                val exercices = mutableListOf<
                        com.example.muscle_help.model.Exercise
                        >()

                for (muscle in muscles) {

                    val resultat = repository
                        .recupererExercicesParMuscle(muscle)

                    exercices.addAll(
                        resultat.take(2)
                    )
                }

                _etat.value = EtatExercices(
                    exercices = exercices.distinctBy { it.id }
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _etat.value = EtatExercices(
                    erreur = e.message
                        ?: "Impossible de charger le programme"
                )
            }
        }
    }
}
