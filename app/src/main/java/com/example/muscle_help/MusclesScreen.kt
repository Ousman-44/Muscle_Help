
package com.example.muscle_help.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// 1. Modèle des données récupérées depuis l'API
data class ExerciceMuscle(
    val id: String,
    val nom: String,
    val muscles: List<String>,
    val materiel: List<String>,
    val instructions: List<String>,
    val gifUrl: String
)

// 2. Liste des muscles proposés à l'utilisateur
val musclesFitBuilder = linkedMapOf(
    "pectorals" to "Pectoraux",
    "biceps" to "Biceps",
    "triceps" to "Triceps",
    "lats" to "Dos",
    "delts" to "Épaules",
    "quads" to "Quadriceps",
    "hamstrings" to "Ischio-jambiers",
    "glutes" to "Fessiers",
    "abs" to "Abdominaux"
)

// 3. Conversion d'un tableau JSON en liste Kotlin
private fun lireTableau(
    objet: JSONObject,
    champ: String
): List<String> {

    val tableau = objet.optJSONArray(champ)
        ?: return emptyList()

    return (0 until tableau.length()).map {
        tableau.optString(it)
    }
}

// 4. Récupération des exercices depuis ExerciseDB
private suspend fun recupererExercices(): List<ExerciceMuscle> =
    withContext(Dispatchers.IO) {

        val url = URL(
            "https://oss.exercisedb.dev/api/v1/exercises?limit=100"
        )

        val connexion = url.openConnection() as HttpURLConnection

        try {
            connexion.requestMethod = "GET"
            connexion.connectTimeout = 10000
            connexion.readTimeout = 10000

            if (connexion.responseCode !in 200..299) {
                throw Exception(
                    "Erreur HTTP : ${connexion.responseCode}"
                )
            }

            val json = connexion.inputStream
                .bufferedReader()
                .use { it.readText() }

            // Gestion des différentes enveloppes JSON
            val racine = JSONTokenerHelper.parse(json)

            (0 until racine.length()).mapNotNull { i ->

                val objet = racine.optJSONObject(i)
                    ?: return@mapNotNull null

                ExerciceMuscle(
                    id = objet.optString("exerciseId", i.toString()),
                    nom = objet.optString("name", "Sans nom"),
                    muscles = lireTableau(objet, "targetMuscles"),
                    materiel = lireTableau(objet, "equipments"),
                    instructions = lireTableau(objet, "instructions"),
                    gifUrl = objet.optString("gifUrl")
                )
            }
        } finally {
            connexion.disconnect()
        }
    }

// 5. Lecture de la réponse JSON
private object JSONTokenerHelper {

    fun parse(json: String): JSONArray {

        val contenu = org.json.JSONTokener(json).nextValue()

        if (contenu is JSONArray) {
            return contenu
        }

        if (contenu is JSONObject) {
            val data = contenu.opt("data")

            if (data is JSONArray) {
                return data
            }

            if (data is JSONObject) {
                return data.optJSONArray("exercises")
                    ?: data.optJSONArray("results")
                    ?: JSONArray()
            }

            return contenu.optJSONArray("exercises")
                ?: contenu.optJSONArray("results")
                ?: JSONArray()
        }

        return JSONArray()
    }
}

// 6. Écran principal des muscles
@Composable
fun MusclesScreen() {

    var muscleSelectionne by remember {
        mutableStateOf("pectorals")
    }

    var exercices by remember {
        mutableStateOf(emptyList<ExerciceMuscle>())
    }

    var chargement by remember {
        mutableStateOf(true)
    }

    var erreur by remember {
        mutableStateOf<String?>(null)
    }

    var tentative by remember {
        mutableIntStateOf(0)
    }

    // Appel API au lancement de l'écran
    LaunchedEffect(tentative) {

        chargement = true
        erreur = null

        try {
            exercices = recupererExercices()
        } catch (e: Exception) {
            erreur = e.message ?: "Erreur inconnue"
        } finally {
            chargement = false
        }
    }

    // Filtrage des exercices selon le muscle choisi
    val exercicesFiltres = exercices.filter { exercice ->
        exercice.muscles.any {
            it.equals(muscleSelectionne, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Choisis ton muscle",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sélectionne un muscle pour voir ses exercices."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Liste horizontale des muscles
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(musclesFitBuilder.toList()) { muscle ->

                FilterChip(
                    selected = muscleSelectionne == muscle.first,
                    onClick = {
                        muscleSelectionne = muscle.first
                    },
                    label = {
                        Text(muscle.second)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Exercices : " +
                    musclesFitBuilder[muscleSelectionne],
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {

            // Chargement API
            chargement -> {
                CircularProgressIndicator()
            }

            // Erreur API
            erreur != null -> {
                Text("Erreur : $erreur")

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { tentative++ }
                ) {
                    Text("Réessayer")
                }
            }

            // Aucun résultat
            exercicesFiltres.isEmpty() -> {
                Text(
                    "Aucun exercice trouvé dans les " +
                            "résultats chargés pour ce muscle."
                )
            }

            // Affichage des exercices
            else -> {

                Text(
                    "${exercicesFiltres.size} exercice(s) trouvé(s)"
                )

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        exercicesFiltres,
                        key = { it.id }
                    ) { exercice ->

                        CarteExercice(exercice)
                    }
                }
            }
        }
    }
}

// 7. Carte d'un exercice
@Composable
fun CarteExercice(exercice: ExerciceMuscle) {

    var ouvert by remember {
        mutableStateOf(false)
    }

    val uriHandler = LocalUriHandler.current

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                ouvert = !ouvert
            }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = exercice.nom,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Matériel : " +
                        exercice.materiel.joinToString()
            )

            if (ouvert) {

                Spacer(Modifier.height(12.dp))

                Text(
                    "Instructions",
                    style = MaterialTheme.typography.titleSmall
                )

                exercice.instructions.forEachIndexed { index, texte ->

                    Spacer(Modifier.height(6.dp))

                    Text("${index + 1}. $texte")
                }

                if (exercice.gifUrl.isNotBlank()) {

                    Spacer(Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            uriHandler.openUri(exercice.gifUrl)
                        }
                    ) {
                        Text("Voir le GIF de l'exercice")
                    }
                }
            } else {

                Spacer(Modifier.height(6.dp))
                Text("Appuie pour voir les détails")
            }
        }
    }
}
