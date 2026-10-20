
package com.example.muscle_help.data

import com.example.muscle_help.model.Exercice
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ExerciceRepository(
    private val api: ExerciceAPI
) {

    private val pagesChargees =
        mutableMapOf<Int, List<Exercice>>()

    private val mutex = Mutex()

    suspend fun recupererExercicesParMuscle(
        muscle: String
    ): List<Exercice> = mutex.withLock {

        val exercicesTrouves = mutableListOf<Exercice>()

        val taillePage = 100
        val nombreMaxPages = 6
        val nombreSouhaite = 8

        for (page in 0 until nombreMaxPages) {

            val exercices = pagesChargees[page]
                ?: api.recupererExercices(
                    limite = taillePage,
                    decalage = page * taillePage
                ).also {
                    pagesChargees[page] = it
                }

            exercicesTrouves.addAll(
                exercices.filter { exercice ->
                    exercice.exercicesCibles.any {
                        it.equals(muscle, ignoreCase = true)
                    }
                }
            )

            if (exercicesTrouves.size >= nombreSouhaite) {
                break
            }

            if (exercices.size < taillePage) {
                break
            }
        }

        exercicesTrouves
            .distinctBy { it.id }
            .take(nombreSouhaite)
    }
}
