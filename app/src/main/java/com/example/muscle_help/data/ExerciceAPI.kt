
package com.example.muscle_help.data

import android.util.Log
import com.example.muscle_help.model.Exercice
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import org.json.JSONArray
import org.json.JSONObject

class ExerciceAPI {

    // Client HTTP Ktor
    private val client = HttpClient(OkHttp) {
        expectSuccess = true

        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 10000
        }
    }

    // Adresse de base de l'API ExerciseDB
    private val urlBase =
        "https://oss.exercisedb.dev/api/v1/exercises"

    // Récupérer les exercices correspondant à un muscle
    suspend fun recupererExercicesParMuscle(
        muscle: String
    ): List<Exercice> {

        val reponse = client.get(urlBase) {
            parameter("targetMuscles", muscle)
        }

        val json = reponse.bodyAsText()

        val exercices = convertirJson(json)

        // Vérifier que les exercices correspondent au muscle
        val exercicesFiltres = exercices.filter { exercice ->
            exercice.exercicesCibles.any {
                it.equals(muscle, ignoreCase = true)
            }
        }

        Log.d(
            "EXERCISE_API",
            "Muscle : $muscle | Reçus : ${exercices.size} | " +
                    "Correspondants : ${exercicesFiltres.size}"
        )

        return exercicesFiltres.distinctBy { it.id }
    }

    // Convertir la réponse JSON en liste d'exercices
    private fun convertirJson(json: String): List<Exercice> {

        val contenu = JSONObject(json)

        val tableau = contenu.optJSONArray("data")
            ?: contenu.optJSONObject("data")
                ?.optJSONArray("exercises")
            ?: JSONArray()

        return (0 until tableau.length()).mapNotNull { indice ->

            val objet = tableau.optJSONObject(indice)
                ?: return@mapNotNull null

            Exercice(
                id = objet.optString("exerciseId"),
                nom = objet.optString("name"),
                exercicesCibles = lireListe(
                    objet,
                    "targetMuscles"
                ),
                equipements = lireListe(
                    objet,
                    "equipments"
                ),
                instructions = lireListe(
                    objet,
                    "instructions"
                ),
                gifUrl = objet.optString("gifUrl")
            )

        }.filter { it.id.isNotBlank() }
    }

    // Transformer un tableau JSON en liste Kotlin
    private fun lireListe(
        objet: JSONObject,
        champ: String
    ): List<String> {

        val tableau = objet.optJSONArray(champ)
            ?: return emptyList()

        return (0 until tableau.length()).map {
            tableau.optString(it)
        }
    }

    // Fermer le client HTTP
    fun fermerClient() {
        client.close()
    }
}
