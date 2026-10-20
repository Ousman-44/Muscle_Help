
package com.example.muscle_help.data

import android.util.Log
import com.example.muscle_help.model.Exercice
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ExerciceRepository(
    private val api: ExerciceAPI
) {

    // Stockage temporaire en mémoire
    private val exercicesEnMemoire = mutableListOf<Exercice>()

    // Curseur de la prochaine page
    private var curseurSuivant: String? = null

    // Indique si la première page a été chargée
    private var premierePageChargee = false

    // Indique si toutes les pages sont chargées
    private var dernierePageAtteinte = false

    // Empêche les appels simultanés
    private val mutex = Mutex()

    suspend fun recupererExercicesParMuscle(
        muscle: String
    ): List<Exercice> = mutex.withLock {

        // Charger la première page si nécessaire
        if (!premierePageChargee) {
            chargerPageSuivante()
        }

        // Filtrer les exercices déjà récupérés
        fun exercicesDuMuscle(): List<Exercice> {
            return exercicesEnMemoire.filter { exercice ->
                exercice.exercicesCibles.any {
                    it.equals(muscle, ignoreCase = true)
                }
            }
        }

        var exercicesTrouves = exercicesDuMuscle()

        // Charger progressivement quelques pages supplémentaires
        var pagesChargees = 0
        val maximumPages = 2
        val objectifExercices = 6

        while (
            exercicesTrouves.size < objectifExercices &&
            !dernierePageAtteinte &&
            pagesChargees < maximumPages
        ) {
            chargerPageSuivante()
            pagesChargees++

            exercicesTrouves = exercicesDuMuscle()
        }

        Log.d(
            "EXERCISE_REPOSITORY",
            "Muscle : $muscle, exercices : ${exercicesTrouves.size}"
        )

        exercicesTrouves.distinctBy { it.id }
    }

    // Charge une page supplémentaire depuis l'API
    private suspend fun chargerPageSuivante() {

        if (dernierePageAtteinte) return

        val curseurActuel = curseurSuivant

        val page = api.recupererExercices(curseurActuel)

        // Ajouter uniquement les nouveaux exercices
        val idsExistants = exercicesEnMemoire
            .map { it.id }
            .toHashSet()

        val nouveauxExercices = page.exercices.filter {
            it.id !in idsExistants
        }

        exercicesEnMemoire.addAll(nouveauxExercices)

        // Arrêter si le curseur ne progresse pas
        if (
            page.curseurSuivant == null ||
            page.curseurSuivant == curseurActuel ||
            nouveauxExercices.isEmpty()
        ) {
            dernierePageAtteinte = true
        } else {
            curseurSuivant = page.curseurSuivant
        }

        premierePageChargee = true

        Log.d(
            "EXERCISE_REPOSITORY",
            "Total en mémoire : ${exercicesEnMemoire.size}"
        )
    }
}
