
package com.example.muscle_help.data

import android.util.Log
import com.example.muscle_help.model.Exercice
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ExerciceRepository(
    private val api: ExerciceAPI
) {

    // Cache temporaire en mémoire
    private val exercicesEnMemoire =
        mutableMapOf<String, List<Exercice>>()

    private val mutex = Mutex()

    // Noms alternatifs des muscles
    private val nomsAlternatifs = mapOf(
        "abs" to "abdominals",
        "quads" to "quadriceps",
        "delts" to "deltoids",
        "lats" to "upper back"
    )

    suspend fun recupererExercicesParMuscle(
        muscle: String
    ): List<Exercice> = mutex.withLock {

        // Vérifier le cache
        exercicesEnMemoire[muscle]?.let {
            return@withLock it
        }

        // Récupérer les exercices
        var exercices =
            api.recupererExercicesParMuscle(muscle)

        // Essayer un nom alternatif si nécessaire
        if (exercices.isEmpty()) {

            val nomAlternatif = nomsAlternatifs[muscle]

            if (nomAlternatif != null) {

                Log.d(
                    "EXERCISE_REPOSITORY",
                    "Nouvelle recherche avec : $nomAlternatif"
                )

                exercices =
                    api.recupererExercicesParMuscle(nomAlternatif)
            }
        }

        // Supprimer les doublons
        exercices = exercices.distinctBy { it.id }

        // Enregistrer en mémoire
        if (exercices.isNotEmpty()) {
            exercicesEnMemoire[muscle] = exercices
        }

        Log.d(
            "EXERCISE_REPOSITORY",
            "Muscle : $muscle | " +
                    "Nombre d'exercices : ${exercices.size}"
        )

        exercices
    }
}
