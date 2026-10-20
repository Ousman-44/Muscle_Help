
package com.example.muscle_help.data

import com.example.muscle_help.model.Exercice
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

class ExerciceAPI {

    private val client = HttpClient(CIO) {
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
        limite: Int = 100,
        decalage: Int = 0
    ): List<Exercice> {

        val reponse = client.get(urlBase) {
            parameter("limit", limite)
            parameter("offset", decalage)
        }

        return convertirJson(reponse.bodyAsText())
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
