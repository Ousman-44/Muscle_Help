
package com.example.muscle_help.data

import android.util.Log
import com.example.muscle_help.model.Exercice
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ExerciceRepository(
    private val api: ExerciceAPI
) {

    // Exercices conservés temporairement en mémoire
    private val exercicesEnMemoire =
        mutableMapOf<String, List<Exercice>>()

    private val mutex = Mutex()

    // Noms alternatifs pour les muscles
    private val nomsAlternatifs = mapOf(
        "abs" to "abdominals",
        "quads" to "quadriceps",
        "delts" to "deltoids",
        "lats" to "upper back"
    )

    suspend fun recupererExercicesParMuscle(
        muscle: String
    ): List<Exercice> = mutex.withLock {

        // Utiliser les exercices déjà récupérés
        exercicesEnMemoire[muscle]?.let {
            return@withLock it
        }

        var exercices = api.recupererExercicesParMuscle(muscle)

        // Essayer un autre nom si aucun exercice
        if (exercices.isEmpty()) {
            val autreNom = nomsAlternatifs[muscle]

            if (autreNom != null) {
                exercices = api.recupererExercicesParMuscle(
                    autreNom
                )
            }
        }

        exercices = exercices.distinctBy { it.id }

        // Mémoriser également les listes vides
        exercicesEnMemoire[muscle] = exercices

        Log.d(
            "EXERCISE_REPOSITORY",
            "Muscle : $muscle | Exercices : ${exercices.size}"
        )

        exercices
    }
}
