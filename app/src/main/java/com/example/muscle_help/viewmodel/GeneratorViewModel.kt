
package com.example.muscle_help.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscle_help.data.ExerciceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeneratorViewModel(
    private val repository: ExerciceRepository
) : ViewModel() {

    private val _etat = MutableStateFlow(EtatExercices())
    val etat: StateFlow<EtatExercices> = _etat

    private var generationEnCours: Job? = null

    fun genererSeance(
        muscle: String,
        nombreExercices: Int
    ) {

        generationEnCours?.cancel()

        generationEnCours = viewModelScope.launch {

            _etat.value = EtatExercices(
                chargement = true
            )

            try {
                val exercices = repository
                    .recupererExercicesParMuscle(muscle)

                val seance = exercices
                    .distinctBy { it.id }
                    .shuffled()
                    .take(nombreExercices.coerceIn(2, 8))

                _etat.value = EtatExercices(
                    exercices = seance
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _etat.value = EtatExercices(
                    erreur = e.message
                        ?: "Impossible de générer la séance"
                )
            }
        }
    }
}
