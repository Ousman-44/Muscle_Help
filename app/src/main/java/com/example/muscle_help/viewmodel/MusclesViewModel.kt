
package com.example.muscle_help.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscle_help.data.ExerciceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusclesViewModel(
    private val repository: ExerciceRepository
) : ViewModel() {

    private val _etat = MutableStateFlow(EtatExercices())
    val etat: StateFlow<EtatExercices> = _etat

    private var rechercheEnCours: Job? = null

    init {
        recupererExercices("pectorals")
    }

    fun recupererExercices(muscle: String) {

        rechercheEnCours?.cancel()

        rechercheEnCours = viewModelScope.launch {

            _etat.value = EtatExercices(
                chargement = true
            )

            try {
                val exercices = repository
                    .recupererExercicesParMuscle(muscle)

                _etat.value = EtatExercices(
                    exercices = exercices
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _etat.value = EtatExercices(
                    erreur = e.message ?: "Erreur inconnue"
                )
            }
        }
    }
}
