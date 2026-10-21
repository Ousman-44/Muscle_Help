
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

    var generationDemandee by remember {
        mutableStateOf(false)
    }

    var nombreDemande by remember {
        mutableIntStateOf(4)
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

        Spacer(Modifier.height(8.dp))

        Text("Personnalise ton entraînement.")

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Muscle à travailler",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Box {
            OutlinedButton(
                onClick = {
                    menuDeroulantOuvert = true
                }
            ) {
                Text(
                    muscles[muscleSelectionne]
                        ?: muscleSelectionne
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

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Nombre d'exercices : $nombreExercices",
            style = MaterialTheme.typography.titleMedium
        )

        Slider(
            value = nombreExercices.toFloat(),
            onValueChange = { valeur ->
                nombreExercices = valeur.toInt()
            },
            valueRange = 2f..8f,
            steps = 5
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                nombreDemande = nombreExercices
                generationDemandee = true

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

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Ma séance",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        when {
            chargement -> {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Création de la séance...")
            }

            erreur != null -> {
                Text(
                    text = "Erreur : $erreur",
                    color = MaterialTheme.colorScheme.error
                )
            }

            exercices.isEmpty() -> {
                Text(
                    if (generationDemandee) {
                        "Aucun exercice trouvé pour cette séance."
                    } else {
                        "Génère une séance pour commencer."
                    }
                )
            }

            else -> {
                Text(
                    text = "${exercices.size} exercices dans la séance",
                    style = MaterialTheme.typography.titleMedium
                )

                if (exercices.size < nombreDemande) {
                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Seulement ${exercices.size} exercice(s) " +
                                "disponible(s) sur les $nombreDemande demandés.",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
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
