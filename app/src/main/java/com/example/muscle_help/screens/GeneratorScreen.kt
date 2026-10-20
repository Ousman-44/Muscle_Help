
package com.example.muscle_help.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.muscle_help.model.Exercice

@Composable
fun GeneratorScreen(
    exercices: List<Exercice> = emptyList(),
    chargement: Boolean = false,
    erreur: String? = null,
    onGenererSeance: (String, Int) -> Unit = { _, _ -> }
) {
    var muscleSelectionne by remember {
        mutableStateOf("pectorals")
    }

    var nombreExercices by remember {
        mutableIntStateOf(4)
    }

    var menuDeroulantOuvert by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Générateur de séance",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Personnalise ton entraînement.")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Muscle à travailler",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Menu déroulant pour choisir le muscle
        Box {
            OutlinedButton(
                onClick = {
                    menuDeroulantOuvert = true
                }
            ) {
                Text(
                    muscles[muscleSelectionne] ?: muscleSelectionne
                )
            }

            DropdownMenu(
                expanded = menuDeroulantOuvert,
                onDismissRequest = {
                    menuDeroulantOuvert = false
                }
            ) {
                muscles.forEach { (identifiant, nomMuscle) ->

                    DropdownMenuItem(
                        text = {
                            Text(nomMuscle)
                        },
                        onClick = {
                            muscleSelectionne = identifiant
                            menuDeroulantOuvert = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Choix du nombre d'exercices
        Text(
            text = "Nombre d'exercices : $nombreExercices",
            style = MaterialTheme.typography.titleMedium
        )

        Slider(
            value = nombreExercices.toFloat(),
            onValueChange = { nouvelleValeur ->
                nombreExercices = nouvelleValeur.toInt()
            },
            valueRange = 2f..8f,
            steps = 5
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bouton pour générer la séance
        Button(
            onClick = {
                onGenererSeance(
                    muscleSelectionne,
                    nombreExercices
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !chargement
        ) {
            Text("Générer ma séance")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Ma séance",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Affichage des résultats
        when {
            chargement -> {
                CircularProgressIndicator()
            }

            erreur != null -> {
                Text("Erreur : $erreur")
            }

            exercices.isEmpty() -> {
                Text("Génère une séance pour commencer.")
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        exercices,
                        key = { it.id }
                    ) { exercice ->

                        CarteExercice(exercice)
                    }
                }
            }
        }
    }
}
