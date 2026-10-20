
package com.example.muscle_help.data

import com.example.muscle_help.model.Exercice
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import android.util.Log

class ExerciceAPI {


    private val client = HttpClient(OkHttp) {
        expectSuccess = true

        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 10000
        }
    }


    private val urlBase =
        "https://oss.exercisedb.dev/api/v1/exercises"

    // Récupérer les exercices depuis l'API


    suspend fun recupererExercices(
        curseur: String? = null
    ): PageExercices {

        val reponse = client.get(urlBase) {
            if (curseur != null) {
                parameter("cursor", curseur)
            }
        }

        val json = reponse.bodyAsText()
        val contenu = JSONObject(json)

        val exercices = convertirJson(json)

        val meta = contenu.optJSONObject("meta")

        val curseurSuivant = meta
            ?.optString("nextCursor")
            ?.takeIf { it.isNotBlank() }

        Log.d(
            "EXERCISE_API",
            "Curseur actuel : $curseur | Suivant : $curseurSuivant"
        )
        Log.d(
            "EXERCISE_API",
            "Exercices reçus : ${exercices.size}"
        )

        return PageExercices(
            exercices = exercices,
            curseurSuivant = curseurSuivant
        )
    }



    // Transformer le JSON en liste d'exercices
    private fun convertirJson(json: String): List<Exercice> {

        val contenu = JSONTokener(json).nextValue()

        val tableau = when (contenu) {

            is JSONArray -> contenu

            is JSONObject -> {
                val donnees = contenu.opt("data")

                when (donnees) {
                    is JSONArray -> donnees

                    is JSONObject ->
                        donnees.optJSONArray("exercises")
                            ?: donnees.optJSONArray("results")
                            ?: JSONArray()

                    else ->
                        contenu.optJSONArray("exercises")
                            ?: contenu.optJSONArray("results")
                            ?: JSONArray()
                }
            }

            else -> JSONArray()
        }

        return (0 until tableau.length()).mapNotNull { indice ->

            val objet = tableau.optJSONObject(indice)
                ?: return@mapNotNull null

            Exercice(
                id = objet.optString("exerciseId"),
                nom = objet.optString("name"),
                exercicesCibles = lireListe(objet, "targetMuscles"),
                equipements = lireListe(objet, "equipments"),
                instructions = lireListe(objet, "instructions"),
                gifUrl = objet.optString("gifUrl")
            )

        }.filter { it.id.isNotBlank() }
    }

    // Lire une liste de chaînes dans le JSON
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

    fun fermerClient() {
        client.close()
    }
}
